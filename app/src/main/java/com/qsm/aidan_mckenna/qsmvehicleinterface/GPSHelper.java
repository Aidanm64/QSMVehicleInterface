package com.qsm.aidan_mckenna.qsmvehicleinterface;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by aidan_mckenna on 2018-03-07.
 *
 * The GPSHelper class is used to provide the necessary GPS data
 * to the rest of the app
 *
 * Send data in Intent.Action.LOCATION_UPDATE
 *
 */

public class GPSHelper extends Service
{

    //debugging tag
    private static final String TAG = "GPSHelper";


    LocationManager locMan;         //responsible for communication with GPS module
    LocationListener locListener;   //handles location updates
    LocationObjectProcessor locProc;//Performs all data processing from raw location object

    //private IBinder mBinder = new GPSHelperBinder(); //might use this as some point


    /*intent used to broadcast location updates to the scope of the application*/
    Intent locationUpdateIntent;

    @Override
    public void onCreate() {

        Log.d(TAG, "GPS Helper Service started");
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "GPS Access permissions granted");

            locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //register gps service
            Log.d(TAG, "Location services accessed");

            if(locMan.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                locListener = new locListener();
                locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener); //register listener to receive updates
                Log.d(TAG, "listening for location changes");

                Toast.makeText(getApplicationContext(), "GPS service initiated", Toast.LENGTH_LONG).show();
            }
            else
            {
                Log.d(TAG,"GPS is not available");
            }

        } else {

        }

        locProc = new LocationObjectProcessor();
        /*The intent that will be used to send the locationdata asynchronously to the scope of the app*/
        locationUpdateIntent = new Intent();
        locationUpdateIntent.setAction("LOCATION_UPDATE");

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        return START_STICKY; //ensures that the service stays running when when its not actually being used
    }

    private void initLocationServices()
    {

    }

    public IBinder onBind(Intent intent) {

        return null;
    }

    /* This is the part of the Service that actually does the work
    *  The devices GPS module sends out a Location object filled with goodies
    *  This receiver grabs that object and extracts what it needs
    *  then it packages all that data into an intent and sends it to the rest of the app
    *  */
    class locListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location)
        {
            Log.d(TAG, "Location update received");
            //speed = (float) 10.10;


            Bundle processedData = locProc.process(location);
            int speedInt = (int) location.getSpeed();
            //Toast.makeText(getApplicationContext(),String.valueOf(location.getSpeed()), Toast.LENGTH_LONG).show();

            /* this is where data is added to the intent from the location object*/
            locationUpdateIntent.putExtra("SPEED", location.getSpeed());
            locationUpdateIntent.putExtra("SPEED_STR", String.valueOf(location.getSpeed()));
            locationUpdateIntent.putExtra("SPEED_INT", speedInt);
            locationUpdateIntent.putExtra("LATITUDE", location.getLatitude());
            locationUpdateIntent.putExtra("LONGITUDE", location.getLongitude());
            locationUpdateIntent.putExtra("BEARING", location.getBearing());
            locationUpdateIntent.putExtra("TIMESTAMP", location.getTime());


            //locationUpdate.putExtra("distance", );
           //Toast.makeText(getApplicationContext(),"Broadcasting GPS Location Update", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "Sending location update broadcast...");
            sendBroadcast(locationUpdateIntent);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "Provider enabled");
            Toast.makeText(getApplicationContext(), "Location Provider: ENABLED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Location Provider: DISABLED", Toast.LENGTH_SHORT).show();

        }
    }

    /*public class GPSHelperBinder extends Binder {
        GPSHelper getService(){
            return GPSHelper.this;
        }
    }*/

    class LocationObjectProcessor
    {
        Location mLocationObj;
        Location lastLocationObj;
        Bundle outputBundle;
        Bundle process(Location newLocationObj)
        {
            lastLocationObj = mLocationObj;
            mLocationObj = newLocationObj;

            return(outputBundle);

        }

        Float getSpeed()
        {
            return(mLocationObj.getSpeed());
        }

    }
}
