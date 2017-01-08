package com.example.toczek.wrumwrum.Dagger.module;


import android.app.Application;

import com.example.toczek.wrumwrum.MyApplication;
import com.example.toczek.wrumwrum.Utils.providers.Obd.ObdProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private MyApplication mApplication;

    public ApplicationModule(MyApplication application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    ObdProvider provideObdProvider() {
        return new ObdProvider();
    }
}
