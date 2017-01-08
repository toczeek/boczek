package com.example.toczek.wrumwrum.Fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.toczek.wrumwrum.R;
import com.github.glomadrian.velocimeterlibrary.VelocimeterView;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.AbsoluteLoadCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * Created by Toczek on 2016-12-08.
 */

public class MenuFragment extends Fragment {
    private String deviceAddress;
    BluetoothSocket socket = null;
    HandlerThread handlerThread;
    Handler mainHandler;
    Runnable mainRunnable;

    LoadCommand loadCommand = null;
    AbsoluteLoadCommand absLoadCommand = null;
    OilTempCommand oilTempCommand = null;
    ObdCommand coolantTempCommand = null;
    RPMCommand rpmCommand = null;
    SpeedCommand speedCommand = null;
    RuntimeCommand runtimeCommand = null;
    FuelLevelCommand fuelLevel = null;
    ObdCommand egrErrorCommand = null;
    ObdCommand commandedEgrErrorCommand = null;

    @BindView(R.id.button1)
    Button mButton;

    @BindView(R.id.rpmVelocimeter)
    VelocimeterView rpmVelocimeter;
    @BindView(R.id.speedVelocimeter)
    VelocimeterView speedVelocimeter;
    @OnClick(R.id.button1)
    public void onClickBtn1() {
        showHelpDialog();
    }
    @OnClick(R.id.button)
    public void OnClickBtn() {
        showBTDeviceDialog();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, view);
        handlerThread = new HandlerThread("");
        handlerThread.start();
        mainHandler = new Handler(handlerThread.getLooper());
        return view;
    }
    private void showHelpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogLayout = inflater.inflate(R.layout.dialog_help, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();
    }
    private void showBTDeviceDialog() {
        ArrayList deviceStrs = new ArrayList();
        final ArrayList devices = new ArrayList();

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        final Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }

        // show list
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    //Log.d("OBDHACK", "init-------------: ");
                    dialog.dismiss();
                    int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    String deviceAddress = devices.get(position).toString();
                    // TODO save deviceAddress
                    //--------------------------------
                    //Log.d("OBDHACK", "22222222222222222-------------: ");
                    Log.d("OBDHACK", "1-------------: ");
                    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                    Log.d("OBDHACK", "2-------------: ");
                    BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
                    Log.d("OBDHACK", "3-------------: ");
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//AA:BB:CC:11:22:33");

                    Log.d("OBDHACK", "4-------------: ");

                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    Log.d("OBDHACK", "5-------------: ");
                    socket.connect();
                    Log.d("OBDHACK", "6-------------: ");
                    //-------------------------------------
                    try {
                        new ObdResetCommand().run(socket.getInputStream(), socket.getOutputStream());
                        //Below is to give the adapter enough time to reset before sending the commands, otherwise the first startup commands could be ignored.
                        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

                        Log.d("OBDHACK", "3333333333333333333-------------: ");
                        new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                        Log.d("OBDHACK", "7-------------: ");
                        new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                        Log.d("OBDHACK", "8-------------: ");
                        new TimeoutCommand(10).run(socket.getInputStream(), socket.getOutputStream());
                        Log.d("OBDHACK", "9-------------: ");
                        new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("OBDHACK", "ER: " + e);
                    }
                    //-------------------------------------------------
                    //Log.d("OBDHACK", "4444444444444444444-------------: ");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("OBDHACK", "ER: " + e);
                }
                try {

                    try {
                        loadCommand = new LoadCommand();//04#
                        absLoadCommand = new AbsoluteLoadCommand();//43#

                        oilTempCommand = new OilTempCommand();
                        coolantTempCommand = new ObdCommand("01 05") {
                            @Override
                            protected void performCalculations() {
                            }

                            @Override
                            public String getFormattedResult() {
                                return null;
                            }

                            @Override
                            public String getCalculatedResult() {
                                return null;
                            }

                            @Override
                            public String getName() {
                                return null;
                            }
                        };

                        rpmCommand = new RPMCommand();

                        speedCommand = new SpeedCommand();

                        runtimeCommand = new RuntimeCommand();

                        fuelLevel = new FuelLevelCommand();

                        egrErrorCommand = new ObdCommand("01 2D") {
                            @Override
                            protected void performCalculations() {
                            }

                            @Override
                            public String getFormattedResult() {
                                return null;
                            }

                            @Override
                            public String getCalculatedResult() {
                                return null;
                            }

                            @Override
                            public String getName() {
                                return null;
                            }
                        };

                        commandedEgrErrorCommand = new ObdCommand("01 2D") {
                            @Override
                            protected void performCalculations() {
                            }

                            @Override
                            public String getFormattedResult() {
                                return null;
                            }

                            @Override
                            public String getCalculatedResult() {
                                return null;
                            }

                            @Override
                            public String getName() {
                                return null;
                            }
                        };
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("OBDHACK", "ER: " + e);
                    }
                    //ObdMultiCommand obdCommand=new ObdMultiCommand();
                    mainRunnable = new Runnable() {
                        @Override
                        public void run() {
                            while(true) {
                                /*
                            try {
                                loadCommand.run(socket.getInputStream(), socket.getOutputStream());

                                Log.d("OBDHACK_RES", "Load: " + loadCommand.getFormattedResult());


                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E1: " + e);
                            }
                            try {
                                absLoadCommand.run(socket.getInputStream(), socket.getOutputStream());
                                Log.d("OBDHACK_RES", "Abs Load: " + absLoadCommand.getResult());

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E2: " + e);
                            }

                            try {
                                oilTempCommand.run(socket.getInputStream(), socket.getOutputStream());
                                Log.d("OBDHACK_RES", "Oil temp: " + oilTempCommand.getResult());

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E3: " + e);
                            }
                            try {
                                coolantTempCommand.run(socket.getInputStream(), socket.getOutputStream());

                                Log.d("OBDHACK_RES", "Coolant temp: " + coolantTempCommand.getResult());

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E4: " + e);
                            }
                            */
                            try {
                                rpmCommand.run(socket.getInputStream(), socket.getOutputStream());
                                String rpmResult = rpmCommand.getResult();
                                rpmResult = rpmResult.substring(rpmResult.length() - 4);
                                int rpmInt = Integer.parseInt(rpmResult, 16) / 4;
                                Log.d("OBDHACK_RES", "Rpm: " + rpmInt);
                                rpmVelocimeter.setValue(rpmInt, false);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E5: " + e);
                            }
                            try {
                                speedCommand.run(socket.getInputStream(), socket.getOutputStream());
                                String speedResult = speedCommand.getResult();
                                speedResult = speedResult.substring(speedResult.length() - 2);
                                int speedInt = Integer.parseInt(speedResult, 16);
                                Log.d("OBDHACK_RES", "Speed: " + speedInt);
                                speedVelocimeter.setValue(speedInt, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E6: " + e);
                            }

/*
                            try {
                                runtimeCommand.run(socket.getInputStream(), socket.getOutputStream());

                                Log.d("OBDHACK_RES", "Runtime since start: " + runtimeCommand.getResult());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E8: " + e);
                            }

                            try {
                                fuelLevel.run(socket.getInputStream(), socket.getOutputStream());

                                Log.d("OBDHACK_RES", "Fuel level: " + fuelLevel.getResult());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E9: " + e);
                            }

                            try {
                                egrErrorCommand.run(socket.getInputStream(), socket.getOutputStream());
                                Log.d("OBDHACK_RES", "Egr err: " + egrErrorCommand.getResult());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E10: " + e);
                            }
                            try {
                                commandedEgrErrorCommand.run(socket.getInputStream(), socket.getOutputStream());

                                Log.d("OBDHACK_RES", "Commanded egr err: " + commandedEgrErrorCommand.getResult());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E11: " + e);
                            }
                            */
                            //obdCommand.run(socket.getInputStream(), socket.getOutputStream());


                            //Log.d("OBDHACK_RES", "Obd data: " + obdCommand.getResult());

                            }
                        }
                    };
                    mainHandler.post(mainRunnable);


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("OBDHACK", "E: " + e);
                }
                //---------------------------------------------
            }
        });


        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
    }
}
