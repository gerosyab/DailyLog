package net.gerosyab.dailylog.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.ChartDataModel;
import net.gerosyab.dailylog.data.StaticData;
import net.gerosyab.dailylog.util.MyLog;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StatisticsActivity extends SuperActivity {
    private static final String LOG_TAG = "StatisticsActivity";
    private static final SimpleDateFormat sdfForDailyChart = new SimpleDateFormat("M/d");
    private static final SimpleDateFormat sdfForWeeklyChart = new SimpleDateFormat("M/d");
    private static final SimpleDateFormat sdfForMonthlyChart = new SimpleDateFormat("yyyy/M");
    private static final SimpleDateFormat sdfForYearlyChart = new SimpleDateFormat("yyyy");
    Category category;
    String categoryID;
    
    TextView totalRecordNumText;
    TextView firstRecordDateText;
    TextView lastRecordDateText;
    TextView totalAvgValueText;
    TextView totalSumValueText;
    TextView totalPeriodText;

    ImageButton chartNextPeriodBtn, chartPrevPeriodBtn;

    CombinedData combinedData;
//    ImageButton chartZoomInBtn, chartZoomOutBtn;

    LinearLayout trendColumn3, trendColumn4, summaryTotalAvgLinear, summaryTotalSumLinear;
    TextView recent7daysNumText, recent15daysNumText, recentMonthNumText, recent2MonthsNumText, recent3MonthsNumText, recent6MonthsNumText, recentYearNumText,
            recent7daysAvgText, recent15daysAvgText, recentMonthAvgText, recent2MonthsAvgText, recent3MonthsAvgText, recent6MonthsAvgText, recentYearAvgText,
            recent7daysSumText, recent15daysSumText, recentMonthSumText, recent2MonthsSumText, recent3MonthsSumText, recent6MonthsSumText, recentYearSumText;

//    BarChart chart;
    CombinedChart chart;
    Spinner spinner;
    int dailyPrevIndex = 0, weeklyPrevIndex = 0, monthlyPrevIndex = 0, yearlyPrevIndex = 0;
    private static final int CHART_TYPE_DAILY = 0, CHART_TYPE_WEEKLY = 1, CHART_TYPE_MONTHLY = 2, CHART_TYPE_YEARLY = 3;

    private static final float xAxisTextSize = 14f;
    private static final float barValueTextSize = 14f;
    private static final float barMonthValueTextSize = 12f;
    private static final float lineValueTextSize = 14f;
    private static final float legendFormSize = 12f;
    private static final float legendTextSize = 14f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(realm == null) realm = getRealm();
        setContentView(R.layout.activity_statistics);
        spinner = (Spinner) findViewById(R.id.chart_date_spinner);
        chartPrevPeriodBtn = (ImageButton) findViewById(R.id.chart_prev_period_btn);
        chartNextPeriodBtn = (ImageButton) findViewById(R.id.chart_next_period_btn);

        MyLog.d(LOG_TAG, "dailyPrevIndex : " + dailyPrevIndex + ", weeklyPrevIndex : " + weeklyPrevIndex + ", monthlyPrevIndex : " + monthlyPrevIndex + ", yearlyPrevIndex : " + yearlyPrevIndex);
        chartPrevPeriodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spinner.getSelectedItemPosition() == 0) { // 0: Day
                    dailyPrevIndex--;
                    //set new chart data
                } else if(spinner.getSelectedItemPosition() == 1) { // 1: Week
                    weeklyPrevIndex--;
                    //set new chart data
                } else if(spinner.getSelectedItemPosition() == 2) { // 2: Month
                    monthlyPrevIndex--;
                    //set new chart data
                } else if(spinner.getSelectedItemPosition() == 3) { // 3: Year
                    yearlyPrevIndex--;
                    //set new chart data
                }
                //chart redraw
                MyLog.d(LOG_TAG, "dailyPrevIndex : " + dailyPrevIndex + ", weeklyPrevIndex : " + weeklyPrevIndex + ", monthlyPrevIndex : " + monthlyPrevIndex + ", yearlyPrevIndex : " + yearlyPrevIndex);
                drawChart();
            }
        });
        chartNextPeriodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spinner.getSelectedItemPosition() == 0) { // 0: Day
                    dailyPrevIndex++;
                    //set new chart data
                } else if(spinner.getSelectedItemPosition() == 1) { // 1: Week
                    weeklyPrevIndex++;
                    //set new chart data
                } else if(spinner.getSelectedItemPosition() == 2) { // 2: Month
                    monthlyPrevIndex++;
                    //set new chart data
                } else if(spinner.getSelectedItemPosition() == 3) { // 3: Year
                    yearlyPrevIndex++;
                    //set new chart data
                }
                //chart redraw
                MyLog.d(LOG_TAG, "dailyPrevIndex : " + dailyPrevIndex + ", weeklyPrevIndex : " + weeklyPrevIndex + ", monthlyPrevIndex : " + monthlyPrevIndex + ", yearlyPrevIndex : " + yearlyPrevIndex);
                drawChart();
            }
        });

