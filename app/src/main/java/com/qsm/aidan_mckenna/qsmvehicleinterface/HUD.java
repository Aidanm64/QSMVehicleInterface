package com.qsm.aidan_mckenna.qsmvehicleinterface;

import android.app.AlertDialog;
import android.app.VoiceInteractor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Optional;
import java.util.Timer;

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

    TextView speedometerTextView;
    TextView rpmTextView;

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

        speedometerTextView = findViewById(R.id.speedometerTextView);
        rpmTextView = findViewById(R.id.rpmTextView);

/*
        //setting up actions for return button
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        */
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
            float speed = bundle.getFloat("SPEED");
            //Toast.makeText(getApplicationContext(), ""+speed, Toast.LENGTH_SHORT).show();
            updateSpeedometer(speed);

        }

    }

    /* updateSpeedometer is called when a new locationdata intent is received by the broadcastReciever
    its purpose is to update all UI elements that require GPS data (speed, distance, bearing) */
    private void updateSpeedometer(float speed)
    {
        //ProgressBar speedometerProgressBar = findViewById(R.id.speedometerProgressBar);

        //float progressBarValue = speed/100*speedometerProgressBar.getMax();
        //speedometerProgressBar.setProgress((int) progressBarValue);

        speedometerTextView.setText(String.valueOf(speed));
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
            //updateFuelEconomyGuage(EFIDataBundle.getInt("FUELPW1")
        }
    }

    /*this updates the ui, should have a title number and gauge, try to make it red if it goes over a certain value
* i think we might set that value in the config activity who knows*/
    private void updateRPMGauge(int RPM)
    {
        //insert code for updating RPM ui elements

        rpmTextView.setText(String.valueOf(RPM));

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
       SimpleTimer raceTimer;
       SimpleTimer lapTimer;
       Chronometer raceChronoView;
       Chronometer lapChronoView;

       TextView lapTextView;
       TextView totalLapsTextView;
       Button startRaceButton;
       Button pauseRaceButton;
       Button stopRaceButton;
       Button incrementLapButton;


       int currentLap = 0;
       int totalLaps;
       long lapTimes[];     //array of lap times to be stored in a file

       AlertDialog.Builder endRaceDialogBuilder;
       AlertDialog endRaceDialog;

       String TAG = "Race Timer";

       private boolean inRace;

       File raceDataDirectory;
       FileOutputStream outputStream;

       public C_Race(int totalLaps)
       {
           raceDataDirectory = getFilesDir();

           this.totalLaps = totalLaps;      //set total laps
           lapTimes = new long[totalLaps];  //
           inRace = false;
           //setLap(0);

           initUIelements();
           initDialogs();
       }

       private void initUIelements()
       {
           raceChronoView = findViewById(R.id.raceChronometer);
           lapChronoView = findViewById(R.id.lapChronometer);
           raceTimer = new SimpleTimer(raceChronoView);
           lapTimer = new SimpleTimer(lapChronoView);


           lapTextView = findViewById(R.id.lapTextView);
           lapTextView.setText(String.valueOf(0));
           totalLapsTextView = findViewById(R.id.totalLapsTextView);
           totalLapsTextView.setText(String.valueOf(totalLaps));

           lapTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gaugeTextNormal));


           /*These buttons are to be used for the race timer
           * Seriously debating making a fragment for this cus i think it would be dope and better
           * */
           stopRaceButton = findViewById(R.id.endRaceButton);
           stopRaceButton.setOnClickListener(new stopButtonListener());

           incrementLapButton = findViewById(R.id.incrementLapButton);
           incrementLapButton.setOnClickListener(new controlButtonListener());
           incrementLapButton.setText(">");


       }

       private void initDialogs()
       {
           endRaceDialogBuilder = new AlertDialog.Builder(HUD.this);

           endRaceDialogBuilder.setMessage("End race now?");
           endRaceDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   endRace();
               }
           });
           endRaceDialogBuilder.setNegativeButton(R.string.returnToRace, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
               }
           });

           endRaceDialog = endRaceDialogBuilder.create();
       }

       void startRace()
       {
           currentLap = 0;
           inRace = true;
           addLap();
           incrementLapButton.setText("LAP");
           lapTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gaugeTextNormal));

           raceTimer.start();
           lapTimer.start();
       }

       void resume()
       {
           raceTimer.resume();
           lapTimer.resume();
       }
       void pause()
       {

           raceTimer.stop();
           lapTimer.stop();
       }
       void endRace()
       {
           inRace = false;
           raceTimer.reset();
           lapTimer.reset();
           //Toast.makeText(getApplicationContext(), "Race ended", Toast.LENGTH_SHORT).show();
           currentLap = 0;
           setLap(0);
           initUIelements();

       }

       private void addLap()
       {
           currentLap++;
           if(currentLap <= totalLaps)
           {
               //lapTimes[currentLap] = lapTimer.getTime();
               setLap(currentLap);
               lapTimer.start();
           }
           else
           {
               endRaceDialog.show();
               Toast.makeText(getApplicationContext(), "Race Complete", Toast.LENGTH_SHORT).show();
           }

       }
       private void setLap(int lap)
       {
           //Toast.makeText(getApplicationContext(), "Setting lap =  " + lap, Toast.LENGTH_SHORT).show();

           /*
           if(lap>9)
               lapTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
           else
               lapTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            */

           lapTextView.setText(String.valueOf(lap));
           if(lap == totalLaps)
           {
               lapTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gaugeTextError));
               incrementLapButton.setText("END");
               Toast.makeText(getApplicationContext(), "LAST LAP", Toast.LENGTH_SHORT).show();
           }
       }
       class LapCounter
       {
           int currentLap;
           int totalLaps;
           TextView mTextView;
           LapCounter(TextView textView, int maxLaps)
           {
               this.totalLaps = maxLaps;
           }
           void updateUI()
           {

           }
           void addLap()
           {

           }

       }

       class stopButtonListener implements View.OnClickListener
       {
           @Override
           public void onClick(View v) {
               if(inRace) {
                   endRaceDialog.show();
               }
           }
       }
       class controlButtonListener implements View.OnClickListener
       {
           @Override
           public void onClick(View v) {
               if (inRace) {
                   addLap();
               } else {
                   startRace();
               }
               //Toast.makeText(getApplicationContext(), "incrementing lap", Toast.LENGTH_SHORT).show();
           }
       }
   }
}