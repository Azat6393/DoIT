package com.kastudio.doit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kastudio.doit.Kanban.ToDoRecyclerView;
import com.kastudio.doit.ToDoList.ToDoListActivity;

import java.util.ArrayList;

public class DashboardToDoListRecyclerView extends RecyclerView.Adapter<DashboardToDoListRecyclerView.PostHolder> {

    private final ArrayList<String> listName;
    private final ArrayList<Integer> listColor;
    private final ArrayList<String> uuid;
    private final Activity mActivity;
    Context mContext;

    public DashboardToDoListRecyclerView (ArrayList<String> listName, ArrayList<Integer> listColor, Activity mActivity, ArrayList<String> uuid){
        this.listName = listName;
        this.listColor = listColor;
        this.mActivity = mActivity;
        this.uuid = uuid;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.dashboard_to_do_list_recycler_view,parent,false);
        mContext = parent.getContext();
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        holder.list.setColorFilter(listColor.get(position));
        holder.forward.setColorFilter(listColor.get(position));
        holder.listText.setText(listName.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ToDoListActivity.class);
                intent.putExtra("uuid",uuid.get(position));
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

            }
        });

    }

    @Override
    public int getItemCount() {
        return listName.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder {

        ImageView list,forward;
        TextView listText;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            list = itemView.findViewById(R.id.dashboard_to_do_list_list_icon);
            forward = itemView.findViewById(R.id.dashboard_to_do_list_forward);
            listText = itemView.findViewById(R.id.dashboard_to_do_list_text_view);

        }
    }
}
