package com.example.soundify;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener{

    ImageButton play_button, previous_button, next_button;
    String[] songs;
    MediaPlayer mediaPlayer;

    boolean isPlaying = false;
    boolean isPause = true;
    int trackNum = 1;
    int timer = 0;

    //    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songs = getResources().getStringArray(R.array.songs);

        play_button = findViewById(R.id.play_button);
        previous_button = findViewById(R.id.previous_button);
        next_button = findViewById(R.id.next_button);
        play_button.setOnClickListener(this);
        previous_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Toast.makeText(this, "onCreate was called", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.play_button){
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

        if(view.getId() == R.id.previous_button){
            System.out.println(trackNum);
            trackNum-=1;
            if(trackNum == 0) trackNum = 5;
            mediaPlayer.release();
            setupMusic();
            mediaPlayer.start();

            timer = 0;  //track time
        }
        if(view.getId() == R.id.next_button){
            System.out.println(trackNum);
            trackNum+=1;
            if(trackNum == 5) trackNum = 1;
            mediaPlayer.release();
            setupMusic();
            mediaPlayer.start();

            timer = 0;  //track time
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
}