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

    public String getSpeedValue() {
        return mObdProvider.getSpeedValue();
    }

    public String getRpmValue() {
        return mObdProvider.getRpmValue();
    }

    public String getAirTemp() {
        return mObdProvider.getAirTemp();
    }

    public String getCoolantTemp() {
        return mObdProvider.getCoolantTemp();
    }

    public String getOilTemp() {
        return mObdProvider.getOilTemp();
    }

    public String getFuelLevel() {
        return mObdProvider.getFuelLevel();
    }

    public String getConsumptionRate() {
        return mObdProvider.getConsumptionRate();
    }

    public String getBarometricPressure() {
        return mObdProvider.getBarometricPressure();
    }

    public String getFuelPressure() {
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
