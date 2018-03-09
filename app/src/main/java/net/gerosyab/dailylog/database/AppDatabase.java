package net.gerosyab.dailylog.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by tremolo on 2016-06-05.
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {
    public static final String NAME = "dailyLogDatabase";
    public static final int VERSION= 1;


}
