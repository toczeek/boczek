package com.example.toczek.wrumwrum.Utils.providers.Fragments;

import com.example.toczek.wrumwrum.MyApplication;
import com.example.toczek.wrumwrum.Utils.providers.Obd.ObdListener;
import com.example.toczek.wrumwrum.Utils.providers.Obd.ObdProvider;

import javax.inject.Inject;

public class DetailsFragmentProvider {
    @Inject
    ObdProvider mObdProvider;

    public DetailsFragmentProvider() {
        MyApplication.inject(this);
    }

    public void setObdListener(ObdListener obdListener) {
        mObdProvider.setObdListener(obdListener);
        mObdProvider.setMode(1);
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




}
