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
    C_Race raceTimer;

    /* -----------------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //for fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //access xml members
        setContentView(R.layout.activity_hud);
        raceTimer = new C_Race(10);


        //setting up actions for return button
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
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
    protected void onResume() {super.onResume();}
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
    /** Race timer and stuff like that
     */

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

        //String speedString = Float.toString(speed);
        speedometerTextView.setText(speed + "");
    }


    /* -----------------------------------------------------------------------------------------------------*/
    /** Sensor based elements */
    /* Broadcast receiver responsible for catching the sensor updates sent by the sensor helper*/

    class SensorHelperListener extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "Sensor Update Received");

            //updateRMPGauge(RPM);
        }
    }





    /* -----------------------------------------------------------------------------------------------------*/
    /** Bluetooth/Network based elements
     */
    /** MATT GO HERE*/
    /*this is where you gotta put the stuff to receive the stuff and split it apart,
    you really just need to grab the rpm for the mean time though*/

    /* Broadcast receiver responsible for catching the location updates sent by the bluetooth helper*/
    class BluetoothHelperListener extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Bluetooth Update Received");

            //get data out of intent
            Bundle EFIDataBundle = intent.getExtras();
            //pass values into ui updating functions
            updateRPMGauge(EFIDataBundle.getInt("RPM"));
            //updateFuelEconomyGuage(EFIDataBundle.getInt("
        }
    }

    /*this updates the ui, should have a title number and gauge, try to make it red if it goes over a certain value
* i think we might set that value in the config activity who knows*/
    private void updateRPMGauge(int RPM)
    {
        //insert code for updating RPM ui elements
    }
    private void updateFuelEconomyGauge()
    {

    }
    private void updateTempGauge()
    {

    }


    /* -----------------------------------------------------------------------------------------------------*/
    //Class that contains
   private class C_Race
   {
       Chronometer raceTimer;
       Chronometer lapTimer;
       TextView lapTextView;
       Button startRaceButton;
       Button pauseRaceButton;
       Button stopRaceButton;
       Button incrementLapButton;

       int currentLap = 0;
       int totalLaps;

       long timeBase;
       long raceElapsedTime = 0;
       long lapElapsedTime = 0;

       String TAG;

       private boolean inRace;

       //private tmp;

       public C_Race(int totalLaps)
       {
           this.totalLaps = totalLaps;
           inRace = false;

           raceTimer = findViewById(R.id.raceChronometer);
           lapTimer = findViewById(R.id.lapChronometer);
           lapTextView = findViewById(R.id.lapTextView);
           pauseRaceButton = findViewById(R.id.pauseRaceButton);


           /*These buttons are to be used for the race timer
           * Seriously debating making a fragment for this cus i think it would be dope and better
           * */

           startRaceButton = findViewById(R.id.startRaceButton);
           startRaceButton.setOnClickListener(new View.OnClickListener(){
               @Override
               public void onClick(View v){
                   if(inRace)
                   {
                       resume();
                   }
                   else
                   {
                       start();
                   }
               }
           });

           pauseRaceButton = findViewById(R.id.pauseRaceButton);
           pauseRaceButton.setOnClickListener(new View.OnClickListener(){
               @Override
               public void onClick(View v){
                   pause();
               }
           });

           stopRaceButton = findViewById(R.id.endRaceButton);
           stopRaceButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   end();
               }
           });
           incrementLapButton = findViewById(R.id.incrementLapButton);
           incrementLapButton.setOnClickListener(new View.OnClickListener(){
               @Override
               public void onClick(View v){
                   addLap();
                   //Toast.makeText(getApplicationContext(), "incrementing lap", Toast.LENGTH_SHORT).show();
               }
           });

           setLap(0);
       }

       void start()
       {
           if(!inRace) {
               timeBase = SystemClock.elapsedRealtime();
               inRace = true;
               setLap(1);
               raceTimer.setBase(timeBase);
               lapTimer.setBase(timeBase);
               //startRaceButton.setText("PAUSE");
           }
           raceTimer.start();
           lapTimer.start();
       }

       void resume()
       {
           raceTimer.setBase(SystemClock.elapsedRealtime() - raceElapsedTime);
           lapTimer.setBase(SystemClock.elapsedRealtime() - lapElapsedTime);

           raceTimer.start();
           lapTimer.start();
       }
       void pause()
       {

           raceElapsedTime = SystemClock.elapsedRealtime() - raceTimer.getBase();
           //Toast.makeText(getApplicationContext(),"elapsed time - " + elapsedTime, Toast.LENGTH_SHORT).show();
           raceTimer.stop();

           //raceTimer.setBase(SystemClock.elapsedRealtime());

           lapElapsedTime = SystemClock.elapsedRealtime() -lapTimer.getBase();
           lapTimer.stop();
           //lapTimer.setBase(SystemClock.elapsedRealtime());
       }
       void end()
       {
           raceTimer.stop();

       }

       private void addLap()
       {
           currentLap++;
           setLap(currentLap);
           lapTimer.setBase(SystemClock.elapsedRealtime());

       }
       private void setLap(int lap)
       {
           if(lap <= totalLaps)
           {
               Toast.makeText(getApplicationContext(), "Setting lap =  " + lap, Toast.LENGTH_SHORT).show();
               lapTextView.setText(lap + " /10");
           }
       }
   }
}
