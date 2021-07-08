package com.example.flashlight;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageButton toggleButton;
    private ImageButton infoButton;
    boolean hasFlash = false;
    boolean flashOn = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleButton = (ImageButton) findViewById(R.id.toggleButton);
        infoButton = (ImageButton) findViewById(R.id.imageButton2);

        hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (hasFlash){
                    if (flashOn) {
                        flashOn = false;
                        toggleButton.setImageResource(R.drawable.flash_off);
                        try {
                            flashLightOff();
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        flashOn = true;
                        toggleButton.setImageResource(R.drawable.flash_on);
                        try {
                            flashLightOn();
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "No flash Light found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Developed by @joyetgeorge", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightOn() throws CameraAccessException {
        CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String camId = camManager.getCameraIdList()[0];
        Toast.makeText(MainActivity.this, "Flash On", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) camManager.setTorchMode(camId, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightOff() throws CameraAccessException {
        CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String camId = camManager.getCameraIdList()[0];
        Toast.makeText(MainActivity.this, "Flash Off", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) camManager.setTorchMode(camId, false);
    }

}