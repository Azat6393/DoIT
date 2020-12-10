package com.kastudio.doit;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kastudio.doit.Kanban.KanbanActivity;
import com.kastudio.doit.PopUp.AddTargetPopUpActivity;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.util.ArrayList;
import java.util.UUID;

import me.itangqi.waveloadingview.WaveLoadingView;

public class DashboardFragment extends Fragment {

    ZinciriKirmaRecyclerView zinciriKirmaRecyclerView;
    ArrayList<String> targets, listName, listUUID;
    ArrayList<Integer> listColor;
    RecyclerView recyclerView, toDoListRecyclerView;
    Button addTargetButton, goToKanbanButton, newListButton;
    TextView toDoTextView, doingTextView, doneTextView;
    Dialog dialog;
    ArrayList<Integer> finish;
    ArrayList<String> uuid;
    DashboardToDoListRecyclerView dashboardToDoListRecyclerView;
    int selectedColor = Color.parseColor("#ffb4a2");

    public static DashboardFragment newInstance(){
        return new DashboardFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addTargetButton = view.findViewById(R.id.add_a_target_button);
        goToKanbanButton = view.findViewById(R.id.go_to_kanban_button);
        newListButton = view.findViewById(R.id.new_list_button);
        toDoTextView = view.findViewById(R.id.kanban_to_do_text_view);
        doingTextView = view.findViewById(R.id.kanban_doing_text_view);
        doneTextView = view.findViewById(R.id.kanban_done_text_view);
        toDoListRecyclerView = view.findViewById(R.id.dashboard_to_do_list_recycler_view);
        recyclerView = view.findViewById(R.id.zinciri_kirma_recycler_view);
        dialog = new Dialog(getActivity());
        targets = new ArrayList<>();
        listName = new ArrayList<>();
        listColor = new ArrayList<>();
        finish = new ArrayList<>();
        uuid = new ArrayList<>();
        listUUID = new ArrayList<>();

        getZinciriKirmaData();
        getKanbanData();
        getToDoListData();

        //dashboard zinciri kirma
        zinciriKirmaRecyclerView = new ZinciriKirmaRecyclerView(targets,getActivity(),finish,uuid);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(zinciriKirmaRecyclerView);

        //dashboard to do list
        dashboardToDoListRecyclerView = new DashboardToDoListRecyclerView(listName,listColor,getActivity(),listUUID);
        toDoListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        toDoListRecyclerView.setAdapter(dashboardToDoListRecyclerView);

        //add a target onClick
        addTargetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddTargetPopUpActivity.newIntent(getActivity());
                startActivity(intent);
            }
        });

        //go to kanban onClick
        goToKanbanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), KanbanActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        //new list button onClick
        newListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.setContentView(R.layout.new_list_dialog);
                dialog.show();

                TextView newListTitle = dialog.findViewById(R.id.new_list_dialog_title_text);
                EditText newListEditText = dialog.findViewById(R.id.new_list_dialog_ditText);
                SpectrumPalette palette = dialog.findViewById(R.id.new_list_dialog_palette);
                Button newListCancelButton = dialog.findViewById(R.id.new_list_dialog_cancel_button);
                Button newListCreateButton = dialog.findViewById(R.id.new_list_dialog_create_button);

                palette.setSelectedColor(Color.parseColor("#ffb4a2"));
                palette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        selectedColor = color;
                    }
                });

                newListCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                newListCreateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!newListEditText.getText().toString().matches("")){
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DoItContentProvider.TO_DO_LIST_LIST_NAME,newListEditText.getText().toString());
                            contentValues.put(DoItContentProvider.TO_DO_LIST_COLOR,selectedColor);
                            UUID uuid = UUID.randomUUID();
                            contentValues.put(DoItContentProvider.TO_DO_LIST_UUID,uuid.toString());
                            getActivity().getContentResolver().insert(DoItContentProvider.TO_DO_LIST_URI,contentValues);
                            clearArrays();
                            getToDoListData();
                            dashboardToDoListRecyclerView.notifyDataSetChanged();
                            dialog.cancel();
                        }
                    }
                });

            }
        });
    }

    public void clearArrays(){
        if (!listName.isEmpty()) {
            listColor.clear();
            listUUID.clear();
            listName.clear();
        }
    }

    public void getToDoListData(){

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(DoItContentProvider.TO_DO_LIST_URI,null,"task is null",null,null);
        if (cursor != null){
            while (cursor.moveToNext()){
                listName.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.TO_DO_LIST_LIST_NAME)));
                listUUID.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.TO_DO_LIST_UUID)));
                listColor.add(cursor.getInt(cursor.getColumnIndex(DoItContentProvider.TO_DO_LIST_COLOR)));
            }
        }
    }

    public void getZinciriKirmaData(){

        targets.clear();
        finish.clear();
        uuid.clear();

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(DoItContentProvider.ZINCIRI_KIRMA_URI,null,"id=?",new String[]{"1"},null);

        if (cursor != null){
            while (cursor.moveToNext()){
                targets.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.ZINCIRI_KIRMA_NAME)));
                finish.add(cursor.getInt(cursor.getColumnIndex(DoItContentProvider.ZINCIRI_KIRMA_FINISH)));
                uuid.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.ZINCIRI_KIRMA_UUID)));
            }
        }
    }

    public void getKanbanData(){
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursorToDo = contentResolver.query(DoItContentProvider.KANBAN_TO_DO_URI,null,null,null,null);
        Cursor cursorDoing = contentResolver.query(DoItContentProvider.KANBAN_DOING_URI,null,null,null,null);
        Cursor cursorDone = contentResolver.query(DoItContentProvider.KANBAN_DONE_URI,null,null,null,null);
        int toDo = 0;
        int doing = 0;
        int done = 0;
        while (cursorToDo.moveToNext()){
            toDo++;
        }
        while (cursorDoing.moveToNext()){
            doing++;
        }
        while (cursorDone.moveToNext()){
            done++;
        }
        toDoTextView.setText("" + toDo);
        doingTextView.setText("" + doing);
        doneTextView.setText("" + done);
    }

    @Override
    public void onResume() {
        super.onResume();
        getZinciriKirmaData();
        zinciriKirmaRecyclerView.notifyDataSetChanged();
        getKanbanData();
        clearArrays();
        getToDoListData();
        dashboardToDoListRecyclerView.notifyDataSetChanged();
    }
}
