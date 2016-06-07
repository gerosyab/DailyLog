package net.gerosyab.dailylog.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.Category_Table;
import net.gerosyab.dailylog.data.StaticData;
import net.gerosyab.dailylog.data.Statistic;
import net.gerosyab.dailylog.data.Statistic_Table;
import net.gerosyab.dailylog.util.Util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by donghe on 2016-06-07.
 */
public class StatisticActivity extends AppCompatActivity {
    Category category;
    Statistic statistic;
    long categoryID;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        textView = (TextView) findViewById(R.id.testText);
        textView.setText("");
//        List<Field> fields = Util.getAllFields(new LinkedList<Field>(), CalendarView.class);
        List<Field> fields = Util.getInheritedFields(CalendarView.class);

            for(Field field : fields){
                String str = field.getName();
                field.setAccessible(true);
                if(str.contains("text") || str.contains("month") || str.contains("Text") || str.contains("Month") || str.contains("Text") || str.contains("Month") ){
                    textView.setTextColor(Color.RED);
                }
                else{
                    textView.setTextColor(Color.BLACK);
                }
                textView.append("- " + field.getName() + "\r\n");
            }

        Intent intent = getIntent();
        categoryID = intent.getLongExtra(StaticData.CATEGORY_ID_INTENT_EXTRA, -1);

        category = SQLite.select()
                .from(Category.class)
                .where(Category_Table.id.eq(categoryID))
                .querySingle();

        statistic = SQLite.select()
                .from(Statistic.class)
                .where(Statistic_Table.category_id.eq(categoryID))
                .querySingle();

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Statistic [" + category.getName() + "]");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
