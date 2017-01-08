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
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.pressure.FuelPressureCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.UUID;

import lombok.Data;

@Data
public class ObdProvider {
    BluetoothSocket socket = null;

    private ObdListener obdListener;
    private String deviceAddress;

    private HandlerThread handlerThread;
    private Handler mainHandler;
    private Runnable mainRunnable;

    private boolean isObdConnected;

    private int mode; //TODO : change to state object

    //VALUES
    private int rpmValue;
    private int speedValue;

    private int airTemp;
    private int coolantTemp;
    private int oilTemp;
    private int fuelLevel;
    private int consumptionRate;
    private int barometricPressure;
    private int fuelPressure;

    //COMMANDS
    private RPMCommand rpmCommand = null;
    private SpeedCommand speedCommand = null;

    private AmbientAirTemperatureCommand ambientAirTemperatureCommand = null;
    private OilTempCommand oilTempCommand = null;
    private EngineCoolantTemperatureCommand coolantTempCommand = null;
    private FuelLevelCommand fuelLevelCommand = null;
    private FuelPressureCommand fuelPressureCommand = null;
    private ConsumptionRateCommand consumptionRateCommand = null;
    private BarometricPressureCommand barometricPressureCommand = null;

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
            rpmCommand = new RPMCommand();
            speedCommand = new SpeedCommand();

            oilTempCommand = new OilTempCommand();
            coolantTempCommand = new EngineCoolantTemperatureCommand();
            fuelLevelCommand = new FuelLevelCommand();
            fuelPressureCommand = new FuelPressureCommand();
            consumptionRateCommand = new ConsumptionRateCommand();
            barometricPressureCommand = new BarometricPressureCommand();
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
                                rpmValue = getIntValue(rpmCommand.getResult(), 4)/4;
                                Log.d("OBDHACK_RES", "Rpm: " + rpmValue);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E5: " + e);
                            }
                            try {
                                speedCommand.run(socket.getInputStream(), socket.getOutputStream());
                                speedValue = getIntValue(speedCommand.getResult(), 2);
                                Log.d("OBDHACK_RES", "Speed: " + speedValue);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E6: " + e);
                            }
                        }
                        else {
                            try {
                                ambientAirTemperatureCommand.run(socket.getInputStream(), socket.getOutputStream());
                                airTemp = getIntValue(ambientAirTemperatureCommand.getResult(), 2) - 40;
                                Log.d("OBDHACK_RES", "Air temp: " + airTemp);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E3: " + e);
                            }
                            try {
                                oilTempCommand.run(socket.getInputStream(), socket.getOutputStream());
                                oilTemp = getIntValue(oilTempCommand.getResult(), 2) - 40;
                                Log.d("OBDHACK_RES", "Oil temp: " + oilTemp);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E3: " + e);
                            }
                            try {
                                coolantTempCommand.run(socket.getInputStream(), socket.getOutputStream());
                                coolantTemp = getIntValue(coolantTempCommand.getResult(), 2) - 40;
                                Log.d("OBDHACK_RES", "Coolant temp: " + coolantTemp);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E4: " + e);
                            }

                            try {
                                fuelLevelCommand.run(socket.getInputStream(), socket.getOutputStream());
                                fuelLevel = getIntValue(fuelLevelCommand.getResult(), 2)*100/255;
                                Log.d("OBDHACK_RES", "Fuel level: " + fuelLevel);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E9: " + e);
                            }
                            try {
                                fuelPressureCommand.run(socket.getInputStream(), socket.getOutputStream());
                                fuelPressure = getIntValue(fuelPressureCommand.getResult(), 2) * 3;
                                Log.d("OBDHACK_RES", "Fuel level: " + fuelPressure);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E9: " + e);
                            }
                            try {
                                consumptionRateCommand.run(socket.getInputStream(), socket.getOutputStream());
                                consumptionRate = getIntValue(consumptionRateCommand.getResult(), 4) / 20;
                                Log.d("OBDHACK_RES", "Fuel level: " + consumptionRateCommand.getResult());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E9: " + e);
                            }
                            try {
                                barometricPressureCommand.run(socket.getInputStream(), socket.getOutputStream());
                                barometricPressure = getIntValue(barometricPressureCommand.getResult(), 2);
                                Log.d("OBDHACK_RES", "Fuel level: " + barometricPressureCommand.getResult());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("OBDHACK", "E9: " + e);
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

    private int getIntValue(String result, int rightBits) {
        return Integer.parseInt(result.substring(result.length()-rightBits), 16);
    }

}
