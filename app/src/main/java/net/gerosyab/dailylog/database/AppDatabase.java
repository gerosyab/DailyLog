package net.gerosyab.dailylog.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by tremolo on 2016-06-05.
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION, foreignKeysSupported = true)
public class AppDatabase {
    public static final String NAME = "dailylog_database";
    public static final int VERSION= 1;


}
