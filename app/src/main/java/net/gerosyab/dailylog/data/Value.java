package net.gerosyab.dailylog.data;

import org.joda.time.Duration;

/**
 * Created by donghe on 2016-06-03.
 */
public class Value {
    boolean mBoolean;
    Duration mDuration;
    int mInt;
    String mString;

    public void setValue(int recordType, Object value){
        switch(recordType){
            case RecordType.BOOLEAN:
                mBoolean = (Boolean)value;
            case RecordType.NUMBER:
                mInt = (int)value;
            case RecordType.DURATION:
                mDuration = (Duration)value;
            case RecordType.STRING:
                mString=(String)value;
            default:
                break;
        }
    }

    public Object getValue(int recordType){
        switch(recordType){
            case RecordType.BOOLEAN:
                return mBoolean;
            case RecordType.NUMBER:
                return mInt;
            case RecordType.DURATION:
                return mDuration;
            case RecordType.STRING:
                return mString;
            default:
                return null;
        }
    }
}
