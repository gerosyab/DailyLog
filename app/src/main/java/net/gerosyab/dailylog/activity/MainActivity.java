package net.gerosyab.dailylog.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.StaticData;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static List<Category> categories;
    ListView listView;
    ArrayAdapter<String> adapter;
    final static int REQUEST_CODE_CATEGORY_ACTIVITY_CREATE = 123;
    final static int REQUEST_CODE_CATEGORY_ACTIVITY_EDIT = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        toolbar.setNavigationIcon(R.drawable.ic_date_range_white_24dp);

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
        builder.setMessage(getResources().getString(R.string.dialog_message_confirm_delete) + " \"" + categories.get((int)id).getName() + "\"")
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
}
