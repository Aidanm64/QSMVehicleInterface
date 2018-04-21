package com.qsm.aidan_mckenna.qsmvehicleinterface;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConfigActivity extends AppCompatActivity {

    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        SharedPreferences prefs = getSharedPreferences("QSM_PREF", 0);

        Button calibAccelButton = findViewById(R.id.callibAccelButton);
        calibAccelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorManager mSensMan = (SensorManager) getSystemService(SENSOR_SERVICE);
                Sensor accel = mSensMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
        });

        Button recordFinishLineButton = findViewById(R.id.recordFinishLineButton);
        recordFinishLineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



}

