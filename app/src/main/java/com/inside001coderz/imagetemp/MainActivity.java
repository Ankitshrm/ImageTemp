package com.inside001coderz.imagetemp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_IMAGE_PICK = 5;

    private static Uri uritext ;

    private EditText etName, etFatherName, etMotherName, etLocation;
    private Button btnUpload;
    private Button btnUploadImg;

    private UploadManager uploadManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.name);
        etFatherName = findViewById(R.id.fname);
        etMotherName = findViewById(R.id.mname);
        etLocation = findViewById(R.id.loca);
        btnUpload = findViewById(R.id.button);
        btnUploadImg =findViewById(R.id.uploadImg) ;

        uploadManager = new UploadManager(this);

        btnUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseNuploadImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    uploadFormData();
                } else {
                    requestPermissions();
                }
            }
        });
    }

    private void chooseNuploadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }


    private boolean checkPermissions() {
        int storagePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return storagePermission == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                uritext=imageUri;
            }
        }
    }


    private void uploadFormData() {
        String name = etName.getText().toString().trim();
        String fatherName = etFatherName.getText().toString().trim();
        String motherName = etMotherName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        // Replace with your server URL
        String url = "http://testingimg-env.eba-2gxmkfcj.ap-south-1.elasticbeanstalk.com/user";

        // Replace with the actual image file path on the device
//        File imageFile = new File("/path/to/image.jpg");


        String imagePath = getImageFilePath(uritext); // Pass the selected image URI here
        Log.w("PATHDE",imagePath);
        System.out.printf("imagePath -> "+imagePath);


        if (imagePath != null) {
            File imageFile = new File(imagePath);
            uploadManager.uploadFormData(url, name, fatherName, motherName, location, imageFile);
        } else {
            Toast.makeText(this, "Failed to get the image file path", Toast.LENGTH_SHORT).show();
        }

    }

    private String getImageFilePath(Uri uri) {
        String imagePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                imagePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return imagePath;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                uploadFormData();
            } else {
                Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

}