//        chartZoomOutBtn = (ImageButton) findViewById(R.id.chart_zoom_out_btn);
//        chartZoomInBtn = (ImageButton) findViewById(R.id.chart_zoom_in_btn);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_date_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(context, "Spinner Item (" + i + ") : " + adapter.getItem(i), Toast.LENGTH_SHORT).show();
                MyLog.d(LOG_TAG, "Spinner Item (" + i + ") : " + adapter.getItem(i));
//                chart.clear();
//                chart.notifyDataSetChanged();
//                chart.invalidate();
                drawChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner.setSelection(0);    // 0: Day, 1: Week, 2: Month, 3: Year

//        chart = (BarChart) findViewById(R.id.chart);
        chart = (CombinedChart) findViewById(R.id.chart);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);

        trendColumn3 = (LinearLayout)findViewById(R.id.TrendColumn3);
        trendColumn4 = (LinearLayout)findViewById(R.id.TrendColumn4);
        summaryTotalAvgLinear = (LinearLayout)findViewById(R.id.summaryTotalAvgLinear);
        summaryTotalSumLinear = (LinearLayout)findViewById(R.id.summaryTotalSumLinear);

        recent7daysNumText = (TextView)findViewById(R.id.recent7DaysNumText);
        recent15daysNumText = (TextView)findViewById(R.id.recent15DaysNumText);
        recentMonthNumText = (TextView)findViewById(R.id.recentMonthNumText);
        recent2MonthsNumText = (TextView)findViewById(R.id.recent2MonthsNumText);
        recent3MonthsNumText = (TextView)findViewById(R.id.recent3MonthsNumText);
        recent6MonthsNumText = (TextView)findViewById(R.id.recent6MonthsNumText);
        recentYearNumText = (TextView)findViewById(R.id.recentYearNumText);
        recent7daysAvgText = (TextView)findViewById(R.id.recent7DaysAvgText);
        recent15daysAvgText = (TextView)findViewById(R.id.recent15DaysAvgText);
        recentMonthAvgText = (TextView)findViewById(R.id.recentMonthAvgText);
        recent2MonthsAvgText = (TextView)findViewById(R.id.recent2MonthsAvgText);
        recent3MonthsAvgText = (TextView)findViewById(R.id.recent3MonthsAvgText);
        recent6MonthsAvgText = (TextView)findViewById(R.id.recent6MonthsAvgText);
        recentYearAvgText = (TextView)findViewById(R.id.recentYearAvgText);
        recent7daysSumText = (TextView)findViewById(R.id.recent7DaysSumText);
        recent15daysSumText = (TextView)findViewById(R.id.recent15DaysSumText);
        recentMonthSumText = (TextView)findViewById(R.id.recentMonthSumText);
        recent2MonthsSumText = (TextView)findViewById(R.id.recent2MonthsSumText);
        recent3MonthsSumText = (TextView)findViewById(R.id.recent3MonthsSumText);
        recent6MonthsSumText = (TextView)findViewById(R.id.recent6MonthsSumText);
        recentYearSumText = (TextView)findViewById(R.id.recentYearSumText);

        totalRecordNumText = (TextView)findViewById(R.id.summaryTotalNumText);
        totalAvgValueText = (TextView)findViewById(R.id.summaryTotalAvgText);
        totalSumValueText = (TextView)findViewById(R.id.summaryTotalSumText);
        firstRecordDateText = (TextView)findViewById(R.id.firstRecordDateText);
        lastRecordDateText = (TextView)findViewById(R.id.lastRecordDateText);
        totalPeriodText = (TextView)findViewById(R.id.totalPeriodText);

        Intent intent = getIntent();
        categoryID = intent.getStringExtra(StaticData.CATEGORY_ID_INTENT_EXTRA);
        category = Category.getCategory(realm, categoryID);
