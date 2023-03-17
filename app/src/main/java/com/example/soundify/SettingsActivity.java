package com.example.soundify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
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
    private RadioButton lightModeRadioButton;
    private RadioButton darkModeRadioButton;
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
        automaticSwitch = findViewById(R.id.automatic_switch);
        saveButton = findViewById(R.id.save_button);

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
                // 添加保存设置的代码
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

        // 保存文件路径以供稍后使用
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // 从文件中加载照片并设置为头像
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            profileImage.setImageBitmap(bitmap);
        }
    }
}
