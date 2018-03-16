package com.qsm.aidan_mckenna.qsmvehicleinterface;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.Toast;

/*

The purpose of the MainActivity is to initialize the app by performing all permission verification
and initializing background services

This activity must end all services when the app closes

 */
public class MainActivity extends AppCompatActivity {

    /*logging tag*/
    static private final String TAG = "MainActivity";

    /*request codes*/
    static private int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;
    static private int MY_BT_ENABLED = 1;



    /* -----------------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"Main activity started");

        setContentView(R.layout.activity_main);

        verifyPermissions();
        verifyHardwareEnabled();

        Log.d(TAG, "Starting services");
        startBackgroundServices();


        //register navigation buttons for interaction
        Button configActivityButton = findViewById(R.id.configActivityButton);
        configActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent configIntent = new Intent(getApplicationContext(), ConfigActivity.class);
                startActivity(configIntent);
            }
        });
        Button launchRaceHUDButton = findViewById(R.id.launchRaceHUDButton);
        launchRaceHUDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent raceHUDIntent = new Intent(getApplicationContext(), HUD.class);
                startActivity(raceHUDIntent);

            }
        });

    }
    private void verifyPermissions()
    {
        if ( ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
    }

    private void verifyHardwareEnabled()
    {
        if(!BluetoothAdapter.getDefaultAdapter().isEnabled())
        {
            int MY_BT_ENABLED = 0;
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, MY_BT_ENABLED);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==MY_PERMISSION_ACCESS_FINE_LOCATION)
        {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.d(TAG, "Location permissions granted");

            }
            else {
                // Permission was denied or request was cancelled
            }

        }
        if(requestCode==MY_BT_ENABLED)
        {

        }
    }

    private void startBackgroundServices()
    {
        //Initializes the GPS helper to run in the background
        Intent GPSHelperServiceIntent = new Intent(getApplicationContext(), GPSHelper.class);
        //
        startService(GPSHelperServiceIntent);
        Log.d(TAG, "GPS Service started");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //stops the background services as the app closes
        stopService(new Intent(getApplicationContext(), GPSHelper.class));
        stopService(new Intent(getApplicationContext(), SensorsHelper.class));
        stopService(new Intent(getApplicationContext(), BluetoothHelper.class));
        Log.d(TAG, "Services Stopped");
    }




    /* -----------------------------------------------------------------------------------------------------*/




}
