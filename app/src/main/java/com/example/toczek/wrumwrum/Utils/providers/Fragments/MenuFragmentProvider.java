package com.example.toczek.wrumwrum.Utils.providers.Fragments;

import com.example.toczek.wrumwrum.MyApplication;
import com.example.toczek.wrumwrum.Utils.models.ValueItem;
import com.example.toczek.wrumwrum.Utils.providers.Obd.ObdListener;
import com.example.toczek.wrumwrum.Utils.providers.Obd.ObdProvider;

import javax.inject.Inject;

public class MenuFragmentProvider {
    @Inject
    ObdProvider mObdProvider;

    public MenuFragmentProvider() {
        MyApplication.inject(this);
    }

    public void setObdListener(ObdListener obdListener) {
        mObdProvider.setObdListener(obdListener);
        mObdProvider.setMode(0);
    }

    public int getSpeedValue() {
        return mObdProvider.getSpeedValue();
    }

    public int getRpmValue() {
        return mObdProvider.getRpmValue();
    }

    public int getAirTemp() {
        return mObdProvider.getAirTemp();
    }

    public int getCoolantTemp() {
        return mObdProvider.getCoolantTemp();
    }

    public int getOilTemp() {
        return mObdProvider.getOilTemp();
    }

    public int getFuelLevel() {
        return mObdProvider.getFuelLevel();
    }

    public int getConsumptionRate() {
        return mObdProvider.getConsumptionRate();
    }

    public int getBarometricPressure() {
        return mObdProvider.getBarometricPressure();
    }

    public int getFuelPressure() {
        return mObdProvider.getFuelPressure();
    }

    public ValueItem optionCombiner(ValueItem valueItem) {
        switch (valueItem.getId()){
            case 0:
                valueItem.setValue(mObdProvider.getSpeedValue());
                valueItem.setMaxValue(255);
                valueItem.setUnit("KM/h");
                break;
            case 1:
                valueItem.setValue(mObdProvider.getRpmValue());
                valueItem.setMaxValue(16000);
                valueItem.setUnit("RPM");
                break;
            case 2:
                valueItem.setValue(mObdProvider.getAirTemp());
                valueItem.setMaxValue(215);
                valueItem.setUnit("°C");
                break;
            case 3:
                valueItem.setValue(mObdProvider.getCoolantTemp());
                valueItem.setMaxValue(210);
                valueItem.setUnit("°C");
                break;
            case 4:
                valueItem.setValue(mObdProvider.getOilTemp());
                valueItem.setMaxValue(210);
                valueItem.setUnit("°C");
                break;
            case 5:
                valueItem.setValue(mObdProvider.getConsumptionRate());
                valueItem.setMaxValue(3000);
                valueItem.setUnit("l/h");
                break;
            case 6:
                valueItem.setValue(mObdProvider.getBarometricPressure());
                valueItem.setMaxValue(255);
                valueItem.setUnit("kPa");
                break;
            case 7:
                valueItem.setValue(mObdProvider.getFuelPressure());
                valueItem.setMaxValue(765);
                valueItem.setUnit("kPa");
                break;
            case 8:
                valueItem.setValue(mObdProvider.getFuelLevel());
                valueItem.setMaxValue(100);
                valueItem.setUnit("%");
                break;
        }
        return valueItem;
    }
    
    public void setupObdAndStart(String device){
        mObdProvider.connect(device);
        mObdProvider.setupObd();
        mObdProvider.setupCommands();
        mObdProvider.startRunnable();
    }
}
