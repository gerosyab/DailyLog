package net.gerosyab.dailylog.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.StaticData;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class StatisticsActivity extends SuperActivity {
    Category category;
    String categoryID;

    TextView last7daysText;
    TextView last15daysText;
    TextView lastMonthText;
    TextView last2MonthsText;
    TextView last3MonthsText;
    TextView last6MonthsText;
    TextView lastYearText;
    TextView totalRecordNumText;
    TextView firstRecordDateText;
    TextView lastRecordDateText;
    LinearLayout averageLinearLayout;
    TextView averageValueText;
    TextView periodText;

    TextView debugText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(realm == null) realm = getRealm();
        setContentView(R.layout.activity_statistics);
        last7daysText = (TextView)findViewById(R.id.last7DaysText);
        last15daysText = (TextView)findViewById(R.id.last15DaysText);
        lastMonthText = (TextView)findViewById(R.id.lastMonthText);
        last2MonthsText = (TextView)findViewById(R.id.last2MonthsText);
        last3MonthsText = (TextView)findViewById(R.id.last3MonthsText);
        last6MonthsText = (TextView)findViewById(R.id.last6MonthsText);
        lastYearText = (TextView)findViewById(R.id.lastYearText);
        totalRecordNumText = (TextView)findViewById(R.id.totalRecordsText);
        firstRecordDateText = (TextView)findViewById(R.id.firstRecordDateText);
        lastRecordDateText = (TextView)findViewById(R.id.lastRecordDateText);
        averageLinearLayout = (LinearLayout)findViewById(R.id.averageLinearLayout);
        averageValueText = (TextView)findViewById(R.id.averageText);
        periodText = (TextView)findViewById(R.id.periodText);

        debugText = (TextView)findViewById(R.id.debugText);

        Intent intent = getIntent();
        categoryID = intent.getStringExtra(StaticData.CATEGORY_ID_INTENT_EXTRA);

        category = Category.getCategory(realm, categoryID);

        last7daysText.setText(String.valueOf(category.getLast7RecordNum(realm)));
        last15daysText.setText(String.valueOf(category.getLast15DaysRecordNum(realm)));
        lastMonthText.setText(String.valueOf(category.getLastMonthRecordNum(realm)));
        last2MonthsText.setText(String.valueOf(category.getLast2MonthsRecordNum(realm)));
        last3MonthsText.setText(String.valueOf(category.getLast3MonthsRecordNum(realm)));
        last6MonthsText.setText(String.valueOf(category.getLast6MonthsRecordNum(realm)));
        lastYearText.setText(String.valueOf(category.getLastYearRecordNum(realm)));

        totalRecordNumText.setText(String.valueOf(category.getTotalRecordNum(realm)));

        DateTime firstRecordDateTime = category.getFirstRecordDateTime(realm);
        DateTime lastRecordDateTime = category.getLastRecordDateTime(realm);

        if(firstRecordDateTime == null){
            firstRecordDateText.setText("-");
        }
        else{
            firstRecordDateText.setText(StaticData.fmt.print(firstRecordDateTime));
        }
        if(lastRecordDateTime == null){
            lastRecordDateText.setText("-");
        }
        else{
            lastRecordDateText.setText(StaticData.fmt.print(lastRecordDateTime));
        }
        if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER){
            averageValueText.setText(String.valueOf(category.getAverage(realm)));
        }else{
            averageLinearLayout.setVisibility(View.GONE);
        }
        Period period = category.getRecordPeriod(realm);
        if(period != null) {
            periodText.setText(period.getYears() + " year(s) " + period.getMonths() + " month(s)\n\r" + period.getWeeks() + " week(s) " + period.getDays() + " day(s)");
        }else{
            periodText.setText("0 year(s) 0 month(s) 0 week(s)  day(s)");
        }

//        List<Record> records = category.getRecords();
//        for(Record record : records) {
//            debugText.append("" + record.getDate() + ", type : " + record.getRecordType() + ", number : " +  record.getNumber() + ", string : " + record.getString() + "\r\n");
//        }

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Statistics [" + category.getName() + "]");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
//                startActivity(intent);
            }
        });
    }

}
