package com.qsm.aidan_mckenna.qsmvehicleinterface;

import android.app.VoiceInteractor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Optional;

/**
The purpose if the HUD activity is to provide an interface for the driver of the QSM vehicle

To ensure responsiveness of the HUD interface components, all data provided to the activity must
be processed externally and sent to the HUD broadcast receiver via and intent containing the relevant information

This will ensure that the only operations/calculations performed the HUD activity are ones that update the displayed values

 */
public class HUD extends AppCompatActivity {

    /*logging tag*/
    static private final String TAG = "HUD";

    //define listeners to be used
    GPSHelperListener GPSListener;
    SensorHelperListener SensListener;
    BluetoothHelperListener BTListener;

    /* -----------------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //for fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //access xml members
        setContentView(R.layout.activity_hud);

        //setting up actions for return button
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        /*These buttons are to be used for the race timer
        * Seriously debating making a fragment for this cus i think it would be dope and better
        * */
        Button startRaceButton = findViewById(R.id.startRaceButton);
        startRaceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });

        Button pauseRaceButton = findViewById(R.id.pauseRaceButton);
        pauseRaceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //currentRace.pause();
            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d(TAG, "HUD Activity started");

        //sets the broadcast listeners to receive specific intents based on filters
        registerListeners();

    }
    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause() {super.onPause();}
    @Override
    protected void onStop()
    {
        super.onStop();

        /* ensure all receivers are disable when the app leaves the foreground */
        unregisterReceiver(GPSListener);
        unregisterReceiver(SensListener);
        unregisterReceiver(BTListener);
    }

    @Override
    protected void onDestroy() {super.onDestroy();}


    /* -----------------------------------------------------------------------------------------------------*/
    /* -----------------------------------------------------------------------------------------------------*/
    /** LISTENER BASED UI ELEMENTS
     * These listeners are used to receive intents from the various background services
     * The supporting methods are used to take values captured by the recievers and update their corresponding UI elements*/

    void registerListeners()
    {
        //Setting up the GPS listener to receive location update intents from the GPSHelper Service
        GPSListener = new GPSHelperListener();
        IntentFilter locationUpdateFilter = new IntentFilter("LOCATION_UPDATE");
        registerReceiver(GPSListener, locationUpdateFilter);
        Log.d(TAG, "GPS listener registered");

        SensListener = new SensorHelperListener();
        IntentFilter sensorUpdateFilter = new IntentFilter("SENSOR_UPDATE");
        registerReceiver(SensListener, sensorUpdateFilter);
        Log.d(TAG, "Sensor listener registered");

        BTListener = new BluetoothHelperListener();
        IntentFilter BTUpdateFilter = new IntentFilter("BT_UPDATE");
        registerReceiver(BTListener, BTUpdateFilter);
        Log.d(TAG, "Bluetooth listener registered");


        Toast.makeText(getApplicationContext(), "Listeners started",Toast.LENGTH_SHORT).show();
    }

    /* -----------------------------------------------------------------------------------------------------*/
    /** GPS based elements*/
    /* Broadcast receiver responsible for catching the location updates sent by the GPS helper*/
    class GPSHelperListener extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "Location Update Received");

            Bundle bundle = intent.getExtras();
            Float speed = bundle.getFloat("SPEED");
            updateSpeedometer(speed);
        }

    }

    /* updateSpeedometer is called when a new locationdata intent is received by the broadcastReciever
    its purpose is to update all UI elements that require GPS data (speed, distance, bearing) */
    private void updateSpeedometer(Float speed)
    {

        TextView speedometerTextView = findViewById(R.id.speedTextView);
        //ProgressBar speedometerProgressBar = findViewById(R.id.speedometerProgressBar);

        //float progressBarValue = speed/100*speedometerProgressBar.getMax();
        //speedometerProgressBar.setProgress((int) progressBarValue);

        String speedString = Float.toString(speed);
        speedometerTextView.setText(speedString);
    }


    /* -----------------------------------------------------------------------------------------------------*/
    /** Sensor based elements */
    /* Broadcast receiver responsible for catching the sensor updates sent by the sensor helper*/


    /** MATT GO HERE*/
    /*this is where you gotta put the stuff to receive the stuff and split it apart,
    you really just need to grab the rpm for the mean time though*/
    class SensorHelperListener extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "Sensor Update Received");

            //updateRMPGauge(RPM);
        }
    }

    /*this updates the ui, should have a title number and gauge, try to make it red if it goes over a certain value
    * i think we might set that value in the config activity who knows*/
    private void updateRMPGauge(int RPM)
    {

    }


    /* -----------------------------------------------------------------------------------------------------*/
    /** Bluetooth/Network based elements
     */
    /* Broadcast receiver responsible for catching the location updates sent by the bluetooth helper*/
    class BluetoothHelperListener extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Bluetooth Update Received");

        }
    }



    /* -----------------------------------------------------------------------------------------------------*/
    /*Class that contains race timer
   private class C_Race
   {
       private Chronometer timer;
       private boolean IN_RACE;
       //private tmp;
        //maybe pass in existing chronometer
       public C_Race()
       {
           timer = findViewById();

       }



       public void start()
       {
           if(!IN_RACE)
           {
               IN_RACE = true;
               timer.setBase(SystemClock.elapsedRealtime());
           }
           timer.start();
       }

       public void resume()
       {
           timer.start();
       }
       public void pause()
       {
           //timer.setBase(SystemClock.elapsedRealtime());
           timer.stop();
       }
       public void end()
       {
           timer.stop();
       }
   }
   */
}
