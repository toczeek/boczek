package com.example.toczek.wrumwrum.Utils.providers.Obd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

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
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.UUID;

import lombok.Data;

@Data
public class ObdProvider {
    BluetoothSocket socket = null;
    private boolean isObdConnected;

    private int mode; //TODO : change to state object

    private int rpmValue;
    private int speedValue;

    private int coolantTemp;
    private int oilTemp;
    private int fuelLevel;
    private int consumptionRate;
    private int barometricPressure;
    private int fuelPressure;

    private ObdListener obdListener;
    private String deviceAddress;

    private HandlerThread handlerThread;
    private Handler mainHandler;
    private Runnable mainRunnable;

    private LoadCommand loadCommand = null;
    private AbsoluteLoadCommand absLoadCommand = null;
    private OilTempCommand oilTempCommand = null;
    private ObdCommand coolantTempCommand = null;
    private RPMCommand rpmCommand = null;
    private SpeedCommand speedCommand = null;
    private RuntimeCommand runtimeCommand = null;
    private FuelLevelCommand fuelLevelCommand = null;
    private ObdCommand egrErrorCommand = null;
    private ObdCommand commandedEgrErrorCommand = null;

    public ObdProvider() {
        handlerThread = new HandlerThread("");
        handlerThread.start();
        mainHandler = new Handler(handlerThread.getLooper());
    }

    public void connect(String devAddress) {
        this.deviceAddress = devAddress;
        Log.d("OBDlog", "1-------------: ");
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("OBDlog", "2-------------: ");
        BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
        Log.d("OBDlog", "3-------------: ");
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//AA:BB:CC:11:22:33");

        Log.d("OBDlog", "4-------------: ");

        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("OBDlog", "5-------------: ");
    }
    
    public void setupObd() {
        Log.d("OBDlog", "6-------------: ");
        try {
            new ObdResetCommand().run(socket.getInputStream(), socket.getOutputStream());
            //Below is to give the adapter enough time to reset before sending the commands, otherwise the first startup commands could be ignored.
            try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

            Log.d("OBDlog", "3333333333333333333-------------: ");
            new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            Log.d("OBDlog", "7-------------: ");
            new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            Log.d("OBDlog", "8-------------: ");
            new TimeoutCommand(10).run(socket.getInputStream(), socket.getOutputStream());
            Log.d("OBDlog", "9-------------: ");
            new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("OBDlog", "ER: " + e);
        }
    }
    
    public void setupCommands() {
        try {
            loadCommand = new LoadCommand();//04#
            absLoadCommand = new AbsoluteLoadCommand();//43#

            oilTempCommand = new OilTempCommand();
            coolantTempCommand = new EngineCoolantTemperatureCommand();
            rpmCommand = new RPMCommand();

            speedCommand = new SpeedCommand();

            runtimeCommand = new RuntimeCommand();

            fuelLevelCommand = new FuelLevelCommand();

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
            Log.d("OBDlog", "ER: " + e);
        }

    }

    public void startRunnable() {
        try {
            mainRunnable = new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        if (mode == 0) {
                            try {
                                rpmCommand.run(socket.getInputStream(), socket.getOutputStream());
                                String rpmResult = rpmCommand.getResult();
                                rpmResult = rpmResult.substring(rpmResult.length() - 4);
                                rpmValue = Integer.parseInt(rpmResult, 16) / 4;
                                Log.d("OBDHACK_RES", "Rpm: " + rpmValue);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E5: " + e);
                            }
                            try {
                                speedCommand.run(socket.getInputStream(), socket.getOutputStream());
                                String speedResult = speedCommand.getResult();
                                speedResult = speedResult.substring(speedResult.length() - 2);
                                speedValue = Integer.parseInt(speedResult, 16);
                                Log.d("OBDHACK_RES", "Speed: " + speedValue);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E6: " + e);
                            }
                        }
                        else {
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

                            try {
                                runtimeCommand.run(socket.getInputStream(), socket.getOutputStream());

                                Log.d("OBDHACK_RES", "Runtime since start: " + runtimeCommand.getResult());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E8: " + e);
                            }

                            try {
                                fuelLevelCommand.run(socket.getInputStream(), socket.getOutputStream());

                                Log.d("OBDHACK_RES", "Fuel level: " + fuelLevelCommand.getResult());
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

                            //obdCommand.run(socket.getInputStream(), socket.getOutputStream());


                            //Log.d("OBDHACK_RES", "Obd data: " + obdCommand.getResult());
                        }

                        if (obdListener != null) {
                            obdListener.wasUpdate();
                        }
                    }
                }
            };
            mainHandler.post(mainRunnable);


        } catch (Exception e) {
            e.printStackTrace();
            Log.d("OBDHACK", "E: " + e);
        }
    }

}
