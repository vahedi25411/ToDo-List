package com.sargent.mark.todolist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.annotation.IntegerRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.sargent.mark.todolist.data.Contract;
import com.sargent.mark.todolist.data.ToDoItem;

import java.util.ArrayList;

/**
 * Created by mark on 7/4/17.
 */

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ItemHolder> {

    private Cursor cursor;
    private ItemClickListener listener;
    private String TAG = "todolistadapter";

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public interface ItemClickListener {
        void onItemClick(int pos, String description, String duedate, int category, boolean isDone, long id);
    }

    public ToDoListAdapter(Cursor cursor, ItemClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    public void swapCursor(Cursor newCursor){
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView descr;
        TextView due;
        //ImageView to show check image near the finished tasks
        ImageView img;
        String duedate;
        String description;
        long id;


        ItemHolder(View view) {
            super(view);
            descr = (TextView) view.findViewById(R.id.description);
            due = (TextView) view.findViewById(R.id.dueDate);
            //assign the reference to ImageView of the layout to img variable
            img = (ImageView) view.findViewById(R.id.check_image);
            view.setOnClickListener(this);
        }

        public void bind(ItemHolder holder, int pos) {
            cursor.moveToPosition(pos);
            id = cursor.getLong(cursor.getColumnIndex(Contract.TABLE_TODO._ID));
            Log.d(TAG, "deleting id: " + id);

            duedate = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE));
            description = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION));
            descr.setText(description);
            due.setText(duedate);

            // Check if todoStatus is 'done' , the background color of view item is going to change
            if (cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_IS_DONE))==1) {
                holder.itemView.setBackgroundColor(Color.parseColor("#EFC8EB"));
                //This line makes the check image visible when a task has been done
                img.setVisibility(View.VISIBLE);
            }
            else {
                //This line makes the check image invisible when a task has not been done
                img.setVisibility(View.INVISIBLE);
                //otherwise the background color will be removedss
                holder.itemView.setBackgroundColor(0);
            }
            holder.itemView.setTag(id);

            //Keeping the value of the category and status of the item in holder to use it fill the update fragment
            holder.itemView.setTag(R.id.categoryData, cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY)));
            holder.itemView.setTag(R.id.todoStatusData, cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_IS_DONE)));
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();

            //Put toDoItem's status to a bool variable to use easily in the code
            boolean status = (Integer.parseInt(v.getTag(R.id.todoStatusData).toString())!=0);
            //This line has changed to add intput parameters for two new patameters category and status
            listener.onItemClick(pos, description, duedate, Integer.parseInt(v.getTag(R.id.categoryData).toString()), status , id);
        }
    }

}
