package com.example.soundify;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noSongsWarning;
    ArrayList<AudioModel> songsList = new ArrayList<>();
    private ImageButton settingsButton;

    private int currentTheme,textSize;

    Button moreMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        currentTheme = intent.getIntExtra("CURRENT_THEME", 0);
        textSize = intent.getIntExtra("TEXT_SIZE", 0);
        System.out.println("current Theme: " + currentTheme);
        System.out.println("text Size: " + textSize);
        applySettings(currentTheme, textSize);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        recyclerView = findViewById(R.id.songList);
        noSongsWarning = findViewById(R.id.noSongsWarning);

        if(checkPermission() == false){
            requestPermission();
            return;
        }

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
        while (cursor.moveToNext()){
            AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2));
            if(new File(songData.getPath()).exists()) {
                songsList.add(songData);
            }
        }

        if(songsList.size() == 0){
            noSongsWarning.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }



        // Set the click event of the set button to jump to settings activity
        settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
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

    @Override
    protected void onResume(){
        super.onResume();
        if(recyclerView != null){
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }
    }

    public void retrieve (View view){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        int brightnessValue = sharedPreferences.getInt("brightness", 0);
        int autoModeValue = sharedPreferences.getInt("autoMode", 0);

        if(brightnessValue == 0){

        }else{

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

    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }

    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(MenuActivity.this, "Read Permission Is Required!", Toast.LENGTH_SHORT).show();
        }else{
            ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0001);
        }
    }
}
