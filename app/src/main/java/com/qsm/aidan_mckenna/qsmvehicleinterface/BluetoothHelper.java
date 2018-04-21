package com.qsm.aidan_mckenna.qsmvehicleinterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;
import android.app.Service;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.icu.util.Output;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

/**
 * Created by aidan_mckenna on 2018-03-07.
 *
 * The Bluetooth Helper manages interaction with the EFI/ECU
 * Data is output from the EFI via data package and sent over bluetooth to
 * the android device bluetooth module
 *
 * Data package:
 * 27x8bit hex bytes (0x00)
 * 115200 baud
 * no parity bit
 * 8 word header
 * 1 stop word
 * sent every 100ms
 *
 * RPM -revolutions per minute
 *
 * MAP - Manifold absolute pressure
 *
 * TPS - Throttle position sensor
 *
 * ECT - Engine Coolant temperature
 *
 * IAT - Intake Air temperature
 *
 * 02S - Air/Fuel ratio (i think)
 *
 * SPARK -
 *
 * FUELPW1 - Fuel Pulse width
 *
 * FUELPW2 - Fuel Pulse width
 *
 * UbAdc - not really sure
 *
 * Mitchell can you set up the bluetooth connection from in here
 *
 */

public class BluetoothHelper extends Service {

    private static final String TAG = "BluetoothHelperService";

    private static final int BTtimeout = 10000;

    private String EFIBluetoothModuleName = "EcotronsA247";
    private String EFIMACaddress = "20:17:03:22:20:85";
    private UUID defaultUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice EFIBTModule;

    BluetoothSocket EFISocket;
    BluetoothServerSocket EFIServerSocket;
    InputStream EFIDataStream;
    Byte[] byteData;

    @Override
    public void onCreate() {
        super.onCreate();


    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        initBluetooth();
        return START_STICKY;
    }

    //TODO: Set up bluetooth connectivity
    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        EFIBTModule = mBluetoothAdapter.getRemoteDevice(EFIMACaddress);

        String name = EFIBTModule.getName();
        Toast.makeText(getApplicationContext(),"Connected to " + name, Toast.LENGTH_SHORT).show();



        try {
            EFISocket = EFIBTModule.createInsecureRfcommSocketToServiceRecord(defaultUUID);
                    //createRfcommSocketToServiceRecord(defaultUUID);
        }
        catch(Exception e)
        {
            Log.d(TAG, "Error creating BT socket");
            Toast.makeText(getApplicationContext(), "Error creating BT socket", Toast.LENGTH_SHORT).show();
        }

        try
        {
            EFISocket.connect();
            Log.d(TAG, "Device Connected");
        }
        catch(IOException e)
        {
            Log.d(TAG, "Closing socket");
            try {
                EFISocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
        }


        try {
            EFIDataStream =  EFISocket.getInputStream();
            Log.d(TAG, "Input stream acquired");
        } catch (IOException e) {
            Log.d(TAG, "Error acquiring input stream");
            e.printStackTrace();
        }

        try {
            int data = EFIDataStream.read();
            Log.d(TAG, "BT_DATA = "+ data);
            Toast.makeText(getApplicationContext(), "EFI_DATA = " + data, Toast.LENGTH_SHORT).show();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

//        try
//        {
//            EFIServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(EFIBluetoothModuleName, defaultUUID);
//            Log.d(TAG,"Server Socket initialized");
//        }
//        catch(Exception e)
//        {
//            Log.d(TAG, "Error initializing Server Socket");
//        }
//
//        try {
//            EFISocket = EFIServerSocket.accept(BTtimeout);
//        }
//        catch(Exception e)
//        {
//            Toast.makeText(getApplicationContext(), "Failed to connect to EFI", Toast.LENGTH_SHORT).show();
//            Log.d(TAG,"Failed to connect to create bluetooth socket");
//        }
//
//        try {
//            EFIServerSocket.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            EFIOutput = EFISocket.getOutputStream();
//        } catch (Exception e) {
//            Log.d(TAG, "Error recovering output stream");
//            e.printStackTrace();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private class EFIDataManager {

        int dataPacketLength = 10;

        public EFIDataManager() {

        }

        private int convertByteToValue(Byte mbyte) {
            return ((mbyte & 0x10) * 256 + (mbyte & 0x01));
        }

        private Intent makeEFIDataPacketIntent(Byte[] byteArray) {
            Intent dataPacket = new Intent();
            dataPacket.setAction("BT_UPDATE");

            dataPacket.putExtra("RPM", getRPM(byteArray[1]));

            return (dataPacket);
        }

        /* Leaving these all separate so the listener has the option to only process a
         a few values
          */
        private float getRPM(Byte RPMbyte) {
            return (convertByteToValue(RPMbyte) / 4);
        }

        private float getMAP(Byte MAPbyte) {
            return (convertByteToValue(MAPbyte) / 256);
        }

        private float getTPS(Byte TPSbyte) {
            return (convertByteToValue(TPSbyte) / 655);
        }

        private float getECT(Byte ECTbyte) {
            return (convertByteToValue(ECTbyte) - 40);
        }

        private float getIAT(Byte IATbyte) {
            return (convertByteToValue(IATbyte) - 40);
        }

        private float getO2S(Byte O2Sbyte) {
            return (convertByteToValue(O2Sbyte) / 205);
        }

        private float getSPARK(Byte SPARKbyte) {
            return (convertByteToValue(SPARKbyte) / 2);
        }

        private float getFUELPW1(Byte FUELPW1byte) {
            return (convertByteToValue(FUELPW1byte) / 1000);
        }

        private float getFUELPW2(Byte FUELPW2byte) {
            return (convertByteToValue(FUELPW2byte) / 1000);
        }

        private float getUbAdc(Byte UbAdcbyte) {
            return (convertByteToValue(UbAdcbyte) / 160);
        }

    }
   /*
   private class ConnectThread extends Thread
    {
        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

        public ConnectThread(BluetoothDevice device)
        {
            BluetoothSocket temp = null;
            this.mDevice = device;

            try{
                Log.d(TAG,"Attempting Bluetooth socket connection");
                //temp = device.createRfcommSocketToServiceRecord(mDevice.getUuids()[0].getUuid());
                temp = device.createRfcommSocketToServiceRecord(defaultUUID);
            }catch(Exception e)
            {
                Log.e(TAG, "Error connecting bluetooth device");
                e.printStackTrace();
            }
            mSocket = temp;

        }

        public void run()
        {
            try{
                mSocket.connect();

            } catch(IOException connectException){

                Toast.makeText(getApplicationContext(),"Bluetooth Socket Failed",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to connect to BT socket");
                try {
                    mSocket.close();
                } catch(IOException closeException)
                {
                    Log.e(TAG, "Failed To close BT socket");
                }
                return;
            }
            Log.d(TAG, "Bluetooth is ready to go");
            manageMyBluetoothSocket(mSocket);
        }

        public void cancel()
        {
            try{
                mSocket.close();
            } catch (IOException closeException)
            {
                Log.e(TAG, "Error closing BT socket");
            }
        }
    }
    */

    private void manageMyBluetoothSocket(BluetoothSocket socket)
    {

    }

}
