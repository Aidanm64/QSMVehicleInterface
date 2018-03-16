package com.qsm.aidan_mckenna.qsmvehicleinterface;

import android.app.VoiceInteractor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Optional;

/*
The purpose if the HUD activity is to provide an interface for the driver of the QSM vehicle

To ensure resposiveness of the HUD interface components, all data provided to the activity must
be processed externally and sent to the HUD broadcast receiver via and intent containing the relevant information

This will ensure that the only operations/calculations performed the HUD activity are ones that update the displayed values

 */
public class HUD extends AppCompatActivity {

    //define listeners to be used
    GPSHelperListener GPSListener;
    SensorHelperListener SensListener;
    BluetoothHelperListener BTListener;


    //C_Race currentRace;


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


        Button startRaceButton = findViewById(R.id.startRaceButton);
        startRaceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /*if (currentRace.IN_RACE) {
                    //currentRace = new C_Race();
                    //currentRace.start();
                }
                else
                {
                    //currentRace.resume();
                }*/
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
        Toast.makeText(getApplicationContext(), "GPS listener started",Toast.LENGTH_SHORT).show();

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
    void registerListeners()
    {
        //Setting up the GPS listener to receive location update intents from the GPSHelper Service
        GPSListener = new GPSHelperListener();
        IntentFilter locationUpdateFilter = new IntentFilter("LOCATION_UPDATE");
        registerReceiver(GPSListener, locationUpdateFilter);

        SensListener = new SensorHelperListener();
        IntentFilter sensorUpdateFilter = new IntentFilter("SENSOR_UPDATE");
        registerReceiver(SensListener, sensorUpdateFilter);

        BTListener = new BluetoothHelperListener();
        IntentFilter BTUpdateFilter = new IntentFilter("BT_UPDATE");
        registerReceiver(BTListener, BTUpdateFilter);

    }

    /* -----------------------------------------------------------------------------------------------------*/
    /* GPS based elements
    /* Broadcast receiver responsible for catching the location updates sent by the GPS helper*/
    class GPSHelperListener extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
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
    /* Sensor based elements */
    /* Broadcast receiver responsible for catching the sensor updates sent by the sensor helper*/

    //MATT
    /*this is where you gotta put the stuff to receive the stuff and split it apart,
    you really just need to grab the rpm for the mean time though*/
    class SensorHelperListener extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            //updateRMPGauge(RPM);
        }
    }

    /*this updates the ui, should have a title number and gauge, try to make it red if it goes over a certain value
    * i think we might set that value in the config activity who knows*/
    private void updateRMPGauge(int RPM)
    {

    }


    /* -----------------------------------------------------------------------------------------------------*/
    /* Bluetooth/Network based elements
     */
    /* Broadcast receiver responsible for catching the location updates sent by the bluetooth helper*/
    class BluetoothHelperListener extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {


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
