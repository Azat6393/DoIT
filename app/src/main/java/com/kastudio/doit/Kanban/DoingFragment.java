package com.kastudio.doit.Kanban;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.kastudio.doit.DoItContentProvider;
import com.kastudio.doit.R;

import java.util.ArrayList;

public class DoingFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<String> note;
    ArrayList<String> uuid;
    ArrayList<Integer> cardViewColor;

    DoingRecyclerView doingRecyclerView;

    LocalBroadcastManager manager;

    public static DoingFragment newInstance(){
        return new DoingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doing_fragment,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        note = new ArrayList<>();
        cardViewColor = new ArrayList<>();
        uuid = new ArrayList<>();
        recyclerView = view.findViewById(R.id.kanban_doing_recycler_view);
        manager = LocalBroadcastManager.getInstance(getContext());

        getData();

        doingRecyclerView = new DoingRecyclerView(note,cardViewColor,uuid);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(doingRecyclerView);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    public void getData(){
        if (getActivity() != null){
            ContentResolver contentResolver = getActivity().getContentResolver();
            Cursor cursor = contentResolver.query(DoItContentProvider.KANBAN_DOING_URI,null,null,null,null);
            if (cursor != null){
                while (cursor.moveToNext()){
                    note.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.KANBAN_DOING_NOTE)));
                    cardViewColor.add(cursor.getInt(cursor.getColumnIndex(DoItContentProvider.KANBAN_DOING_COLOR)));
                    uuid.add(cursor.getString(cursor.getColumnIndex(DoItContentProvider.KANBAN_DOING_UUID)));
                }
            }
        }
    }

    public void clearArrays(){
        if(!note.isEmpty()){
            note.clear();
            cardViewColor.clear();
            uuid.clear();
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
                case ItemTouchHelper.RIGHT:
                    deletedUUID = uuid.get(position);
                    deletedColor = cardViewColor.get(position);
                    deletedNote = note.get(position);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DoItContentProvider.KANBAN_DONE_NOTE,note.get(position));
                    contentValues.put(DoItContentProvider.KANBAN_DONE_COLOR,cardViewColor.get(position));
                    contentValues.put(DoItContentProvider.KANBAN_DONE_UUID,uuid.get(position));
                    getActivity().getContentResolver().insert(DoItContentProvider.KANBAN_DONE_URI,contentValues);
                    getActivity().getContentResolver().delete(DoItContentProvider.KANBAN_DOING_URI,"uuid=?",new String[]{uuid.get(position)});
                    note.remove(position);
                    cardViewColor.remove(position);
                    uuid.remove(position);
                    doingRecyclerView.notifyItemRemoved(position);
                    update();
                    Snackbar.make(requireView(),deletedNote,Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#56E39F"))
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    contentValues.clear();
                                    contentValues.put(DoItContentProvider.KANBAN_DOING_NOTE,deletedNote);
                                    contentValues.put(DoItContentProvider.KANBAN_DOING_UUID,deletedUUID);
                                    contentValues.put(DoItContentProvider.KANBAN_DOING_COLOR,deletedColor);
                                    getActivity().getContentResolver().insert(DoItContentProvider.KANBAN_DOING_URI,contentValues);
                                    getActivity().getContentResolver().delete(DoItContentProvider.KANBAN_DONE_URI,"uuid=?",new String[]{deletedUUID});
                                    note.add(position,deletedNote);
                                    cardViewColor.add(position,deletedColor);
                                    uuid.add(position,deletedUUID);
                                    doingRecyclerView.notifyItemInserted(position);
                                    update();
                                }
                            }).show();
                    break;
                case ItemTouchHelper.LEFT:
                    deletedUUID = uuid.get(position);
                    deletedColor = cardViewColor.get(position);
                    deletedNote = note.get(position);
                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put(DoItContentProvider.KANBAN_TO_DO_NOTE,note.get(position));
                    contentValues1.put(DoItContentProvider.KANBAN_TO_DO_COLOR,cardViewColor.get(position));
                    contentValues1.put(DoItContentProvider.KANBAN_TO_DO_UUID,uuid.get(position));
                    getActivity().getContentResolver().insert(DoItContentProvider.KANBAN_TO_DO_URI,contentValues1);
                    getActivity().getContentResolver().delete(DoItContentProvider.KANBAN_DOING_URI,"uuid=?",new String[]{uuid.get(position)});
                    note.remove(position);
                    cardViewColor.remove(position);
                    uuid.remove(position);
                    doingRecyclerView.notifyItemRemoved(position);
                    update();
                    Snackbar.make(requireView(),deletedNote,Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#56E39F"))
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    contentValues1.clear();
                                    contentValues1.put(DoItContentProvider.KANBAN_DOING_NOTE,deletedNote);
                                    contentValues1.put(DoItContentProvider.KANBAN_DOING_UUID,deletedUUID);
                                    contentValues1.put(DoItContentProvider.KANBAN_DOING_COLOR,deletedColor);
                                    getActivity().getContentResolver().insert(DoItContentProvider.KANBAN_DOING_URI,contentValues1);
                                    getActivity().getContentResolver().delete(DoItContentProvider.KANBAN_TO_DO_URI,"uuid=?",new String[]{deletedUUID});
                                    note.add(position,deletedNote);
                                    cardViewColor.add(position,deletedColor);
                                    uuid.add(position,deletedUUID);
                                    doingRecyclerView.notifyItemInserted(position);
                                    update();
                                }
                            }).show();
                    break;
            }
        }
    };

    public void update (){
        Intent intent = new Intent(getContext(),KanbanBroadcastReceiver.class);
        intent.putExtra("update","second");
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
        doingRecyclerView.notifyDataSetChanged();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("update").matches("first") || intent.getStringExtra("update").matches("third")){
                clearArrays();
                getData();
                doingRecyclerView.notifyDataSetChanged();
            }
        }
    };

}
