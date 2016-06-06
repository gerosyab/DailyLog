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
public class Record extends BaseModel{
    @Column
    @PrimaryKey(autoincrement =  true)
    private long id;

    @ForeignKey(tableClass = Category.class)
    private ForeignKeyContainer<Category> category;

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

    public void associateCategory(Category category) {
        this.category = FlowManager.getContainerAdapter(Category.class)
                .toForeignKeyContainer(category); // convenience conversion
    }

    public Record(){
    }

    public Record(long id, ForeignKeyContainer<Category> category, long recordType, Date date, boolean bool, long number, String string) {
        this.id = id;
        this.category = category;
        this.recordType = recordType;
        this.date = date;
        this.bool = bool;
        this.number = number;
        this.string = string;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ForeignKeyContainer<Category> getCategory() {
        return category;
    }

    public void setCategory(ForeignKeyContainer<Category> category) {
        this.category = category;
    }

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
}
