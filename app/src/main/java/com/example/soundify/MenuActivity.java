package com.example.soundify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView songList;
    private ImageButton settingsButton;

    Button song1, song2;
    Button moreMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

//        songList = findViewById(R.id.song_list);
//        settingsButton = findViewById(R.id.settings_button);
//
//        // set up layout manager
//        songList.setLayoutManager(new LinearLayoutManager(this));
//
//        // read songs' name
//        String[] songs = getResources().getStringArray(R.array.songs);

        // set up adapter
//        SensorAd adapter = new SongAdapter(songs);
//        songList.setAdapter(adapter);

        // Set the click event of the set button to jump to settings activity
        settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        song1 = findViewById(R.id.song1);
        song1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        song2 = findViewById(R.id.song2);
        song2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        moreMusic = findViewById(R.id.moreMusic);
        moreMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://music.youtube.com/explore"));
                startActivity(browserIntent);
            }
        });
    }

    public void retrieve (View view){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        int brightnessValue = sharedPreferences.getInt("brightness", 0);
        int autoModeValue = sharedPreferences.getInt("autoMode", 0);

        if(brightnessValue == 0){

        }else{

        }
    }

}
