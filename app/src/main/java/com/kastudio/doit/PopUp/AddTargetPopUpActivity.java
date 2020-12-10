package com.kastudio.doit.PopUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kastudio.doit.R;

public class AddTargetPopUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_target_pop_up);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.add_a_target_pop_up_activity);

        fragment = AddTargetPopUp.newInstance();
        fragmentManager.beginTransaction().add(R.id.add_a_target_pop_up_activity,fragment).commit();

    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context,AddTargetPopUpActivity.class);
        return intent;
    }
}