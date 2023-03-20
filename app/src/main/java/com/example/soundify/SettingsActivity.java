package com.example.soundify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
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

    private int brightnessValue = 0, autoModeValue = 0;
    private ImageView profileImage;
    private Button changeProfileButton;
    private EditText nameEditText;
    private EditText emailEditText;
    private RadioGroup brightnessRadioGroup;
    RadioButton lightModeRadioButton, darkModeRadioButton;
    private RadioGroup autoRadioGroup;
    RadioButton noneAutoRadioButton, autoModeRadioButton, sameWithSystemRadioButton;
    private Button backButton;
    private Button saveButton;
    private String mCurrentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String BRIGHTNESS_KEY = "brightness";
    private static final String AUTO_MODE_KEY = "autoMode";

    private SensorManager sensorManager;
    private Sensor lightSensor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting);
        brightnessRadioGroup = findViewById(R.id.brightness_radiogroup);
        autoRadioGroup = findViewById(R.id.autoRadiogroup);

        loadSettings();

        setContentView(R.layout.setting);

        profileImage = findViewById(R.id.profile_image);
        changeProfileButton = findViewById(R.id.change_profile_button);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.save_button);

        nameEditText = findViewById(R.id.name_edittext);
        emailEditText = findViewById(R.id.email_edittext);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MenuActivity.class);
                startActivity(intent);
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

        // Apply brightness by using light sensor
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                System.out.println("Light Sensor:" + event.values[0]);
                if(autoModeRadioButton.isChecked()) {
                    float light = event.values[0];
                    if (light < 100) {
                        // If the current brightness is less than 10, switch to dark mode
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        recreate(); // recreate the Activity to apply new theme
                    } else {
                        // Else light mode
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        recreate(); // recreate the Activity to apply new theme
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        brightnessValue = sharedPreferences.getInt("brightness", 0);
        autoModeValue = sharedPreferences.getInt("autoMode", 0);

        lightModeRadioButton = findViewById(R.id.lightMode);
        darkModeRadioButton = findViewById(R.id.darkMode);

        noneAutoRadioButton = findViewById(R.id.noneAuto);
        autoModeRadioButton = findViewById(R.id.autoMode);
        sameWithSystemRadioButton = findViewById(R.id.sameWithSystem);

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
        System.out.println(noneAutoRadioButton.isChecked());
        System.out.println(autoModeRadioButton.isChecked());
        System.out.println(sameWithSystemRadioButton.isChecked());
        if(lightModeRadioButton.isChecked()){
            brightnessValue = 0;
        }else{
            brightnessValue = 1;
        }

        if(noneAutoRadioButton.isChecked()){
            autoModeValue = 0;
        } else if (autoModeRadioButton.isChecked()) {
            autoModeValue = 1;
        } else if (sameWithSystemRadioButton.isChecked()) {
            System.out.println("Problem 1");
            autoModeValue = 2;
        }

        // Save selected radio buttons
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("brightness", brightnessValue);
        editor.putInt("autoMode", autoModeValue);
        editor.putString("USER_NAME", nameEditText.getText().toString());
        editor.putString("EMAIL", emailEditText.getText().toString());
        editor.apply();

        // Apply settings
        applySettings(brightnessValue, autoModeValue);
        // Show saved message
        Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
        recreate();
    }

    private void applySettings(int brightnessValue, int autoModeValue) {
        // Apply auto mode
        System.out.println(autoModeValue);
        System.out.println(brightnessValue);
        if(autoModeValue == 0){
            // Apply brightness setting directly
            System.out.println(autoModeValue+"//"+brightnessValue);
            if (brightnessValue == 0) {
                setTheme(R.style.AppTheme_Light);
            } else {
                setTheme(R.style.AppTheme_Dark);
            }
        }else if(autoModeValue == 2){
            // Apply brightness same with system
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
        }else{
            // line 143 above
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

    protected void onPause(){
        super.onPause();
    }
}
