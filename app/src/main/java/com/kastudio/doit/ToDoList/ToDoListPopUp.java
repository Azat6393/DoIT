package com.kastudio.doit.ToDoList;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.kastudio.doit.DoItContentProvider;
import com.kastudio.doit.MainActivity;
import com.kastudio.doit.R;
import com.kastudio.doit.RemindMe;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ToDoListPopUp extends Fragment implements DatePickerDialog.OnDateSetListener {

    String taskString, uuidString, dateString, noteString, listNameString;
    int check;

    ImageView backButton, delete, remindMeIcon, remindMeCancel;
    TextView listName, date, remindMeTextView, remindMeDate;
    EditText task, note;
    CheckBox checkBox;
    CardView remindMe;

    public static ToDoListPopUp newInstance(){
        return new ToDoListPopUp();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.to_do_list_pop_up,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backButton = view.findViewById(R.id.to_do_list_pop_up_back_button);
        delete = view.findViewById(R.id.to_do_list_pop_up_delete);
        listName = view.findViewById(R.id.to_do_list_pop_up_list_name_text_view);
        date = view.findViewById(R.id.to_do_list_pop_up_date);
        task = view.findViewById(R.id.to_do_list_pop_up_task_edit_text);
        checkBox = view.findViewById(R.id.to_do_list_pop_up_check_box);
        remindMe = view.findViewById(R.id.to_do_list_pop_up_remind_me);
        note = view.findViewById(R.id.to_do_list_pop_up_note);
        remindMeTextView = view.findViewById(R.id.to_do_list_pop_up_remind_me_text_view);
        remindMeDate = view.findViewById(R.id.to_do_list_pop_up_remind_me_date_text_view);
        remindMeIcon = view.findViewById(R.id.to_do_list_pop_up_remind_me_icon);
        remindMeCancel = view.findViewById(R.id.to_do_list_pop_up_remind_me_cancel);

        Intent intent = getActivity().getIntent();
        check = intent.getIntExtra("check",0);
        listNameString = intent.getStringExtra("listName");
        taskString = intent.getStringExtra("task");
        uuidString = intent.getStringExtra("uuid");
        dateString = intent.getStringExtra("date");
        noteString = intent.getStringExtra("note");

        listName.setText(listNameString);
        date.setText(dateString);
        task.setText(taskString);
        note.setText(noteString);

        if (check == 2){
            task.setPaintFlags(task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            task.setTextColor(Color.parseColor("#A9A9A9"));
            checkBox.setChecked(true);
        }

        back();
        onCheckBoxChanged();
        textWatcher();
        deleteButton();
        remindMeOnClick();
        remindMeCancelOnClick();
        checkRemindMe();
    }

    public void deleteButton(){
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext(),R.style.MyAlertDialogStyle);
                alert.setTitle(getResources().getString(R.string.warning));
                alert.setMessage(getResources().getString(R.string.are_you_sure_delete_task));
                alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().getContentResolver().delete(DoItContentProvider.TO_DO_LIST_URI,"uuidTask=?",new String[]{uuidString});
                        getActivity().finish();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }
        });
    }

    public void textWatcher(){

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DoItContentProvider.TO_DO_LIST_TASK,task.getText().toString());
                contentValues.put(DoItContentProvider.TO_DO_LIST_NOTE,note.getText().toString());
                getActivity().getContentResolver().update(DoItContentProvider.TO_DO_LIST_URI,contentValues,"uuidTask=?",new String[]{uuidString});
            }
        };
        task.addTextChangedListener(textWatcher);
        note.addTextChangedListener(textWatcher);

    }

    public void remindMeOnClick(){
        remindMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    public void showDatePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    int dayCalendar,yearCalendar,monthCalendar,hourCalendar,minuteCalendar;
    long timeInMillis;
    String remindMeDateString;

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dayCalendar = dayOfMonth;
        yearCalendar = year;
        monthCalendar = month + 1;
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourCalendar = hourOfDay;
                minuteCalendar = minute;

                SimpleDateFormat formatter = new SimpleDateFormat("dd,MM,yyyy, kk:mm");
                String dateString = dayOfMonth + "," + monthCalendar + "," + year + ", " + hourOfDay + ":" + minute;
                try {
                    Date mDate = formatter.parse(dateString);
                    timeInMillis = mDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (timeInMillis > System.currentTimeMillis()){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy  kk:mm");
                    remindMeDateString = simpleDateFormat.format(timeInMillis);
                    remindMeTextView.setText(getResources().getString(R.string.remind_me_at) + " " + hourOfDay + ":" + minute);
                    remindMeTextView.setTextColor(Color.parseColor("#56E39F"));
                    remindMeDate.setText(remindMeDateString);
                    remindMeIcon.setColorFilter(Color.parseColor("#56E39F"));
                    remindMeDate.setVisibility(View.VISIBLE);
                    remindMeCancel.setVisibility(View.VISIBLE);
                    int x = (int)((timeInMillis / 1000) / 60);
                    int y = (int) ((System.currentTimeMillis() / 1000) / 60);
                    int reminderInMinute = x - y;
                    createRemindMe(reminderInMinute);
                }else {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        },Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE),
                android.text.format.DateFormat.is24HourFormat(getContext()));
        timePickerDialog.show();
    }

    public void remindMeCancelOnClick(){
        remindMeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindMeDate.setVisibility(View.GONE);
                remindMeTextView.setTextColor(Color.parseColor("#a9a9a9"));
                remindMeIcon.setColorFilter(Color.parseColor("#a9a9a9"));
                remindMeTextView.setText(getResources().getString(R.string.remind_me));
                remindMeCancel.setVisibility(View.INVISIBLE);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DoItContentProvider.TO_DO_LIST_REMIND_TIME,0);
                contentValues.put(DoItContentProvider.TO_DO_LIST_REMINDER,false);
                getActivity().getContentResolver().update(DoItContentProvider.TO_DO_LIST_URI,contentValues,"uuidTask=?",new String[]{uuidString});
                //Cancel WorkManger
                WorkManager.getInstance(getContext()).cancelAllWorkByTag(uuidString);
            }
        });
    }

    public void createRemindMe(int timeInLong){
        Data data = new Data.Builder()
                .putString("taskName",taskString)
                .putString("uuid",uuidString)
                .build();
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(RemindMe.class)
                .setInitialDelay(timeInLong, TimeUnit.MINUTES)
                .setInputData(data)
                .addTag(uuidString)
                .build();
        WorkManager.getInstance(getContext()).enqueue(workRequest);

        ContentValues contentValues = new ContentValues();
        contentValues.put(DoItContentProvider.TO_DO_LIST_REMIND_TIME,timeInMillis);
        contentValues.put(DoItContentProvider.TO_DO_LIST_REMINDER,true);
        getActivity().getContentResolver().update(DoItContentProvider.TO_DO_LIST_URI,contentValues,"uuidTask=?",new String[]{uuidString});
    }


    public void checkRemindMe(){
        int reminder = 0;
        long remindMeTime = 0;
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(DoItContentProvider.TO_DO_LIST_URI,null,"uuidTask=?",new String[]{uuidString},null);
        if (cursor != null){
            while (cursor.moveToNext()){
                reminder = cursor.getInt(cursor.getColumnIndex(DoItContentProvider.TO_DO_LIST_REMINDER));
                remindMeTime = cursor.getLong(cursor.getColumnIndex(DoItContentProvider.TO_DO_LIST_REMIND_TIME));
            }
        }
        if (reminder == 1){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("kk:mm");
            remindMeDateString = simpleDateFormat.format(remindMeTime);
            remindMeTextView.setText(getResources().getString(R.string.remind_me_at) + " " + simpleDateFormat2.format(remindMeTime));
            remindMeTextView.setTextColor(Color.parseColor("#56E39F"));
            remindMeDate.setText(remindMeDateString);
            remindMeIcon.setColorFilter(Color.parseColor("#56E39F"));
            remindMeDate.setVisibility(View.VISIBLE);
            remindMeCancel.setVisibility(View.VISIBLE);
        }
    }

    public void onCheckBoxChanged(){
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ContentValues contentValues = new ContentValues();
                if (isChecked){
                    contentValues.put(DoItContentProvider.TO_DO_LIST_COMPLETED,1);
                    task.setPaintFlags(task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    task.setTextColor(Color.parseColor("#A9A9A9"));
                }else {
                    contentValues.put(DoItContentProvider.TO_DO_LIST_COMPLETED,0);
                    task.setPaintFlags(0);
                    task.setTypeface(task.getTypeface(), Typeface.BOLD);
                    task.setTextColor(Color.parseColor("#707070"));
                }
                getActivity().getContentResolver().update(DoItContentProvider.TO_DO_LIST_URI,contentValues,"uuidTask=?",new String[]{uuidString});
                contentValues.clear();
            }
        });

    }

    public void back(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

    }
}
