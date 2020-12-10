package com.kastudio.doit.PopUp;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kastudio.doit.DashboardFragment;
import com.kastudio.doit.DoItContentProvider;
import com.kastudio.doit.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AddTargetPopUp extends Fragment {

    RadioGroup radioGroupOne,radioGroupTwo;
    RadioButton days7, days21, month1, months3, months6, year1;
    Button addTarget;
    EditText targetEditText;

    int days = 7;

    public static AddTargetPopUp newInstance(){
        return new AddTargetPopUp();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_target_dialog,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroupOne = view.findViewById(R.id.zinciri_kirma_radioGroup_one);
        radioGroupTwo = view.findViewById(R.id.zinciri_kirma_radioGroup_two);
        days7 = view.findViewById(R.id.radio_button_7_days);
        days21 = view.findViewById(R.id.radio_button_21_days);
        month1 = view.findViewById(R.id.radio_button_1_month);
        months3 = view.findViewById(R.id.radio_button_3_months);
        months6 = view.findViewById(R.id.radio_button_6_months);
        year1 = view.findViewById(R.id.radio_button_1_year);
        addTarget = view.findViewById(R.id.dialog_add_a_target);
        targetEditText = view.findViewById(R.id.zinciri_kirma_editText);

        chooseTime();
        checkButton();

        radioGroupOne.clearCheck();
        radioGroupTwo.clearCheck();
        radioGroupOne.setOnCheckedChangeListener(listener1);
        radioGroupTwo.setOnCheckedChangeListener(listener2);

        days7.setChecked(true);

        addTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (days == 0){
                    Toast.makeText(getActivity(), "Choose period", Toast.LENGTH_SHORT).show();
                }else {
                    addTarget.setEnabled(false);
                    String targetName = targetEditText.getText().toString();
                    int startTime = (int) (System.currentTimeMillis() / (3600000 * 24));

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DoItContentProvider.ZINCIRI_KIRMA_NAME,targetName);
                    contentValues.put(DoItContentProvider.ZINCIRI_KIRMA_DAYS,days);
                    contentValues.put(DoItContentProvider.ZINCIRI_KIRMA_START,startTime);
                    contentValues.put(DoItContentProvider.ZINCIRI_KIRMA_LAST,0);
                    contentValues.put(DoItContentProvider.ZINCIRI_KIRMA_FINISH,false);
                    UUID uuid = UUID.randomUUID();
                    String newUUID = uuid.toString();
                    contentValues.put(DoItContentProvider.ZINCIRI_KIRMA_UUID,newUUID);
                    for (int x = 0; x < days; x++){
                        contentValues.put(DoItContentProvider.ZINCIRI_KIRMA_ID,x + 1);
                        contentValues.put(DoItContentProvider.ZINCIRI_KIRMA_COMPLETED,false);
                        getActivity().getContentResolver().insert(DoItContentProvider.ZINCIRI_KIRMA_URI,contentValues);
                    }
                    getActivity().finish();
                }
            }
        });

    }

    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            radioGroupTwo.setOnCheckedChangeListener(null);
            radioGroupTwo.clearCheck();
            radioGroupTwo.setOnCheckedChangeListener(listener2);
        }
    };

    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            radioGroupOne.setOnCheckedChangeListener(null);
            radioGroupOne.clearCheck();
            radioGroupOne.setOnCheckedChangeListener(listener1);
        }
    };

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
        targetEditText.addTextChangedListener(textWatcher);
        checkInsert();
    }
    public void checkInsert(){

        if (targetEditText.getText().toString().equals("")){
            addTarget.setEnabled(false);
        }else {
            addTarget.setEnabled(true);
        }
    }

    public void chooseTime(){

        days7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days = 7;
            }
        });
        days21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days = 21;
            }
        });
        month1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days = 31;
            }
        });
        months3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days = 93;
            }
        });
        months6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days = 186;
            }
        });
        year1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               days = 365;
            }
        });

    }

}
