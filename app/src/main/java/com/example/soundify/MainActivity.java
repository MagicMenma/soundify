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
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener{

    ImageButton play_button, previous_button, next_button;
    SeekBar seekBar;
    TextView titleTextView, currentTime, totalTime;
    String[] songs;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
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

        titleTextView = findViewById(R.id.songsname);
        seekBar = findViewById(R.id.seekBar);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);

        titleTextView.setSelected(true);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();
    }

    @Override
    protected void onPause(){
        super.onPause();
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

    private void setResourcesWithMusic(){
        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        titleTextView.setText(currentSong.getTitle());

        totalTime.setText(convertToMMSS(currentSong.getDuration()));

        play_button.setOnClickListener(v-> pausePlay());
        next_button.setOnClickListener(v-> playNextSong());
        previous_button.setOnClickListener(v-> playPreviousSong());

        playMusic();
    }

    private void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void playNextSong(){
        if(MyMediaPlayer.currentIndex == songsList.size()-1)
            return;
        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void playPreviousSong(){
        if(MyMediaPlayer.currentIndex == 0)
            return;
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay(){
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }else{
            mediaPlayer.start();
        }
    }

    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

//    @Override
//    public void onClick(View view){
//        if(view.getId() == R.id.playButton){
//            isPause = !isPause;
//            if(!isPlaying){
//                Toast.makeText(this, R.string.play_button, Toast.LENGTH_SHORT).show();
//                setupMusic();
//                isPause = false;
//                isPlaying = true;
//                mediaPlayer.start();
//
//                timer++; //track time
//            }
//            if(isPause) {
//                Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
//                mediaPlayer.pause();
//            }
//
//            if(!isPause) {
//                mediaPlayer.start();
//            }
//        }
//
//        if(view.getId() == R.id.previousButton){
//            System.out.println(trackNum);
//            Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show();
//            trackNum-=1;
//            if(trackNum == 0) trackNum = 5;
//            mediaPlayer.release();
//            setupMusic();
//            mediaPlayer.start();
//
//            timer = 0;  //track time
//        }
//        if(view.getId() == R.id.nextButton){
//            System.out.println(trackNum);
//            Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();
//            trackNum+=1;
//            if(trackNum == 5) trackNum = 1;
//            mediaPlayer.release();
//            setupMusic();
//            mediaPlayer.start();
//
//            timer = 0;  //track time
//        }
//    }

    //    public void setupMusic(){
//        try {
//            mediaPlayer = new MediaPlayer();
//            int resID = getResources().getIdentifier(songs[trackNum-1],"raw",getPackageName());
//            mediaPlayer.reset();
//            //mediaPlayer.setDataSource(songs[trackNum-1]);
//            //mediaPlayer.setDataSource(songs[i])中的songs[i] 是字符串数组中每首歌曲的名字，而非音频文件的路径
//            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + resID));
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}