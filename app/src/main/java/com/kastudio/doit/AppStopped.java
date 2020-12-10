package com.kastudio.doit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AppStopped extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        SharedPreferences preferences = getSharedPreferences("pomodoro", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("timerRunning",false);
        editor.putLong("millisLeft",1500000);
        editor.putLong("endTime",0);
        editor.putBoolean("breakOrWork",false);
        editor.putInt("tomato",0);
        editor.apply();
    }
}
