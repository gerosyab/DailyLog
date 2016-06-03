package net.gerosyab.dailylog.data;

import com.orm.SugarRecord;

/**
 * Created by donghe on 2016-06-03.
 */
public class Category extends SugarRecord {
    String name;
    String unit;
    int order;
    int recordType;
    Statistic statistic;

    public Category() {
    }

    public Category(String name, String unit, int order, int recordType) {
        this.name = name;
        this.unit = unit;
        this.order = order;
        this.recordType = recordType;
        this.statistic = new Statistic();
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }
}

