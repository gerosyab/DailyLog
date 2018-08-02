package net.gerosyab.dailylog.data;

public class ChartDataModel {
    private long xAxis;
    private long value; // boolean : 0/1, number for daily chart, record num for weekly/monthly/yearly chart
//    private long recordNum;
    private double average;
    private long sum;

    public ChartDataModel() {
    }

    public long getXAxis() {
        return xAxis;
    }

    public void setXAxis(long xAxis) {
        this.xAxis = xAxis;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

//    public long getRecordNum() {
//        return recordNum;
//    }
//
//    public void setRecordNum(long recordNum) {
//        this.recordNum = recordNum;
//    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }
}
