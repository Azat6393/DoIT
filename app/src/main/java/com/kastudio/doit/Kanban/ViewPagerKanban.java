package com.kastudio.doit.Kanban;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerKanban extends FragmentPagerAdapter {

    Context mContext;

    public ViewPagerKanban(@NonNull FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 0){
            return ToDoFragment.newInstance();
        }else if (position == 1){
            return DoingFragment.newInstance();
        }else {
            return DoneFragment.newInstance();
        }

    }

    @Override
    public int getCount() {
        return 3;
    }
}
