package com.kastudio.doit.Kanban;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.kastudio.doit.DoItContentProvider;
import com.kastudio.doit.R;
import com.kastudio.doit.ZinciriKirmaPackage.ZinciriKirma;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.util.ArrayList;
import java.util.UUID;

public class ToDoFragment extends Fragment {

    RecyclerView recyclerView;
    ToDoRecyclerView toDoRecyclerView;
    ArrayList<String> note;
    ArrayList<Integer> cardViewColor;
    ArrayList<String> uuid;

    Button save;
    EditText toDo;

    int selectedColor = Color.WHITE;

    LocalBroadcastManager manager;

    public static ToDoFragment newInstance(){
        return new ToDoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.to_do_fragment,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.kanban_to_do_recycler_view);
        note = new ArrayList<>();
        cardViewColor = new ArrayList<>();
        uuid = new ArrayList<>();
        manager = LocalBroadcastManager.getInstance(getContext());

        getData();

        toDoRecyclerView = new ToDoRecyclerView(note,cardViewColor,uuid);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(toDoRecyclerView);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = view.findViewById(R.id.kanban_add_to_do);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View dialogView = LayoutInflater.from(getContext())
                        .inflate(R.layout.kanban_bottom_sheet,view.findViewById(R.id.kanban_bottom_sheet));

                SpectrumPalette palette = dialogView.findViewById(R.id.kanban_bottom_sheet_palette);
                toDo = dialogView.findViewById(R.id.kanban_bottom_sheet_edit_text);
                save = dialogView.findViewById(R.id.kanban_bottom_sheet_save);
                checkButton();

                palette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        selectedColor = color;
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DoItContentProvider.KANBAN_TO_DO_NOTE,toDo.getText().toString());
                        contentValues.put(DoItContentProvider.KANBAN_TO_DO_COLOR,selectedColor);
                        UUID uuid = UUID.randomUUID();
                        contentValues.put(DoItContentProvider.KANBAN_TO_DO_UUID,uuid.toString());

                        getActivity().getContentResolver().insert(DoItContentProvider.KANBAN_TO_DO_URI,contentValues);
                        clearArrays();
                        getData();
                        toDoRecyclerView.notifyDataSetChanged();
                        selectedColor = Color.WHITE;
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();

            }
        });

    }

    public void getData(){
        if (getActivity() != null){
            ContentResolver contentResolver = getActivity().getContentResolver();
            Cursor cursor = contentResolver.query(DoItContentProvider.KANBAN_TO_DO_URI,null,null,null,null);

            if (cursor != null){
                while(cursor.moveToNext()){
                    note.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.KANBAN_TO_DO_NOTE)));
                    cardViewColor.add(cursor.getInt(cursor.getColumnIndex(DoItContentProvider.KANBAN_TO_DO_COLOR)));
                    uuid.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.KANBAN_TO_DO_UUID)));
                }
            }
        }
    }

    public void clearArrays(){
        if (!note.isEmpty()) {
            note.clear();
            cardViewColor.clear();
            uuid.clear();
        }
    }

    String deletedNote, deletedUUID;
    int deletedColor;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback( 0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();

            switch (direction){
                case ItemTouchHelper.RIGHT:
                    deletedUUID = uuid.get(position);
                    deletedColor = cardViewColor.get(position);
                    deletedNote = note.get(position);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DoItContentProvider.KANBAN_DOING_NOTE,note.get(position));
                    contentValues.put(DoItContentProvider.KANBAN_DOING_COLOR,cardViewColor.get(position));
                    contentValues.put(DoItContentProvider.KANBAN_DOING_UUID,uuid.get(position));
                    getActivity().getContentResolver().insert(DoItContentProvider.KANBAN_DOING_URI,contentValues);
                    getActivity().getContentResolver().delete(DoItContentProvider.KANBAN_TO_DO_URI,"uuid=?",new String[]{uuid.get(position)});
                    note.remove(position);
                    cardViewColor.remove(position);
                    uuid.remove(position);
                    toDoRecyclerView.notifyItemRemoved(position);
                    update();
                    Snackbar.make(requireView(),deletedNote,Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#56E39F"))
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    contentValues.clear();
                                    contentValues.put(DoItContentProvider.KANBAN_TO_DO_NOTE,deletedNote);
                                    contentValues.put(DoItContentProvider.KANBAN_TO_DO_UUID,deletedUUID);
                                    contentValues.put(DoItContentProvider.KANBAN_TO_DO_COLOR,deletedColor);
                                    getActivity().getContentResolver().insert(DoItContentProvider.KANBAN_TO_DO_URI,contentValues);
                                    getActivity().getContentResolver().delete(DoItContentProvider.KANBAN_DOING_URI,"uuid=?",new String[]{deletedUUID});
                                    note.add(position,deletedNote);
                                    cardViewColor.add(position,deletedColor);
                                    uuid.add(position,deletedUUID);
                                    toDoRecyclerView.notifyItemInserted(position);
                                    update();
                                }
                            }).show();
                    break;
            }

        }
    };

    public void update (){
        Intent intent = new Intent(getContext(),KanbanBroadcastReceiver.class);
        intent.putExtra("update","first");
        getActivity().sendBroadcast(intent);
    }

    public void checkButton(){

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkText();
            }
        };

        toDo.addTextChangedListener(textWatcher);
        checkText();
    }

    public void checkText(){
        if (toDo.getText().toString().equals("")){
            save.setEnabled(false);
        }else {
            save.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("my.result.receiver");

        manager.registerReceiver(broadcastReceiver,intentFilter);

        clearArrays();
        getData();
        toDoRecyclerView.notifyDataSetChanged();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("update").matches("second")){
                clearArrays();
                getData();
                toDoRecyclerView.notifyDataSetChanged();
            }
        }
    };

}
