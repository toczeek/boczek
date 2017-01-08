package com.example.toczek.wrumwrum;

import android.app.Application;

import com.example.toczek.wrumwrum.Fragments.DetailsFragment;
import com.example.toczek.wrumwrum.Utils.providers.Fragments.DetailsFragmentProvider;
import com.example.toczek.wrumwrum.Utils.providers.Fragments.MenuFragmentProvider;

import com.example.toczek.wrumwrum.Dagger.ApplicationComponent;
import com.example.toczek.wrumwrum.Dagger.DaggerApplicationComponent;
import com.example.toczek.wrumwrum.Dagger.module.ApplicationModule;

public class MyApplication extends Application {
    private static ApplicationComponent mApplicationComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
        mApplicationComponent.inject(this);
    }

    public static void inject(MenuFragmentProvider menuFragmentProvider) {
        mApplicationComponent.inject(menuFragmentProvider);
    }

    public static void inject(DetailsFragmentProvider detailsFragmentProvider) {
        mApplicationComponent.inject(detailsFragmentProvider);
    }


}
