package com.kastudio.doit.Chain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kastudio.doit.DoItContentProvider;
import com.kastudio.doit.R;

import java.util.ArrayList;

public class ZinciriKirma extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView targetTextView;
    Button stepButton;
    ImageView dotsButton;

    int daySize, finish;
    ArrayList<Integer> target;
    boolean visibility = false;
    int lastClick, startTime;
    ZinciriKirmaCircleRecyclerView zinciriKirmaCircleRecyclerView;
    String targetName, uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zinciri_kirma);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        recyclerView = findViewById(R.id.zinciri_kirma_activity_recycler_view);
        targetTextView = findViewById(R.id.zinciri_kirma_target_text_view);
        stepButton = findViewById(R.id.zinciri_kirma_step_button);
        dotsButton = findViewById(R.id.zinciri_kirma_dots);

        Intent intent = getIntent();
        targetName = intent.getStringExtra("target");
        uuid = intent.getStringExtra("uuid");
        targetTextView.setText(targetName);
        target = new ArrayList<>();
        getData();
        zinciriKirmaCircleRecyclerView = new ZinciriKirmaCircleRecyclerView(daySize,target,visibility);
        recyclerView.setLayoutManager(new GridLayoutManager(this,6));
        recyclerView.setAdapter(zinciriKirmaCircleRecyclerView);

        if (finish == 1){
            Toast.makeText(this, getResources().getString(R.string.congratulation), Toast.LENGTH_SHORT).show();
            stepButton.setEnabled(false);
        }
        registerForContextMenu(dotsButton);

        dotsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContextMenu(v);
            }
        });

    }

    public void getData(){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(DoItContentProvider.ZINCIRI_KIRMA_URI,null,"uuid=?",new String[]{uuid},null);

        if (cursor != null){
            while (cursor.moveToNext()){
                daySize = cursor.getInt(cursor.getColumnIndex(DoItContentProvider.ZINCIRI_KIRMA_DAYS));
                target.add(cursor.getInt(cursor.getColumnIndex(DoItContentProvider.ZINCIRI_KIRMA_COMPLETED)));
                lastClick = cursor.getInt(cursor.getColumnIndex(DoItContentProvider.ZINCIRI_KIRMA_LAST));
                startTime = cursor.getInt(cursor.getColumnIndex(DoItContentProvider.ZINCIRI_KIRMA_START));
                finish = cursor.getInt(cursor.getColumnIndex(DoItContentProvider.ZINCIRI_KIRMA_FINISH));
            }
        }
    }

    public void updateLastClick(){
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(DoItContentProvider.ZINCIRI_KIRMA_LAST,(int) (System.currentTimeMillis() / (3600000 * 24)));
        getContentResolver().update(DoItContentProvider.ZINCIRI_KIRMA_URI,contentValues1,"uuid=?",new String[]{uuid});
    }

    public void zincirBackOnClick(View view){
        finish();
    }

    public void stepButton(View view){

        if (lastClick == 0){
            ContentValues contentValues = new ContentValues();
            contentValues.put(DoItContentProvider.ZINCIRI_KIRMA_COMPLETED,true);
            getContentResolver().update(DoItContentProvider.ZINCIRI_KIRMA_URI,contentValues,"id=? and uuid=?",new String[]{"1",uuid});
            target.add(0,1);
            zinciriKirmaCircleRecyclerView.notifyItemChanged(0);
            updateLastClick();
            getData();
        }else {
            int realTime = (int) (System.currentTimeMillis() / (3600000 * 24));
            if (realTime == lastClick){
                Toast.makeText(this, getResources().getString(R.string.already_made_step), Toast.LENGTH_SHORT).show();
            }else {
                if (realTime < lastClick){
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DoItContentProvider.ZINCIRI_KIRMA_COMPLETED, true);
                    int x;
                    if (realTime - startTime + 1 > daySize){
                        x = daySize;
                    }else {
                        x = realTime - startTime + 1;
                    }
                    getContentResolver().update(DoItContentProvider.ZINCIRI_KIRMA_URI, contentValues, "id=? and uuid=?", new String[]{""+x, uuid});
                    target.add(x - 1, 1);
                    zinciriKirmaCircleRecyclerView.notifyItemChanged(x - 1);
                    target.clear();
                    if (x >= daySize){
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(DoItContentProvider.ZINCIRI_KIRMA_FINISH,1);
                        getContentResolver().update(DoItContentProvider.ZINCIRI_KIRMA_URI,contentValues1,"uuid=?",new String[]{uuid});
                        stepButton.setEnabled(false);
                    }else {
                        updateLastClick();
                    }
                    getData();
                }
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        switch (v.getId()){
            case R.id.zinciri_kirma_dots:
                menu.add(0,1,0,getResources().getString(R.string.delete));
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 1){
            AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);
            alert.setTitle(getResources().getString(R.string.warning));
            alert.setMessage(getResources().getString(R.string.are_you_sure_delete_target));
            alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getContentResolver().delete(DoItContentProvider.ZINCIRI_KIRMA_URI,"uuid=?",new String[]{uuid});
                    finish();
                }
            }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}