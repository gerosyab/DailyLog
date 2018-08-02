package net.gerosyab.dailylog.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.gerosyab.dailylog.util.MyLog;

import io.realm.Realm;

/**
 * Created by donghe on 2018-03-10.
 */

public class SuperActivity extends AppCompatActivity {
    Context context;
    Realm realm ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        context = getApplicationContext();
        realm = getRealm();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        realm = getRealm();
    }

    public Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            try {
                realm = Realm.getDefaultInstance();
                MyLog.d("dailylog", "realm.getPath() : " + realm.getPath());
            } catch (IllegalStateException e) {
                if (e.getMessage().contains("Call `Realm.init(Context)` before creating a RealmConfiguration")) {
                    Realm.init(getApplicationContext());
                    realm = Realm.getDefaultInstance();
                } else {
                    throw e;
                }
            }
        }

        return realm;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(realm != null && !realm.isClosed()){
//            realm.close();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(realm == null || realm.isEmpty() || realm.isClosed()){
//            realm = getRealm();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