//        List<Record> records = category.getRecords();
//        for(Record record : records) {
//            debugText.append("" + record.getDate() + ", type : " + record.getRecordType() + ", number : " +  record.getNumber() + ", string : " + record.getString() + "\r\n");
//        }

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Statistics [" + category.getName() + " - " +  StaticData.RECORD_TYPE_NAME[(int) category.getRecordType()]+ "]");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
//                startActivity(intent);
            }
        });

        setData();
    }

    public void setData(){
        recent7daysNumText.setText(String.valueOf(category.getRecentRecordNum(Category.DAY, 7, realm)));
        recent15daysNumText.setText(String.valueOf(category.getRecentRecordNum(Category.DAY, 7, realm)));
        recentMonthNumText.setText(String.valueOf(category.getRecentRecordNum(Category.MONTH, 1, realm)));
        recent2MonthsNumText.setText(String.valueOf(category.getRecentRecordNum(Category.MONTH, 2, realm)));
        recent3MonthsNumText.setText(String.valueOf(category.getRecentRecordNum(Category.MONTH, 3, realm)));
        recent6MonthsNumText.setText(String.valueOf(category.getRecentRecordNum(Category.MONTH, 6, realm)));
        recentYearNumText.setText(String.valueOf(category.getRecentRecordNum(Category.YEAR, 1, realm)));
        totalRecordNumText.setText(String.valueOf(category.getTotalRecordNum(realm)));

        if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
            recent7daysAvgText.setText(String.format("%.2f", category.getRecentAverage(Category.DAY, 7, realm)));
            recent15daysAvgText.setText(String.format("%.2f", category.getRecentAverage(Category.DAY, 15, realm)));
            recentMonthAvgText.setText(String.format("%.2f", category.getRecentAverage(Category.MONTH, 1, realm)));
            recent2MonthsAvgText.setText(String.format("%.2f", category.getRecentAverage(Category.MONTH, 2, realm)));
            recent3MonthsAvgText.setText(String.format("%.2f", category.getRecentAverage(Category.MONTH, 3, realm)));
            recent6MonthsAvgText.setText(String.format("%.2f", category.getRecentAverage(Category.MONTH, 6, realm)));
            recentYearAvgText.setText(String.format("%.2f", category.getRecentAverage(Category.YEAR, 1, realm)));
            recent7daysSumText.setText(String.valueOf(category.getRecentSum(Category.DAY, 7, realm)));
            recent15daysSumText.setText(String.valueOf(category.getRecentSum(Category.DAY, 15, realm)));
            recentMonthSumText.setText(String.valueOf(category.getRecentSum(Category.MONTH, 1, realm)));
            recent2MonthsSumText.setText(String.valueOf(category.getRecentSum(Category.MONTH, 2, realm)));
            recent3MonthsSumText.setText(String.valueOf(category.getRecentSum(Category.MONTH, 3, realm)));
            recent6MonthsSumText.setText(String.valueOf(category.getRecentSum(Category.MONTH, 6, realm)));
            recentYearSumText.setText(String.valueOf(category.getRecentSum(Category.YEAR, 1, realm)));
            totalAvgValueText.setText(String.format("%.2f", category.getTotalAverage(realm)));
            totalSumValueText.setText(String.valueOf(category.getTotalSum(realm)));

        } else{
            trendColumn3.setVisibility(View.GONE);
            trendColumn4.setVisibility(View.GONE);
            summaryTotalAvgLinear.setVisibility(View.GONE);
            summaryTotalSumLinear.setVisibility(View.GONE);
        }


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

        Period period = category.getRecordPeriod(realm);
        if(period != null) {
            String msg = "";
            if(period.getYears() > 1) msg += period.getYears() + " Years ";
            else msg += period.getYears() + " Year ";
            if(period.getMonths() > 1) msg += period.getMonths() + " Months\n\r";
            else msg += period.getMonths() + " Month\n\r";
            if(period.getDays() > 1) msg += period.getDays() + " Days";
            else msg += period.getDays() + " Day";
            totalPeriodText.setText(msg);
        }else{
            totalPeriodText.setText("0 Year 0 Month 0 Week 0 Day");
        }
    }

    private void drawChart(){

        if(chart.getBarData() != null) chart.getBarData().clearValues();
        if(chart.getLineData() != null) chart.getLineData().clearValues();

        chart.clear();
        int prevIndex = 0;
        int chartType = 0;
        MyXAxisValueFormatter formatter = null;
        DateTime baseDt = new DateTime();
        DateTime fromDt = null;
        ArrayList<ChartDataModel> list = null;
        if(combinedData == null) combinedData = new CombinedData();
        combinedData.clearValues();

        if(spinner.getSelectedItemPosition() == 0) { // 0: Day
            prevIndex = dailyPrevIndex;
            chartType = CHART_TYPE_DAILY;
            fromDt = category.getDailyChartFromDtTime(baseDt, prevIndex);
            formatter = new MyXAxisValueFormatter(fromDt, sdfForDailyChart, chartType);
            list = category.getDailyChartDataSet(realm, fromDt);
            ArrayList<BarEntry> barEntries = null;
            ArrayList<Entry> lineEntries = null;

            if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER){
                lineEntries = new ArrayList<Entry>(list.size());
            } else {
                barEntries = new ArrayList<BarEntry>(list.size());
            }

            for(ChartDataModel model : list){
                if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER){
                    lineEntries.add(new BarEntry(model.getXAxis(), model.getValue()));
                } else {
                    barEntries.add(new BarEntry(model.getXAxis(), model.getValue()));
                }
//                MyLog.d(LOG_TAG,
//                        "daily ChartDataModel - getXAxis() : "
//                                + model.getXAxis()
//                                + ", getValue() : "
//                                + model.getValue()
//                                + ", millis : "
//                                + (fromDt.plusDays((int)model.getXAxis()).getMillis())
//                                + ", "
//                                + StaticData.sdf.format(new Date(fromDt.plusDays((int)model.getXAxis()).getMillis()))
//                );
            }
            if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER){
                LineData lineData = new LineData();
                LineDataSet lineDataSet = new LineDataSet(lineEntries, "Number");
                lineDataSet.setColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setLineWidth(2.5f);
                lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setCircleRadius(5f);
                lineDataSet.setFillColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setMode(LineDataSet.Mode.LINEAR);
                lineDataSet.setDrawValues(true);
                lineDataSet.setValueTextSize(lineValueTextSize);
                lineDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineData.addDataSet(lineDataSet);
                lineData.setValueFormatter(new MyFloatValueFormatter());
                combinedData.setData(lineData);
            } else {
                BarDataSet dataSet = new BarDataSet(barEntries, "Record");
                dataSet.setColor(ContextCompat.getColor(this, R.color.bar_chart_color));
                dataSet.setValueTextSize(barValueTextSize);
                dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.bar_chart_text_color));;
                BarData barData = new BarData(dataSet);
                barData.setValueFormatter(new MyCountValueFormatter());
                combinedData.setData(barData);
            }

        } else if(spinner.getSelectedItemPosition() == 1) { // 1: Week
            prevIndex = weeklyPrevIndex;
            chartType = CHART_TYPE_WEEKLY;
            fromDt = category.getWeeklyChartFromDtTime(baseDt, prevIndex);
            formatter = new MyXAxisValueFormatter(fromDt, sdfForWeeklyChart, chartType);
            list = category.getWeeklyChartDataSet(realm, fromDt);
            ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>(list.size());
            ArrayList<Entry> lineEntries = null;

            if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
                lineEntries = new ArrayList<Entry>(list.size());
            }

            for(ChartDataModel model : list){
//                float[] arr = {model.getValue(), (float) model.getAverage(), model.getSum()};
//                entries.add(new BarEntry(model.getXAxis(), arr));
                barEntries.add(new BarEntry(model.getXAxis(), model.getValue()));
                if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
                    lineEntries.add(new Entry(model.getXAxis(), (float) model.getAverage()));
                }
                MyLog.d(LOG_TAG,
                        "weekly ChartDataModel - getXAxis() : "
                                + model.getXAxis()
                                + ", getValue() : "
                                + model.getValue()
                                + ", millis : "
                                + (fromDt.plusWeeks((int)model.getXAxis()).getMillis())
                                + ", "
                                + StaticData.sdf.format(new Date(fromDt.plusWeeks((int)model.getXAxis()).getMillis()))
                );
            }

            BarDataSet barDataSet = new BarDataSet(barEntries, "Record Count");
            barDataSet.setColor(ContextCompat.getColor(this, R.color.bar_chart_color));
            barDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.bar_chart_text_color));
            barDataSet.setValueTextSize(barValueTextSize);
