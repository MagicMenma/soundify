package com.example.soundify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private ImageView profileImage;
    private Button changeProfileButton;
    private EditText nameEditText;
    private EditText emailEditText;
    private RadioGroup brightnessRadioGroup;
    private RadioButton lightModeRadioButton, darkModeRadioButton;
    private RadioGroup autoRadioGroup;
    private RadioButton noneAuto, autoMode, sameWithSystem;
    private Button saveButton;
    private String mCurrentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String BRIGHTNESS_KEY = "brightness";
    private static final String AUTO_MODE_KEY = "autoMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        loadSettings();

        profileImage = findViewById(R.id.profile_image);
        changeProfileButton = findViewById(R.id.change_profile_button);
        saveButton = findViewById(R.id.save_button);

        nameEditText = findViewById(R.id.name_edittext);
        emailEditText = findViewById(R.id.email_edittext);

        //Automatically select the default theme based on the theme set by the system
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                setTheme(R.style.AppTheme_Light);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                setTheme(R.style.AppTheme_Dark);
                break;
        }
        setContentView(R.layout.activity_main);

        changeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                    }

                    if (photoFile != null) {
                        Uri photoUri = FileProvider.getUriForFile(SettingsActivity.this, "com.example.android.fileprovider", photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();

//                String userName = nameEditText.getText().toString();
//                String email = emailEditText.getText().toString();
//                Boolean lightMode = lightModeRadioButton.isChecked();
//                Boolean brightnessSwitchState = automaticSwitch.isChecked();
//
//                Intent intent = new Intent(SettingsActivity.this, MenuActivity.class);
//                intent.putExtra("USER_NAME", userName);
//                intent.putExtra("EMAIL", email);
//                intent.putExtra("LIGHT_MODE", lightMode);
//                intent.putExtra("BSS", brightnessSwitchState);
//
//                startActivity(intent);

            }
        });
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int brightnessValue = sharedPreferences.getInt(BRIGHTNESS_KEY, 0);
        int autoModeValue = sharedPreferences.getInt(AUTO_MODE_KEY, 0);

        // Set radio buttons to saved values
        RadioButton lightModeRadioButton = findViewById(R.id.lightMode);
        RadioButton darkModeRadioButton = findViewById(R.id.darkMode);

        RadioButton noneAutoRadioButton = findViewById(R.id.noneAuto);
        RadioButton autoModeRadioButton = findViewById(R.id.autoMode);
        RadioButton sameWithSystemRadioButton = findViewById(R.id.sameWithSystem);

        if (brightnessValue == 0) {
            lightModeRadioButton.setChecked(true);
        } else {
            darkModeRadioButton.setChecked(true);
        }

        if (autoModeValue == 0) {
            noneAutoRadioButton.setChecked(true);
        } else if (autoModeValue == 1) {
            autoModeRadioButton.setChecked(true);
        } else {
            sameWithSystemRadioButton.setChecked(true);
        }
    }

    private void saveSettings() {
        // Get selected radio buttons
        int brightnessValue = brightnessRadioGroup.getCheckedRadioButtonId() == R.id.lightMode ? 0 : 1;
        int autoModeValue = 0;
        if (autoRadioGroup.getCheckedRadioButtonId() == R.id.autoMode) {
            autoModeValue = 1;
        } else if (autoRadioGroup.getCheckedRadioButtonId() == R.id.sameWithSystem) {
            autoModeValue = 2;
        }

        // Save selected radio buttons
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(BRIGHTNESS_KEY, brightnessValue);
        editor.putInt(AUTO_MODE_KEY, autoModeValue);
        editor.apply();

        // Apply settings
        applySettings(brightnessValue, autoModeValue);

        // Show saved message
        Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
    }

    private void applySettings(int brightnessValue, int autoModeValue) {
        // Apply auto mode
        if(autoModeValue == 0){
            // Apply brightness setting
            if (brightnessValue == 0) {
                setTheme(R.style.AppTheme_Light);
            } else {
                setTheme(R.style.AppTheme_Dark);
            }
        }else if(autoModeValue == 1){

        }else{

        }
    }

    private File createImageFile() throws IOException {
        // create a name for the image file
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save the file path for later use
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Load a photo from a file and set it as an avatar
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            profileImage.setImageBitmap(bitmap);
        }
    }
}
