package net.gerosyab.dailylog.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import net.gerosyab.dailylog.R;

public class DetailActivity extends AppCompatActivity {

    MaterialCalendarView widget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        widget = (MaterialCalendarView)findViewById(R.id.calendarView);


    }
}
