package net.gerosyab.dailylog;

import android.app.Application;
import android.os.Build;

import net.gerosyab.dailylog.database.AppDatabase;
import net.gerosyab.dailylog.database.MyMigration;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class DailyLogApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("dailylog.realm")
                .schemaVersion(AppDatabase.VERSION)
                .migration(new MyMigration())
                .build();
        Realm.setDefaultConfiguration(config);

    }
}
