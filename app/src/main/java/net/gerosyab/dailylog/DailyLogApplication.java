package net.gerosyab.dailylog;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import net.gerosyab.dailylog.database.AppDatabase;

/**
 * Created by tremolo on 2016-06-05.
 */
public class DailyLogApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(new FlowConfig.Builder(this).build());
    }
}
