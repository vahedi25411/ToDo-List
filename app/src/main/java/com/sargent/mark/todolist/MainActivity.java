package com.sargent.mark.todolist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.sargent.mark.todolist.data.Contract;
import com.sargent.mark.todolist.data.DBHelper;

public class MainActivity extends AppCompatActivity implements AddToDoFragment.OnDialogCloseListener, UpdateToDoFragment.OnUpdateDialogCloseListener{

    private RecyclerView rv;
    private FloatingActionButton button;
    private DBHelper helper;
    private Cursor cursor;
    private SQLiteDatabase db;
    ToDoListAdapter adapter;
    private final String TAG = "mainactivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "oncreate called in main activity");
        button = (FloatingActionButton) findViewById(R.id.addToDo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                AddToDoFragment frag = new AddToDoFragment();
                frag.show(fm, "addtodofragment");
            }
        });
        rv = (RecyclerView) findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (db != null) db.close();
        if (cursor != null) cursor.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        cursor = getAllItems(db);

        adapter = new ToDoListAdapter(cursor, new ToDoListAdapter.ItemClickListener() {

            @Override
            public void onItemClick(int pos, String description, String duedate, int category, boolean isDone, long id) {
                Log.d(TAG, "item click id: " + id);
                String[] dateInfo = duedate.split("-");
                int year = Integer.parseInt(dateInfo[0].replaceAll("\\s",""));
                int month = Integer.parseInt(dateInfo[1].replaceAll("\\s",""));
                int day = Integer.parseInt(dateInfo[2].replaceAll("\\s",""));

                FragmentManager fm = getSupportFragmentManager();

                UpdateToDoFragment frag = UpdateToDoFragment.newInstance(year, month, day, description, category, isDone , id);
                frag.show(fm, "updatetodofragment");
            }
        });

        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                Log.d(TAG, "passing id: " + id);
                removeToDo(db, id);
                adapter.swapCursor(getAllItems(db));
            }
        }).attachToRecyclerView(rv);
    }

    @Override
    public void closeDialog(int year, int month, int day, String description, int category, boolean status) {
        addToDo(db, description, formatDate(year, month, day), category, status);
        cursor = getAllItems(db);
        adapter.swapCursor(cursor);
    }

    public String formatDate(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }



    private Cursor getAllItems(SQLiteDatabase db) {
        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }

    private long addToDo(SQLiteDatabase db, String description, String duedate, int category, boolean status) {
        ContentValues cv = new ContentValues();

        int isDone = (status) ? 1 : 0;
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY, category);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_IS_DONE, isDone);

        return db.insert(Contract.TABLE_TODO.TABLE_NAME, null, cv);
    }

    private boolean removeToDo(SQLiteDatabase db, long id) {
        Log.d(TAG, "deleting id: " + id);
        return db.delete(Contract.TABLE_TODO.TABLE_NAME, Contract.TABLE_TODO._ID + "=" + id, null) > 0;
    }


    private int updateToDo(SQLiteDatabase db, int year, int month, int day, String description, int category, boolean status, long id){

        String duedate = formatDate(year, month - 1, day);

        //Since we don't have boolean data type in sqlite we have to save data as integer and convert it to boolean in code as needed
        //In this line I have changed the boolean data to integer to make it ready to insert database
        int isDone = (status) ? 1 : 0;

        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);

        //add two new parameters to ContentValue object to use them to update database
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY, category);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_IS_DONE, isDone);

        return db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
    }

    //The input parameters of this method have changed to cover two new ToDoItem's properties
    @Override
    public void closeUpdateDialog(int year, int month, int day, String description, int category, boolean status, long id) {
        updateToDo(db, year, month, day, description, category, status, id);
        adapter.swapCursor(getAllItems(db));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Reading the string array from resources which has been used for making predefined categories
        String[] titles = getResources().getStringArray(R.array.categories_array);

        //put 'All' as the first and default value for menu
        menu.add(1,-1,0,"All");

        //Reading all the strings in string array and put them in menu
        for (int i=0;i<titles.length;i++){
            menu.add(1,i,i+1,titles[i]);
        }

        //Inflate menu with generated items
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //check the selected menu item and change recyclerview's data according to the selected menu
        if (item.getItemId()==-1)
            cursor = getAllItems(db);
        else
            cursor = getSpecificCategoryItems(db,item.getItemId());

        //fill the adapter with the changed data using the cursor which created in above lines
        adapter.swapCursor(cursor);
        return true;
    }

    // in this method according to the specified category, appropriate query is generated and the result Cursor will return
    private Cursor getSpecificCategoryItems(SQLiteDatabase db, int categoryId) {

        String[] args = { String.valueOf(categoryId) };


        return db.query(Contract.TABLE_TODO.TABLE_NAME,
                null,
                "category = ?",
                args,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE);
    }
}

