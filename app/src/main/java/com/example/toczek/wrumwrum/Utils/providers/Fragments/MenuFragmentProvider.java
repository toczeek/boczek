package com.example.toczek.wrumwrum.Utils.providers.Fragments;

import com.example.toczek.wrumwrum.MyApplication;
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

    public int getSpeed() {
        return mObdProvider.getSpeedValue();
    }

    public int getRpm() {
        return mObdProvider.getRpmValue();
    }

    public void setupObdAndStart(String device){
        mObdProvider.connect(device);
        mObdProvider.setupObd();
        mObdProvider.setupCommands();
        mObdProvider.startRunnable();
    }
}
