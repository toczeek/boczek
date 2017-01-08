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




}
