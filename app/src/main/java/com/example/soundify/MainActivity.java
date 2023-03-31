package com.example.soundify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener{

    ImageButton play_button, previous_button, next_button;
    SeekBar seekBar;
    TextView currentTime, totalTime;
    String[] songs;
    MediaPlayer mediaPlayer;
    private int currentTheme,textSize;

    boolean isPlaying = false;
    boolean isPause = true;
    int trackNum = 1;
    int timer = 0;

    //    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        currentTheme = intent.getIntExtra("CURRENT_THEME", 0);
        textSize = intent.getIntExtra("TEXT_SIZE", 0);
        System.out.println("current Theme: " + currentTheme);
        System.out.println("text Size: " + textSize);
        applySettings(currentTheme, textSize);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songs = getResources().getStringArray(R.array.songs);

        play_button = findViewById(R.id.playButton);
        previous_button = findViewById(R.id.previousButton);
        next_button = findViewById(R.id.nextButton);
        play_button.setOnClickListener(this);
        previous_button.setOnClickListener(this);
        next_button.setOnClickListener(this);

        seekBar = findViewById(R.id.seekBar);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Toast.makeText(this, "onCreate was called", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.playButton){
            isPause = !isPause;
            if(!isPlaying){
                Toast.makeText(this, R.string.play_button, Toast.LENGTH_SHORT).show();
                setupMusic();
                isPause = false;
                isPlaying = true;
                mediaPlayer.start();

                timer++; //track time
            }
            if(isPause) {
                Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
            }

            if(!isPause) {
                mediaPlayer.start();
            }
        }

        if(view.getId() == R.id.previousButton){
            System.out.println(trackNum);
            Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show();
            trackNum-=1;
            if(trackNum == 0) trackNum = 5;
            mediaPlayer.release();
            setupMusic();
            mediaPlayer.start();

            timer = 0;  //track time
        }
        if(view.getId() == R.id.nextButton){
            System.out.println(trackNum);
            Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();
            trackNum+=1;
            if(trackNum == 5) trackNum = 1;
            mediaPlayer.release();
            setupMusic();
            mediaPlayer.start();

            timer = 0;  //track time
        }
    }

    private void applySettings(int currentTheme, int textSize){
        if(currentTheme == 0 && textSize == 0){
            setTheme(R.style.AppTheme_Light_Reg);
        }else if(currentTheme == 0 && textSize == 1){
            setTheme(R.style.AppTheme_Light_Large);
        }else if(currentTheme == 1 && textSize == 0){
            setTheme(R.style.AppTheme_Dark_Reg);
        }else if(currentTheme == 1 && textSize == 1){
            setTheme(R.style.AppTheme_Dark_Large);
        }
    }

    public void setupMusic(){
        try {
            mediaPlayer = new MediaPlayer();
            int resID = getResources().getIdentifier(songs[trackNum-1],"raw",getPackageName());
            mediaPlayer.reset();
            //mediaPlayer.setDataSource(songs[trackNum-1]);
            //mediaPlayer.setDataSource(songs[i])中的songs[i] 是字符串数组中每首歌曲的名字，而非音频文件的路径
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + resID));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.HOURS.toSeconds(1));
    }
}