package com.canakkoca.sample;

import android.app.Application;

import com.canakkoca.andzu.base.AndzuApp;

/**
 * Created by can.akkoca on 4/12/2017.
 */

public class App extends Application {

    private AndzuApp andzu;

    @Override
    public void onCreate() {
        super.onCreate();
        andzu = AndzuApp.init(this);
        sInstance = this;
    }

    private static App sInstance;

    public static App getInstance() {
        return sInstance;
    }

    public AndzuApp getAndzu() {
        return andzu;
    }

    @Override
    public void onTerminate() {
        AndzuApp.getAndzuApp().onTerminate();
        super.onTerminate();
    }
}
