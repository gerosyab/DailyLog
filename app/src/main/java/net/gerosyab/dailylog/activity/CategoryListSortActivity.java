package net.gerosyab.dailylog.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragListView;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Category;
import net.gerosyab.dailylog.data.StaticData;
import net.gerosyab.dailylog.util.MyLog;

import java.util.ArrayList;

import io.realm.RealmResults;

public class CategoryListSortActivity extends SuperActivity {
    private static final String LOG_TAG = "CategoryListSort";
    private static RealmResults<Category> categories;
    private DragListView mDragListView;
    private ArrayList<CategoryHolder> categoryHolderList;
    private boolean isDraged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list_sort);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        ab.setTitle("Ordering categories");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setVisibility(View.GONE);

        mDragListView = (DragListView) findViewById(R.id.listView);
        mDragListView.setLayoutManager(new LinearLayoutManager(context));
        mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mDragListView.setDragEnabled(true);
        mDragListView.setCanDragHorizontally(false);

        mDragListView.setDragListListener(new DragListView.DragListListener() {
            @Override
            public void onItemDragStarted(int position) {
                isDraged = true;
            }

            @Override
            public void onItemDragging(int itemPosition, float x, float y) {

            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                // Toast.makeText(context, "onItemDragEnded() - fromPostion : " + fromPosition + ", toPosition : " + toPosition, Toast.LENGTH_LONG).show();
                MyLog.d(LOG_TAG, "onItemDragEnded() - fromPostion : " + fromPosition + ", toPosition : " + toPosition);

                if(fromPosition < toPosition) {
                    for (int i = fromPosition; i <= toPosition; i++) {
                        CategoryHolder category = categoryHolderList.get(i);
                        category.setNewOrder(i);
                        MyLog.d(LOG_TAG, "onItemDragEnded() - i : " + i + ", name : " + category.getName() + ", order : " + category.getOrder() + " -> " + category.getNewOrder());
                    }
                }
                else if(toPosition < fromPosition){
                    for (int i = fromPosition; i >= toPosition; i--){
                        CategoryHolder category = categoryHolderList.get(i);
                        category.setNewOrder(i);
                        MyLog.d(LOG_TAG, "onItemDragEnded() - i : " + i + ", name : " + category.getName() + ", order : " + category.getOrder() + " -> " + category.getNewOrder());
                    }
                }
                mDragListView.getAdapter().notifyDataSetChanged();
            }
        });

        categories = Category.getCategories(realm);
        setCategoryHolderList();
        ItemAdapter listAdapter = new ItemAdapter(categoryHolderList, R.layout.simple_list_item_2, R.id.list_drag_holder, false);
        mDragListView.setAdapter(listAdapter, true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isDraged) {
            // apply new order
            setCategories();
            setResult(RESULT_OK);
        }
    }

    public class ItemAdapter extends DragItemAdapter<CategoryHolder, ItemAdapter.ViewHolder> {

        private int mLayoutId;
        private int mGrabHandleId;
        private boolean mDragOnLongPress;

        public ItemAdapter(ArrayList<CategoryHolder> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
            mLayoutId = layoutId;
            mGrabHandleId = grabHandleId;
            mDragOnLongPress = dragOnLongPress;
            setItemList(list);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            String text = mItemList.get(position).getName();
            holder.mText.setText(text + " ( Type : " + StaticData.RECORD_TYPE_NAME[(int) mItemList.get(position).getRecordType()] + " )");
            holder.itemView.setTag(mItemList.get(position));
        }

        @Override
        public long getUniqueItemId(int position) {
//            MyLog.d(LOG_TAG, "getUniqueItemId() - position : " + position + ", name : " + mItemList.get(position).getName() + ", order : " + mItemList.get(position).getOrder());
            return mItemList.get(position).getOrder();
        }

        class ViewHolder extends DragItemAdapter.ViewHolder {
            TextView mText;

            ViewHolder(final View itemView) {
                super(itemView, mGrabHandleId, mDragOnLongPress);
                mText = (TextView) itemView.findViewById(R.id.text1);
            }

            @Override
            public void onItemClicked(View view) {

            }

            @Override
            public boolean onItemLongClicked(View view) {
                return true;
            }
        }
    }

    private class CategoryHolder {
        public String categoryId;
        public String name;
        public long recordType;
        public long order;
        public long newOrder;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getOrder() {
            return order;
        }

        public void setOrder(long order) {
            this.order = order;
        }

        public long getRecordType() {
            return recordType;
        }

        public void setRecordType(long recordType) {
            this.recordType = recordType;
        }

        public long getNewOrder() {
            return newOrder;
        }

        public void setNewOrder(long newOrder) {
            this.newOrder = newOrder;
        }

        public CategoryHolder(String categoryId, String name, long order, long recordType) {
            this.categoryId = categoryId;
            this.name = name;
            this.order = order;
            this.recordType = recordType;
            this.newOrder = order;
        }

    }

    public CategoryHolder getHolderFromCategory(Category category){
        return new CategoryHolder(category.getCategoryId(), category.getName(), category.getOrder(), category.getRecordType());
    }

    public void setCategoryHolderList(){
        categoryHolderList = new ArrayList<CategoryHolder>(categories.size());
        for(Category category : categories) {
            categoryHolderList.add(new CategoryHolder(category.getCategoryId(), category.getName(), category.getOrder(), category.getRecordType()));
        }
    }

    public void setCategories(){
        for(CategoryHolder c : categoryHolderList){
            realm.beginTransaction();
            Category category = Category.getCategory(realm, c.categoryId);
            category.setOrder(c.getNewOrder());
            realm.insertOrUpdate(category);
            realm.commitTransaction();
        }
    }
}
