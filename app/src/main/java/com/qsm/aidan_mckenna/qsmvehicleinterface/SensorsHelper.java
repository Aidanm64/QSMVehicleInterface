package com.qsm.aidan_mckenna.qsmvehicleinterface;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by aidan_mckenna on 2018-03-07.
 */

public class SensorsHelper extends Service{

    private static final String TAG = "Sensor Service";

    public static boolean IS_RUNNING;

    SensorManager mSensorManager;
    Sensor linAccelSensor;
    SensorEventListener accelListener;
    Sensor tiltSensor;

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelListener = new AccelListener();

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
        {

        }

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null)
        {
            linAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            mSensorManager.registerListener(accelListener, linAccelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(accelListener);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    class AccelListener implements SensorEventListener
    {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            //Toast.makeText(getApplicationContext(), "New Acceleration Value", Toast.LENGTH_SHORT).show();
            //Log.d(TAG, "New X Acceleration Value = "+event.values[0]);
            Intent sensorUpdateIntent = new Intent("SENSOR_UPDATE");
            sensorUpdateIntent.putExtra("LINEAR_ACCELERATION", event.values[2]);
            sendBroadcast(sensorUpdateIntent);
        }
    }

}
