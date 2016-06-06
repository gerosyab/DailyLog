package net.gerosyab.dailylog.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

import net.gerosyab.dailylog.database.AppDatabase;

import java.sql.Date;

/**
 * Created by donghe on 2016-06-03.
 */
@Table(database = AppDatabase.class)
public class Statistic extends BaseModel {
    @ForeignKey(tableClass = Category.class)
    @PrimaryKey
    private ForeignKeyContainer<Category> category;

    //Basic Statistic
    @Column
    private Date firstRecordDate;

    @Column
    private Date lastRecordDate;

    @Column
    private long totalRecordNum;

    @Column
    private long weekRecordNum;

    @Column
    private long halfMonthRecordNum;

    @Column
    private long monthRecordNum;

    @Column
    private long quarterYearRecordNum;

    @Column
    private long halfYearRecordNum;

    @Column
    private long yearRecordNum;

    @Column
    private long recordDurationMillis;

    @Column
    private double averageNumber;

    public void associateCategory(Category category) {
        this.category = FlowManager.getContainerAdapter(Category.class)
                .toForeignKeyContainer(category); // convenience conversion
    }

    public Statistic(){
    }

    public Statistic(ForeignKeyContainer<Category> category, Date firstRecordDate, Date lastRecordDate, long totalRecordNum, long weekRecordNum, long halfMonthRecordNum, long monthRecordNum, long quarterYearRecordNum, long halfYearRecordNum, long yearRecordNum, long recordDurationMillis, double averageNumber) {
        this.category = category;
        this.firstRecordDate = firstRecordDate;
        this.lastRecordDate = lastRecordDate;
        this.totalRecordNum = totalRecordNum;
        this.weekRecordNum = weekRecordNum;
        this.halfMonthRecordNum = halfMonthRecordNum;
        this.monthRecordNum = monthRecordNum;
        this.quarterYearRecordNum = quarterYearRecordNum;
        this.halfYearRecordNum = halfYearRecordNum;
        this.yearRecordNum = yearRecordNum;
        this.recordDurationMillis = recordDurationMillis;
        this.averageNumber = averageNumber;
    }

    public ForeignKeyContainer<Category> getCategory() {
        return category;
    }

    public void setCategory(ForeignKeyContainer<Category> category) {
        this.category = category;
    }

    public Date getFirstRecordDate() {
        return firstRecordDate;
    }

    public void setFirstRecordDate(Date firstRecordDate) {
        this.firstRecordDate = firstRecordDate;
    }

    public Date getLastRecordDate() {
        return lastRecordDate;
    }

    public void setLastRecordDate(Date lastRecordDate) {
        this.lastRecordDate = lastRecordDate;
    }

    public long getTotalRecordNum() {
        return totalRecordNum;
    }

    public void setTotalRecordNum(long totalRecordNum) {
        this.totalRecordNum = totalRecordNum;
    }

    public long getWeekRecordNum() {
        return weekRecordNum;
    }

    public void setWeekRecordNum(long weekRecordNum) {
        this.weekRecordNum = weekRecordNum;
    }

    public long getHalfMonthRecordNum() {
        return halfMonthRecordNum;
    }

    public void setHalfMonthRecordNum(long halfMonthRecordNum) {
        this.halfMonthRecordNum = halfMonthRecordNum;
    }

    public long getMonthRecordNum() {
        return monthRecordNum;
    }

    public void setMonthRecordNum(long monthRecordNum) {
        this.monthRecordNum = monthRecordNum;
    }

    public long getQuarterYearRecordNum() {
        return quarterYearRecordNum;
    }

    public void setQuarterYearRecordNum(long quarterYearRecordNum) {
        this.quarterYearRecordNum = quarterYearRecordNum;
    }

    public long getHalfYearRecordNum() {
        return halfYearRecordNum;
    }

    public void setHalfYearRecordNum(long halfYearRecordNum) {
        this.halfYearRecordNum = halfYearRecordNum;
    }

    public long getYearRecordNum() {
        return yearRecordNum;
    }

    public void setYearRecordNum(long yearRecordNum) {
        this.yearRecordNum = yearRecordNum;
    }

    public long getRecordDurationMillis() {
        return recordDurationMillis;
    }

    public void setRecordDurationMillis(long recordDurationMillis) {
        this.recordDurationMillis = recordDurationMillis;
    }

    public double getAverageNumber() {
        return averageNumber;
    }

    public void setAverageNumber(double averageNumber) {
        this.averageNumber = averageNumber;
    }
}
