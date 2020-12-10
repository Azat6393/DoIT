package com.kastudio.doit.ToDoList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.kastudio.doit.DoItContentProvider;
import com.kastudio.doit.Kanban.DoingFragment;
import com.kastudio.doit.R;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ToDoListActivity extends AppCompatActivity {

    MaterialButton completedButton;
    Button bottomSheetButton;
    RecyclerView taskRecyclerView, completedTaskRecyclerView;
    ImageView editButton, deleteButton;
    FloatingActionButton fab;
    ArrayList<String> task,completedTask, taskUUID, completedTaskUUID, taskNote, completedTaskNote, taskDate, completedTaskDate;
    ViewGroup viewGroup;
    BottomSheetDialog bottomSheetDialog;
    EditText bottomSheetEditText;
    Dialog dialog;
    TextView listNameTextView;

    String listNameString, listUUIDString;
    int listColor, newColor;

    TaskRecyclerView taskRecyclerView1;
    CompletedTaskRecyclerView completedTaskRecyclerView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        completedButton = (MaterialButton) findViewById(R.id.to_do_list_completed_button);
        taskRecyclerView = findViewById(R.id.to_do_list_task_recycler_view);
        completedTaskRecyclerView = findViewById(R.id.to_do_list_completed_recycler_view);
        editButton = findViewById(R.id.to_do_list_edit_button);
        deleteButton = findViewById(R.id.to_do_list_delet_button);
        fab = findViewById(R.id.to_do_list_fab);
        viewGroup = findViewById(R.id.to_do_list);
        listNameTextView = findViewById(R.id.to_do_list_name_text_view);

        Intent intent = getIntent();
        listUUIDString = intent.getStringExtra("uuid");
        getListData();

        viewGroup.setBackgroundColor(listColor);
        listNameTextView.setText(listNameString);


        dialog = new Dialog(ToDoListActivity.this);
        dialog.setContentView(R.layout.new_list_dialog);

        bottomSheetDialog = new BottomSheetDialog(ToDoListActivity.this,R.style.BottomSheetDialogTheme);
        View dialogView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.to_do_list_bottom_sheet,(LinearLayout)ToDoListActivity.this.findViewById(R.id.to_do_list_bottom_sheet));

        bottomSheetEditText = dialogView.findViewById(R.id.to_do_list_bottom_sheet_edit_text);
        bottomSheetButton = dialogView.findViewById(R.id.to_do_list_bottom_sheet_button);
        checkButton();
        bottomSheetDialog.setContentView(dialogView);

        task = new ArrayList<>();
        completedTask = new ArrayList<>();
        taskUUID = new ArrayList<>();
        completedTaskUUID = new ArrayList<>();
        taskNote = new ArrayList<>();
        completedTaskNote = new ArrayList<>();
        taskDate = new ArrayList<>();
        completedTaskDate = new ArrayList<>();

        getTaskData();
        checkCompletedButton();

        taskRecyclerView1 = new TaskRecyclerView(task,taskUUID,taskNote,taskDate,listNameString,this);
        completedTaskRecyclerView1 = new CompletedTaskRecyclerView(completedTask,completedTaskUUID,completedTaskNote,completedTaskDate,listNameString,this);

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(ToDoListActivity.this));
        completedTaskRecyclerView.setLayoutManager(new LinearLayoutManager(ToDoListActivity.this));

        taskRecyclerView.setAdapter(taskRecyclerView1);
        completedTaskRecyclerView.setAdapter(completedTaskRecyclerView1);

        editList();
        deleteList();
        addTask();

        if (completedTaskRecyclerView.getVisibility() == View.VISIBLE){
            completedButton.setIconResource(R.drawable.ic_baseline_arrow_drop_down_24);
        }else {
            completedButton.setIconResource(R.drawable.ic_baseline_arrow_right_24);
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(completedTaskRecyclerView);
        ItemTouchHelper itemTouchHelper1 = new ItemTouchHelper(callback1);
        itemTouchHelper1.attachToRecyclerView(taskRecyclerView);

    }

    public void checkCompletedButton(){
        if (completedTask.isEmpty()){
            completedButton.setVisibility(View.INVISIBLE);
        }else {
            completedButton.setVisibility(View.VISIBLE);
        }
    }

    public void getListData(){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(DoItContentProvider.TO_DO_LIST_URI,null,"uuid=?",new String[]{listUUIDString},null);
        if (cursor != null){
            while (cursor.moveToNext()){
                listColor = cursor.getInt(cursor.getColumnIndex(DoItContentProvider.TO_DO_LIST_COLOR));
                listNameString = cursor.getString(cursor.getColumnIndex(DoItContentProvider.TO_DO_LIST_LIST_NAME));
            }
        }
    }

    public void getTaskData(){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(DoItContentProvider.TO_DO_LIST_URI,null,"task is not null and completed=? and uuid=?",new String[]{"0",listUUIDString},null);
        Cursor cursor1 = contentResolver.query(DoItContentProvider.TO_DO_LIST_URI,null,"task is not null and completed=? and uuid=?",new String[]{"1",listUUIDString},null);
        if (cursor != null){
            while (cursor.moveToNext()){
                task.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.TO_DO_LIST_TASK)));
                taskDate.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.TO_DO_LIST_DATE)));
                taskUUID.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.TO_DO_LIST_UUID_TASK)));
                taskNote.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.TO_DO_LIST_NOTE)));
            }
            while (cursor1.moveToNext()){
                completedTask.add(cursor1.getString(cursor1.getColumnIndex(DoItContentProvider.TO_DO_LIST_TASK)));
                completedTaskDate.add(cursor1.getString(cursor1.getColumnIndex(DoItContentProvider.TO_DO_LIST_DATE)));
                completedTaskUUID.add(cursor1.getString(cursor1.getColumnIndex(DoItContentProvider.TO_DO_LIST_UUID_TASK)));
                completedTaskNote.add(cursor1.getString(cursor1.getColumnIndex(DoItContentProvider.TO_DO_LIST_NOTE)));
            }
        }
        completedButton.setText("Completed " + completedTask.size());
    }

    public void addTask(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.show();

                bottomSheetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DoItContentProvider.TO_DO_LIST_LIST_NAME,listNameString);
                        contentValues.put(DoItContentProvider.TO_DO_LIST_UUID,listUUIDString);
                        contentValues.put(DoItContentProvider.TO_DO_LIST_COLOR,listColor);
                        contentValues.put(DoItContentProvider.TO_DO_LIST_TASK,bottomSheetEditText.getText().toString());
                        contentValues.put(DoItContentProvider.TO_DO_LIST_COMPLETED,false);
                        UUID uuid = UUID.randomUUID();
                        contentValues.put(DoItContentProvider.TO_DO_LIST_UUID_TASK,uuid.toString());
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy  hh:mm");
                        contentValues.put(DoItContentProvider.TO_DO_LIST_DATE,simpleDateFormat.format(System.currentTimeMillis()));
                        getContentResolver().insert(DoItContentProvider.TO_DO_LIST_URI,contentValues);
                        clearArrays();
                        getTaskData();
                        bottomSheetEditText.setText("");
                        taskRecyclerView1.notifyDataSetChanged();
                        completedTaskRecyclerView1.notifyDataSetChanged();
                        checkCompletedButton();
                        bottomSheetDialog.cancel();
                    }
                });
            }
        });
    }

    public void clearArrays(){
        task.clear();
        completedTask.clear();
        taskUUID.clear();
        completedTaskUUID.clear();
        taskNote.clear();
        completedTaskNote.clear();
        taskDate.clear();
        completedTaskDate.clear();
    }

    public void deleteList(){
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(ToDoListActivity.this,R.style.MyAlertDialogStyle);
                alert.setTitle(getResources().getString(R.string.warning));
                alert.setMessage(getResources().getString(R.string.are_you_sure_delete_list));
                alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getContentResolver().delete(DoItContentProvider.TO_DO_LIST_URI,"uuid=?",new String[]{listUUIDString});
                        finish();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
    }

    public void editList(){
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView textView = dialog.findViewById(R.id.new_list_dialog_title_text);
                EditText editText = dialog.findViewById(R.id.new_list_dialog_ditText);
                Button saveButton = dialog.findViewById(R.id.new_list_dialog_create_button);
                Button cancelButton = dialog.findViewById(R.id.new_list_dialog_cancel_button);
                SpectrumPalette palette = dialog.findViewById(R.id.new_list_dialog_palette);

                saveButton.setText(getResources().getString(R.string.save));
                textView.setText(getResources().getString(R.string.edit_list));
                editText.setText(listNameString);
                palette.setSelectedColor(listColor);
                palette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        viewGroup.setBackgroundColor(color);
                        newColor = color;
                    }
                });
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DoItContentProvider.TO_DO_LIST_LIST_NAME,editText.getText().toString());
                        contentValues.put(DoItContentProvider.TO_DO_LIST_COLOR,newColor);
                        getContentResolver().update(DoItContentProvider.TO_DO_LIST_URI,contentValues,"uuid=?",new String[]{listUUIDString});
                        listColor = newColor;
                        getListData();
                        listNameTextView.setText(editText.getText().toString());
                        listNameString = editText.getText().toString();
                        dialog.cancel();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewGroup.setBackgroundColor(listColor);
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }

    String taskDeleted = null;
    String taskUUIDDeleted = null;
    String taskNoteDeleted = null;
    String taskDateDeleted = null;
    String completedTaskDateDeleted = null;
    String completedTaskNoteDeleted = null;
    String completedTaskUUIDDeleted = null;
    String completedTaskDeleted = null;

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    completedTaskDeleted = completedTask.get(position);
                    completedTaskNoteDeleted = completedTaskNote.get(position);
                    completedTaskDateDeleted = completedTaskDate.get(position);
                    completedTaskUUIDDeleted = completedTaskUUID.get(position);
                    getContentResolver().delete(DoItContentProvider.TO_DO_LIST_URI,"uuidTask=?", new String[]{completedTaskUUID.get(position)});
                    completedTask.remove(position);
                    completedTaskUUID.remove(position);
                    completedTaskNote.remove(position);
                    completedTaskDate.remove(position);
                    completedTaskRecyclerView1.notifyItemRemoved(position);
                    completedButton.setText("Completed " + completedTask.size());
                    checkCompletedButton();

                    Snackbar.make(completedTaskRecyclerView, completedTaskDeleted, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_LIST_NAME,listNameString);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_UUID,listUUIDString);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_COLOR,listColor);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_NOTE,completedTaskNoteDeleted);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_TASK,completedTaskDeleted);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_UUID_TASK,completedTaskUUIDDeleted);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_DATE,completedTaskDateDeleted);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_COMPLETED,1);
                                    getContentResolver().insert(DoItContentProvider.TO_DO_LIST_URI,contentValues);
                                    completedTask.add(position,completedTaskDeleted);
                                    completedTaskUUID.add(position,completedTaskUUIDDeleted);
                                    completedTaskNote.add(position,completedTaskNoteDeleted);
                                    completedTaskDate.add(position,completedTaskDateDeleted);
                                    completedTaskRecyclerView1.notifyItemInserted(position);
                                    completedButton.setText("Completed " + completedTask.size());
                                    checkCompletedButton();
                                }
                            }).setActionTextColor(getResources().getColor(R.color.white)).show();
                    break;
            }
        }
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(ToDoListActivity.this,c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ToDoListActivity.this,R.color.deleteRed))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    ItemTouchHelper.SimpleCallback callback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    taskDeleted = task.get(position);
                    taskNoteDeleted = taskNote.get(position);
                    taskDateDeleted = taskDate.get(position);
                    taskUUIDDeleted = taskUUID.get(position);
                    getContentResolver().delete(DoItContentProvider.TO_DO_LIST_URI,"uuidTask=?", new String[]{taskUUID.get(position)});
                    task.remove(position);
                    taskUUID.remove(position);
                    taskNote.remove(position);
                    taskDate.remove(position);
                    taskRecyclerView1.notifyItemRemoved(position);
                    completedButton.setText("Completed " + completedTask.size());

                    Snackbar.make(taskRecyclerView, taskDeleted, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_LIST_NAME,listNameString);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_UUID,listUUIDString);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_COLOR,listColor);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_NOTE,taskNoteDeleted);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_TASK,taskDeleted);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_UUID_TASK,taskUUIDDeleted);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_DATE,taskDateDeleted);
                                    contentValues.put(DoItContentProvider.TO_DO_LIST_COMPLETED,0);
                                    getContentResolver().insert(DoItContentProvider.TO_DO_LIST_URI,contentValues);
                                    task.add(position,taskDeleted);
                                    taskUUID.add(position,taskUUIDDeleted);
                                    taskNote.add(position,taskNoteDeleted);
                                    taskDate.add(position,taskDateDeleted);
                                    taskRecyclerView1.notifyItemInserted(position);
                                    completedButton.setText("Completed " + completedTask.size());
                                }
                            }).setActionTextColor(getResources().getColor(R.color.white)).show();
                    TransitionManager.beginDelayedTransition(viewGroup);
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(ToDoListActivity.this,c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ToDoListActivity.this,R.color.deleteRed))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public void completedClick(View view){
        TransitionManager.beginDelayedTransition(viewGroup);
        if (completedTaskRecyclerView.getVisibility() == View.VISIBLE){
            completedTaskRecyclerView.setVisibility(View.GONE);
            completedButton.setIconResource(R.drawable.ic_baseline_arrow_right_24);
        }else {
           completedTaskRecyclerView.setVisibility(View.VISIBLE);
            completedButton.setIconResource(R.drawable.ic_baseline_arrow_drop_down_24);
        }
    }
    public void back(View view){
        finish();
    }

    public void checkButton() {

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkInsert();
            }
        };
        bottomSheetEditText.addTextChangedListener(textWatcher);
        checkInsert();
    }

    public void checkInsert(){
        if (bottomSheetEditText.getText().toString().equals("")){
            bottomSheetButton.setEnabled(false);
        }else {
            bottomSheetButton.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        clearArrays();
        getTaskData();
        checkCompletedButton();
        taskRecyclerView1.notifyDataSetChanged();
        completedTaskRecyclerView1.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}