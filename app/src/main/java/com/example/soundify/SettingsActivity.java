package com.example.soundify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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

    private static final int ACCESS_CODE = 0002;
    private static final int IMAGE_CAPTURE_CODE = 0003;
    private int brightnessValue = 0, autoModeValue = 0, autoModeWillChangeTo = -1, textSizeValue = 0;
    private ImageView profileImage;
    private Button changeProfileButton;
    private EditText nameEditText;
    private EditText emailEditText;
    private RadioGroup brightnessRadioGroup;
    RadioButton lightModeRadioButton, darkModeRadioButton;
    private RadioGroup autoRadioGroup;
    RadioButton noneAutoRadioButton, autoModeRadioButton, sameWithSystemRadioButton;
    private RadioGroup textSizeRadioGroup;
    RadioButton regularSizeRadioButton, largeSizeRadioButton;
    private Button backButton;
    private Button saveButton;
    private String mCurrentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private int currentTheme;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String BRIGHTNESS_KEY = "brightness";
    private static final String AUTO_MODE_KEY = "autoMode";
    private static final String TEXT_SIZE_KEY = "textSize";

    private SensorManager sensorManager;
    private Sensor lightSensor;

    Uri image_uri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //apply the setting
        preLoadSettings();
        applySettings(brightnessValue, autoModeValue, textSizeValue);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting);
        brightnessRadioGroup = findViewById(R.id.brightness_radiogroup);
        autoRadioGroup = findViewById(R.id.autoRadiogroup);
        textSizeRadioGroup = findViewById(R.id.textSizeRadiogroup);

        loadSettings();

        profileImage = findViewById(R.id.profile_image);
        changeProfileButton = findViewById(R.id.change_profile_button);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.save_button);

        nameEditText = findViewById(R.id.name_edittext);
        emailEditText = findViewById(R.id.email_edittext);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        nameEditText.setText(sharedPreferences.getString("USER_NAME", "User_100234"));
        emailEditText.setText(sharedPreferences.getString("EMAIL", "soundify@gmail.com"));

        changeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    //check the permission
                    if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, ACCESS_CODE);
                    }else{
                        //permission already granted
                        openCamera();
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MenuActivity.class);
                intent.putExtra("CURRENT_THEME", currentTheme);
                intent.putExtra("TEXT_SIZE", textSizeValue);
                startActivity(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        //Apply brightness by using light sensor
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                System.out.println("Light Sensor:" + event.values[0]);
                System.out.println("Now Mode: " + currentTheme);
                if(autoModeValue == 1) {
                    float light = event.values[0];
                    if (light < 2000 && currentTheme == 0) {
                        System.out.println("Now to Dark " + currentTheme);
                        // If the current brightness is less than 200 & now is light mode, switch to dark mode
                        autoModeWillChangeTo = 1;
                        System.out.println("autoModeChanger: " + autoModeWillChangeTo);
                        saveSettings();
                        recreate();
                    } else if(light >= 2000 && currentTheme == 1){
                        System.out.println("Now to light " + currentTheme);
                        // If the current brightness is more than 200 & now is dark mode, switch to dark mode
                        autoModeWillChangeTo = 0;
                        System.out.println("autoModeChanger: " + autoModeWillChangeTo);
                        saveSettings();
                        recreate();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //called when image was captured from camera
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //set the image capture to ImageView
            profileImage.setImageURI(image_uri);
        }
    }

    private void preLoadSettings(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        brightnessValue = sharedPreferences.getInt("brightness", 0);
        autoModeValue = sharedPreferences.getInt("autoMode", 0);
        autoModeWillChangeTo = sharedPreferences.getInt("autoModeChanger", 0);
        textSizeValue = sharedPreferences.getInt("textSize", 0);
    }
    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        brightnessValue = sharedPreferences.getInt("brightness", 0);
        autoModeValue = sharedPreferences.getInt("autoMode", 0);
        autoModeWillChangeTo = sharedPreferences.getInt("autoModeChanger", 0);
        textSizeValue = sharedPreferences.getInt("textSize", 0);


        System.out.println("Now Mode: " + currentTheme);

        lightModeRadioButton = findViewById(R.id.lightMode);
        darkModeRadioButton = findViewById(R.id.darkMode);

        noneAutoRadioButton = findViewById(R.id.noneAuto);
        autoModeRadioButton = findViewById(R.id.autoMode);
        sameWithSystemRadioButton = findViewById(R.id.sameWithSystem);

        regularSizeRadioButton = findViewById(R.id.pt15);
        largeSizeRadioButton = findViewById(R.id.pt20);

        if (brightnessValue == 0) {
            lightModeRadioButton.setChecked(true);
        } else {
            darkModeRadioButton.setChecked(true);
        }

        if (autoModeValue == 0) {
            if(currentTheme == 0){
                lightModeRadioButton.setChecked(true);
            }else{
                darkModeRadioButton.setChecked(true);
            }
            noneAutoRadioButton.setChecked(true);
        } else if (autoModeValue == 1) {
            autoModeRadioButton.setChecked(true);
            lightModeRadioButton.setChecked(false);
            darkModeRadioButton.setChecked(false);
        } else {
            sameWithSystemRadioButton.setChecked(true);
            lightModeRadioButton.setChecked(false);
            darkModeRadioButton.setChecked(false);
        }

        if (textSizeValue == 0) {
            regularSizeRadioButton.setChecked(true);
        } else if (textSizeValue == 1) {
            largeSizeRadioButton.setChecked(true);
        }
    }

    private void saveSettings() {
        // Get selected radio buttons
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
            autoModeValue = 2;
        }

        if(regularSizeRadioButton.isChecked()){
            textSizeValue = 0;
        } else if (largeSizeRadioButton.isChecked()) {
            textSizeValue = 1;
        }

        // Save selected radio buttons
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("brightness", brightnessValue);
        editor.putInt("autoMode", autoModeValue);
        editor.putInt("textSize", textSizeValue);
        editor.putInt("autoModeChanger", autoModeWillChangeTo);
        editor.putString("USER_NAME", nameEditText.getText().toString());
        editor.putString("EMAIL", emailEditText.getText().toString());
        editor.commit();
        editor.apply();

        // Show saved message
        Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
        recreate();
    }

    private void applySettings(int brightnessValue, int autoModeValue, int textSizeValue) {
        // Apply auto mode
        if(autoModeValue == 0) {
            // Apply brightness setting directly
            if (brightnessValue == 0 && textSizeValue == 0) {
                setTheme(R.style.AppTheme_Light_Reg);
                currentTheme = 0;
            } else if (brightnessValue == 0 && textSizeValue == 1) {
                setTheme(R.style.AppTheme_Light_Large);
                currentTheme = 0;
            } else if (brightnessValue == 1 && textSizeValue == 0) {
                setTheme(R.style.AppTheme_Dark_Reg);
                currentTheme = 1;
            } else if (brightnessValue == 1 && textSizeValue == 1) {
                setTheme(R.style.AppTheme_Dark_Large);
                currentTheme = 1;
            }
        }else if(autoModeValue == 1){
            if (autoModeWillChangeTo == 0 && textSizeValue == 0){
                setTheme(R.style.AppTheme_Light_Reg);
                currentTheme = 0;
                autoModeWillChangeTo = -1;
            }else if (autoModeWillChangeTo == 0 && textSizeValue == 1) {
                setTheme(R.style.AppTheme_Light_Large);
                currentTheme = 0;
                autoModeWillChangeTo = -1;
            } else if (autoModeWillChangeTo == 1 && textSizeValue == 0) {
                setTheme(R.style.AppTheme_Dark_Reg);
                currentTheme = 1;
                autoModeWillChangeTo = -1;
            } else if (autoModeWillChangeTo == 1 && textSizeValue == 1) {
                setTheme(R.style.AppTheme_Dark_Large);
                currentTheme = 1;
                autoModeWillChangeTo = -1;
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
                    currentTheme = 0;
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    if(textSizeValue == 0){
                        setTheme(R.style.AppTheme_Dark_Reg);
                    }else{
                        setTheme(R.style.AppTheme_Dark_Large);
                    }
                    currentTheme = 1;
                    break;
            }
        }
    }


}
