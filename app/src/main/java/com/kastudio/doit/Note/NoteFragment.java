package com.kastudio.doit.Note;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kastudio.doit.DoItContentProvider;
import com.kastudio.doit.R;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

public class NoteFragment extends Fragment {

    RecyclerView recyclerView;
    CardView cardView;

    Animation animation;

    ArrayList<String> title;
    ArrayList<String> note;
    ArrayList<String> date;
    ArrayList<Integer> color;
    ArrayList<String> uuid;

    NoteRecyclerView noteRecyclerView;

    int selectedColor = Color.WHITE;

    EditText toDoEditText;
    EditText noteEditText;
    Button doneButton;

    public static NoteFragment newInstance(){
        return new NoteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_fragment,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.note_recycler_view);
        cardView = view.findViewById(R.id.note_cardView);

        animation = AnimationUtils.loadAnimation(getActivity(),R.anim.animation_on_click);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.startAnimation(animation);

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(),R.style.BottomSheetDialogTheme);
                View dialogView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.bottom_sheet_dialog,(LinearLayout)getActivity().findViewById(R.id.bottom_sheet_id));

                SpectrumPalette palette = dialogView.findViewById(R.id.bottom_sheet_dialog_palette);
                toDoEditText = dialogView.findViewById(R.id.bottom_sheet_dialog_title);
                noteEditText = dialogView.findViewById(R.id.bottom_sheet_dialog_note);
                doneButton = dialogView.findViewById(R.id.bottom_sheet_dialog_done_button);

                checkButton();

                palette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        //selectedColor = String.format("#%06X", (0xFFFFFF & color));
                        selectedColor = color;
                    }
                });

                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DoItContentProvider.NOTE_TITLE, toDoEditText.getText().toString());
                        contentValues.put(DoItContentProvider.NOTE_NOTE, noteEditText.getText().toString());
                        contentValues.put(DoItContentProvider.NOTE_COLOR, selectedColor);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy  hh:mm");
                        contentValues.put(DoItContentProvider.NOTE_DATE, simpleDateFormat.format(System.currentTimeMillis()));

                        UUID uuid = UUID.randomUUID();
                        String newUUID = uuid.toString();
                        contentValues.put(DoItContentProvider.NOTE_UUID,newUUID);

                        getActivity().getContentResolver().insert(DoItContentProvider.NOTE_URI, contentValues);
                        clearArrays();
                        getData();
                        noteRecyclerView.notifyDataSetChanged();
                        selectedColor = Color.WHITE;
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
            }
        });

        title = new ArrayList<>();
        note = new ArrayList<>();
        date = new ArrayList<>();
        color = new ArrayList<>();
        uuid = new ArrayList<>();
        getData();
        noteRecyclerView = new NoteRecyclerView(note,title,date,color,uuid);
        recyclerView.setAdapter(noteRecyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

    }

    public void clearArrays(){
        title.clear();
        note.clear();
        date.clear();
        color.clear();
        uuid.clear();
    }

    public void getData(){
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(DoItContentProvider.NOTE_URI,null,null,null,null);
        if (cursor != null){
            while (cursor.moveToNext()){
                title.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.NOTE_TITLE)));
                note.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.NOTE_NOTE)));
                color.add(cursor.getInt(cursor.getColumnIndex(DoItContentProvider.NOTE_COLOR)));
                date.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.NOTE_DATE)));
                uuid.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.NOTE_UUID)));
            }
        }
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
                checkInsert();
            }
        };
        //toDoEditText.addTextChangedListener(textWatcher);
        noteEditText.addTextChangedListener(textWatcher);
        checkInsert();
    }

    public void checkInsert(){
        doneButton.setEnabled(!noteEditText.getText().toString().equals(""));
    }

    @Override
    public void onResume() {
        super.onResume();
        clearArrays();
        getData();
        noteRecyclerView.notifyDataSetChanged();
    }
}
