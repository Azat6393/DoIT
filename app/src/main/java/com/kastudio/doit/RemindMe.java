package com.kastudio.doit;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class RemindMe extends Worker {

    Context mContext;

    public RemindMe(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        String taskName = data.getString("taskName");
        String taskUUID = data.getString("uuid");
        cancelFromDataBase(taskUUID);
        Notification notification = new NotificationCompat.Builder(mContext,"channel1")
                .setSmallIcon(R.drawable.logo_foreground_image)
                .setContentTitle(mContext.getResources().getString(R.string.reminder))
                .setContentText(taskName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(mContext);
        manager.notify(1, notification);

        return Result.success();
    }

    public void cancelFromDataBase(String uuid){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DoItContentProvider.TO_DO_LIST_REMINDER,false);
        mContext.getContentResolver().update(DoItContentProvider.TO_DO_LIST_URI,contentValues,"uuidTask=?",new String[]{uuid});
    }

}
