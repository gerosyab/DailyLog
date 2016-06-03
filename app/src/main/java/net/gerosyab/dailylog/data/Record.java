package net.gerosyab.dailylog.data;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Created by donghe on 2016-06-03.
 */
public class Record {
    int recordId;
    int categoryId;
    int recordType;
    DateTime dt;

    //Value
    boolean bool;
    Duration duration;
    int number;
    String string;
    double score;
}
