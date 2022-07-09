package com.example.flashlight;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageButton toggleButton;
    private TextView modeTextView;
    private boolean hasFlash = false;
    private boolean flashOn = false;
    private boolean blinkMode = false; //To indicate the mode of choice
    private boolean inBlink = false;    //To indicate the current mode
    private volatile boolean flag = false;  //Thread control

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        modeTextView = findViewById(R.id.flashStatus);

        toggleButton = findViewById(R.id.toggleButton);

        hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!blinkMode) { //Not in blink mode (i.e) Normal mode..
                    if (hasFlash) {
                        if (flashOn) {
                            flashOn = false;
                            toggleButton.setImageResource(R.drawable.flash_off);
                            try {
                                flashLightOff();
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        } else {
                            flashOn = true;
                            toggleButton.setImageResource(R.drawable.flash_on);
                            try {
                                flashLightOn();
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No flash Light found!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Blinky blinky...", Toast.LENGTH_SHORT).show();
                    Thread bg = new Thread(new Runnable() { //Thread for the blink mode
                        @Override
                        public void run() {
                            for(;;){
                                if(flag) return;
                                try {
                                    flashLightOn();
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    flashLightOff();
                                    Thread.sleep(1000);
                                } catch (CameraAccessException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    bg.start();
                }
            }
        });

        toggleButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(inBlink) {
                    flag = true;
                    Log.i("Blink","Off");
                    Toast.makeText(MainActivity.this, "Blink Mode off!", Toast.LENGTH_SHORT).show();
                    modeTextView.setText(R.string.normalMode);
                    blinkMode=false;
                    inBlink=false;
                    return true;

                }else{
                    flag = false;
                    Log.i("Blink","On");
                    modeTextView.setText(R.string.blinkMode);
                    Toast.makeText(MainActivity.this, "Blink Mode activated", Toast.LENGTH_SHORT).show();
                    blinkMode = true;
                    inBlink = true;
                    return true;
                }

            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightOn() throws CameraAccessException {
        CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String camId = camManager.getCameraIdList()[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) camManager.setTorchMode(camId, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightOff() throws CameraAccessException {
        CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String camId = camManager.getCameraIdList()[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) camManager.setTorchMode(camId, false);
    }

}