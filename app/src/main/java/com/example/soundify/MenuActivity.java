package com.example.soundify;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView songList;
    private ImageButton settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        songList = findViewById(R.id.song_list);
        settingsButton = findViewById(R.id.settings_button);

        // set up layout manager
        songList.setLayoutManager(new LinearLayoutManager(this));

        // read songs' name
        String[] songs = getResources().getStringArray(R.array.songs);

        // set up adapter
        adapter = new SongAdapter(songs);
        songList.setAdapter(adapter);

        // 设置设置按钮的点击事件，跳转到settings activity
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

}
