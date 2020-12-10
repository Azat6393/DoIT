package com.kastudio.doit.Kanban;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.kastudio.doit.DoItContentProvider;
import com.kastudio.doit.R;

import java.util.ArrayList;

public class DoneFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<String> note;
    ArrayList<Integer> cardViewColor;
    ArrayList<String> uuid;

    DoneRecyclerView doneRecyclerView;

    LocalBroadcastManager manager;

    public static DoneFragment newInstance(){
        return new DoneFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.done_fragment,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.kanban_done_recycler_view);
        note = new ArrayList<>();
        cardViewColor = new ArrayList<>();
        uuid = new ArrayList<>();

        manager = LocalBroadcastManager.getInstance(getContext());

        getData();

        doneRecyclerView = new DoneRecyclerView(note,cardViewColor,uuid);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(doneRecyclerView);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    public void getData(){
        if (getActivity() != null){
            ContentResolver contentResolver = getActivity().getContentResolver();
            Cursor cursor = contentResolver.query(DoItContentProvider.KANBAN_DONE_URI,null,null,null,null);
            if (cursor != null){
                while (cursor.moveToNext()){
                    note.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.KANBAN_DONE_NOTE)));
                    cardViewColor.add(cursor.getInt(cursor.getColumnIndex(DoItContentProvider.KANBAN_DONE_COLOR)));
                    uuid.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.KANBAN_DONE_UUID)));
                }
            }
        }
    }

    public void clearArrays(){
        if (!note.isEmpty()){
            note.clear();
            uuid.clear();
            cardViewColor.clear();
        }
    }

    String deletedNote, deletedUUID;
    int deletedColor;

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    deletedUUID = uuid.get(position);
                    deletedColor = cardViewColor.get(position);
                    deletedNote = note.get(position);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DoItContentProvider.KANBAN_DOING_NOTE,note.get(position));
                    contentValues.put(DoItContentProvider.KANBAN_DOING_COLOR,cardViewColor.get(position));
                    contentValues.put(DoItContentProvider.KANBAN_DOING_UUID,uuid.get(position));
                    getActivity().getContentResolver().insert(DoItContentProvider.KANBAN_DOING_URI,contentValues);
                    getActivity().getContentResolver().delete(DoItContentProvider.KANBAN_DONE_URI,"uuid=?",new String[]{uuid.get(position)});
                    note.remove(position);
                    cardViewColor.remove(position);
                    uuid.remove(position);
                    doneRecyclerView.notifyItemRemoved(position);
                    update();
                    Snackbar.make(requireView(),deletedNote,Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#56E39F"))
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    contentValues.clear();
                                    contentValues.put(DoItContentProvider.KANBAN_DONE_NOTE,deletedNote);
                                    contentValues.put(DoItContentProvider.KANBAN_DONE_UUID,deletedUUID);
                                    contentValues.put(DoItContentProvider.KANBAN_DONE_COLOR,deletedColor);
                                    getActivity().getContentResolver().insert(DoItContentProvider.KANBAN_DONE_URI,contentValues);
                                    getActivity().getContentResolver().delete(DoItContentProvider.KANBAN_DOING_URI,"uuid=?",new String[]{deletedUUID});
                                    note.add(position,deletedNote);
                                    cardViewColor.add(position,deletedColor);
                                    uuid.add(position,deletedUUID);
                                    doneRecyclerView.notifyItemInserted(position);
                                    update();
                                }
                            }).show();
                    break;
            }
        }
    };

    public void update (){
        Intent intent = new Intent(getContext(),KanbanBroadcastReceiver.class);
        intent.putExtra("update","third");
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("my.result.receiver");

        manager.registerReceiver(broadcastReceiver,intentFilter);

        clearArrays();
        getData();
        doneRecyclerView.notifyDataSetChanged();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("update").matches("second")){
                clearArrays();
                getData();
                doneRecyclerView.notifyDataSetChanged();
            }
        }
    };

}
