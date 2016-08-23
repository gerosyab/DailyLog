package net.gerosyab.dailylog.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.Category_Table;
import net.gerosyab.dailylog.data.Record;
import net.gerosyab.dailylog.data.StaticData;
import net.gerosyab.dailylog.database.AppDatabase;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Context context;
    private static List<Category> categories;
    ListView listView;
    ArrayAdapter<String> adapter;
    final static int REQUEST_CODE_CATEGORY_ACTIVITY_CREATE = 123;
    final static int REQUEST_CODE_CATEGORY_ACTIVITY_EDIT = 456;

    FilePickerDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra(StaticData.CATEGORY_ID_INTENT_EXTRA, categories.get((int) id).getId());
                startActivity(intent);
            }
        });

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_home_white_24dp);

        refreshList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_CATEGORY_ACTIVITY_CREATE || requestCode == REQUEST_CODE_CATEGORY_ACTIVITY_EDIT){
            if(resultCode == RESULT_OK){
                refreshList();
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
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_import){
            //파일 선택 다이얼로그 호출
            //선택된 파일을 검사 후 형식이 정상적이면 새로운 카테고리 생성해 db 에 저장
            importCategory();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
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


    private void refreshList(){
        categories = SQLite.select()
                .from(Category.class)
                .where()
                .queryList();

        adapter.clear();

        for (Category category : categories){
            adapter.add(category.getName());
        }
        adapter.notifyDataSetChanged();
    }

    private void addCategory(){
        Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
        intent.putExtra(StaticData.CATEGORY_MODE_INTENT_EXTRA, StaticData.CATEGORY_MODE_CREATE);
        long order;
        if(categories == null || categories.size() == 0){
            order = 0;
        }
        else{
            order = categories.size() + 1;
        }
        intent.putExtra(StaticData.CATEGORY_ORDER_INTENT_EXTRA, order);
        startActivityForResult(intent, REQUEST_CODE_CATEGORY_ACTIVITY_CREATE);
    }

    private void editCategory(long id){
        Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
        intent.putExtra(StaticData.CATEGORY_MODE_INTENT_EXTRA, StaticData.CATEGORY_MODE_EDIT);
        intent.putExtra(StaticData.CATEGORY_ID_INTENT_EXTRA, categories.get((int) id).getId());
        startActivityForResult(intent, REQUEST_CODE_CATEGORY_ACTIVITY_EDIT);
    }

    private void deleteCategory(final long id){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getResources().getString(R.string.dialog_message_confirm_delete_category) + " [" + categories.get((int)id).getName() + "]")
        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categories.get((int)id).delete();
                refreshList();
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

        String filename = category.getName() + "" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".dat";
        FileOutputStream outputStream = null;
        File resultFilePath = null;
        File resultFile = null;
        CSVWriter cw = null;

        try {
            resultFile = new File(context.getCacheDir(), filename);

            outputStream = new FileOutputStream(resultFile.getAbsolutePath());
            cw = new CSVWriter(new OutputStreamWriter(outputStream, "UTF-8"),'\t');

            // Export Data
            String[] metaDataStr = {"#Version:" + AppDatabase.VERSION,
                                    "#Name:" + category.getName(),
                                    "#Unit:" + category.getUnit(),
                                    "#Type:" + category.getRecordType(),
                                    "#DefaultValue:" + category.getDefaultValue(),
                                    "#Columns:date(yyyy-MM-dd 24HH:mm:ss)/value(boolean|numeric|string)"};
            cw.writeNext(metaDataStr);

            List<Record> records = category.getRecordsOrderByIdAscending();
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
                    CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(files[0]), "UTF-8"), '\t');
                    String [] nextLine;
                    long lineNum = 0;

                    boolean dbVersionCheck = false, categoryNameCheck = false, categoryUnitCheck = false;
                    boolean categoryTypeCheck = false, defaultValueCheck = false, headerCheck = false;
                    boolean isCategoryNameExists = false, dataCheck = false;
                    String dbVersion = null, categoryName = null, categoryUnit = null;
                    long categoryType = -1, defaultValue = -1;

                    Category category = null;

                    while ((nextLine = reader.readNext()) != null) {
                        // nextLine[] is an array of values from the line
                        String temp2 = "";
                        for (int i = 0; i < nextLine.length; i++) {
                            temp2 += nextLine[i] + ", ";
                        }
                        Log.d("opencsv", temp2);

                        if(lineNum == 0){
                            //header

                            Log.d("opencsv", "nextLine.length : " + nextLine.length);
                            for (int i = 0; i < nextLine.length; i++) {
                                String[] split2 = nextLine[i].split(":");
                                Log.d("opencsv", "split2.length : " + split2.length);
                                Log.d("opencsv", "split2[0] : " + split2[0]);
                                if(split2[0].replaceAll("\\s+","").equals("#Version")){
                                    if(split2[1] != null && !split2[1].equals("")){
                                        dbVersion = split2[1];
                                        dbVersionCheck = true;
                                    }
                                }
                                else if(split2[0].replaceAll("\\s+","").equals("#Name")){
                                    if(split2[1] != null && !split2[1].equals("")){
                                        categoryName = split2[1];
                                        if(categoryName.length() <= Category.getMaxNameLength()) {
                                            categoryNameCheck = true;
                                        }
                                    }
                                }
                                else if(split2[0].replaceAll("\\s+","").equals("#Unit")){
                                    if(split2[1] != null && !split2[1].equals("")){
                                        categoryUnit = split2[1];
                                        categoryUnitCheck = true;
                                    }
                                }
                                else if(split2[0].replaceAll("\\s+","").equals("#Type")){
                                    if(split2[1] != null && !split2[1].equals("")){
                                        if(split2[1].equals("0") || split2[1].equals("1") || split2[1].equals("2")){
                                            categoryType = Long.parseLong(split2[1]);
                                            categoryTypeCheck = true;
                                        }
                                    }
                                }
                                else if(split2[0].replaceAll("\\s+","").equals("#DefaultValue")){
                                    if(split2[1] != null && !split2[1].equals("")){
                                        if(NumberUtils.isDigits(split2[1]) && NumberUtils.isNumber(split2[1])){
                                            defaultValue = Long.parseLong(split2[1]);
                                            Log.d("opencsv", "split2[1] : " + split2[1]);
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
                            else if (Category.isCategoryNameExists(categoryName)){
                                isCategoryNameExists = true;
                                break;
                            }
                            else{
                                category = new Category(categoryName, categoryUnit, Category.getLastOrderNum(), categoryType);
                            }
                        }
                        else{
                            //data

                        }
                        lineNum++;
                    }

                    if(!headerCheck){
                        Toast.makeText(context, "Data file's header context/value is not correct", Toast.LENGTH_LONG).show();
                    }
                    else if(isCategoryNameExists){
                        Toast.makeText(context, "Category Name [" + categoryName + "] is already exists", Toast.LENGTH_LONG).show();
                    }
                    else if(!dataCheck){
                        Toast.makeText(context, "Data file's record data is not correct", Toast.LENGTH_LONG).show();
                    }
                    else{
                        category.save();
                        List<Record> records = category.getRecords();
                        for(Record record:records){
                            record.save();
                        }
                    }


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
