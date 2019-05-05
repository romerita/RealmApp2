package com.example.realmapp;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ApplicationRealm extends Application {

    //configuracion del realm
    @Override
    public void onCreate() {
        super.onCreate();
        setupRealmConfig();
        Realm realm = Realm.getDefaultInstance();
        realm.close();
    }
    private void setupRealmConfig(){
        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}