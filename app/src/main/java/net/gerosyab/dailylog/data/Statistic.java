package net.gerosyab.dailylog.data;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Created by donghe on 2016-06-03.
 */
public class Statistic {
    //Basic Statistic
    DateTime firstRecordDt;
    DateTime lastRecordDt;
    int totalRecordNum;
    int weekRecordNum;
    int halfMonthRecordNum;
    int monthRecordNum;
    int quarterYearRecordNum;
    int halfYearRecordNum;
    int yearRecordNum;
    Duration recordDuration;

    //Specific Statistic
    Duration totalDuration;
    Duration totalNumber;
    float averageScore;

}