//            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
                LineData lineData = new LineData();
                LineDataSet lineDataSet = new LineDataSet(lineEntries, "Average");
                lineDataSet.setColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setLineWidth(2.5f);
                lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setCircleRadius(5f);
                lineDataSet.setFillColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setMode(LineDataSet.Mode.LINEAR);
                lineDataSet.setDrawValues(true);
                lineDataSet.setValueTextSize(lineValueTextSize);
                lineDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineData.addDataSet(lineDataSet);
                lineData.setValueFormatter(new MyFloatValueFormatter());
                combinedData.setData(lineData);
            }

            BarData barData = new BarData(barDataSet);
            barData.setValueFormatter(new MyCountValueFormatter());
            combinedData.setData(barData);
        }
        else if(spinner.getSelectedItemPosition() == 2) { // 2: Month
            prevIndex = monthlyPrevIndex;
            chartType = CHART_TYPE_MONTHLY;
            fromDt = category.getMonthlyChartFromDtTime(baseDt, prevIndex);
            formatter = new MyXAxisValueFormatter(fromDt, sdfForMonthlyChart, chartType);
            list = category.getMonthlyChartDataSet(realm, fromDt);
            ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>(list.size());
            ArrayList<Entry> lineEntries = null;

            if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
                lineEntries = new ArrayList<Entry>(list.size());
            }

            for(ChartDataModel model : list){
//                float[] arr = {model.getValue(), (float) model.getAverage(), model.getSum()};
//                entries.add(new BarEntry(model.getXAxis(), arr));
                barEntries.add(new BarEntry(model.getXAxis(), model.getValue()));
                if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
                    lineEntries.add(new Entry(model.getXAxis(), (float) model.getAverage()));
                }
                MyLog.d(LOG_TAG,
                        "monthly ChartDataModel - getXAxis() : "
                                + model.getXAxis()
                                + ", getValue() : "
                                + model.getValue()
                                + ", finalMillisTime : "
                                + (fromDt.plusMonths((int)model.getXAxis()).getMillis())
                                + ", "
                                + StaticData.sdf.format(new Date(fromDt.plusMonths((int)model.getXAxis()).getMillis()))
                );
            }

            BarDataSet barDataSet = new BarDataSet(barEntries, "Record Count");
            barDataSet.setColor(ContextCompat.getColor(this, R.color.bar_chart_color));
            barDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.bar_chart_text_color));
            barDataSet.setValueTextSize(barMonthValueTextSize);
