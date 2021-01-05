package com.kastudio.doit.ToDoList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kastudio.doit.DoItContentProvider;
import com.kastudio.doit.Kanban.KanbanBroadcastReceiver;
import com.kastudio.doit.R;

import java.util.ArrayList;

public class CompletedTaskRecyclerView extends RecyclerView.Adapter<CompletedTaskRecyclerView.PostHolder> {

    Context mContext;
    private final ArrayList<String> completedTask;
    private final ArrayList<String> uuid;
    private final ArrayList<String> note;
    private final ArrayList<String> date;
    private final String listName;
    private final Activity activity;
    private final OnCheckedChangeListener changed;

    public CompletedTaskRecyclerView(ArrayList<String> completedTask,
                                     ArrayList<String> uuid,
                                     ArrayList<String> note,
                                     ArrayList<String> date,
                                     String listName,
                                     Activity activity,
                                     OnCheckedChangeListener changed){
        this.completedTask = completedTask;
        this.uuid = uuid;
        this.note = note;
        this.date = date;
        this.listName = listName;
        this.activity = activity;
        this.changed = changed;
    }

    @NonNull
    @Override
    public CompletedTaskRecyclerView.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.completed_task_recycler_view,parent,false);
        mContext = parent.getContext();
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedTaskRecyclerView.PostHolder holder, int position) {

        holder.textView.setText(completedTask.get(position));
        holder.textView.setPaintFlags(holder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    //holder.textView.setPaintFlags(0);
                    //holder.textView.setTypeface(holder.textView.getTypeface(), Typeface.NORMAL);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DoItContentProvider.TO_DO_LIST_COMPLETED,0);
                    mContext.getContentResolver().update(DoItContentProvider.TO_DO_LIST_URI,contentValues,"uuidTask=?",new String[]{uuid.get(position)});
                    changed.onItemClick(position);
                    holder.checkBox.setChecked(true);
                }
            }
        });

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ToDoListPopUpActivity.newIntent(mContext);
                intent.putExtra("check",2);
                intent.putExtra("listName",listName);
                intent.putExtra("task",completedTask.get(position));
                intent.putExtra("note",note.get(position));
                intent.putExtra("uuid",uuid.get(position));
                intent.putExtra("date",date.get(position));
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return completedTask.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder {

        TextView textView;
        CardView cardView;
        CheckBox checkBox;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.completed_task_recycler_view_text_view);
            cardView = itemView.findViewById(R.id.completed_task_recycler_view_card_view);
            checkBox = itemView.findViewById(R.id.completed_task_recycler_view_check_box);

        }
    }

    interface OnCheckedChangeListener{
        void onItemClick(int position);
    }

}
