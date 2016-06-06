package net.gerosyab.dailylog.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.Category_Table;
import net.gerosyab.dailylog.data.StaticData;

public class CategoryActivity extends AppCompatActivity {
    long categoryMode;
    View rootView;
    EditText categoryNameEditText, unitEditText;
    TextView unitTitleTextView;
    RadioButton radioButton1, radioButton2, radioButton3;
    Category category;
    String categoryNameStr = "";
    String unitStr = "";
    long recordType;
    long categoryNum;
    long categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        rootView = (View)findViewById(R.id.categoryRootView);
        categoryNameEditText = (EditText)findViewById(R.id.categoryNameEditText);
        unitEditText = (EditText)findViewById(R.id.unitEditText);
        unitTitleTextView = (TextView)findViewById(R.id.unitTextView);
        radioButton1  = (RadioButton)findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton)findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton)findViewById(R.id.radioButton3);

        Intent intent = getIntent();
        categoryMode = intent.getLongExtra(StaticData.CATEGORY_MODE_INTENT_EXTRA, StaticData.CATEGORY_MODE_CREATE);
        categoryNum = intent.getLongExtra(StaticData.CATEGORY_NUM_INTENT_EXTRA, 0);
        categoryId = intent.getLongExtra(StaticData.CATEGORY_ID_INTENT_EXTRA, -1);

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        ab.setTitle("");

        //Boolean
        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton2.setChecked(false);
                radioButton3.setChecked(false);
                unitTitleTextView.setEnabled(false);
                unitEditText.setEnabled(false);
                recordType = StaticData.RECORD_TYPE_BOOLEAN;
            }
        });

        //Number
        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton1.setChecked(false);
                radioButton3.setChecked(false);
                unitTitleTextView.setEnabled(true);
                unitEditText.setEnabled(true);
                recordType = StaticData.RECORD_TYPE_NUMBER;
            }
        });

        //Memo
        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton1.setChecked(false);
                radioButton2.setChecked(false);
                unitTitleTextView.setEnabled(false);
                unitEditText.setEnabled(false);
                recordType = StaticData.RECORD_TYPE_MEMO;
            }
        });

        //View Intialization
        if(categoryMode == StaticData.CATEGORY_MODE_CREATE){

            radioButton1.setChecked(true);
            radioButton2.setChecked(false);
            radioButton3.setChecked(false);
            unitTitleTextView.setEnabled(false);
            unitEditText.setEnabled(false);
            recordType = StaticData.RECORD_TYPE_BOOLEAN;
            category.setRecordType(recordType);
        }
        else if(categoryMode == StaticData.CATEGORY_MODE_EDIT){
            SQLite.select()
                    .from(Category.class)
                    .where(Category_Table.id.is(categoryId))
                    .async()
                    .queryResultCallback(new QueryTransaction.QueryResultCallback<Category>() {
                        @Override
                        public void onQueryResult(QueryTransaction transaction, @NonNull CursorResult<Category> tResult) {
                            category = tResult.toModelClose();
                            categoryNameEditText.setText(category.getName());
                            recordType = category.getRecordType();
                            if(recordType == StaticData.RECORD_TYPE_BOOLEAN){
                                radioButton1.setEnabled(true);
                                radioButton2.setEnabled(false);
                                radioButton3.setEnabled(false);
                                unitTitleTextView.setEnabled(false);
                                unitEditText.setEnabled(false);
                            }
                            else if(recordType == StaticData.RECORD_TYPE_NUMBER){
                                radioButton1.setEnabled(false);
                                radioButton2.setEnabled(true);
                                radioButton3.setEnabled(false);
                                unitTitleTextView.setText(category.getUnit());
                                unitTitleTextView.setEnabled(true);
                                unitEditText.setEnabled(true);
                            }
                            else if(recordType == StaticData.RECORD_TYPE_MEMO){
                                radioButton1.setEnabled(false);
                                radioButton2.setEnabled(false);
                                radioButton3.setEnabled(true);
                                unitTitleTextView.setEnabled(false);
                                unitEditText.setEnabled(false);
                            }

                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_save) {

            categoryNameStr= categoryNameEditText.getText().toString();

            if(categoryNameStr.equalsIgnoreCase("")){
                Snackbar.make(rootView, "Category Name must be specified", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
            }else if(categoryNameStr.length() > 50){
                Snackbar.make(rootView, "The maximum length of Category Name is 50 characters", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
            }

            if(radioButton2.isChecked()) {

                unitStr = categoryNameEditText.getText().toString();

                if(unitStr.equalsIgnoreCase("")){
                    Snackbar.make(rootView, "Unit must be specified if the record type is numeric", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return true;
                }else if(unitStr.length() > 20){
                    Snackbar.make(rootView, "The maximum length of Unit is 20 characters", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return true;
                }
            }
            else{
                unitStr="";
            }

            //같은 이름의 카테고리가 있는지 확인

            // DB 신규 저장 or 업데이트 처리
            category.setName(categoryNameStr);
            category.setUnit(unitStr);
            category.setRecordType(recordType);
            if(categoryMode == StaticData.CATEGORY_MODE_CREATE){
                category.setOrder(categoryNum + 1);
            }
            else if(categoryMode == StaticData.CATEGORY_MODE_EDIT){

            }
            category.save();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
