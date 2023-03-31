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

    private int brightnessValue = 0, autoModeValue = 0, textSizeValue = 0;
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

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String BRIGHTNESS_KEY = "brightness";
    private static final String AUTO_MODE_KEY = "autoMode";
    private static final String TEXT_SIZE_KEY = "textSize";

    private boolean noneAuto, autoMode, sameWithSystem;

    private SensorManager sensorManager;
    private Sensor lightSensor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

                Intent intent = new Intent(SettingsActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        //Apply brightness by using light sensor
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                System.out.println("Light Sensor:" + event.values[0]);
                if(autoModeValue == 1) {
                    float light = event.values[0];
                    if (light < 200) {
                        // If the current brightness is less than 200, switch to dark mode
                        if(textSizeValue == 0){
                            setTheme(R.style.AppTheme_Dark_Reg);
                        }else{
                            setTheme(R.style.AppTheme_Dark_Large);
                        }
                        recreate(); // recreate the Activity to apply new theme
                    } else {
                        // Else light mode
                        if(textSizeValue == 0){
                            setTheme(R.style.AppTheme_Light_Reg);
                        }else{
                            setTheme(R.style.AppTheme_Light_Large);
                        }
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
        textSizeValue = sharedPreferences.getInt("textSize", 0);

        System.out.println("Load brightnessValue as " + brightnessValue);
        System.out.println("Load autoModeValue as " + autoModeValue);
        System.out.println("Load textSizeValue as " + textSizeValue);

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
            noneAutoRadioButton.setChecked(true);
        } else if (autoModeValue == 1) {
            autoModeRadioButton.setChecked(true);
        } else {
            sameWithSystemRadioButton.setChecked(true);
        }

        if (textSizeValue == 0) {
            regularSizeRadioButton.setChecked(true);
        } else if (textSizeValue == 1) {
            largeSizeRadioButton.setChecked(true);
        }
    }

    private void saveSettings() {
        // Get selected radio buttons
        System.out.println(noneAuto);
        System.out.println(autoMode);
        System.out.println(sameWithSystem);
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
        editor.putString("USER_NAME", nameEditText.getText().toString());
        editor.putString("EMAIL", emailEditText.getText().toString());
        editor.commit();
        editor.apply();

        // Apply settings
        applySettings(brightnessValue, autoModeValue, textSizeValue);
        // Show saved message
        Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
        recreate();
    }

    private void applySettings(int brightnessValue, int autoModeValue, int textSizeValue) {
        // Apply auto mode
        System.out.println(autoModeValue);
        System.out.println(brightnessValue);
        System.out.println(textSizeValue);
        if(autoModeValue == 0){
            // Apply brightness setting directly
            if (brightnessValue == 0 && textSizeValue == 0) {
                setTheme(R.style.AppTheme_Light_Reg);
            } else if (brightnessValue == 0 && textSizeValue == 1) {
                setTheme(R.style.AppTheme_Light_Large);
            }else if (brightnessValue == 1 && textSizeValue == 0){
                setTheme(R.style.AppTheme_Dark_Reg);
            }else if (brightnessValue == 1 && textSizeValue == 1){
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
        }else{
            // line 130 above when using sensor mode
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
