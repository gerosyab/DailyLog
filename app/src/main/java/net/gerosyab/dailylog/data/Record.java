package net.gerosyab.dailylog.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import net.gerosyab.dailylog.database.AppDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Date;

/**
 * Created by donghe on 2016-06-03.
 */
@Table(database = AppDatabase.class)
public class Record extends BaseModel {
    @Column
    @PrimaryKey(autoincrement =  true)
    private long recordId;

    @ForeignKey(tableClass = Category.class, stubbedRelationship = true, references = @ForeignKeyReference(columnName = "categoryId", foreignKeyColumnName = "categoryId"))
    private long categoryId;

    @Column
    private long recordType;

    @Column
    private Date date;

    //Value
    @Column
    private boolean bool;

    @Column
    private long number;

    @Column
    private String string;

    public Record(){
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public  long getCategoryId() {return categoryId; }

    public void setCategoryId( long categoryId) { this.categoryId = categoryId; }

    public long getRecordType() {
        return recordType;
    }

    public void setRecordType(long recordType) {
        this.recordType = recordType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getDateString(){
        return StaticData.fmt.print(new DateTime(this.getDate()));
    }
    public String getDateString(DateTimeFormatter dtf){
        return dtf.print(new DateTime(this.getDate()));
    }
}
