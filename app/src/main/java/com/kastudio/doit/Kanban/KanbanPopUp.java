package com.kastudio.doit.Kanban;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.kastudio.doit.DoItContentProvider;
import com.kastudio.doit.R;
import com.thebluealliance.spectrum.SpectrumPalette;

public class KanbanPopUp extends Fragment {

    ImageView back, edit, changeColor, delete, save;
    TextView noteTextView;
    EditText noteEditView;
    SpectrumPalette palette;
    ViewGroup viewGroup;
    CardView cardView;
    ConstraintLayout constraintLayout;
    String noteString, uuid;
    int selectedColor, kanban;

    public static KanbanPopUp newInstance(){
        return new KanbanPopUp();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.kanban_pop_up,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        back = view.findViewById(R.id.kanban_pop_up_back);
        edit = view.findViewById(R.id.kanban_pop_up_edit);
        changeColor = view.findViewById(R.id.kanban_pop_up_color);
        delete = view.findViewById(R.id.kanban_pop_up_delete);
        noteTextView = view.findViewById(R.id.kanban_pop_up_note_textView);
        noteEditView = view.findViewById(R.id.kanban_pop_up_note_editText);
        palette = view.findViewById(R.id.kanban_pop_up_palette);
        save = view.findViewById(R.id.kanban_pop_up_save_edit);
        viewGroup = view.findViewById(R.id.kanban_pop_up);
        cardView = view.findViewById(R.id.kanban_pop_up_cardView);
        constraintLayout = view.findViewById(R.id.kanban_card_view_layout);

        Intent intent = getActivity().getIntent();
        noteString = intent.getStringExtra("note");
        uuid = intent.getStringExtra("uuid");
        kanban = intent.getIntExtra("kanban",0);
        selectedColor = intent.getIntExtra("color",0);

        noteTextView.setText(noteString);
        noteEditView.setText(noteString);

        palette.setSelectedColor(selectedColor);
        palette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                selectedColor = color;
                ContentValues contentValues = new ContentValues();
                switch (kanban){
                    case 1:
                        contentValues.put(DoItContentProvider.KANBAN_TO_DO_COLOR,selectedColor);
                        getActivity().getContentResolver().update(DoItContentProvider.KANBAN_TO_DO_URI,contentValues,"uuid=?",new String[]{uuid});
                        break;
                    case 2:
                        contentValues.put(DoItContentProvider.KANBAN_DOING_COLOR,selectedColor);
                        getActivity().getContentResolver().update(DoItContentProvider.KANBAN_DOING_URI,contentValues,"uuid=?",new String[]{uuid});
                        break;
                    case 3:
                        contentValues.put(DoItContentProvider.KANBAN_DONE_COLOR,selectedColor);
                        getActivity().getContentResolver().update(DoItContentProvider.KANBAN_DONE_URI,contentValues,"uuid=?",new String[]{uuid});
                        break;
                }
            }
        });

        //back onClick
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote();
                getActivity().finish();
            }
        });

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

        //edit onClick
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteTextView.setVisibility(View.INVISIBLE);
                noteEditView.setVisibility(View.VISIBLE);
                edit.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
            }
        });

        //save onClick
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteTextView.setVisibility(View.VISIBLE);
                noteEditView.setVisibility(View.INVISIBLE);
                edit.setVisibility(View.VISIBLE);
                save.setVisibility(View.INVISIBLE);

                updateNote();
                noteString = noteEditView.getText().toString();
                noteTextView.setText(noteString);
                noteEditView.setText(noteString);

            }
        });
        //delete onClick
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext(),R.style.MyAlertDialogStyle);
                alert.setTitle(getResources().getString(R.string.warning));
                alert.setMessage(getResources().getString(R.string.are_you_sure_delete_task));
                alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (kanban){
                            case 1:
                                getActivity().getContentResolver().delete(DoItContentProvider.KANBAN_TO_DO_URI,"uuid=?",new String[]{uuid});
                                break;
                            case 2:
                                getActivity().getContentResolver().delete(DoItContentProvider.KANBAN_DOING_URI,"uuid=?",new String[]{uuid});
                                break;
                            case 3:
                                getActivity().getContentResolver().delete(DoItContentProvider.KANBAN_DONE_URI,"uuid=?",new String[]{uuid});
                                break;
                        }
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

    @Override
    public void onStop() {
        super.onStop();
        updateNote();
    }

    public void updateNote(){
        ContentValues contentValues = new ContentValues();
        switch (kanban){
            case 1:
                contentValues.put(DoItContentProvider.KANBAN_TO_DO_NOTE,noteEditView.getText().toString());
                getActivity().getContentResolver().update(DoItContentProvider.KANBAN_TO_DO_URI,contentValues,"uuid=?",new String[]{uuid});
                break;
            case 2:
                contentValues.put(DoItContentProvider.KANBAN_DOING_NOTE,noteEditView.getText().toString());
                getActivity().getContentResolver().update(DoItContentProvider.KANBAN_DOING_URI,contentValues,"uuid=?",new String[]{uuid});
                break;
            case 3:
                contentValues.put(DoItContentProvider.KANBAN_DONE_NOTE,noteEditView.getText().toString());
                getActivity().getContentResolver().update(DoItContentProvider.KANBAN_DONE_URI,contentValues,"uuid=?",new String[]{uuid});
                break;
        }
    }

}
