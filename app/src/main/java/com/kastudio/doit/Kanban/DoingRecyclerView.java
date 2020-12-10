package com.kastudio.doit.Kanban;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kastudio.doit.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class DoingRecyclerView extends RecyclerView.Adapter<DoingRecyclerView.PostHolder> {

    private final ArrayList<String> note;
    private final ArrayList<Integer> color;
    private final ArrayList<String> uuid;

    int position;

    Context mContext;

    public int getPosition(){
        return position;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public DoingRecyclerView(ArrayList<String> note, ArrayList<Integer> color, ArrayList<String> uuid){
        this.note = note;
        this.color = color;
        this.uuid = uuid;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.to_do_recyler_view,parent,false);
        mContext = parent.getContext();
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        holder.note.setText(note.get(position));
        holder.cardView.setCardBackgroundColor(color.get(position));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = KanbanPopUpActivity.newIntent(mContext);
                intent.putExtra("kanban",2);
                intent.putExtra("note",note.get(position));
                intent.putExtra("color",color.get(position));
                intent.putExtra("uuid",uuid.get(position));
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return note.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder {
        TextView note;
        CardView cardView;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            note = itemView.findViewById(R.id.kanban_to_do_recycler_view_note);
            cardView = itemView.findViewById(R.id.kanban_to_do_recycler_view_card_view);

        }
    }
}
