package com.kastudio.doit.Kanban;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kastudio.doit.R;

public class KanbanPopUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanban_pop_up);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.kanban_pop_up_activity);

        fragment = KanbanPopUp.newInstance();
        fragmentManager.beginTransaction().add(R.id.kanban_pop_up_activity,fragment).commit();

    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context,KanbanPopUpActivity.class);
        return intent;
    }

}