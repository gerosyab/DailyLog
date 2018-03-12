package net.gerosyab.dailylog.data;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

//import java.sql.Date;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Record extends RealmObject{
    @PrimaryKey
    private String recordId;

    @Index
    private String categoryId;

    private long recordType;

    @Index
    private Date date;

    private boolean bool;

    private long number;

    private String string;

    public Record(){
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public  String getCategoryId() {return categoryId; }

    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

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
