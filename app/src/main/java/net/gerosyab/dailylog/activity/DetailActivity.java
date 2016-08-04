package net.gerosyab.dailylog.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.Category_Table;
import net.gerosyab.dailylog.data.Record;
import net.gerosyab.dailylog.data.StaticData;

import org.joda.time.DateTime;

import java.text.DateFormatSymbols;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    Context context;
    CaldroidFragment caldroidFragment;
    LinearLayout calendarContainerLinear;
    Category category;
    long categoryID;
    TextView yearTextView, monthTextView;
    ListView yearListView, monthListView;
    FloatingActionButton fab;
    int visibleViewIndex;
    final int CALENDER_VIEW = 0;
    final int YEAR_LIST_VIEW = 1;
    final int MONTH_LIST_VIEW = 2;


    DateTime dt;
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

        context = getApplicationContext();
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        categoryID = intent.getLongExtra(StaticData.CATEGORY_ID_INTENT_EXTRA, -1);

        category = SQLite.select()
                .from(Category.class)
                .where(Category_Table.id.eq(categoryID))
                .querySingle();

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
        DateTime startDayOfMonthDt, endDayOfMonthDt;
        startDayOfMonthDt = dt.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
        endDayOfMonthDt = dt.dayOfMonth().withMaximumValue().withTime(23, 59, 59, 0);
        List<Record> records = category.getRecords(new java.sql.Date(startDayOfMonthDt.toDate().getTime()), new java.sql.Date(endDayOfMonthDt.toDate().getTime()));

        for (Record record : records) {
            java.util.Date utilDate = new java.util.Date(record.getDate().getTime());
            caldroidFragment.setBackgroundDrawableForDate(ContextCompat.getDrawable(context, R.drawable.selected_date_cell_bg), utilDate);
            caldroidFragment.setTextColorForDate(R.color.selected_cell_text_color, utilDate);
        }

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
        }
        else if (index == YEAR_LIST_VIEW){
            visibleViewIndex = YEAR_LIST_VIEW;
            calendarContainerLinear.setVisibility(View.INVISIBLE);
            yearListView.setVisibility(View.VISIBLE);
            monthListView.setVisibility(View.INVISIBLE);
            yearTextView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            yearTextView.setAlpha(1f);
            monthTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            monthTextView.setAlpha(0.5f);
        }else if(index == MONTH_LIST_VIEW){
            visibleViewIndex = MONTH_LIST_VIEW;
            calendarContainerLinear.setVisibility(View.INVISIBLE);
            yearListView.setVisibility(View.INVISIBLE);
            monthListView.setVisibility(View.VISIBLE);
            yearTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            yearTextView.setAlpha(0.5f);
            monthTextView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            monthTextView.setAlpha(1f);
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
        dt = new DateTime();

        if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER){

        }

        calendarContainerLinear = (LinearLayout) findViewById(R.id.calendarContainerLinear);
        yearTextView = (TextView) findViewById(R.id.yearText);
        monthTextView = (TextView) findViewById(R.id.monthText);
        yearListView = (ListView) findViewById(R.id.yearListView);
        monthListView = (ListView) findViewById(R.id.monthListView);

        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, dt.getMonthOfYear());
        args.putInt(CaldroidFragment.YEAR, dt.getYear());
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.MyCaldroidTheme);
        caldroidFragment.setArguments(args);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendarContainerLinear, caldroidFragment);
        t.commit();


        caldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onChangeMonth(int month, int year) {
                super.onChangeMonth(month, year);
                int day = dt.getDayOfMonth();
                dt = new DateTime(year, month, day, 0, 0);
                yearTextView.setText("" + year);
                monthTextView.setText(monthStr[month - 1]);

                DateTime startDayOfMonthDt, endDayOfMonthDt;
                startDayOfMonthDt = dt.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
//                endDayOfMonthDt = dt.dayOfMonth().withMaximumValue().withTime(23, 59, 59, 0);
                endDayOfMonthDt = dt.dayOfMonth().withMaximumValue().withTimeAtStartOfDay();
                List<Record> records = category.getRecords(new java.sql.Date(startDayOfMonthDt.toDate().getTime()), new java.sql.Date(endDayOfMonthDt.toDate().getTime()));
                for (Record record : records) {
                    java.util.Date utilDate = new java.util.Date(record.getDate().getTime());
                    caldroidFragment.setBackgroundDrawableForDate(ContextCompat.getDrawable(context, R.drawable.selected_date_cell_bg), utilDate);
                    caldroidFragment.setTextColorForDate(R.color.selected_cell_text_color, utilDate);
                }
            }

            @Override
            public void onSelectDate(java.util.Date date, View view) {
                if(category.getRecordType() != StaticData.RECORD_TYPE_BOOLEAN){
                    final TextView tx = (TextView) view;
                    final java.util.Date finalDate = date;

                    Record record = category.getRecord(new java.sql.Date(date.getTime()));
                    if(record == null) {
                        //dialog 에서 새로 입력
                        record = new Record();
                        record.setDate(new java.sql.Date(finalDate.getTime()));
                        record.associateCategory(category);
                        if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER){
                            record.setRecordType(StaticData.RECORD_TYPE_NUMBER);

                            record.setNumber(1);
                        }else if(category.getRecordType() == StaticData.RECORD_TYPE_MEMO){
                            record.setRecordType(StaticData.RECORD_TYPE_MEMO);

                            record.setString("");
                        }
                        //dialog 결과에 따라
                        //record.save();

                    }
                    else{
                        //dialog 로 데이터 보여줌

                        //dialog 에서 edit, delete 눌러 처리
                    }

                    final Record finalRecord = record;

//                    new AlertDialog.Builder(DetailActivity.this, R.style.AppTheme)
//                            .setTitle("Input the number(s)")
//                            .setView(numberPicker)
//                            .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    tx.setBackground(ContextCompat.getDrawable(context, R.drawable.selected_date_cell_bg));
//                                    tx.setTextColor(ContextCompat.getColor(context, R.color.selected_cell_text_color));
//                                    finalRecord.setNumber(numberPicker.getValue());
//                                    finalRecord.save();
//                                }
//                            }).setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    tx.setBackground(ContextCompat.getDrawable(context, R.drawable.date_cell_bg));
//                                    tx.setTextColor(ContextCompat.getColor(context, R.color.cell_text_color));
//                                    finalRecord.delete();
//                                }
//                    }).show();
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
                    TextView tx = (TextView) view;
                    Record record = category.getRecord(new java.sql.Date(date.getTime()));
                    if (record != null) {
//                        Toast.makeText(context, "Long Clicked - record : " + record, Toast.LENGTH_LONG).show();
                        tx.setBackground(ContextCompat.getDrawable(context, R.drawable.date_cell_bg));
                        tx.setTextColor(ContextCompat.getColor(context, R.color.cell_text_color));
                        record.delete();
                    } else {
//                        Toast.makeText(context, "Long Clicked - record null" + record, Toast.LENGTH_LONG).show();
                        tx.setBackground(ContextCompat.getDrawable(context, R.drawable.selected_date_cell_bg));
                        tx.setTextColor(ContextCompat.getColor(context, R.color.selected_cell_text_color));
                        record = new Record();
                        record.associateCategory(category);
                        record.setRecordType(StaticData.RECORD_TYPE_BOOLEAN);
                        record.setBool(true);
                        record.setDate(new java.sql.Date(date.getTime()));
                        record.save();
                    }
                }
            }
        });

        yearListView.setDivider(null);
        monthListView.setDivider(null);

        yearTextView.setText("" + dt.getYear());
        monthTextView.setText(dt.monthOfYear().getAsText());

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
                            yearListView.setSelection(dt.getYear() - startYear);
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
                            monthListView.setSelection(dt.getMonthOfYear() - 1);
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
                Intent intent = new Intent(getApplicationContext(), StatisticActivity.class);
                intent.putExtra(StaticData.CATEGORY_ID_INTENT_EXTRA, category.getId());
                startActivity(intent);
            }
        });

        yearListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dt = dt.withYear(startYear + position);
                caldroidFragment.moveToDate(dt.toDate());
                changeView(CALENDER_VIEW);
            }
        });

        monthListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dt = dt.withMonthOfYear(position + 1);
                caldroidFragment.moveToDate(dt.toDate());
                changeView(CALENDER_VIEW);
            }
        });
    }
}
