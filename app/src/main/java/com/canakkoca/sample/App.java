package com.canakkoca.sample;

import android.app.Application;

import com.canakkoca.andzu.base.AndzuApp;
import com.canakkoca.andzu.base.DaoMaster;
import com.canakkoca.andzu.base.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by can.akkoca on 4/12/2017.
 */

public class App extends Application {

    private static App sInstance;

    public static void initAndzu() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getInstance(), "andzu-db");
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        AndzuApp.init(getInstance(), daoSession);
    }

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initAndzu();
    }

    @Override
    public void onTerminate() {
        AndzuApp.onTerminate();
        super.onTerminate();
    }
}
