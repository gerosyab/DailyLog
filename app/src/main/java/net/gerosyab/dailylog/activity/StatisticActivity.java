package net.gerosyab.dailylog.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.Category_Table;
import net.gerosyab.dailylog.data.StaticData;

import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by donghe on 2016-06-07.
 */
public class StatisticActivity extends AppCompatActivity {
    Category category;
    long categoryID;

    TextView last7daysText;
    TextView last15daysText;
    TextView lastMonthText;
    TextView last2MonthsText;
    TextView last3MonthsText;
    TextView last6MonthsText;
    TextView lastYearText;
    TextView firstRecordDateText;
    TextView lastRecordDateText;
    TextView averageValueText;
    TextView periodText;

    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        last7daysText = (TextView)findViewById(R.id.last7DaysText);
        last15daysText = (TextView)findViewById(R.id.last15DaysText);
        lastMonthText = (TextView)findViewById(R.id.lastMonthText);
        last2MonthsText = (TextView)findViewById(R.id.last2MonthsText);
        last3MonthsText = (TextView)findViewById(R.id.last3MonthsText);
        last6MonthsText = (TextView)findViewById(R.id.last6MonthsText);
        lastYearText = (TextView)findViewById(R.id.lastYearText);
        firstRecordDateText = (TextView)findViewById(R.id.firstRecordDateText);
        lastRecordDateText = (TextView)findViewById(R.id.lastRecordDateText);
        averageValueText = (TextView)findViewById(R.id.averageText);
        periodText = (TextView)findViewById(R.id.periodText);

        Intent intent = getIntent();
        categoryID = intent.getLongExtra(StaticData.CATEGORY_ID_INTENT_EXTRA, -1);

        category = SQLite.select()
                .from(Category.class)
                .where(Category_Table.id.eq(categoryID))
                .querySingle();

        last7daysText.setText(String.valueOf(category.getLast7RecordNum()));
        last15daysText.setText(String.valueOf(category.getLast15DaysRecordNum()));
        lastMonthText.setText(String.valueOf(category.getLastMonthRecordNum()));
        last2MonthsText.setText(String.valueOf(category.getLast2MonthsRecordNum()));
        last3MonthsText.setText(String.valueOf(category.getLast3MonthsRecordNum()));
        last6MonthsText.setText(String.valueOf(category.getLast6MonthsRecordNum()));
        lastYearText.setText(String.valueOf(category.getLastYearRecordNum()));
        firstRecordDateText.setText(fmt.print(category.getFirstRecordDateTime()));
        lastRecordDateText.setText(fmt.print(category.getLastRecordDateTime()));
        if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER){
            averageValueText.setText(String.valueOf(category.getAverage()));
        }else{
            averageValueText.setVisibility(View.GONE);
        }
        lastRecordDateText.setText(fmt.print(category.getLastRecordDateTime()));
        Period period = category.getRecordPeriod();
        periodText.setText(period.getYears() + " year(s) " + period.getMonths() + " month(s)\n\r" + period.getWeeks() + " week(s) " + period.getDays() + " day(s)");

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Statistic [" + category.getName() + "]");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
            }
        });
    }

}
