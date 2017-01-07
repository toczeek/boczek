package com.example.toczek.wrumwrum.Fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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
import com.github.anastr.speedviewlib.ImageSpeedometer;
import com.github.anastr.speedviewlib.SpeedView;
import com.github.glomadrian.velocimeterlibrary.VelocimeterView;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
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
    Handler pairingHandler;
    Runnable pairRunnable;
    Runnable showRpmAndSpeed;
    RPMCommand engineRpmCommand = new RPMCommand();
    SpeedCommand speedCommand = new SpeedCommand();
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

        pairingHandler = new Handler();
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
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                deviceAddress = devices.get(position).toString();
                Log.d("PAIRTHIS",deviceAddress);

                pairRunnable=new Runnable() {
                    @Override
                    public void run() {
                                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

                                BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

                                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                                try {
                                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                                    socket.connect();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        try {
                            new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                            new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                            new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
                            new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
                            new AmbientAirTemperatureCommand().run(socket.getInputStream(), socket.getOutputStream());

                        } catch (Exception e) {
                            // handle errors
                        }
                        pairingHandler.post(showRpmAndSpeed);
                    }
                    };
                showRpmAndSpeed = new Runnable() {
                    @Override
                    public void run() {

                        try {
                            engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
                            speedCommand.run(socket.getInputStream(), socket.getOutputStream());
                            Log.d(TAG, "RPM: " + engineRpmCommand.getFormattedResult());

                            rpmVelocimeter.setValue(engineRpmCommand.getRPM(),true);
                            Log.d(TAG, "Speed: " + speedCommand.getFormattedResult());

                            speedVelocimeter.setValue(speedCommand.getMetricSpeed());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // TODO handle commands result

                        pairingHandler.post(showRpmAndSpeed);
                    }
                };
                pairingHandler.post(pairRunnable);


            }
        });

        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
    }
}
