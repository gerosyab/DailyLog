package net.gerosyab.dailylog.data;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.gerosyab.dailylog.database.AppDatabase;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.sql.Date;
import java.util.List;

import static com.raizlabs.android.dbflow.sql.language.Method.count;
import static com.raizlabs.android.dbflow.sql.language.Method.max;
import static com.raizlabs.android.dbflow.sql.language.Method.sum;

/**
 * Created by donghe on 2016-06-03.
 */
@ModelContainer
@Table(database = AppDatabase.class)
public class Category extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    @Unique
    private String name;

    @Column
    private String unit;

    @Column
    private long order;

    @Column
    private long recordType;

    @Column
    private long defaultValue;

    private static final long MAX_VALUE = 99999;
    private static final long MAX_MEMO_LENGTH = 500;
    private static final long MAX_NAME_LENGTH = 50;

    List<Record> records;

    public Category() {
    }

    public Category(String name, String unit, long order, long  recordType) {
        this.name = name;
        this.unit = unit;
        this.order = order;
        this.recordType = recordType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public static long getMaxNameLength(){ return MAX_NAME_LENGTH; }

    public void deleteCategory() {
        SQLite.delete(Record.class)
                .where(Record_Table.category_id.eq(id))
                .execute();
        delete();
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "records")
    public List<Record> getRecords() {
        if (records == null || records.isEmpty()) {
            records = SQLite.select()
                    .from(Record.class)
                    .where(Record_Table.category_id.eq(id))
                    .queryList();
        }
        return records;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "records")
    public List<Record> getRecordsOrderByIdAscending() {
        if (records == null || records.isEmpty()) {
            records = SQLite.select()
                    .from(Record.class)
                    .where(Record_Table.category_id.eq(id))
                    .orderBy(Record_Table.id, true)    //ascending
                    .queryList();
        }
        return records;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "records")
    public List<Record> getRecordsOrderByDateAscending() {
        if (records == null || records.isEmpty()) {
            records = SQLite.select()
                    .from(Record.class)
                    .where(Record_Table.category_id.eq(id))
                    .orderBy(Record_Table.date, true)    //ascending
                    .queryList();
        }
        return records;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "records")
    public List<Record> getRecords(Date fromDate, Date toDate) {
            return SQLite.select()
                    .from(Record.class)
                    .where(Record_Table.category_id.eq(id))
                    .and(Record_Table.date.between(fromDate).and(toDate))
                    .queryList();
    }

    public boolean hasRecord(Date date){
        List<Record> records = SQLite.select().from(Record.class).where(Record_Table.category_id.eq(id)).and(Record_Table.date.eq(date)).queryList();

        if(records == null || records.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    public Record getRecord(Date date){
        return SQLite.select().from(Record.class).where(Record_Table.category_id.eq(id)).and(Record_Table.date.eq(date)).querySingle();
    }

    public DateTime getFirstRecordDateTime(){
       Record record = SQLite.select()
               .from(Record.class)
               .where(Record_Table.category_id.eq(id))
               .orderBy(Record_Table.date, true)    //ascending
               .querySingle();

        if(record == null){
            Log.d("Statistic", "getFirstRecordDateTime is null");
            return null;
        }
        else{
            Log.d("Statistic", "getFirstRecordDateTime is not null");
            return new DateTime(record.getDate());
        }
    }

    public DateTime getLastRecordDateTime(){
        Record record = SQLite.select()
                .from(Record.class)
                .where(Record_Table.category_id.eq(id))
                .orderBy(Record_Table.date, false)    //descending
                .querySingle();

        if(record == null){
            Log.d("Statistic", "getlastRecordDateTime is null");
            return null;
        }
        else{
            Log.d("Statistic", "getlastRecordDateTime is not null");
            return new DateTime(record.getDate());
        }
    }

    public long getTotalRecordNum(){
        return SQLite.select(count(Record_Table.id))
                .from(Record.class)
                .where(Record_Table.category_id.eq(id)).count();
    }

    public long getLast7RecordNum(){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusDays(7).withTimeAtStartOfDay();

        return SQLite.select(count(Record_Table.id))
                .from(Record.class)
                .where(Record_Table.category_id.eq(id))
                .and(Record_Table.date.between(new java.sql.Date(fromDate.toDate().getTime())).and(new java.sql.Date(toDate.toDate().getTime())))
                .count();
    }

    public long getLast15DaysRecordNum(){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusDays(15).withTimeAtStartOfDay();

        return SQLite.select(count(Record_Table.id))
                .from(Record.class)
                .where(Record_Table.category_id.eq(id))
                .and(Record_Table.date.between(new java.sql.Date(fromDate.toDate().getTime())).and(new java.sql.Date(toDate.toDate().getTime())))
                .count();
    }

    public long getLastMonthRecordNum(){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusMonths(1).withTimeAtStartOfDay();

        return SQLite.select(count(Record_Table.id))
                .from(Record.class)
                .where(Record_Table.category_id.eq(id))
                .and(Record_Table.date.between(new java.sql.Date(fromDate.toDate().getTime())).and(new java.sql.Date(toDate.toDate().getTime())))
                .count();
    }

    public long getLast2MonthsRecordNum(){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusMonths(2).withTimeAtStartOfDay();

        return SQLite.select(count(Record_Table.id))
                .from(Record.class)
                .where(Record_Table.category_id.eq(id))
                .and(Record_Table.date.between(new java.sql.Date(fromDate.toDate().getTime())).and(new java.sql.Date(toDate.toDate().getTime())))
                .count();
    }

    public long getLast3MonthsRecordNum(){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusMonths(3).withTimeAtStartOfDay();

        return SQLite.select(count(Record_Table.id))
                .from(Record.class)
                .where(Record_Table.category_id.eq(id))
                .and(Record_Table.date.between(new java.sql.Date(fromDate.toDate().getTime())).and(new java.sql.Date(toDate.toDate().getTime())))
                .count();
    }

    public long getLast6MonthsRecordNum(){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusMonths(6).withTimeAtStartOfDay();

        return SQLite.select(count(Record_Table.id))
                .from(Record.class)
                .where(Record_Table.category_id.eq(id))
                .and(Record_Table.date.between(new java.sql.Date(fromDate.toDate().getTime())).and(new java.sql.Date(toDate.toDate().getTime())))
                .count();
    }

    public long getLastYearRecordNum(){
        DateTime toDate = new DateTime().withTimeAtStartOfDay();
        DateTime fromDate = toDate.minusYears(1).withTimeAtStartOfDay();

        return SQLite.select(count(Record_Table.id))
                .from(Record.class)
                .where(Record_Table.category_id.eq(id))
                .and(Record_Table.date.between(new java.sql.Date(fromDate.toDate().getTime())).and(new java.sql.Date(toDate.toDate().getTime())))
                .count();
    }

    public Period getRecordPeriod(){
        DateTime firstRecordDateTime = this.getFirstRecordDateTime();
        DateTime lastRecordDateTime = this.getLastRecordDateTime();

        if(firstRecordDateTime != null && lastRecordDateTime != null) {
            Period period = new Period(firstRecordDateTime.toInstant(), lastRecordDateTime.toInstant());
            return period;
        }
        else{
            return null;
        }

    }

    public double getAverage(){

            Cursor cursor = SQLite.select(sum(Record_Table.number))
            .from(Record.class)
            .where(Record_Table.category_id.eq(id)).query();

        if(cursor != null && cursor.moveToFirst() ) {
            long sum, cnt;
            if(cursor.getCount() > 0) {
                sum = cursor.getLong(0);
                cnt = this.getTotalRecordNum();
                return (double) sum / cnt;
            }
            else {
                return 0;
            }
        }
        else {
            return 0;
        }
    }

    public static long getLastOrderNum(){
        Cursor cursor =  SQLite.select(max(Category_Table.order))
                .from(Category.class).query();
        if(cursor != null && cursor.moveToFirst() ) {
            if(cursor.getCount() > 0) {
                return cursor.getLong(0);
            }
        }
        return 0;
    }

    public static boolean isCategoryNameExists(String categoryNameStr){
        List<Category> categories =SQLite.select(Category_Table.name)
                .from(Category.class)
                .queryList();
        for(Category category : categories){
            if(categoryNameStr.equals(category.getName())){
                return true;
            }
        }
        return false;
    }
}

