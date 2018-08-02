package net.gerosyab.dailylog.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.Record;
import net.gerosyab.dailylog.data.StaticData;
import net.gerosyab.dailylog.database.AppDatabase;
import net.gerosyab.dailylog.util.MyLog;
import net.gerosyab.dailylog.util.Util;

import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class MainActivity extends SuperActivity {

    private static final String LOG_TAG = "MainActivity";
    private static RealmResults<Category> categories;
    ListView listView;
    MyListAdapter adapter;

    final static int REQUEST_CODE_CATEGORY_ACTIVITY_CREATE = 123;
    final static int REQUEST_CODE_CATEGORY_ACTIVITY_EDIT = 456;
    final static int REQUEST_CODE_CATEGORY_LIST_SORT = 789;
    FilePickerDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.listView);

        categories = Category.getCategories(realm);
        adapter = new MyListAdapter(categories);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra(StaticData.CATEGORY_ID_INTENT_EXTRA, categories.get((int) id).getCategoryId());
                startActivity(intent);
            }
        });

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_home_white_24dp);

//        refreshList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory();
            }
        });

    }

    public class MyListAdapter extends RealmBaseAdapter<Category> implements ListAdapter {

        private class ViewHolder {
            TextView categoryTypeText;
            TextView categoryNameText;
        }

        MyListAdapter(OrderedRealmCollection<Category> realmResults) {
            super(realmResults);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item_1, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.categoryTypeText = (TextView) convertView.findViewById(R.id.type_text);
                viewHolder.categoryNameText = (TextView) convertView.findViewById(R.id.title_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (adapterData != null) {
                final Category category = adapterData.get(position);
                viewHolder.categoryNameText.setText(category.getName());
                viewHolder.categoryTypeText.setText(StaticData.RECORD_TYPE_NAME[(int) category.getRecordType()]);
            }
            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_CATEGORY_ACTIVITY_CREATE || requestCode == REQUEST_CODE_CATEGORY_ACTIVITY_EDIT || requestCode == REQUEST_CODE_CATEGORY_LIST_SORT){
            if(resultCode == RESULT_OK){
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_license) {
            Intent intent = new Intent(getApplicationContext(), RawTextViewActivity.class);
            intent.putExtra(StaticData.RAW_TEXT_INTENT_EXTRA, RawTextViewActivity.LICENSE);
            startActivity(intent);
        }
        else if (id == R.id.action_info) {
            Intent intent = new Intent(getApplicationContext(), RawTextViewActivity.class);
            intent.putExtra(StaticData.RAW_TEXT_INTENT_EXTRA, RawTextViewActivity.INFORMATION);
            startActivity(intent);
        }
        else if (id == R.id.action_import){
            //파일 선택 다이얼로그 호출
            //선택된 파일을 검사 후 형식이 정상적이면 새로운 카테고리 생성해 db 에 저장
            importCategory();
        }
        else if (id == R.id.action_order_category){
            //카테고리 정렬 액티비티 호출
            Intent intent = new Intent(context, CategoryListSortActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CATEGORY_LIST_SORT);
        }
        /* else if (id == R.id.action_make_test_data){
            // 카테고리 소팅용 테스트 데이터 생성
            for(Category category : categories){
                category.deleteCategory(realm);
            }
            String[] names = {"A", "B", "C", "D", "E", "F"};
            for(int i = 0; i < names.length; i++) {
                realm.beginTransaction();
                Category category = realm.createObject(Category.class, UUID.randomUUID().toString());
                category.setName(names[i]);
                category.setUnit("a");

                category.setDefaultValue(0);
                category.setRecordType(StaticData.RECORD_TYPE_BOOLEAN);
                category.setOrder(i);
                realm.insertOrUpdate(category);
                realm.commitTransaction();
            }
            String cateUUID = UUID.randomUUID().toString();
            realm.beginTransaction();
            Category category = realm.createObject(Category.class, cateUUID);
            category.setName("NUM");
            category.setUnit("a");
            category.setDefaultValue(1);
            category.setRecordType(StaticData.RECORD_TYPE_NUMBER);
            category.setOrder(6);
            realm.insertOrUpdate(category);
            realm.commitTransaction();

            realm.beginTransaction();
            DateTime dateTime = DateTime.now();


            for(int i = 0; i < 10000; i++){
                DateTime dt = dateTime.withTimeAtStartOfDay().minusDays(i);
                Record record = new Record();
                record = realm.createObject(Record.class, UUID.randomUUID().toString());
                record.setCategoryId(cateUUID);
                record.setRecordType(StaticData.RECORD_TYPE_NUMBER);
                record.setNumber((long) Math.random() * 10);
                record.setDate(new java.sql.Date(dt.toDate().getTime()));
            }
            realm.commitTransaction();
            adapter.notifyDataSetChanged();
        } */

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MyLog.d(LOG_TAG, "onCreateContextMenu() - menuInfo : " + menuInfo.toString());
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        MyLog.d(LOG_TAG, "onContextItemSelected() - item : " + item.toString());
        switch (item.getItemId()) {
            case R.id.action_edit:
                editCategory(info.id);
                return true;
            case R.id.action_delete:
                deleteCategory(info.id);
                return true;
            case R.id.action_export:
                exportCategory(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    private void addCategory(){
        Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
        intent.putExtra(StaticData.CATEGORY_MODE_INTENT_EXTRA, StaticData.CATEGORY_MODE_CREATE);
        startActivityForResult(intent, REQUEST_CODE_CATEGORY_ACTIVITY_CREATE);
    }

    private void editCategory(long id){
        Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
        intent.putExtra(StaticData.CATEGORY_MODE_INTENT_EXTRA, StaticData.CATEGORY_MODE_EDIT);
        intent.putExtra(StaticData.CATEGORY_ID_INTENT_EXTRA, categories.get((int) id).getCategoryId());
        startActivityForResult(intent, REQUEST_CODE_CATEGORY_ACTIVITY_EDIT);
    }

    private void deleteCategory(final long id){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getResources().getString(R.string.dialog_message_confirm_delete_category) + " [" + categories.get((int)id).getName() + "]")
        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categories.get((int) id).deleteCategory(realm);
                adapter.notifyDataSetChanged();
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void exportCategory(final long id){

        //선택된 카테고리의 데이터를 csv 형태로 출력
        //출력된 파일을 공유하도록 처리
        //공유완료 혹은 취소된 경우 출련된 파일 삭제
        Category category = categories.get((int) id);

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Exporting data [" + category.getName() + "]");
        progressDialog.show();

        String filename = category.getName() + "" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".data";
        FileOutputStream outputStream = null;
        File resultFilePath = null;
        File resultFile = null;
        CSVWriter cw = null;

        try {
            resultFile = new File(context.getCacheDir(), filename);

            outputStream = new FileOutputStream(resultFile.getAbsolutePath());
//            cw = new CSVWriter(new OutputStreamWriter(outputStream, "UTF-8"),'\t', '"');
            cw = new CSVWriter(new OutputStreamWriter(outputStream, "UTF-8"),',', '"');

            // Export Data
            String[] metaDataStr = {"Version:" + AppDatabase.VERSION,
                                    "Name:" + category.getName(),
                                    "Unit:" + category.getUnit(),
                                    "Type:" + category.getRecordType(),
                                    "DefaultValue:" + category.getDefaultValue(),
                                    "Columns:date(yyyy-MM-dd 24HH:mm:ss)/value(boolean|numeric|string)"};
            cw.writeNext(metaDataStr);

            List<Record> records = category.getRecordsOrderByDateAscending(realm);
            for(Record record:records){
                String value = null;
                if(category.getRecordType() == StaticData.RECORD_TYPE_BOOLEAN){
                    value = "true";
                }
                else if(category.getRecordType() == StaticData.RECORD_TYPE_NUMBER){
                    value = ""+record.getNumber();
                }
                else if(category.getRecordType() == StaticData.RECORD_TYPE_MEMO){
                    value = record.getString();
                }
                String[] s = {record.getDateString(StaticData.fmtForBackup), value};
                cw.writeNext(s);
            }

            cw.close();
            outputStream.close();

            progressDialog.dismiss();

            Uri fileUri = FileProvider.getUriForFile(context, "net.gerosyab.dailylog.fileprovider",resultFile);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.setType("text/plain");

            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));

        } catch (UnsupportedEncodingException e) {
            Log.e("DailyLog", e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Log.e("DailyLog", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("DailyLog", e.getMessage());
            e.printStackTrace();
        } finally {
            progressDialog.dismiss();
        }
    }

    private void importCategory() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        dialog = new FilePickerDialog(MainActivity.this, properties);

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {

                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("Importing data [" + files[0] + "]");
                progressDialog.show();

                //files is the array of the paths of files selected by the Application User.
                try {
                    String newCategoryUUID = UUID.randomUUID().toString();
//                    CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(files[0]), "UTF-8"), '\t');
                    CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(files[0]), "UTF-8"), ',');
                    String [] nextLine;
                    long lineNum = 0;

                    boolean dbVersionCheck = false, categoryNameCheck = false, categoryUnitCheck = false;
                    boolean categoryTypeCheck = false, defaultValueCheck = false, headerCheck = false;
                    boolean isCategoryNameExists = false, rowDataCheck = false, dataCheck = false;
                    String dbVersion = null, categoryName = null, categoryUnit = null;
                    long categoryType = -1, defaultValue = -1;

//                    Category category = null;
                    List<Record> records = new ArrayList<Record>();

                    while ((nextLine = reader.readNext()) != null) {
                        // nextLine[] is an array of values from the line
                        String temp2 = "";
                        for (int i = 0; i < nextLine.length; i++) {
                            temp2 += nextLine[i] + ", ";
                        }
                        MyLog.d("opencsv", temp2);

                        if(lineNum == 0){
                            //header
                            MyLog.d("opencsv", "nextLine.length : " + nextLine.length);
                            for (int i = 0; i < nextLine.length; i++) {
                                String[] split2 = nextLine[i].split(":");
                                MyLog.d("opencsv", "split2.length : " + split2.length);
                                MyLog.d("opencsv", "split2[0] : " + split2[0]);
                                if(split2[0].replaceAll("\\s+","").equals("Version")){
                                    if(split2[1] != null && !split2[1].equals("")){
                                        dbVersion = split2[1];
                                        dbVersionCheck = true;
                                    }
                                }
                                else if(split2[0].replaceAll("\\s+","").equals("Name")){
                                    if(split2[1] != null && !split2[1].equals("")){
                                        categoryName = split2[1];
                                        if(categoryName.length() <= Category.getMaxCategoryNameLength()) {
                                            categoryNameCheck = true;
                                        }
                                    }
                                }
                                else if(split2[0].replaceAll("\\s+","").equals("Unit")){
                                    if(split2.length == 1){
                                        categoryUnitCheck = true;
                                    }else if(split2[1] != null && !split2[1].equals("")){
                                        categoryUnit = split2[1];
                                        categoryUnitCheck = true;
                                    }
                                }
                                else if(split2[0].replaceAll("\\s+","").equals("Type")){
                                    if(split2[1] != null && !split2[1].equals("")){
                                        if(split2[1].equals("0") || split2[1].equals("1") || split2[1].equals("2")){
                                            categoryType = Long.parseLong(split2[1]);
                                            categoryTypeCheck = true;
                                        }
                                    }
                                }
                                else if(split2[0].replaceAll("\\s+","").equals("DefaultValue")){
                                    if(split2.length == 1){
                                        defaultValueCheck = true;
                                    }else if(split2[1] != null && !split2[1].equals("")){
                                        if(NumberUtils.isDigits(split2[1]) && NumberUtils.isNumber(split2[1])){
                                            defaultValue = Long.parseLong(split2[1]);
                                            MyLog.d("opencsv", "split2[1] : " + split2[1]);
                                            if(defaultValue >= 0){
                                                defaultValueCheck = true;
                                            }
                                        }
                                    }
                                }
                            }

                            if(dbVersionCheck && categoryNameCheck &&categoryTypeCheck) {
                                if (categoryType == 1) {
                                    //number
                                    if (categoryUnitCheck && defaultValueCheck) {
                                        headerCheck = true;
                                    }
                                } else {
                                    //boolean, string
                                    headerCheck = true;
                                }
                            }

                            if(!headerCheck){
                                break; //header parsing, data checking failed
                            }
                            else if (Category.isCategoryNameExists(realm, categoryName)){
                                isCategoryNameExists = true;
                                break;
                            }
                            else{
//                                category = new Category(categoryName, categoryUnit, Category.getLastOrderNum(realm), categoryType);
                            }
                        }
                        else{
                            //data
                            Record record;
                            rowDataCheck = true;
                            if(nextLine.length != 2){
                                rowDataCheck = false;
                                break;
                            }else {
                                record = new Record();
                                record.setRecordType(categoryType);
                                DateTime dateTime = StaticData.fmtForBackup.parseDateTime(nextLine[0]);
                                if (dateTime != null) {
                                    record.setDate(new java.sql.Date(dateTime.toDate().getTime()));
                                } else {
                                    rowDataCheck = false;
                                    break;
                                }

                                if (categoryType == StaticData.RECORD_TYPE_BOOLEAN) {
                                    if (nextLine[1].equals("true")) {
                                        record.setBool(true);
                                    } else {
                                        rowDataCheck = false;
                                        break;
                                    }
                                } else if (categoryType == StaticData.RECORD_TYPE_NUMBER) {
                                    if(NumberUtils.isNumber(nextLine[1]) && NumberUtils.isDigits(nextLine[1])) {
                                        long value = Long.parseLong(nextLine[1]);
                                        if(value > Category.getMaxValue()){
                                            rowDataCheck = false;
                                            break;
                                        }
                                        else{
                                            record.setNumber(value);
                                        }
                                    } else{
                                        dataCheck = false;
                                        break;
                                    }

                                } else if (categoryType == StaticData.RECORD_TYPE_MEMO) {
                                    String value = nextLine[1];
                                    if (value.length() > Category.getMaxMemoLength()) {
                                        rowDataCheck = false;
                                        break;
                                    }
                                    else{
                                        record.setString(nextLine[1]);
                                    }
                                }
                            }
                            if(record != null && rowDataCheck == true){
                                record.setCategoryId(newCategoryUUID);
                                record.setRecordId(UUID.randomUUID().toString());
                                records.add(record);
                            }
                        }
                        lineNum++;
                    }

                    if(!headerCheck){
                        Toast.makeText(context, "Data file's header context/value is not correct", Toast.LENGTH_LONG).show();
                    }
                    else if(isCategoryNameExists){
                        Toast.makeText(context, "Category Name [" + categoryName + "] is already exists", Toast.LENGTH_LONG).show();
                    }
                    else if(!rowDataCheck){
                        Toast.makeText(context, "Data file's record data is not correct", Toast.LENGTH_LONG).show();
                    }
                    else{
                        long newOrder = Category.getNewOrderNum(realm);
                        realm.beginTransaction();
                        Category category = realm.createObject(Category.class, newCategoryUUID);
                        category.setName(categoryName);
                        category.setUnit(categoryUnit);
                        category.setOrder(newOrder);
                        category.setRecordType(categoryType);
                        realm.insert(category);
                        realm.insertOrUpdate(records);
                        realm.commitTransaction();
                        Toast.makeText(context, "Data import [ " + categoryName + " ] is completed!!", Toast.LENGTH_LONG).show();
//                        refreshList();
//                        categoryHolderList.add(getHolderFromCategory(category));
//                        mDragListView.getAdapter().notifyDataSetChanged();
//                        mDragListView.getAdapter().notifyItemInserted((int) newOrder);
//                        setCategoryHolderList();
//                        mDragListView.getAdapter().setItemList(categoryHolderList);
                        adapter.notifyDataSetChanged();
                    }
                } catch (FileNotFoundException e) {
                    Toast.makeText(context, "Selected file is not found", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    Toast.makeText(context, "File I/O exception occured", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    //Add this method to show Dialog when the required permission has been granted to the app.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (dialog != null) {   //Show dialog if the read permission has been granted.
                        dialog.show();
                    }
                } else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(MainActivity.this, "Permission is Required for getting list of files", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}
