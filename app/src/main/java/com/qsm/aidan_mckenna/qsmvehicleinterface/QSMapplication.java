package com.qsm.aidan_mckenna.qsmvehicleinterface;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by aidan_mckenna on 2018-04-13.
 */

public class QSMapplication extends Application {

    private static final String TAG = "QSMApplication";

    @Override
    public void onCreate()
    {
        super.onCreate();

        Toast.makeText(this, "G'day", Toast.LENGTH_SHORT).show();

    }
    public void onTerminate() {

        //stops the background services as the app closes
        stopService(new Intent(getApplicationContext(), GPSHelper.class));
        stopService(new Intent(getApplicationContext(), SensorsHelper.class));
        stopService(new Intent(getApplicationContext(), BluetoothHelper.class));
        Log.d(TAG, "Services Stopped");

        super.onTerminate();


    }
}
