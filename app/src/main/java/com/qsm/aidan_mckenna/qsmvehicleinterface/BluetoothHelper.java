package com.qsm.aidan_mckenna.qsmvehicleinterface;

import android.app.Service;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

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
 * UbAdc
 *
 *
 *
 *
 */

public class BluetoothHelper extends Service {

    BluetoothAdapter mBluetoothAdapter;
    Byte[] byteData;

    @Override
    public void onCreate() {
        super.onCreate();


    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        initBluetooth();
        return START_STICKY;
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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


        public EFIDataManager(int dataPacketLength) {

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
}
