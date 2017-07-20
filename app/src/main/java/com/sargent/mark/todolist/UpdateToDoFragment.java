package com.sargent.mark.todolist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

/**
 * Created by mark on 7/5/17.
 */

public class UpdateToDoFragment extends DialogFragment {

    private EditText toDo;
    private DatePicker dp;
    private Button add;
    //Add spinner to use in add and update fragment
    private Spinner spinner;
    //Add checkbox to use in add and update fragment to get the todoItem status
    private CheckBox isDoneCheckbox;
    private final String TAG = "updatetodofragment";
    private long id;


    public UpdateToDoFragment(){}

    public static UpdateToDoFragment newInstance(int year, int month, int day, String descrpition, int category, boolean status, long id) {
        UpdateToDoFragment f = new UpdateToDoFragment();

        int isDone = (status) ? 1 : 0;
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        args.putLong("id", id);
        args.putString("description", descrpition);
        // put category and isDone parameter values in bundle to send to UpdateFragment
        args.putInt("category", category);
        args.putInt("isDone", isDone);

        f.setArguments(args);

        return f;
    }

    //To have a way for the activity to get the data from the dialog
    public interface OnUpdateDialogCloseListener {
        //Changed to have category and status input parameters
        void closeUpdateDialog(int year, int month, int day, String description, int category, boolean status, long id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_adder, container, false);
        toDo = (EditText) view.findViewById(R.id.toDo);
        dp = (DatePicker) view.findViewById(R.id.datePicker);
        add = (Button) view.findViewById(R.id.add);
        //Make object from spinner and checkbox of the view
        spinner = (Spinner) view.findViewById(R.id.toDoCategory);
        isDoneCheckbox = (CheckBox) view.findViewById(R.id.isDone);

        //Create an ArrayAdapter to use for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this.getContext() ,
                R.array.categories_array , R.layout.support_simple_spinner_dropdown_item);
        //Set the spinner item's layout in adapter
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        //Set the spinner's adapter
        spinner.setAdapter(adapter);



        int year = getArguments().getInt("year");
        int month = getArguments().getInt("month");
        int day = getArguments().getInt("day");
        id = getArguments().getLong("id");
        String description = getArguments().getString("description");

        //Fill spinner and checkbox using the values in Bundle
        spinner.setSelection(getArguments().getInt("category"));
        boolean isDone = (getArguments().getInt("isDone") == 1);
        isDoneCheckbox.setChecked(isDone);


        dp.updateDate(year, month, day);

        toDo.setText(description);

        add.setText("Update");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateToDoFragment.OnUpdateDialogCloseListener activity = (UpdateToDoFragment.OnUpdateDialogCloseListener) getActivity();
                Log.d(TAG, "id: " + id);
                //Input parameters changed to send spinner and checkbox values as well
                activity.closeUpdateDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), toDo.getText().toString(), spinner.getSelectedItemPosition(), isDoneCheckbox.isChecked(), id);
                UpdateToDoFragment.this.dismiss();
            }
        });

        return view;
    }
}