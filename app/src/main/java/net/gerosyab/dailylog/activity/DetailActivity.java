package net.gerosyab.dailylog.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.Record;
import net.gerosyab.dailylog.data.StaticData;
import net.gerosyab.dailylog.fragment.MessagePopupDialog;
import net.gerosyab.dailylog.fragment.NumberPickerDialog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DetailActivity extends SuperActivity implements NumberPickerDialog.NumberPickerDialogListener, MessagePopupDialog.MessagePopupDialogListener {
    private static final String LOG_TAG = "DetailActivity";
    private static final DateTimeFormatter fmtForMonthSummary = DateTimeFormat.forPattern("yyyy/MM");

    CaldroidFragment caldroidFragment;
    LinearLayout calendarContainerLinear;
    Category category;
    String categoryID;
    TextView yearTextView, monthTextView;
    TextView summaryTextView;
    ListView yearListView, monthListView;
    Button todayBtn;
    FloatingActionButton fab;
    int visibleViewIndex;
    final int CALENDER_VIEW = 0;
    final int YEAR_LIST_VIEW = 1;
    final int MONTH_LIST_VIEW = 2;

    InputMethodManager imm;
    TextView globalTx;

    DateTime calendarDt, todayDt;
    final int startYear = 1900;
    final int endYear = 2100;
    final int yearLength = endYear - startYear;
    DateFormatSymbols dfs = new DateFormatSymbols();
    final String[] monthStr = {
            dfs.getMonths()[0], dfs.getMonths()[1], dfs.getMonths()[2], dfs.getMonths()[3], dfs.getMonths()[4], dfs.getMonths()[5],
            dfs.getMonths()[6], dfs.getMonths()[7], dfs.getMonths()[8], dfs.getMonths()[9], dfs.getMonths()[10], dfs.getMonths()[11]
    };
    ArrayAdapter<String> yearArrayAdapter;
    ArrayAdapter<String> monthArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(realm == null) realm = getRealm();
//        context = getApplicationContext();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        categoryID = intent.getStringExtra(StaticData.CATEGORY_ID_INTENT_EXTRA);

        category = Category.getCategory(realm, categoryID);

        initViews();

        yearArrayAdapter = new ArrayAdapter<String>(context, R.layout.year_month_list_item);
        monthArrayAdapter = new ArrayAdapter<String>(context, R.layout.year_month_list_item);

        yearListView.setAdapter(yearArrayAdapter);
        monthListView.setAdapter(monthArrayAdapter);

        for(int i = 0; i < yearLength; i++){
            yearArrayAdapter.add("" + (startYear + i));
        }

        int monthLength = monthStr.length;
        for(int i = 0; i < monthLength; i++){
            monthArrayAdapter.add(monthStr[i]);
        }

//        records = category.getRecords();
//        DateTime startDayOfMonthDt, endDayOfMonthDt;
//        startDayOfMonthDt = dt.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
//        endDayOfMonthDt = dt.dayOfMonth().withMaximumValue().withTime(23, 59, 59, 0);
//        List<Record> records = category.getRecords(new java.sql.Date(startDayOfMonthDt.toDate().getTime()), new java.sql.Date(endDayOfMonthDt.toDate().getTime()));
//
//        for (Record record : records) {
//            java.util.Date utilDate = new java.util.Date(record.getDate().getTime());
//            caldroidFragment.setBackgroundDrawableForDate(ContextCompat.getDrawable(context, R.drawable.selected_date_cell_bg), utilDate);
//            caldroidFragment.setTextColorForDate(R.color.selected_cell_text_color, utilDate);
//        }

        changeView(CALENDER_VIEW);
    }

    private void changeView(int index){
        if(index == CALENDER_VIEW){
            visibleViewIndex = CALENDER_VIEW;
            calendarContainerLinear.setVisibility(View.VISIBLE);
            yearListView.setVisibility(View.INVISIBLE);
            monthListView.setVisibility(View.INVISIBLE);
            yearTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            yearTextView.setAlpha(1f);
            monthTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            monthTextView.setAlpha(1f);
            summaryTextView.setVisibility(View.VISIBLE);
            todayBtn.setVisibility(View.VISIBLE);
        }
        else if (index == YEAR_LIST_VIEW){
            visibleViewIndex = YEAR_LIST_VIEW;
            calendarContainerLinear.setVisibility(View.INVISIBLE);
            yearListView.setVisibility(View.VISIBLE);
            monthListView.setVisibility(View.INVISIBLE);
            yearTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            yearTextView.setAlpha(1f);
            monthTextView.setTextColor(ContextCompat.getColor(context, R.color.gray));
            monthTextView.setAlpha(0.5f);
            summaryTextView.setVisibility(View.INVISIBLE);
            todayBtn.setVisibility(View.INVISIBLE);
        }else if(index == MONTH_LIST_VIEW){
            visibleViewIndex = MONTH_LIST_VIEW;
            calendarContainerLinear.setVisibility(View.INVISIBLE);
            yearListView.setVisibility(View.INVISIBLE);
            monthListView.setVisibility(View.VISIBLE);
            yearTextView.setTextColor(ContextCompat.getColor(context, R.color.gray));
            yearTextView.setAlpha(0.5f);
            monthTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            monthTextView.setAlpha(1f);
            summaryTextView.setVisibility(View.INVISIBLE);
            todayBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if(visibleViewIndex != CALENDER_VIEW){
            changeView(CALENDER_VIEW);
        }
        else{
            super.onBackPressed();
        }
    }

    private void initViews(){
        todayDt = calendarDt = new DateTime();

        if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER){

        }

        calendarContainerLinear = (LinearLayout) findViewById(R.id.calendarContainerLinear);
        yearTextView = (TextView) findViewById(R.id.yearText);
        monthTextView = (TextView) findViewById(R.id.monthText);
        yearListView = (ListView) findViewById(R.id.yearListView);
        monthListView = (ListView) findViewById(R.id.monthListView);

        summaryTextView = (TextView) findViewById(R.id.summaryText);

        yearTextView.setTextIsSelectable(false);
        monthTextView.setTextIsSelectable(false);
        summaryTextView.setTextIsSelectable(false);
        yearTextView.setFocusable(false);
        monthTextView.setFocusable(false);
        summaryTextView.setFocusable(false);
        yearTextView.setFocusableInTouchMode(false);
        monthTextView.setFocusableInTouchMode(false);
        summaryTextView.setFocusableInTouchMode(false);

        todayBtn = (Button) findViewById(R.id.todayBtn);

        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, calendarDt.getMonthOfYear());
        args.putInt(CaldroidFragment.YEAR, calendarDt.getYear());
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.MyCaldroidTheme);
        caldroidFragment.setArguments(args);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendarContainerLinear, caldroidFragment);
        t.commit();


        caldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onChangeMonth(int month, int year) {
//                MyLog.d("DetailActivity", "onChangeMonth() - month : " + month + ", year : " + year);
                super.onChangeMonth(month, year);
                yearTextView.setText("" + year);
                monthTextView.setText(monthStr[month - 1]);
                summaryTextView.setText(getSummaryTextViewMsg(month, year));
                calendarDt = calendarDt.withYear(year).withMonthOfYear(month);
                redrawCaldroid();
            }

            @Override
            public void onSelectDate(java.util.Date date, View view) {
//                MyLog.d("DetailActivity", "onSelectDate");
                if(category.getRecordType() != StaticData.RECORD_TYPE_BOOLEAN){
                    final java.util.Date finalDate = date;

                    Record record = category.getRecord(realm, new java.sql.Date(date.getTime()));
                    if(record == null) {
//                        MyLog.d("DetailActivity", "Record Null");
                        //dialog 에서 새로 입력
                        record = new Record();
                        record.setDate(new java.sql.Date(finalDate.getTime()));
                        record.setCategoryId(categoryID);

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);

                        if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER){
                            record.setRecordType(StaticData.RECORD_TYPE_NUMBER);
                            NumberPickerDialog dialog = NumberPickerDialog.newInstance(record.getDateString(), "" + category.getDefaultValue(),
                                                            category.getUnit(), category.getMaxValue(), StaticData.DIALOG_MODE_CREATE, record, realm);
                            dialog.show(ft, "numberPickerDialog");
                        }else if(category.getRecordType() == StaticData.RECORD_TYPE_MEMO){
                            record.setRecordType(StaticData.RECORD_TYPE_MEMO);
                            MessagePopupDialog dialog = MessagePopupDialog.newInstance(record.getDateString(), "",
                                    category.getMaxMemoLength(), StaticData.DIALOG_MODE_CREATE, record, realm);
                            dialog.show(ft, "messagePopupDialog");
                        }
                    }
                    else{
                        //dialog 로 데이터 보여줌
//                        MyLog.d("DetailActivity", "(" + category.getName() + ")Record Not Null : " + record.getRecordType() + ", " + record.getDate() + ", " + record.getNumber() + ", " + record.getString() );
                        //dialog 에서 edit, delete 눌러 처리
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER){
                            NumberPickerDialog dialog = NumberPickerDialog.newInstance(record.getDateString(), "" + record.getNumber(),
                                    category.getUnit(), category.getMaxValue(), StaticData.DIALOG_MODE_EDIT, record, realm);
                            dialog.show(ft, "numberPickerDialog");
                        }else if(category.getRecordType() == StaticData.RECORD_TYPE_MEMO){
                            MessagePopupDialog dialog = MessagePopupDialog.newInstance(record.getDateString(), record.getString(),
                                    category.getMaxMemoLength(), StaticData.DIALOG_MODE_EDIT, record, realm);
                            dialog.show(ft, "messagePopupDialog");
                        }
                    }
                }
            }

            @Override
            public void onCaldroidViewCreated() {
                super.onCaldroidViewCreated();
                caldroidFragment.setShowNavigationArrows(false);
                caldroidFragment.getMonthTitleTextView().setVisibility(View.GONE);
            }

            @Override
            public void onLongClickDate(java.util.Date date, View view) {
                if (category.getRecordType() == StaticData.RECORD_TYPE_BOOLEAN) {
                    Record record = category.getRecord(realm, new java.sql.Date(date.getTime()));
                    if (record != null) {
                        Toast.makeText(context, "Record removed : " + record.getDateString(), Toast.LENGTH_LONG).show();
                        realm.beginTransaction();
                        record.deleteFromRealm();
                        realm.commitTransaction();

                    } else {

                        realm.beginTransaction();
                        record = realm.createObject(Record.class, UUID.randomUUID().toString());
                        record.setCategoryId(categoryID);
                        record.setRecordType(StaticData.RECORD_TYPE_BOOLEAN);
                        record.setBool(true);
                        record.setDate(new java.sql.Date(date.getTime()));
                        realm.commitTransaction();
                        Toast.makeText(context, "Record saved : " + record.getDateString(), Toast.LENGTH_LONG).show();
                    }
                    redrawCaldroid();
                    summaryTextView.setText(getSummaryTextViewMsg());
                }
            }
        });

        yearListView.setDivider(null);
        monthListView.setDivider(null);

        yearTextView.setText("" + calendarDt.getYear());
        monthTextView.setText(calendarDt.monthOfYear().getAsText());

        summaryTextView.setText(getSummaryTextViewMsg());

        ActionBar ab = getSupportActionBar();
        ab.setTitle(category.getName());

        fab = (FloatingActionButton) findViewById(R.id.fab);


        yearTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visibleViewIndex == YEAR_LIST_VIEW){ //set Year Pick List view invisible, change to calendar view
                    changeView(CALENDER_VIEW);
                }
                else{ //set Year Pick List view visible
                    visibleViewIndex = YEAR_LIST_VIEW;
//                    yearListView.clearFocus();
                    yearArrayAdapter.notifyDataSetChanged();
                    yearListView.requestFocusFromTouch();
                    yearListView.post(new Runnable() {
                        @Override
                        public void run() {
                            yearListView.setSelection(calendarDt.getYear() - startYear);
                            yearListView.clearFocus();
                        }
                    });
                    changeView(YEAR_LIST_VIEW);
                }
            }
        });

        monthTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visibleViewIndex == MONTH_LIST_VIEW){ //set Month Pick List view invisible, change to calendar view
                    changeView(CALENDER_VIEW);
                }
                else{ //set Month Pick List view visible
                    visibleViewIndex = MONTH_LIST_VIEW;
//                    monthListView.clearFocus();
                    monthArrayAdapter.notifyDataSetChanged();
                    monthListView.requestFocusFromTouch();
                    monthListView.post(new Runnable() {
                        @Override
                        public void run() {
                            monthListView.setSelection(calendarDt.getMonthOfYear() - 1);
                            monthListView.clearFocus();
                        }
                    });
                    changeView(MONTH_LIST_VIEW);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
                intent.putExtra(StaticData.CATEGORY_ID_INTENT_EXTRA, category.getCategoryId());
                startActivity(intent);
            }
        });

        yearListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                calendarDt = calendarDt.withYear(startYear + position);
                caldroidFragment.moveToDate(calendarDt.toDate());
                changeView(CALENDER_VIEW);
                summaryTextView.setText(getSummaryTextViewMsg());
            }
        });

        monthListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                calendarDt = calendarDt.withMonthOfYear(position + 1);
                caldroidFragment.moveToDate(calendarDt.toDate());
                changeView(CALENDER_VIEW);
                summaryTextView.setText(getSummaryTextViewMsg());
            }
        });

        todayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarDt = todayDt;
                caldroidFragment.moveToDate(calendarDt.toDate());
                redrawCaldroid();
                summaryTextView.setText(getSummaryTextViewMsg());
            }
        });
    }

    @Override
    public void onNumberPickerDialogPositiveClick(DialogFragment dialog, IBinder iBinder, String dateString) {
//        globalTx.setBackground(ContextCompat.getDrawable(context, R.drawable.selected_date_cell_bg));
//        globalTx.setTextColor(ContextCompat.getColor(context, R.color.selected_cell_text_color));
        Toast.makeText(context, "Record saved : " + dateString, Toast.LENGTH_LONG).show();
        redrawCaldroid();
        summaryTextView.setText(getSummaryTextViewMsg());
        imm.hideSoftInputFromWindow(iBinder, 0);
    }

    @Override
    public void onNumberPickerDialogNegativeClick(DialogFragment dialog, IBinder iBinder) {
        imm.hideSoftInputFromWindow(iBinder, 0);
    }

    @Override
    public void onNumberPickerDialogDeleteClick(DialogFragment dialog, IBinder iBinder, String dateString) {
//        globalTx.setBackground(ContextCompat.getDrawable(context, R.drawable.date_cell_bg));
//        globalTx.setTextColor(ContextCompat.getColor(context, R.color.cell_text_color));
        Toast.makeText(context, "Record removed : " + dateString, Toast.LENGTH_LONG).show();
        redrawCaldroid();
        summaryTextView.setText(getSummaryTextViewMsg());
        imm.hideSoftInputFromWindow(iBinder, 0);
    }

    @Override
    public void onMessagePopupDialogPositiveClick(DialogFragment dialog, IBinder iBinder, String dateString) {
//        globalTx.setBackground(ContextCompat.getDrawable(context, R.drawable.selected_date_cell_bg));
//        globalTx.setTextColor(ContextCompat.getColor(context, R.color.selected_cell_text_color));
        Toast.makeText(context, "Record saved : " + dateString, Toast.LENGTH_LONG).show();
        redrawCaldroid();
        summaryTextView.setText(getSummaryTextViewMsg());
        imm.hideSoftInputFromWindow(iBinder, 0);
    }

    @Override
    public void onMessagePopupDialogNegativeClick(DialogFragment dialog, IBinder iBinder) {
        imm.hideSoftInputFromWindow(iBinder, 0);
    }

    @Override
    public void onMessagePopupDialogDeleteClick(DialogFragment dialog, IBinder iBinder, String dateString) {
//        globalTx.setBackground(ContextCompat.getDrawable(context, R.drawable.date_cell_bg));
//        globalTx.setTextColor(ContextCompat.getColor(context, R.color.cell_text_color));
        Toast.makeText(context, "Record removed : " + dateString, Toast.LENGTH_LONG).show();
        redrawCaldroid();
        summaryTextView.setText(getSummaryTextViewMsg());
        imm.hideSoftInputFromWindow(iBinder, 0);
    }

    public void redrawCaldroid(){
        // caldroid month view : 7 columns * 6 rows = 42 cells
        // startDay of month is always displayed in first row
        // endDay of month can be displayed in fourth, fifth, sixth row
        DateTime nowDt = new DateTime().withTimeAtStartOfDay();

        // for display current month
        DateTime startDayOfMonthDt = calendarDt.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
        DateTime endDayOfMonthDt = calendarDt.dayOfMonth().withMaximumValue().withTimeAtStartOfDay();

        int daysOfMonth = calendarDt.dayOfMonth().getMaximumValue();                      // number of days of this month
        int startDayOfMonthRow, startDayOfMonthCol, endDayOfMonthRow, endDayOfMonthCol;     // row, col position (start with 0)
        startDayOfMonthRow = startDayOfMonthDt.getDayOfWeek() % 7;                          // Sunday(7) : 0, Monday(1) : 1, ... Saturday(6) : 6
        // startDayOfMonthCol = 0;                                                             // always first column
        endDayOfMonthRow = endDayOfMonthDt.getDayOfWeek() % 7;                              // Sunday(7) : 0, Monday(1) : 1, ... Saturday(6) : 6
        // endDayOfMonthCol = (startDayOfMonthRow + (daysOfMonth - 1)) / 7;                    // last day of month cell position (calculated as 1-dimen array) / 7 = col index

        // for displaying prev/next month's few days
        DateTime extraStartDayOfMonthDt, extraEndDayOfMonthDt;
        if(startDayOfMonthRow == 0) extraStartDayOfMonthDt = startDayOfMonthDt;     //first cell
        else extraStartDayOfMonthDt = calendarDt.dayOfMonth().withMinimumValue().withTimeAtStartOfDay().minusDays(startDayOfMonthRow);
        if(endDayOfMonthRow == 41) extraEndDayOfMonthDt = endDayOfMonthDt;          //last cell
        else extraEndDayOfMonthDt = calendarDt.dayOfMonth().withMaximumValue().withTimeAtStartOfDay().plusDays(42 - endDayOfMonthRow - 1);

        //clear
        for(DateTime curDt = extraStartDayOfMonthDt; !curDt.isEqual(extraEndDayOfMonthDt); curDt = curDt.plusDays(1)){
            Date date = curDt.toDate();
            caldroidFragment.setBackgroundDrawableForDate(ContextCompat.getDrawable(context, R.drawable.date_cell_bg), date);
            // if curDt is out of this month
            if(curDt.isBefore(startDayOfMonthDt) || curDt.isAfter(endDayOfMonthDt)) {
                if(curDt.getDayOfWeek() == 6) { // Saturday
                    caldroidFragment.setTextColorForDate(R.color.unselected_other_month_saturday_cell_text_color, date);
                } else if(curDt.getDayOfWeek() == 7) { // Sunday
                    caldroidFragment.setTextColorForDate(R.color.unselected_other_month_sunday_cell_text_color, date);
                } else {
                    caldroidFragment.setTextColorForDate(R.color.unselected_other_month_cell_text_color, date);
                }
            } else {
                if(curDt.getDayOfWeek() == 6) { // Saturday
                    caldroidFragment.setTextColorForDate(R.color.unselected_this_month_saturday_cell_text_color, date);
                } else if(curDt.getDayOfWeek() == 7) { // Sunday
                    caldroidFragment.setTextColorForDate(R.color.unselected_this_month_sunday_cell_text_color, date);
                } else {
                    caldroidFragment.setTextColorForDate(R.color.unselected_this_month_cell_text_color, date);
                }
            }
        }

        List<Record> records = category.getRecords(realm, new java.sql.Date(extraStartDayOfMonthDt.toDate().getTime()), new java.sql.Date(extraEndDayOfMonthDt.toDate().getTime()));

        boolean isTodaysRecordExists = false;
        for (Record record : records) {
            Date recordDate = record.getDate();
            DateTime recordDt = new DateTime(recordDate);
//            java.util.Date utilDate = new java.util.Date(recordDate.getTime());
            // draw recorded date background
            if(nowDt.toDate().equals(recordDate)){
                isTodaysRecordExists = true;
                if(recordDt.isBefore(startDayOfMonthDt) || recordDt.isAfter(endDayOfMonthDt)) {
                    caldroidFragment.setBackgroundDrawableForDate(ContextCompat.getDrawable(context, R.drawable.today_selected_other_month_date_cell_bg), recordDate);
                } else {
                    caldroidFragment.setBackgroundDrawableForDate(ContextCompat.getDrawable(context, R.drawable.today_selected_date_cell_bg), recordDate);
                }
            }else{
                if(recordDt.isBefore(startDayOfMonthDt) || recordDt.isAfter(endDayOfMonthDt)) {
                    caldroidFragment.setBackgroundDrawableForDate(ContextCompat.getDrawable(context, R.drawable.selected_other_month_date_cell_bg), recordDate);
                } else {
                    caldroidFragment.setBackgroundDrawableForDate(ContextCompat.getDrawable(context, R.drawable.selected_date_cell_bg), recordDate);
                }
            }
            // set recorded date text color
            caldroidFragment.setTextColorForDate(R.color.selected_cell_text_color, recordDate);
        }

        // draw today's background (orange ring) if today is not recorded
        if(!isTodaysRecordExists){
            Date date = nowDt.toDate();
            if(nowDt.isBefore(startDayOfMonthDt) || nowDt.isAfter(endDayOfMonthDt)) {
                caldroidFragment.setBackgroundDrawableForDate(ContextCompat.getDrawable(context, R.drawable.today_other_month_date_cell_bg), date);
                caldroidFragment.setTextColorForDate(R.color.unselected_other_month_cell_text_color, date);
            } else {
                caldroidFragment.setBackgroundDrawableForDate(ContextCompat.getDrawable(context, R.drawable.today_date_cell_bg), date);
                caldroidFragment.setTextColorForDate(R.color.unselected_cell_text_color, date);
            }
        }

        caldroidFragment.refreshView();
    }

    public String getSummaryTextViewMsg(){
        java.sql.Date startDayOfMonthDate = new java.sql.Date(calendarDt.dayOfMonth().withMinimumValue().withTimeAtStartOfDay().toDate().getTime());
        java.sql.Date endDayOfMonthDate = new java.sql.Date(calendarDt.dayOfMonth().withMaximumValue().withTimeAtStartOfDay().toDate().getTime());
        long recordNum = category.getRecordNum(startDayOfMonthDate, endDayOfMonthDate, realm);
//        String msg = calendarDt.toString(fmtForMonthSummary) + " ";
        String msg = "";
        if(recordNum > 1) msg += "Records : " + recordNum;
        else msg += "Record : " + recordNum;

        if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
            long sum = category.getSum(startDayOfMonthDate, endDayOfMonthDate, realm);
            double average = category.getAverage(startDayOfMonthDate, endDayOfMonthDate, realm);
            msg += ", Average : " + String.format("%.2f", average) + ", Sum : " + sum;
        }
        return msg;
    }

    public String getSummaryTextViewMsg(int month, int year){
        DateTime dateTime = new DateTime().withYear(year).withMonthOfYear(month);
//        MyLog.d(LOG_TAG, "getSummaryTextViewMsg : getYear() : " + dateTime.getYear() + ", getMonthOfYear() : " + dateTime.getMonthOfYear());
        java.sql.Date startDayOfMonthDate = new java.sql.Date(dateTime.dayOfMonth().withMinimumValue().withTimeAtStartOfDay().toDate().getTime());
        java.sql.Date endDayOfMonthDate = new java.sql.Date(dateTime.dayOfMonth().withMaximumValue().withTimeAtStartOfDay().toDate().getTime());
        long recordNum = category.getRecordNum(startDayOfMonthDate, endDayOfMonthDate, realm);
//        String msg = dateTime.toString(fmtForMonthSummary) + " ";
        String msg = "";
        if(recordNum > 1) msg += "Records : " + recordNum;
        else msg += "Record : " + recordNum;

        if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
            long sum = category.getSum(startDayOfMonthDate, endDayOfMonthDate, realm);
            double average = category.getAverage(startDayOfMonthDate, endDayOfMonthDate, realm);
            msg += ", Average : " + String.format("%.2f", average) + ", Sum : " + sum;
        }
        return msg;
    }
}
