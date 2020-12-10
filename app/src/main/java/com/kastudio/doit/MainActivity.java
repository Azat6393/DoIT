package com.kastudio.doit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    private CircularProgressBar circularProgressBar;
    private Button buttonOne, buttonTwo, buttonThree;
    private ImageView tomatoOne, tomatoTwo, tomatoThree, tomatoFour;
    private TextView counterTextView;

    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linearLayout;
    private ToggleButton toggleButton;
    private CountDownTimer countDownTimer;
    private CountDownTimer breakCountDownTimer;

    private final long mStartTimeMillis = 1500000;
    private final long mBreakTimeMillis = 300000;
    private boolean mTimerRunning, breakOrWork;
    private long mTimeLeftInMillis;
    private long mEntTime;
    private int tomato;

    private Animation anim1 = null;
    private Animation anim2 = null;
    private Animation anim3 = null;
    private Animation back1 = null;
    private Animation back2 = null;
    private Animation back3 = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        startService(new Intent(getBaseContext(), AppStopped.class));
        init();
        ViewPagerMainActivity viewPagerMainActivity = new ViewPagerMainActivity(getSupportFragmentManager(),this);

        viewPager.setAdapter(viewPagerMainActivity);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(getResources().getString(R.string.dashboard));
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.note));

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    toggleButton.setChecked(true);
                }else if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    toggleButton.setChecked(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        onClickButtonOne();
        onClickButtonTwo();
        onClickButtonThree();

    }

    public void init(){

        tabLayout = findViewById(R.id.main_activity_tab_layout);
        viewPager = findViewById(R.id.main_activity_view_pager);
        linearLayout = findViewById(R.id.pomodoro_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        toggleButton = findViewById(R.id.pomodoro_bottom_sheet_button);
        circularProgressBar = findViewById(R.id.pomodoro_bottom_sheet_circular_progress_bar);
        buttonOne = findViewById(R.id.pomodoro_bottom_sheet_start_button_one);
        buttonTwo = findViewById(R.id.pomodoro_bottom_sheet_start_button_two);
        buttonThree = findViewById(R.id.pomodoro_bottom_sheet_start_button_three);
        tomatoOne = findViewById(R.id.pomodoro_bottom_sheet_tomato_one);
        tomatoTwo = findViewById(R.id.pomodoro_bottom_sheet_tomato_two);
        tomatoThree = findViewById(R.id.pomodoro_bottom_sheet_tomato_three);
        tomatoFour = findViewById(R.id.pomodoro_bottom_sheet_tomato_four);
        counterTextView = findViewById(R.id.pomodoro_bottom_sheet_counter_text_view);
        anim1 = AnimationUtils.loadAnimation(this,R.anim.anim1);
        anim2 = AnimationUtils.loadAnimation(this,R.anim.anim2);
        anim3 = AnimationUtils.loadAnimation(this,R.anim.anim3);
        back1 = AnimationUtils.loadAnimation(this,R.anim.back1);
        back2 = AnimationUtils.loadAnimation(this,R.anim.back2);
        back3 = AnimationUtils.loadAnimation(this,R.anim.back3);
    }

    public void onClickButtonOne(){
        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTimerRunning){
                    pauseTime();
                    buttonOne.setVisibility(View.INVISIBLE);
                    buttonTwo.startAnimation(anim1);
                    buttonThree.startAnimation(anim3);
                }else {
                    if (!breakOrWork){
                        workTime();
                    }else {
                        breakTime();
                    }
                }
                checkTomato();
                updateButtons();
            }
        });
    }

    public void onClickButtonTwo(){
        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!breakOrWork){
                    workTime();
                }else {
                    breakTime();
                }
                buttonOne.startAnimation(back2);
                buttonTwo.startAnimation(back1);
                buttonThree.startAnimation(back3);
                updateButtons();
            }
        });
    }

    public void onClickButtonThree(){
        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }
    public  void checkTomato(){
        switch (tomato){
            case 0:
                tomatoOne.setVisibility(View.INVISIBLE);
                tomatoTwo.setVisibility(View.INVISIBLE);
                tomatoThree.setVisibility(View.INVISIBLE);
                tomatoFour.setVisibility(View.INVISIBLE);
                break;
            case 1:
                tomatoOne.setVisibility(View.VISIBLE);
                break;
            case 2:
                tomatoOne.setVisibility(View.VISIBLE);
                tomatoTwo.setVisibility(View.VISIBLE);
                break;
            case 3:
                tomatoOne.setVisibility(View.VISIBLE);
                tomatoTwo.setVisibility(View.VISIBLE);
                tomatoThree.setVisibility(View.VISIBLE);
                break;
            case 4:
                tomatoOne.setVisibility(View.VISIBLE);
                tomatoTwo.setVisibility(View.VISIBLE);
                tomatoThree.setVisibility(View.VISIBLE);
                tomatoFour.setVisibility(View.VISIBLE);
                break;
        }
    }
    public void updateButtons(){

        if (mTimerRunning){
            buttonOne.setText(getResources().getString(R.string.pause));
            buttonOne.setVisibility(View.VISIBLE);
            buttonTwo.setVisibility(View.INVISIBLE);
            buttonThree.setVisibility(View.INVISIBLE);
        }else {
            buttonOne.setText(getResources().getString(R.string.start));
            buttonOne.setVisibility(View.VISIBLE);
            buttonTwo.setVisibility(View.INVISIBLE);
            buttonThree.setVisibility(View.INVISIBLE);
            if (mTimeLeftInMillis != mStartTimeMillis){
                buttonOne.setVisibility(View.INVISIBLE);
                buttonTwo.setVisibility(View.VISIBLE);
                buttonThree.setVisibility(View.VISIBLE);
            }
            if (mTimeLeftInMillis == mBreakTimeMillis || mTimeLeftInMillis == mStartTimeMillis){
                buttonOne.setText(getResources().getString(R.string.start));
                buttonOne.setVisibility(View.VISIBLE);
                buttonTwo.setVisibility(View.INVISIBLE);
                buttonThree.setVisibility(View.INVISIBLE);
            }
        }
    }
    public void workTime(){
        mEntTime = System.currentTimeMillis() + mTimeLeftInMillis;

        countDownTimer = new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCounterText();
            }

            @Override
            public void onFinish() {
                tomato++;
                checkTomato();
                if (tomato == 4){
                    mTimeLeftInMillis = mStartTimeMillis;
                    circularProgressBar.setProgressMax((int)(mStartTimeMillis / 1000));
                    mTimerRunning = false;
                    breakOrWork = false;
                    checkTomato();
                    tomato = 0;
                    updateButtons();
                    updateCounterText();
                }else {
                    mTimerRunning = false;
                    breakOrWork = true;
                    mTimeLeftInMillis = mBreakTimeMillis;
                    circularProgressBar.setProgressMax((int)(mBreakTimeMillis / 1000));
                    updateCounterText();
                    updateButtons();
                }
            }
        }.start();
        mTimerRunning = true;
    }

    public void breakTime(){
        mEntTime = System.currentTimeMillis() + mTimeLeftInMillis;

        breakCountDownTimer = new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCounterText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                breakOrWork = false;
                mTimeLeftInMillis = mStartTimeMillis;
                circularProgressBar.setProgressMax((int)(mStartTimeMillis / 1000));
                updateButtons();
                updateCounterText();
            }
        }.start();
        mTimerRunning = true;
    }

    public void pauseTime(){
        if (!breakOrWork){
            countDownTimer.cancel();
        }else {
            breakCountDownTimer.cancel();
        }
        mTimerRunning = false;
    }

    public void reset (){

        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(this,R.style.MyAlertDialogStyle);
        alert.setTitle(getResources().getString(R.string.warning));
        alert.setMessage(getResources().getString(R.string.stop_this_pomodoro));
        alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mTimeLeftInMillis = mStartTimeMillis;
                circularProgressBar.setProgressMax((int)(mStartTimeMillis / 1000));
                mTimerRunning = false;
                breakOrWork = false;
                tomato = 0;
                checkTomato();
                updateButtons();
                updateCounterText();

            }
        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }

    public void updateCounterText(){

        int minutes = (int)(mTimeLeftInMillis / 1000) / 60;
        int seconds = (int)(mTimeLeftInMillis / 1000) % 60;
        String timeLeft = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        counterTextView.setText(timeLeft);

        circularProgressBar.setProgress((int)(mTimeLeftInMillis / 1000));

    }

    @Override
    public void onStop() {
        super.onStop();
        save();
    }

    public void save(){
        SharedPreferences preferences = this.getSharedPreferences("pomodoro", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("timerRunning",mTimerRunning);
        editor.putLong("millisLeft",mTimeLeftInMillis);
        editor.putLong("endTime",mEntTime);
        editor.putBoolean("breakOrWork",breakOrWork);
        editor.putInt("tomato",tomato);
        editor.apply();

        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences preferences = this.getSharedPreferences("pomodoro", Context.MODE_PRIVATE);

        mTimeLeftInMillis = preferences.getLong("millisLeft",mStartTimeMillis);
        mTimerRunning = preferences.getBoolean("timerRunning",false);
        breakOrWork = preferences.getBoolean("breakOrWork",false);
        tomato = preferences.getInt("tomato",0);

        updateCounterText();
        updateButtons();
        checkTomato();

        if (!breakOrWork){
            circularProgressBar.setProgressMax((int)(mStartTimeMillis / 1000));
        }else {
            circularProgressBar.setProgressMax((int)(mBreakTimeMillis / 1000));
        }

        if (mTimerRunning){
            mEntTime = preferences.getLong("endTime",0);
            mTimeLeftInMillis = mEntTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0){
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                if (!breakOrWork){
                    tomato++;
                    mTimeLeftInMillis = mBreakTimeMillis;
                    circularProgressBar.setProgressMax((int)(mBreakTimeMillis / 1000));
                    breakOrWork = true;
                    if (tomato == 4){
                        mTimeLeftInMillis = mStartTimeMillis;
                        circularProgressBar.setProgressMax((int)(mStartTimeMillis / 1000));
                        mTimerRunning = false;
                        breakOrWork = false;
                        checkTomato();
                        tomato = 0;
                        updateButtons();
                        updateCounterText();
                    }
                }else {
                    mTimeLeftInMillis = mStartTimeMillis;
                    circularProgressBar.setProgressMax((int)(mStartTimeMillis / 1000));
                    breakOrWork = false;
                }
                checkTomato();
                updateCounterText();
                updateButtons();
            }else {
                if (!breakOrWork){
                    workTime();
                }else {
                    breakTime();
                }
            }
        }
    }
}