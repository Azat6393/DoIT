package com.kastudio.doit.Note;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.kastudio.doit.DashboardFragment;
import com.kastudio.doit.DoItContentProvider;
import com.kastudio.doit.MainActivity;
import com.kastudio.doit.R;
import com.thebluealliance.spectrum.SpectrumPalette;

public class NotePopUp extends Fragment {

    TextView title, note, date;
    ImageView down, delete, changeColor, edit, saveEdit;
    CardView cardView;
    SpectrumPalette palette;
    ViewGroup viewGroup;
    int selectedColor, charactersSize;
    EditText titleEditText, noteEditText;

    String titleString, noteString, dateString, uuid;

    ConstraintLayout constraintLayout;

    public static NotePopUp newInstance(){
        return new NotePopUp();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_pop_up,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.note_pop_up_title_textView);
        note = view.findViewById(R.id.note_pop_up_note_textView);
        date = view.findViewById(R.id.note_pop_up_date_textView);
        down = view.findViewById(R.id.note_pop_up_back);
        delete = view.findViewById(R.id.note_pop_up_delete);
        changeColor = view.findViewById(R.id.note_pop_up_color);
        edit = view.findViewById(R.id.note_pop_up_edit);
        cardView = view.findViewById(R.id.note_pop_up_cardView);
        palette = view.findViewById(R.id.note_pop_up_palette);
        viewGroup = view.findViewById(R.id.note_pop_up);
        constraintLayout = view.findViewById(R.id.card_view_layout);
        titleEditText = view.findViewById(R.id.note_pop_up_title_edit_text);
        noteEditText = view.findViewById(R.id.note_pop_up_note_editText);
        saveEdit = view.findViewById(R.id.note_pop_up_save_edit);

        Intent intent = getActivity().getIntent();
        selectedColor = intent.getIntExtra("color",0);
        titleString = intent.getStringExtra("title");
        noteString = intent.getStringExtra("note");
        dateString = intent.getStringExtra("date");
        uuid = intent.getStringExtra("uuid");
        charactersSize = noteString.length();
        title.setText(titleString);
        note.setText(noteString);
        date.setText(dateString + " | " + charactersSize + " characters");
        titleEditText.setText(titleString);
        noteEditText.setText(noteString);

        checkEditText();

        //change color onClick
        changeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(viewGroup);
                if (palette.getVisibility() == View.VISIBLE){
                    palette.setVisibility(View.GONE);
                    changeColor.setImageDrawable(getResources().getDrawable(R.drawable.tshirt_icon));
                }else {
                    palette.setVisibility(View.VISIBLE);
                    changeColor.setImageDrawable(getResources().getDrawable(R.drawable.tshirt_icon_onclick));
                }
            }
        });

        //back button onClick
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
                getActivity().finish();
            }
        });

        palette.setSelectedColor(selectedColor);
        //on choose color
        palette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                selectedColor = color;
                ContentValues contentValues = new ContentValues();
                contentValues.put(DoItContentProvider.NOTE_COLOR,color);
                getActivity().getContentResolver().update(DoItContentProvider.NOTE_URI,contentValues,"note=?",new String[]{noteString});
            }
        });

        //onClick edit button
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    title.setVisibility(View.INVISIBLE);
                    note.setVisibility(View.INVISIBLE);
                    titleEditText.setVisibility(View.VISIBLE);
                    noteEditText.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.INVISIBLE);
                    saveEdit.setVisibility(View.VISIBLE);
            }
        });
        //save button onClick
        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setVisibility(View.VISIBLE);
                note.setVisibility(View.VISIBLE);
                titleEditText.setVisibility(View.INVISIBLE);
                noteEditText.setVisibility(View.INVISIBLE);
                edit.setVisibility(View.VISIBLE);
                saveEdit.setVisibility(View.INVISIBLE);
                updateData();
                title.setText(titleString);
                note.setText(noteString);
            }
        });
        //delete button onClick
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext(),R.style.MyAlertDialogStyle);
                alert.setTitle(getResources().getString(R.string.warning));
                alert.setMessage(getResources().getString(R.string.are_you_sure_delete_note));
                alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().getContentResolver().delete(DoItContentProvider.NOTE_URI,"uuid=?",new String[]{uuid});
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

    public void checkEditText(){

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                charactersSize = noteEditText.getText().length();
                date.setText(dateString + " | " + charactersSize + " " + getResources().getString(R.string.characters));
                titleString = titleEditText.getText().toString();
                noteString = noteEditText.getText().toString();

            }
        };
        noteEditText.addTextChangedListener(textWatcher);
        titleEditText.addTextChangedListener(textWatcher);
    }

    public void updateData(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DoItContentProvider.NOTE_TITLE,titleString);
        contentValues.put(DoItContentProvider.NOTE_NOTE,noteString);
        getActivity().getContentResolver().update(DoItContentProvider.NOTE_URI,contentValues,"uuid=?",new String[]{uuid});

    }

    @Override
    public void onStop() {
        super.onStop();
        updateData();
    }
}