//            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
                LineData lineData = new LineData();
                LineDataSet lineDataSet = new LineDataSet(lineEntries, "Average");
                lineDataSet.setColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setLineWidth(2.5f);
                lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setCircleRadius(5f);
                lineDataSet.setFillColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setMode(LineDataSet.Mode.LINEAR);
                lineDataSet.setDrawValues(true);
                lineDataSet.setValueTextSize(lineValueTextSize);
                lineDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineData.addDataSet(lineDataSet);
                lineData.setValueFormatter(new MyFloatValueFormatter());
                combinedData.setData(lineData);
            }

            BarData barData = new BarData(barDataSet);
            barData.setValueFormatter(new MyCountValueFormatter());
            combinedData.setData(barData);

        } else if(spinner.getSelectedItemPosition() == 3) { // 3: Year
            prevIndex = yearlyPrevIndex;
            chartType = CHART_TYPE_YEARLY;
            fromDt = category.getYearlyChartFromDtTime(baseDt, prevIndex);
            formatter = new MyXAxisValueFormatter(fromDt, sdfForYearlyChart, chartType);
            list = category.getYearlyChartDataSet(realm, fromDt);
            ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>(list.size());
            ArrayList<Entry> lineEntries = null;

            if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
                lineEntries = new ArrayList<Entry>(list.size());
            }

            for(ChartDataModel model : list){
//                float[] arr = {model.getValue(), (float) model.getAverage(), model.getSum()};
//                entries.add(new BarEntry(model.getXAxis(), arr));
                barEntries.add(new BarEntry(model.getXAxis(), model.getValue()));
                if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
                    lineEntries.add(new Entry(model.getXAxis(), (float) model.getAverage()));
                }
                MyLog.d(LOG_TAG,
                        "yearly ChartDataModel - getXAxis() : "
                                + model.getXAxis()
                                + ", getValue() : "
                                + model.getValue()
                                + ", finalMillisTime : "
                                + (fromDt.plusYears((int)model.getXAxis()).getMillis())
                                + ", "
                                + StaticData.sdf.format(new Date(fromDt.plusYears((int)model.getXAxis()).getMillis()))
                );
            }

            BarDataSet barDataSet = new BarDataSet(barEntries, "Record Count");
            barDataSet.setColor(ContextCompat.getColor(this, R.color.bar_chart_color));
            barDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.bar_chart_text_color));
            barDataSet.setValueTextSize(barValueTextSize);
