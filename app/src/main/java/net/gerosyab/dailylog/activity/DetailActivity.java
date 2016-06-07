package net.gerosyab.dailylog.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.Category_Table;
import net.gerosyab.dailylog.data.Record;
import net.gerosyab.dailylog.data.Record_Table;
import net.gerosyab.dailylog.data.StaticData;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    MaterialCalendarView widget;
    Category category;
    List<Record> records;
    long categoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        widget = (MaterialCalendarView)findViewById(R.id.calendarView);

        Intent intent = getIntent();
        categoryID = intent.getLongExtra(StaticData.CATEGORY_ID_INTENT_EXTRA, -1);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.mipmap.ic_stat3);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StatisticActivity.class);
                intent.putExtra(StaticData.CATEGORY_ID_INTENT_EXTRA, category.getId());
                startActivity(intent);
            }
        });

        category = SQLite.select()
                .from(Category.class)
                .where(Category_Table.id.eq(categoryID))
                .querySingle();


        ActionBar ab = getSupportActionBar();
        ab.setTitle(category.getName());

        records = SQLite.select()
                .from(Record.class)
                .where(Record_Table.category_id.eq(categoryID))
                .queryList();

        //calendar view init
    }
}
