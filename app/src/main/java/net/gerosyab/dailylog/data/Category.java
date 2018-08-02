package net.gerosyab.dailylog.data;

import android.app.PendingIntent;
import android.util.Log;

import net.gerosyab.dailylog.util.MyLog;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.sql.Date;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Category extends RealmObject {
    private static final String LOG_TAG = "Category";

    @PrimaryKey
    private String categoryId;

    @Index
    private String name;

    private String unit;

    private long order;

    private long recordType;

    private long defaultValue;

    private static final long MAX_VALUE = 99999;
    private static final long MAX_MEMO_LENGTH = 500;
    private static final long MAX_CATEGORY_NAME_LENGTH = 50;
    private static final long MAX_UNIT_NAME_LENGTH = 20;

    public static final int DAY = 0;
    public static final int MONTH = 1;
    public static final int YEAR = 2;

    private static final int CHART_DAILY_PERIOD = 7;
    private static final int CHART_WEEKLY_PERIOD = 6;
    private static final int CHART_MONTHLY_PERIOD = 6;
    private static final int CHART_YEARLY_PERIOD = 5;

    RealmList<Record> records;

    public Category() {
    }

    public Category(String name, String unit, long order, long recordType) {
        this.name = name;
        this.unit = unit;
        this.order = order;
        this.recordType = recordType;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public long getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(long defaultValue) {
        this.defaultValue = defaultValue;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public long getRecordType() {
        return recordType;
    }

    public void setRecordType(long recordType) {
        this.recordType = recordType;
    }

    public static long getMaxValue() {
        return MAX_VALUE;
    }

    public static long getMaxMemoLength() {
        return MAX_MEMO_LENGTH;
    }

    public static long getMaxCategoryNameLength() {
        return MAX_CATEGORY_NAME_LENGTH;
    }

    public static long getMaxUnitNameLength() {
        return MAX_UNIT_NAME_LENGTH;
    }

    public void deleteCategory(Realm realm) {
        realm.beginTransaction();
        RealmResults<Record> records = realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll();
        records.deleteAllFromRealm();

        // re-sorting order value
        RealmResults<Category> results = realm.where(Category.class)
                .notEqualTo("categoryId", categoryId)
                .findAll().sort("order", Sort.ASCENDING);
        if (results != null && !results.isEmpty()) {
            for (int i = 0; i < results.size(); i++) {
                results.get(i).setOrder(i);
            }
        }

        RealmResults<Category> category = realm.where(Category.class)
                .equalTo("categoryId", categoryId)
                .findAll();
        category.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static RealmResults<Category> getCategories(Realm realm) {
        return realm.where(Category.class)
                .findAll().sort("order", Sort.ASCENDING);
    }

    public static Category getCategory(Realm realm, String categoryId) {
        return realm.where(Category.class)
                .equalTo("categoryId", categoryId)
                .findFirst();
    }

    public RealmResults<Record> getRecords(Realm realm) {
        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll();
    }

    public RealmResults<Record> getRecordsOrderByIdAscending(Realm realm) {
        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll()
                .sort("recordId", Sort.ASCENDING);
    }

    public RealmResults<Record> getRecordsOrderByDateAscending(Realm realm) {
        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll()
                .sort("date", Sort.ASCENDING);
    }

    public RealmResults<Record> getRecords(Realm realm, Date fromDate, Date toDate) {
        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .between("date", fromDate, toDate)
                .findAll()
                .sort("date", Sort.ASCENDING);
    }

    public boolean hasRecord(Realm realm, Date date) {
        RealmResults<Record> records = realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .equalTo("date", date)
                .findAll();

        if (records == null || records.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public Record getRecord(Realm realm, Date date) {
        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .equalTo("date", date)
                .findFirst();
    }

    public DateTime getFirstRecordDateTime(Realm realm) {
        java.util.Date firstRecordDate = realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll().minDate("date");
        if (firstRecordDate == null) {
            MyLog.d("Statistic", "getFirstRecordDateTime is null");
            return null;
        } else {
            MyLog.d("Statistic", "getFirstRecordDateTime is not null");
            return new DateTime(firstRecordDate);
        }
    }

    public DateTime getLastRecordDateTime(Realm realm) {
        java.util.Date firstRecordDate = realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll().maxDate("date");
        if (firstRecordDate == null) {
            MyLog.d("Statistic", "getLastRecordDateTime is null");
            return null;
        } else {
            MyLog.d("Statistic", "getLastRecordDateTime is not null");
            return new DateTime(firstRecordDate);
        }
    }

    public long getTotalRecordNum(Realm realm) {
        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll().size();
    }

    public long getRecentRecordNum(int periodType, int periodValue, Realm realm) {
        DateTime toDt = new DateTime().withTimeAtStartOfDay();
        DateTime fromDt = null;
        if (periodType == DAY) fromDt = toDt.minusDays(periodValue).withTimeAtStartOfDay();
        else if (periodType == MONTH) fromDt = toDt.minusMonths(periodValue).withTimeAtStartOfDay();
        else if (periodType == YEAR) fromDt = toDt.minusYears(periodValue).withTimeAtStartOfDay();

        if (fromDt != null) {
            return realm.where(Record.class)
                    .equalTo("categoryId", categoryId)
                    .between("date", new java.sql.Date(fromDt.toDate().getTime()), new java.sql.Date(toDt.toDate().getTime()))
                    .findAll().size();
        } else {
            return 0;
        }
    }

    public long getRecordNum(Date fromDate, Date toDate, Realm realm) {
        if (fromDate != null && toDate != null) {
            return realm.where(Record.class)
                    .equalTo("categoryId", categoryId)
                    .between("date", fromDate, toDate)
                    .findAll().size();
        } else {
            return 0;
        }
    }

    public double getRecentAverage(int periodType, int periodValue, Realm realm) {
        DateTime toDt = new DateTime().withTimeAtStartOfDay();
        DateTime fromDt = null;
        if (periodType == DAY) fromDt = toDt.minusDays(periodValue).withTimeAtStartOfDay();
        else if (periodType == MONTH) fromDt = toDt.minusMonths(periodValue).withTimeAtStartOfDay();
        else if (periodType == YEAR) fromDt = toDt.minusYears(periodValue).withTimeAtStartOfDay();

        if (fromDt != null) {
            RealmResults<Record> results = realm.where(Record.class)
                    .equalTo("categoryId", categoryId)
                    .between("date", new java.sql.Date(fromDt.toDate().getTime()), new java.sql.Date(toDt.toDate().getTime()))
                    .findAll();

            if (results != null && !results.isEmpty()) {
                return results.average("number");
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public double getTotalAverage(Realm realm) {
        RealmResults<Record> results = realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll();

        if (results != null && !results.isEmpty()) {
            return results.average("number");
        } else {
            return 0;
        }
    }

    public double getAverage(Date fromDate, Date toDate, Realm realm) {
        if (fromDate != null && toDate != null) {
            return realm.where(Record.class)
                    .equalTo("categoryId", categoryId)
                    .between("date", fromDate, toDate)
                    .findAll().average("number");
        } else {
            return 0;
        }
    }

    public long getRecentSum(int periodType, int periodValue, Realm realm) {
        DateTime toDt = new DateTime().withTimeAtStartOfDay();
        DateTime fromDt = null;
        if (periodType == DAY) fromDt = toDt.minusDays(periodValue).withTimeAtStartOfDay();
        else if (periodType == MONTH) fromDt = toDt.minusMonths(periodValue).withTimeAtStartOfDay();
        else if (periodType == YEAR) fromDt = toDt.minusYears(periodValue).withTimeAtStartOfDay();

        if (fromDt != null) {
            RealmResults<Record> results = realm.where(Record.class)
                    .equalTo("categoryId", categoryId)
                    .between("date", new java.sql.Date(fromDt.toDate().getTime()), new java.sql.Date(toDt.toDate().getTime()))
                    .findAll();

            if (results != null && !results.isEmpty()) {
                return results.sum("number").longValue();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public long getTotalSum(Realm realm) {
        RealmResults<Record> results = realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll();

        if (results != null && !results.isEmpty()) {
            return results.sum("number").longValue();
        } else {
            return 0;
        }
    }

    public long getSum(Date fromDate, Date toDate, Realm realm) {
        if (fromDate != null && toDate != null) {
            return realm.where(Record.class)
                    .equalTo("categoryId", categoryId)
                    .between("date", fromDate, toDate)
                    .findAll().sum("number").longValue();
        } else {
            return 0;
        }
    }

    public Period getRecordPeriod(Realm realm) {
        DateTime firstRecordDateTime = this.getFirstRecordDateTime(realm);
        DateTime lastRecordDateTime = this.getLastRecordDateTime(realm);

        if (firstRecordDateTime != null && lastRecordDateTime != null) {
            Period period = new Period(firstRecordDateTime.toInstant(), lastRecordDateTime.toInstant());
            return period;
        } else {
            return null;
        }
    }

    public static long getNewOrderNum(Realm realm) {
        RealmResults<Category> results = realm.where(Category.class)
                .findAll();
        if (results != null && !results.isEmpty()) {
            return results.max("order").longValue() + 1;
        }
        return 0;
    }

    public static boolean isCategoryNameExists(Realm realm, String categoryNameStr) {
        Category category = realm.where(Category.class)
                .equalTo("name", categoryNameStr)
                .findFirst();
        if (category != null) {
            return true;
        } else {
            return false;
        }
    }

    public static int getChartDailyPeriod() {
        return CHART_DAILY_PERIOD;
    }

    public static int getChartWeeklyPeriod() {
        return CHART_WEEKLY_PERIOD;
    }

    public static int getChartMonthlyPeriod() {
        return CHART_MONTHLY_PERIOD;
    }

    public static int getChartYearlyPeriod() {
        return CHART_YEARLY_PERIOD;
    }

    public ArrayList<ChartDataModel> getDailyChartDataSet(Realm realm, DateTime fromDt) {
        MyLog.d(LOG_TAG, "entry - CHART_DAILY_PERIOD : " + CHART_DAILY_PERIOD);
        ArrayList<ChartDataModel> list = new ArrayList<ChartDataModel>(CHART_DAILY_PERIOD);
        for (int i = 0; i < CHART_DAILY_PERIOD; i++) {
            long value;
            DateTime subFromDt = fromDt.plusDays(i);
            Record record = realm.where(Record.class)
                    .equalTo("categoryId", categoryId)
                    .equalTo("date", new java.sql.Date(subFromDt.getMillis()))
                    .findFirst();
            if (record == null) value = 0;
            else if (this.getRecordType() == StaticData.RECORD_TYPE_NUMBER)
                value = record.getNumber();
            else value = 1;
//            DailyChartDataModel model = new DailyChartDataModel((fromDt.plusDays(i).getMillis() - fromDt.getMillis()) / 1000, value); //convert millis to seconds
            ChartDataModel model = new ChartDataModel();
            model.setXAxis(i);
            model.setValue(value);
            list.add(model);
            MyLog.d(LOG_TAG, "i : " + i + ", subFromDt : " + subFromDt.toString(StaticData.fmtForBackup) + ", subToDt : X, value : " + value);
        }
        MyLog.d(LOG_TAG, "exit - list.size() : " + list.size());
        return list;
    }

    public ArrayList<ChartDataModel> getWeeklyChartDataSet(Realm realm, DateTime fromDt) {
        MyLog.d(LOG_TAG, "entry - CHART_WEEKLY_PERIOD : " + CHART_WEEKLY_PERIOD);
        ArrayList<ChartDataModel> list = new ArrayList<ChartDataModel>(CHART_WEEKLY_PERIOD);
        for (int i = 0; i < CHART_WEEKLY_PERIOD; i++) {
            long num, sum;
            double average;
            DateTime subFromDt = fromDt.plusWeeks(i);
            DateTime subToDt = fromDt.plusWeeks(i + 1).dayOfWeek().withMaximumValue().minusDays(1);
            RealmResults<Record> records = realm.where(Record.class)
                    .equalTo("categoryId", categoryId)
//                    .equalTo("date", new java.sql.Date(fromDt.plusDays(i).getMillis()))
//                    .between("date", new java.sql.Date(fromDt.plusWeeks(i).getMillis()), new java.sql.Date(fromDt.plusWeeks(i).dayOfWeek().withMaximumValue().getMillis()))
                    .between("date", new java.sql.Date(subFromDt.getMillis()), new java.sql.Date(subToDt.getMillis()))
                    //start day of week is monday in joda time, so dayOfWeek().withMaximumValue().minusDays(1) gives saturday
                    .findAll();
            if (records == null || records.size() == 0) {
                num = 0;
                sum = 0;
                average = 0;
            } else if (this.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
                num = records.size();
                sum = records.sum("number").longValue();
                average = records.average("number");
            } else {
                num = records.size();
                sum = 0;
                average = 0;
            }
//            DailyChartDataModel model = new DailyChartDataModel((fromDt.plusDays(i).getMillis() - fromDt.getMillis()) / 1000, value); //convert millis to seconds
            ChartDataModel model = new ChartDataModel();
            model.setXAxis(i);
//            model.setRecordNum(num);
            model.setValue(num);
            model.setAverage(average);
            model.setSum(sum);
            list.add(model);
            MyLog.d(LOG_TAG, "i : " + i + ", subFromDt : " + subFromDt.toString(StaticData.fmtForBackup) + ", subToDt : " + subToDt.toString(StaticData.fmtForBackup));
            MyLog.d(LOG_TAG, "i : " + i + ", recordNum : " + num + ", average : " + average + ", sum : " + sum);
        }
        MyLog.d(LOG_TAG, "exit - list.size() : " + list.size());
        return list;
    }

    public ArrayList<ChartDataModel> getMonthlyChartDataSet(Realm realm, DateTime fromDt) {
        MyLog.d(LOG_TAG, "entry - CHART_MONTHLY_PERIOD : " + CHART_MONTHLY_PERIOD);
        ArrayList<ChartDataModel> list = new ArrayList<ChartDataModel>(CHART_MONTHLY_PERIOD);
        for (int i = 0; i < CHART_MONTHLY_PERIOD; i++) {
            long num, sum;
            double average;
            DateTime subFromDt = fromDt.plusMonths(i);
            DateTime subToDt = fromDt.plusMonths(i).dayOfMonth().withMaximumValue();
            RealmResults<Record> records = realm.where(Record.class)
                    .equalTo("categoryId", categoryId)
                    .between("date", new java.sql.Date(subFromDt.getMillis()), new java.sql.Date(subToDt.getMillis()))
                    .findAll();
            if (records == null || records.size() == 0) {
                num = 0;
                sum = 0;
                average = 0;
            } else if (this.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
                num = records.size();
                sum = records.sum("number").longValue();
                average = records.average("number");
            } else {
                num = records.size();
                sum = 0;
                average = 0;
            }
            ChartDataModel model = new ChartDataModel();
            model.setXAxis(i);
//            model.setRecordNum(num);
            model.setValue(num);
            model.setAverage(average);
            model.setSum(sum);
            list.add(model);
            MyLog.d(LOG_TAG, "i : " + i + ", subFromDt : " + subFromDt.toString(StaticData.fmtForBackup) + ", subToDt : " + subToDt.toString(StaticData.fmtForBackup));
            MyLog.d(LOG_TAG, "i : " + i + ", recordNum : " + num + ", average : " + average + ", sum : " + sum);
        }
        MyLog.d(LOG_TAG, "exit - list.size() : " + list.size());
        return list;
    }

    public ArrayList<ChartDataModel> getYearlyChartDataSet(Realm realm, DateTime fromDt) {
        MyLog.d(LOG_TAG, "entry - CHART_YEARLY_PERIOD : " + CHART_YEARLY_PERIOD);
        ArrayList<ChartDataModel> list = new ArrayList<ChartDataModel>(CHART_YEARLY_PERIOD);
        for (int i = 0; i < CHART_YEARLY_PERIOD; i++) {
            long num, sum;
            double average;
            DateTime subFromDt = fromDt.plusYears(i);
            DateTime subToDt = fromDt.plusYears(i).dayOfYear().withMaximumValue();
            RealmResults<Record> records = realm.where(Record.class)
                    .equalTo("categoryId", categoryId)
                    .between("date", new java.sql.Date(subFromDt.getMillis()), new java.sql.Date(subToDt.getMillis()))
                    .findAll();
            if (records == null || records.size() == 0) {
                num = 0;
                sum = 0;
                average = 0;
            } else if (this.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
                num = records.size();
                sum = records.sum("number").longValue();
                average = records.average("number");
            } else {
                num = records.size();
                sum = 0;
                average = 0;
            }
            ChartDataModel model = new ChartDataModel();
            model.setXAxis(i);
//            model.setRecordNum(num);
            model.setValue(num);
            model.setAverage(average);
            model.setSum(sum);
            list.add(model);
            MyLog.d(LOG_TAG, "i : " + i + ", subFromDt : " + subFromDt.toString(StaticData.fmtForBackup) + ", subToDt : " + subToDt.toString(StaticData.fmtForBackup));
            MyLog.d(LOG_TAG, "i : " + i + ", recordNum : " + num + ", average : " + average + ", sum : " + sum);
        }
        MyLog.d(LOG_TAG, "exit - list.size() : " + list.size());
        return list;
    }

    public DateTime getDailyChartFromDtTime(DateTime baseDt, int prevIndex) {
        MyLog.d(LOG_TAG, "prevIndex : " + prevIndex + ", CHART_DAILY_PERIOD : " + CHART_DAILY_PERIOD);
        MyLog.d(LOG_TAG, "prevIndex * CHART_DAILY_PERIOD - CHART_DAILY_PERIOD + 1 : " + (prevIndex * CHART_DAILY_PERIOD - CHART_DAILY_PERIOD + 1));

        DateTime fromDt = baseDt.plusDays(prevIndex * CHART_DAILY_PERIOD - CHART_DAILY_PERIOD + 1).withTimeAtStartOfDay();

        MyLog.d(LOG_TAG, "baseDt : " + baseDt.toString(StaticData.fmtForBackup));
        MyLog.d(LOG_TAG, "fromDt : " + fromDt.toString(StaticData.fmtForBackup));
        return fromDt;
    }

    public DateTime getWeeklyChartFromDtTime(DateTime baseDt, int prevIndex) {
        MyLog.d(LOG_TAG, "prevIndex : " + prevIndex + ", CHART_WEEKLY_PERIOD : " + CHART_WEEKLY_PERIOD);
        MyLog.d(LOG_TAG, "prevIndex * CHART_WEEKLY_PERIOD - CHART_WEEKLY_PERIOD + 1 : " + (prevIndex * CHART_WEEKLY_PERIOD - CHART_WEEKLY_PERIOD + 1));
//        DateTime fromDt = baseDt.dayOfWeek().withMinimumValue().plusWeeks(prevIndex * CHART_WEEKLY_PERIOD - CHART_WEEKLY_PERIOD + 1).withTimeAtStartOfDay();
        DateTime fromDt = baseDt.plusWeeks(prevIndex * CHART_WEEKLY_PERIOD - CHART_WEEKLY_PERIOD + 1).withTimeAtStartOfDay().dayOfWeek().withMinimumValue().minusDays(1);
        //start day of week is monday in joda time, so dayOfWeek().withMinimumValue().minusDays(1) gives sunday
        MyLog.d(LOG_TAG, "baseDt : " + baseDt.toString(StaticData.fmtForBackup));
        MyLog.d(LOG_TAG, "fromDt : " + fromDt.toString(StaticData.fmtForBackup));
        return fromDt;
    }

    public DateTime getMonthlyChartFromDtTime(DateTime baseDt, int prevIndex) {
        MyLog.d(LOG_TAG, "prevIndex : " + prevIndex + ", CHART_MONTHLY_PERIOD : " + CHART_MONTHLY_PERIOD);
        MyLog.d(LOG_TAG, "prevIndex * CHART_MONTHLY_PERIOD - CHART_MONTHLY_PERIOD + 1 : " + (prevIndex * CHART_MONTHLY_PERIOD - CHART_MONTHLY_PERIOD + 1));
        DateTime fromDt = baseDt.plusMonths(prevIndex * CHART_MONTHLY_PERIOD - CHART_MONTHLY_PERIOD + 1).withTimeAtStartOfDay().dayOfMonth().withMinimumValue();
        MyLog.d(LOG_TAG, "baseDt : " + baseDt.toString(StaticData.fmtForBackup));
        MyLog.d(LOG_TAG, "fromDt : " + fromDt.toString(StaticData.fmtForBackup));
        return fromDt;
    }

    public DateTime getYearlyChartFromDtTime(DateTime baseDt, int prevIndex) {
        MyLog.d(LOG_TAG, "prevIndex : " + prevIndex + ", CHART_YEARLY_PERIOD : " + CHART_YEARLY_PERIOD);
        MyLog.d(LOG_TAG, "prevIndex * CHART_YEARLY_PERIOD - CHART_YEARLY_PERIOD + 1 : " + (prevIndex * CHART_YEARLY_PERIOD - CHART_YEARLY_PERIOD + 1));
        DateTime fromDt = baseDt.plusYears(prevIndex * CHART_YEARLY_PERIOD - CHART_YEARLY_PERIOD + 1).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        MyLog.d(LOG_TAG, "baseDt : " + baseDt.toString(StaticData.fmtForBackup));
        MyLog.d(LOG_TAG, "fromDt : " + fromDt.toString(StaticData.fmtForBackup));
        return fromDt;
    }
}

