package com.kastudio.doit.Chain;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kastudio.doit.R;

import java.util.ArrayList;

public class ZinciriKirmaCircleRecyclerView extends RecyclerView.Adapter<ZinciriKirmaCircleRecyclerView.PostHolder> {


    private final ArrayList<Integer> target;
    private final int daySize;
    private final boolean visibility;

    public ZinciriKirmaCircleRecyclerView(int daySize, ArrayList<Integer> target, boolean visibility){
        this.daySize = daySize;
        this.target = target;
        this.visibility = visibility;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.zinciri_kirma_circle_recycler_view,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        if (target.get(position) == 1){
            holder.imageView.setColorFilter(Color.parseColor("#56E39F"));
            System.out.println("asdsadad   " + position);
        }else {
            holder.imageView.setColorFilter(Color.parseColor("#C9C9C9"));
        }

        int x = position + 1;
        holder.count.setText("" + x);

        //change last imageView
        if (position == daySize - 1){
            holder.view.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return daySize;
    }

    public class PostHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        View view, view2;
        TextView count;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.zinciri_kirma_circle_recycler_view_image_view);
            view = itemView.findViewById(R.id.zinciri_kirma_circle_recycler_view_line_right);
            view2 = itemView.findViewById(R.id.zinciri_kirma_circle_recycler_view_line_left);
            count = itemView.findViewById(R.id.zinciri_kirma_recycler_view_size);
        }
    }
}
