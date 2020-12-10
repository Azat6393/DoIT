package com.kastudio.doit.Kanban;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class KanbanBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

       // if (intent.getStringExtra("update").matches("first")){
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
            Intent intentToMain = new Intent("my.result.receiver");
            intentToMain.putExtra("update",intent.getStringExtra("update"));
            manager.sendBroadcast(intentToMain);
        /*} else if (intent.getStringExtra("update").matches("second")) {
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
            Intent intentToMain = new Intent("my.result.receiver");
            intentToMain.putExtra("update","second");
            manager.sendBroadcast(intentToMain);
        } else if (intent.getStringExtra("update").matches("third")){
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
            Intent intentToMain = new Intent("my.result.receiver");
            intentToMain.putExtra("update","third");
            manager.sendBroadcast(intentToMain);
        }*/
    }
}
