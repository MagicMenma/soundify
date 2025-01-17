package com.example.soundify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    ImageView avatorImage;
    private int brightnessValue, autoModeValue, textSizeValue;
    private int x = 0;

    boolean isPlaying = false;
    boolean isPause = true;
    int trackNum = 1;
    int timer = 0;

    //    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preLoadSettings();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songs = getResources().getStringArray(R.array.songs);

        avatorImage = findViewById(R.id.avatorImage);
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

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //set the current time
                if(mediaPlayer != null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTime.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        avatorImage.setRotation(x++);
                    }else{
                        avatorImage.setRotation(0);
                    }
                }
                //smooth the seek bar
                new Handler().postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void preLoadSettings(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        brightnessValue = sharedPreferences.getInt("brightness", 0);
        autoModeValue = sharedPreferences.getInt("autoMode", 0);
        textSizeValue = sharedPreferences.getInt("textSize", 0);

        if(autoModeValue == 0) {
            // Apply brightness setting directly
            if (brightnessValue == 0 && textSizeValue == 0) {
                setTheme(R.style.AppTheme_Light_Reg);
            } else if (brightnessValue == 0 && textSizeValue == 1) {
                setTheme(R.style.AppTheme_Light_Large);
            } else if (brightnessValue == 1 && textSizeValue == 0) {
                setTheme(R.style.AppTheme_Dark_Reg);
            } else if (brightnessValue == 1 && textSizeValue == 1) {
                setTheme(R.style.AppTheme_Dark_Large);
            }
        }else if(autoModeValue == 1){
            if (textSizeValue == 0){
                setTheme(R.style.AppTheme_Light_Reg);
            }else if (textSizeValue == 1) {
                setTheme(R.style.AppTheme_Light_Large);
            } else if (textSizeValue == 0) {
                setTheme(R.style.AppTheme_Dark_Reg);
            } else if (textSizeValue == 1) {
                setTheme(R.style.AppTheme_Dark_Large);
            }

        }else if(autoModeValue == 2){
            // Apply brightness same with system
            int currentNightMode = getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
            switch (currentNightMode) {
                case Configuration.UI_MODE_NIGHT_NO:
                    if(textSizeValue == 0){
                        setTheme(R.style.AppTheme_Light_Reg);
                    }else{
                        setTheme(R.style.AppTheme_Light_Large);
                    }
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    if(textSizeValue == 0){
                        setTheme(R.style.AppTheme_Dark_Reg);
                    }else{
                        setTheme(R.style.AppTheme_Dark_Large);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
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
        if(MyMediaPlayer.currentIndex == songsList.size()-1){
            Toast.makeText(this, "This Is The Last Song.", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();
        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void playPreviousSong(){
        if(MyMediaPlayer.currentIndex == 0){
            Toast.makeText(this, "This Is The First Song.", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show();
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay(){
        if(mediaPlayer.isPlaying()) {
            Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
            mediaPlayer.pause();
        }else{
            Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
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
}