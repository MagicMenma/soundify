package com.example.soundify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
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
    private RadioGroup autoRadiogroup;
    private RadioButton noneAuto, autoMode, sameWithSystem;
    private Switch automaticSwitch;
    private Button saveButton;
    private String mCurrentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        profileImage = findViewById(R.id.profile_image);
        changeProfileButton = findViewById(R.id.change_profile_button);
        nameEditText = findViewById(R.id.name_edittext);
        emailEditText = findViewById(R.id.email_edittext);
        brightnessRadioGroup = findViewById(R.id.brightness_radiogroup);
        lightModeRadioButton = findViewById(R.id.lightMode);
        darkModeRadioButton = findViewById(R.id.darkMode);
        autoRadiogroup = findViewById(R.id.autoRadiogroup);
        noneAuto = findViewById(R.id.noneAuto);
        autoMode = findViewById(R.id.autoMode);
        sameWithSystem = findViewById(R.id.sameWithSystem);
        saveButton = findViewById(R.id.save_button);

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
