package com.kastudio.doit.Kanban;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.kastudio.doit.DoItContentProvider;
import com.kastudio.doit.MainActivity;
import com.kastudio.doit.R;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.util.UUID;

public class KanbanActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanban);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        viewPager = findViewById(R.id.kanban_view_pager);
        tabLayout = findViewById(R.id.kanban_tab_layout);
        button = findViewById(R.id.kanban_back_button);

        ViewPagerKanban viewPagerKanban = new ViewPagerKanban(getSupportFragmentManager(),KanbanActivity.this);
        viewPager.setAdapter(viewPagerKanban);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(getResources().getString(R.string.to_do));
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.doing));
        tabLayout.getTabAt(2).setText(getResources().getString(R.string.done));

    }

    public void kanbanBackButton(View view){
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}