package com.kastudio.doit.ToDoList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kastudio.doit.DoItContentProvider;
import com.kastudio.doit.Kanban.KanbanBroadcastReceiver;
import com.kastudio.doit.R;

import java.util.ArrayList;

public class TaskRecyclerView extends RecyclerView.Adapter<TaskRecyclerView.PostHolder> {

    Context mContext;
    private final ArrayList<String> task;
    private final ArrayList<String> uuidTask;
    private final ArrayList<String> note;
    private final ArrayList<String> date;
    private final String listName;
    private final Activity activity;

    public TaskRecyclerView (ArrayList<String> task, ArrayList<String> uuidTask, ArrayList<String> note, ArrayList<String> date, String listName, Activity activity){
        this.task = task;
        this.uuidTask = uuidTask;
        this.note = note;
        this.date = date;
        this.listName = listName;
        this.activity = activity;
    }

    @NonNull
    @Override
    public TaskRecyclerView.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.task_recycler_view,parent,false);
        mContext = parent.getContext();
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskRecyclerView.PostHolder holder, int position) {

        holder.textView.setText(task.get(position));

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    holder.textView.setPaintFlags(holder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DoItContentProvider.TO_DO_LIST_COMPLETED,1);
                    mContext.getContentResolver().update(DoItContentProvider.TO_DO_LIST_URI,contentValues,"uuidTask=?",new String[]{uuidTask.get(position)});
                    Intent intent = activity.getIntent();
                    activity.finish();
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0,0);
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ToDoListPopUpActivity.newIntent(mContext);
                intent.putExtra("check",1);
                intent.putExtra("listName",listName);
                intent.putExtra("task",task.get(position));
                intent.putExtra("note",note.get(position));
                intent.putExtra("uuid",uuidTask.get(position));
                intent.putExtra("date",date.get(position));
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return task.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder {

        TextView textView;
        CardView cardView;
        CheckBox checkBox;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.task_recycler_view_text_view);
            cardView = itemView.findViewById(R.id.task_recycler_view_card_view);
            checkBox = itemView.findViewById(R.id.task_recycler_view_check_box);
        }
    }
}
