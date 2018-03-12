package net.gerosyab.dailylog.data;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.sql.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Category extends RealmObject{
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

    RealmList<Record> records;

    public Category() {
    }

    public Category(String name, String unit, long order, long  recordType) {
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

    public static long getMaxValue(){ return MAX_VALUE; }

    public static long getMaxMemoLength(){ return MAX_MEMO_LENGTH; }

    public static long getMaxCategoryNameLength(){ return MAX_CATEGORY_NAME_LENGTH; }

    public static long getMaxUnitNameLength(){ return MAX_UNIT_NAME_LENGTH; }

    public void deleteCategory(Realm realm) {
        realm.beginTransaction();
        RealmResults<Record> records = realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll();
        records.deleteAllFromRealm();

        RealmResults<Category> category = realm.where(Category.class)
                .equalTo("categoryId", categoryId)
                .findAll();
        category.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static RealmResults<Category> getCategories(Realm realm) {
        return realm.where(Category.class)
                .findAll();
    }

    public static Category getCategory(Realm realm, String categoryId){
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

    public boolean hasRecord(Realm realm, Date date){
        RealmResults<Record> records = realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .equalTo("date", date)
                .findAll();

        if(records == null || records.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    public Record getRecord(Realm realm, Date date){
        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .equalTo("date", date)
                .findFirst();
    }

    public DateTime getFirstRecordDateTime(Realm realm){
        java.util.Date firstRecordDate = realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll().minDate("date");
        if(firstRecordDate == null){
            Log.d("Statistic", "getFirstRecordDateTime is null");
            return null;
        } else{
            Log.d("Statistic", "getFirstRecordDateTime is not null");
            return new DateTime(firstRecordDate);
        }
    }

    public DateTime getLastRecordDateTime(Realm realm){
        java.util.Date firstRecordDate = realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll().maxDate("date");
        if(firstRecordDate == null){
            Log.d("Statistic", "getLastRecordDateTime is null");
            return null;
        } else{
            Log.d("Statistic", "getLastRecordDateTime is not null");
            return new DateTime(firstRecordDate);
        }
    }

    public long getTotalRecordNum(Realm realm){
        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll().size();
    }

    public long getLast7RecordNum(Realm realm){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusDays(7).withTimeAtStartOfDay();

        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .between("date", new java.sql.Date(fromDate.toDate().getTime()), new java.sql.Date(toDate.toDate().getTime()))
                .findAll().size();
    }

    public long getLast15DaysRecordNum(Realm realm){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusDays(15).withTimeAtStartOfDay();

        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .between("date", new java.sql.Date(fromDate.toDate().getTime()), new java.sql.Date(toDate.toDate().getTime()))
                .findAll().size();
    }

    public long getLastMonthRecordNum(Realm realm){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusMonths(1).withTimeAtStartOfDay();

        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .between("date", new java.sql.Date(fromDate.toDate().getTime()), new java.sql.Date(toDate.toDate().getTime()))
                .findAll().size();
    }

    public long getLast2MonthsRecordNum(Realm realm){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusMonths(2).withTimeAtStartOfDay();

        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .between("date", new java.sql.Date(fromDate.toDate().getTime()), new java.sql.Date(toDate.toDate().getTime()))
                .findAll().size();
    }

    public long getLast3MonthsRecordNum(Realm realm){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusMonths(3).withTimeAtStartOfDay();

        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .between("date", new java.sql.Date(fromDate.toDate().getTime()), new java.sql.Date(toDate.toDate().getTime()))
                .findAll().size();
    }

    public long getLast6MonthsRecordNum(Realm realm){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusMonths(6).withTimeAtStartOfDay();

        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .between("date", new java.sql.Date(fromDate.toDate().getTime()), new java.sql.Date(toDate.toDate().getTime()))
                .findAll().size();
    }

    public long getLastYearRecordNum(Realm realm){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusYears(1).withTimeAtStartOfDay();

        return realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .between("date", new java.sql.Date(fromDate.toDate().getTime()), new java.sql.Date(toDate.toDate().getTime()))
                .findAll().size();
    }

    public Period getRecordPeriod(Realm realm){
        DateTime firstRecordDateTime = this.getFirstRecordDateTime(realm);
        DateTime lastRecordDateTime = this.getLastRecordDateTime(realm);

        if(firstRecordDateTime != null && lastRecordDateTime != null) {
            Period period = new Period(firstRecordDateTime.toInstant(), lastRecordDateTime.toInstant());
            return period;
        }
        else{
            return null;
        }

    }

    public double getAverage(Realm realm){
        RealmResults<Record> results = realm.where(Record.class)
                .equalTo("categoryId", categoryId)
                .findAll();

        if(results != null && !results.isEmpty()) {
            return results.average("number");
        }
        else {
            return 0;
        }
    }

    public static long getLastOrderNum(Realm realm){
        RealmResults<Category> results = realm.where(Category.class)
                .findAll();
        if(results != null && !results.isEmpty() ) {
            return results.max("order").longValue();
        }
        return 0;
    }

    public static boolean isCategoryNameExists(Realm realm, String categoryNameStr){
        Category category = realm.where(Category.class)
                .equalTo("name", categoryNameStr)
                .findFirst();
        if(category != null){
            return true;
        }
        else {
            return false;
        }
    }
}

