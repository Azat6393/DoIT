package com.kastudio.doit;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.kastudio.doit.Note.NoteFragment;

public class ViewPagerMainActivity extends FragmentPagerAdapter {

    Context mContext;

    public ViewPagerMainActivity(@NonNull FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 0){
            return DashboardFragment.newInstance();
        }else if (position == 1){
            return NoteFragment.newInstance();
        }else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
