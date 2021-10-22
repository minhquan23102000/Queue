package com.android.queue.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.queue.R;
import com.android.queue.SessionManager;
import com.android.queue.adapters.MyWaiterAdapter;
import com.android.queue.firebase.realtimedatabase.RoomEntryRequester;
import com.android.queue.models.Participant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.RoomEntry.*;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WaiterListFragment extends Fragment {

    private CheckBox showAll;
    private AutoCompleteTextView filter;
    private ListView listView;
    private SessionManager sessionManager;
    private RoomEntryRequester roomEntryRequester;
    private DatabaseReference roomReference;
    private ArrayList<Participant> participantList;
    private MyWaiterAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getContext());
        roomEntryRequester = new RoomEntryRequester(getContext());
        roomReference = roomEntryRequester.find(sessionManager.getCurrentRoomKey());
        roomReference.child(ParticipantListEntry.ROOT_NAME).addValueEventListener(listListener);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participant_list, container, false);
        showAll = view.findViewById(R.id.showAll);
        filter = view.findViewById(R.id.filled_exposed_dropdown);
        listView = view.findViewById(R.id.list);

        //Kick out adapter
        participantList = new ArrayList<>();
        adapter = new MyWaiterAdapter(view.getContext(), participantList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private ValueEventListener listListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull  DataSnapshot snapshot) {
            participantList = new ArrayList<>();
            if (snapshot.getChildrenCount() > 0) {
                for (DataSnapshot waiter: snapshot.getChildren()) {
                    participantList.add(waiter.getValue(Participant.class));
                }
            }
            adapter.addAll(participantList);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull  DatabaseError error) {
            Toast.makeText(getContext(), "Lỗi: " + error.getDetails(), Toast.LENGTH_LONG).show();
        }
    };
}
