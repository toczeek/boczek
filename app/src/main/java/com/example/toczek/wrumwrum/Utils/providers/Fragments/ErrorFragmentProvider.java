package com.example.toczek.wrumwrum.Utils.providers.Fragments;

import com.example.toczek.wrumwrum.MyApplication;
import com.example.toczek.wrumwrum.Utils.providers.Obd.ObdListener;
import com.example.toczek.wrumwrum.Utils.providers.Obd.ObdProvider;

import javax.inject.Inject;

public class ErrorFragmentProvider {
    @Inject
    ObdProvider mObdProvider;

    public ErrorFragmentProvider() {
        MyApplication.inject(this);
    }

    public void setObdListener(ObdListener obdListener) {
        mObdProvider.setObdListener(obdListener);
        mObdProvider.setMode(1);
    }

    public String getTroubleCodes() {
        return mObdProvider.getTroubleCodes();
    }

    public String getPendingTroubleCodes() {
        return mObdProvider.getPendingTroubleCodes();
    }

    public String getPermanentTroubleCodes() {
        return mObdProvider.getPermanentTroubleCodes();
    }


}