//            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER) {
                LineData lineData = new LineData();
                LineDataSet lineDataSet = new LineDataSet(lineEntries, "Average");
                lineDataSet.setColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setLineWidth(2.5f);
                lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setCircleRadius(5f);
                lineDataSet.setFillColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setMode(LineDataSet.Mode.LINEAR);
                lineDataSet.setDrawValues(true);
                lineDataSet.setValueTextSize(lineValueTextSize);
                lineDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.line_chart_color));
                lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineData.addDataSet(lineDataSet);
                lineData.setValueFormatter(new MyFloatValueFormatter());
                combinedData.setData(lineData);
            }

            BarData barData = new BarData(barDataSet);
            barData.setValueFormatter(new MyCountValueFormatter());
            combinedData.setData(barData);
        }

        chart.getAxisLeft().setAxisMinimum(0f);
//        chart.getAxisLeft().setGranularity(2f);
        chart.getAxisRight().setAxisMinimum(0f);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(xAxisTextSize);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.dark_gray));
        xAxis.setValueFormatter(formatter);
        xAxis.setGranularity(1f);
        xAxis.setYOffset(10f);
        xAxis.setAxisMinimum(combinedData.getXMin() - .5f); // prevent clipping first/last bar in combined chart
        xAxis.setAxisMaximum(combinedData.getXMax() + .5f); // prevent clipping first/last bar in combined chart

        Legend legend = chart.getLegend();
        legend.setFormSize(legendFormSize); // set the size of the legend forms/shapes
        legend.setForm(Legend.LegendForm.SQUARE); // set what type of form/shape should be used
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setTextSize(legendTextSize);
        legend.setTextColor(ContextCompat.getColor(this, R.color.dark_gray));
        legend.setXEntrySpace(8f); // set the space between the legend entries on the x-axis
        legend.setYEntrySpace(8f); // set the space between the legend entries on the y-axis

        chart.setData(combinedData);
        chart.fitScreen();
        chart.setExtraOffsets(10f, 10f, 10f, 10f);
        chart.setDrawValueAboveBar(false);
        chart.animateY(200, Easing.EasingOption.Linear);
        chart.invalidate();
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {
        private DateTime fromDateTime;
        private SimpleDateFormat sdf;
        private int chartType;

        public MyXAxisValueFormatter(DateTime fromDateTime, SimpleDateFormat sdf, int chartType) {
            this.fromDateTime = fromDateTime;
            this.sdf = sdf;
            this.chartType = chartType;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if(chartType == CHART_TYPE_DAILY){
                return sdf.format(new Date(fromDateTime.plusDays((int) value).getMillis()));
            } else if (chartType == CHART_TYPE_WEEKLY) {
//                return sdf.format(new Date(fromDateTime.dayOfWeek().withMinimumValue().plusWeeks((int) value * 1).getMillis()));
                return sdf.format(new Date(fromDateTime.plusWeeks((int) value).getMillis()));
            } else if (chartType == CHART_TYPE_MONTHLY) {
//                return sdf.format(new Date(fromDateTime.dayOfMonth().withMinimumValue().plusMonths((int) value * 1).getMillis()));
                return sdf.format(new Date(fromDateTime.plusMonths((int) value).getMillis()));
            } else if (chartType == CHART_TYPE_YEARLY) {
//                return sdf.format(new Date(fromDateTime.dayOfYear().withMinimumValue().plusYears((int) value * 1).getMillis()));
                return sdf.format(new Date(fromDateTime.plusYears((int) value).getMillis()));
            }
            return "none";
        }
    }

    public class MyFloatValueFormatter implements IValueFormatter {

        public MyFloatValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            if(value > 0) {
                return String.format("%.2f", value);
            } else {
                return "";
            }
        }
    }

    public class MyCountValueFormatter implements IValueFormatter {

        public MyCountValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            if(value > 0) {
                return String.format("%d", Math.round(value));
            } else {
                return "";
            }
        }
    }
}
