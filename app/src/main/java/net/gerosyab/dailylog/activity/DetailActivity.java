package net.gerosyab.dailylog.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.Category_Table;
import net.gerosyab.dailylog.data.Record;
import net.gerosyab.dailylog.data.Record_Table;
import net.gerosyab.dailylog.data.StaticData;
import net.gerosyab.dailylog.util.Util;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    CalendarView calendarView;
    Category category;
    List<Record> records;
    long categoryID;
    TextView monthTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        calendarView = (CalendarView) findViewById(R.id.calendarView);

        try {
            List<Field> fields = Util.getAllFields(new LinkedList<Field>(), CalendarView.class);
            Field mMonthTextField = null;
            for(Field field : fields){
                if(field.getName().equals("mMonthName")){
                    mMonthTextField = field;
                    mMonthTextField.setAccessible(true);
                    break;
                }
            }
            Toast.makeText(getApplicationContext(), "mMonthTextField : " + mMonthTextField, Toast.LENGTH_LONG).show();

            if(mMonthTextField != null) {
                monthTextView = (TextView) mMonthTextField.get(calendarView);
                monthTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "Month Text Clicked", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        categoryID = intent.getLongExtra(StaticData.CATEGORY_ID_INTENT_EXTRA, -1);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

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
