package net.gerosyab.dailylog.data;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by tremolo on 2016-06-04.
 */
public class StaticData {
    public static final String CATEGORY_MODE_INTENT_EXTRA = "Intent Extra for Category Mode";
    public static final String CATEGORY_NUM_INTENT_EXTRA = "Intent Extra for Category Num";
    public static final String CATEGORY_NAME_INTENT_EXTRA = "Intent Extra for Category Name";
    public static final String CATEGORY_ORDER_INTENT_EXTRA = "Intent Extra for Category Order";
    public static final String CATEGORY_ID_INTENT_EXTRA = "Intent Extra for Category IDr";
    public static final long CATEGORY_MODE_CREATE = 0;
    public static final long CATEGORY_MODE_EDIT = 1;
    public static final long DIALOG_MODE_CREATE = 0;
    public static final long DIALOG_MODE_EDIT = 1;

    public static final long RECORD_TYPE_BOOLEAN = 0;
    public static final long RECORD_TYPE_NUMBER = 1;
    public static final long RECORD_TYPE_MEMO = 2;

    public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter fmtForBackup = DateTimeFormat.forPattern("yyyy-MM-dd 24HH:mm:ss");
}
