package com.research.jugnig.weatherservice;

import android.app.Application;

import com.research.jugnig.weatherservice.sync.utils.SyncUtils;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by JugniG on 16-11-2016.
 */

public class WeatherApplication extends Application {

    public static final String INTENT_UPDATE_VIEW = "intent_update_view";

    @Override
    public void onCreate() {
        super.onCreate();
        SyncUtils.CreateSyncAccount(this);
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
//        Realm.deleteRealm(realmConfig); // Delete Realm between app restarts.
        Realm.setDefaultConfiguration(realmConfig);
    }
}
