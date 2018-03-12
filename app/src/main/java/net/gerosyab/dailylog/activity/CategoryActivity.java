package net.gerosyab.dailylog.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.StaticData;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.UUID;

public class CategoryActivity extends SuperActivity {
    long categoryMode;
    View rootView;
    EditText categoryNameEditText, unitEditText, defaultValueEditText;
    TextView unitTitleTextView, defaultValueTitleTextView;
    RadioButton radioButton1, radioButton2, radioButton3;
    Category category;
    String categoryNameStr = "";
    String unitStr = "";
    String defaultValueStr = "";
    long defaultValueNum;
    long recordType;
    String categoryID;
    long categoryOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(realm == null) realm = getRealm();
        setContentView(R.layout.activity_category);

        rootView = (View)findViewById(R.id.categoryRootView);
        categoryNameEditText = (EditText)findViewById(R.id.categoryNameEditText);
        unitEditText = (EditText)findViewById(R.id.unitEditText);
        unitTitleTextView = (TextView)findViewById(R.id.unitTextView);
        radioButton1  = (RadioButton)findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton)findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton)findViewById(R.id.radioButton3);
        defaultValueTitleTextView = (TextView)findViewById(R.id.defaultValueTextView);
        defaultValueEditText = (EditText)findViewById(R.id.defaultValueEditText);

        Intent intent = getIntent();
        categoryMode = intent.getLongExtra(StaticData.CATEGORY_MODE_INTENT_EXTRA, StaticData.CATEGORY_MODE_CREATE);
//        categoryNum = intent.getLongExtra(StaticData.CATEGORY_NUM_INTENT_EXTRA, 0);
        categoryID = intent.getStringExtra(StaticData.CATEGORY_ID_INTENT_EXTRA);
        categoryOrder = intent.getLongExtra(StaticData.CATEGORY_ORDER_INTENT_EXTRA, 0);

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
                defaultValueTitleTextView.setEnabled(false);
                defaultValueEditText.setEnabled(false);
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
                defaultValueTitleTextView.setEnabled(true);
                defaultValueEditText.setEnabled(true);
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
                defaultValueTitleTextView.setEnabled(false);
                defaultValueEditText.setEnabled(false);
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
            defaultValueTitleTextView.setEnabled(false);
            defaultValueEditText.setEnabled(false);
            recordType = StaticData.RECORD_TYPE_BOOLEAN;
//            category.setRecordType(recordType);
        }
        else if(categoryMode == StaticData.CATEGORY_MODE_EDIT){
            category = Category.getCategory(realm, categoryID);

            if(category != null) {
                categoryNameEditText.setText(category.getName());

                recordType = category.getRecordType();

                radioButton1.setEnabled(false);
                radioButton2.setEnabled(false);
                radioButton3.setEnabled(false);
                unitTitleTextView.setEnabled(false);
                unitEditText.setEnabled(false);
                defaultValueTitleTextView.setEnabled(false);
                defaultValueEditText.setEnabled(false);

                if (recordType == StaticData.RECORD_TYPE_BOOLEAN) {
                    radioButton1.setChecked(true);
                    radioButton2.setChecked(false);
                    radioButton3.setChecked(false);
                } else if (recordType == StaticData.RECORD_TYPE_NUMBER) {
                    radioButton1.setChecked(false);
                    radioButton2.setChecked(true);
                    radioButton3.setChecked(false);
                    unitTitleTextView.setEnabled(true);
                    unitEditText.setEnabled(true);
                    unitEditText.setText(category.getUnit());
                    defaultValueTitleTextView.setEnabled(true);
                    defaultValueEditText.setText(String.valueOf(category.getDefaultValue()));
                    defaultValueEditText.setEnabled(true);
                } else if (recordType == StaticData.RECORD_TYPE_MEMO) {
                    radioButton1.setChecked(false);
                    radioButton2.setChecked(false);
                    radioButton3.setChecked(true);
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Can't find category \"" + category.getName() + "\"", Toast.LENGTH_LONG).show();
                finish();
            }
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

            // category name checking
            if(categoryNameStr.equalsIgnoreCase("")){
                Toast.makeText(getApplicationContext(), "Category Name must be specified", Toast.LENGTH_LONG).show();
                return true;
            }else if(categoryNameStr.length() > Category.getMaxCategoryNameLength()){
                Toast.makeText(getApplicationContext(), "The maximum length of Category Name is " + Category.getMaxCategoryNameLength() +" characters", Toast.LENGTH_LONG).show();
                return true;
            }else if(Category.isCategoryNameExists(realm, categoryNameStr)){
                if(categoryMode == StaticData.CATEGORY_MODE_CREATE) {
                    Toast.makeText(getApplicationContext(), "Category Name \"" + categoryNameStr + "\" already exists", Toast.LENGTH_LONG).show();
                    return true;
                }
            }

            // unit name and default value checking (NUMERIC TYPE)
            if(radioButton2.isChecked()) {

                unitStr = categoryNameEditText.getText().toString();

                if(unitStr.equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), "Unit must be specified if the record type is numeric", Toast.LENGTH_LONG).show();
                    return true;
                }else if(unitStr.length() > Category.getMaxUnitNameLength()){
                    Toast.makeText(getApplicationContext(), "The maximum length of Unit is " + Category.getMaxUnitNameLength() + " characters", Toast.LENGTH_LONG).show();
                    return true;
                }

                defaultValueStr = defaultValueEditText.getText().toString();

                if(defaultValueStr == null || defaultValueStr.equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), "Default value has to be specified if the record type is numeric", Toast.LENGTH_LONG).show();
                    return true;
                }else if(!NumberUtils.isCreatable(defaultValueStr)){
                    Toast.makeText(getApplicationContext(), "Default value has to be numeric value", Toast.LENGTH_LONG).show();
                    return true;
                }
                else if(!(0 <= Long.parseLong(defaultValueStr) && Long.parseLong(defaultValueStr) <= category.getMaxValue())){
                    Toast.makeText(getApplicationContext(), "Default value has to be between 0 to " + category.getMaxValue(), Toast.LENGTH_LONG).show();
                    return true;
                }

            }
            else{
                unitStr="";
                defaultValueStr="";
            }

            //같은 이름의 카테고리가 있는지 확인

            // DB 신규 저장 or 업데이트 처리
            realm.beginTransaction();
            if(categoryMode == StaticData.CATEGORY_MODE_CREATE) {
                category = realm.createObject(Category.class, UUID.randomUUID().toString());
            }
//            category = new Category("", "", categoryOrder, StaticData.RECORD_TYPE_BOOLEAN);
            category.setName(categoryNameStr);
            category.setUnit(unitStr);

            if(defaultValueStr == null || defaultValueStr.equalsIgnoreCase("")){
                category.setDefaultValue(0);
            }else {
                category.setDefaultValue(Long.parseLong(defaultValueStr));
            }
            category.setRecordType(recordType);
            if(categoryMode == StaticData.CATEGORY_MODE_CREATE){
                category.setOrder(categoryOrder);
            }
            else if(categoryMode == StaticData.CATEGORY_MODE_EDIT){

            }

            realm.insertOrUpdate(category);
            realm.commitTransaction();
            setResult(RESULT_OK);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
