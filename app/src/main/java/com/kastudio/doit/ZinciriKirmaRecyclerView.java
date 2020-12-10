package com.kastudio.doit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kastudio.doit.ZinciriKirmaPackage.ZinciriKirma;

import java.util.ArrayList;

public class ZinciriKirmaRecyclerView extends RecyclerView.Adapter<ZinciriKirmaRecyclerView.PostHolder> {

    private int position;
    private final ArrayList<String> targets;
    Context mContext;
    private final Activity mActivity;
    private final ArrayList<Integer> finish;
    private final ArrayList<String> uuid;

    public int getPosition(){
        return position;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public ZinciriKirmaRecyclerView(ArrayList<String> targets, Activity mActivity, ArrayList<Integer> finish, ArrayList<String> uuid){
        this.targets = targets;
        this.mActivity = mActivity;
        this.finish = finish;
        this.uuid = uuid;
    }


    @NonNull
    @Override
    public ZinciriKirmaRecyclerView.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.zinrici_kirma_recycler_view,parent,false);
        mContext = parent.getContext();
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZinciriKirmaRecyclerView.PostHolder holder, int position) {

        holder.textView.setText(targets.get(position));
        if (finish.get(position) == 1){
            holder.textView.setTextColor(Color.parseColor("#56E39F"));
            //holder.textView.setTypeface(holder.textView.getTypeface(), Typeface.BOLD);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ZinciriKirma.class);
                intent.putExtra("target",targets.get(position));
                intent.putExtra("uuid",uuid.get(position));
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

    }

    @Override
    public int getItemCount() {
        return targets.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.zinciri_kirma_recycler_view_text_view);

        }
    }
}
