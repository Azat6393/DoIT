package com.kastudio.doit.Note;

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

public class NoteRecyclerView extends RecyclerView.Adapter<NoteRecyclerView.PostHolder> {

    private final ArrayList<String> note;
    private final ArrayList<String> title;
    private final ArrayList<String> date;
    private final ArrayList<Integer> color;
    private final ArrayList<String> uuid;
    Context mContext;

    private int position;

    public int getPosition(){
        return position;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public NoteRecyclerView(ArrayList<String> note, ArrayList<String> title, ArrayList<String> date, ArrayList<Integer> color, ArrayList<String> uuid){
        this.note = note;
        this.title = title;
        this.date = date;
        this.color = color;
        this.uuid = uuid;
    }

    @NonNull
    @Override
    public NoteRecyclerView.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.note_recycler_view,parent,false);
        mContext = parent.getContext();
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteRecyclerView.PostHolder holder, int position) {

        holder.titleTextView.setText(title.get(position));
        holder.noteTextView.setText(note.get(position));
        holder.dateTextView.setText(date.get(position));
        holder.cardView.setCardBackgroundColor(color.get(position));

        if (holder.titleTextView.getText().equals("")){
            holder.titleTextView.setHeight(0);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = NotePopUpActivity.newIntent(mContext);
                intent.putExtra("title",title.get(position));
                intent.putExtra("note",note.get(position));
                intent.putExtra("date",date.get(position));
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

        TextView titleTextView, noteTextView, dateTextView;
        CardView cardView;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_recycler_view_title);
            noteTextView = itemView.findViewById(R.id.note_recycler_view_note);
            dateTextView = itemView.findViewById(R.id.note_recycler_view_time);
            cardView = itemView.findViewById(R.id.note_recycler_view_card_view);
        }
    }
}
