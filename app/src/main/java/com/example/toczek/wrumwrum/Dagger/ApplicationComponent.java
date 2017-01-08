package com.example.toczek.wrumwrum.Dagger;


import com.example.toczek.wrumwrum.MyApplication;
import com.example.toczek.wrumwrum.Utils.providers.Fragments.MenuFragmentProvider;

import javax.inject.Singleton;

import com.example.toczek.wrumwrum.Dagger.module.ApplicationModule;
import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    void inject(MyApplication myApplication);
    void inject(MenuFragmentProvider menuFragmentProvider);
}
