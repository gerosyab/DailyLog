package net.gerosyab.dailylog.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import net.gerosyab.dailylog.database.AppDatabase;

import java.sql.Date;
import java.util.List;

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

    public boolean hasRecord(Date date){
        List<Record> records = SQLite.select().from(Record.class).where(Record_Table.date.eq(date)).queryList();

        if(records == null || records.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    public Record getRecord(Date date){
        return SQLite.select().from(Record.class).where(Record_Table.date.eq(date)).querySingle();
    }
}

