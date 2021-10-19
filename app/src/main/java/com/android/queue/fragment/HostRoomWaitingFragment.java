package com.android.queue.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.queue.R;
import com.android.queue.SessionManager;
import com.android.queue.adapter.StatsRoomAdapter;
import com.android.queue.firebase.realtimedatabase.RoomEntryRequester;
import com.android.queue.models.Room;
import com.android.queue.models.RoomData;
import com.android.queue.models.StatsRoomDataContract;
import com.android.queue.utils.TimestampHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.RoomEntry.RoomDataEntry;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.RoomEntry.ParticipantListEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HostRoomWaitingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HostRoomWaitingFragment extends Fragment {

    //Init view
    private MaterialTextView waiterNameTv;
    private MaterialTextView waiterPhoneTv;
    private MaterialTextView waiterNumberTv;
    private MaterialButton doneBtn;
    private MaterialTextView skipBtn;
    private TextView nextWaiterNameTv;
    private TextView nextWaterPhoneTv;
    private MaterialButton viewListParticipantBtn;
    private ListView statsRoomListView;

    //Init session and firebase service
    private SessionManager sessionManager;
    private RoomEntryRequester roomEntryRequester;

    //Init context and activity
    private Context mContext;
    private Activity mActivity;

    //Init model for this Room
    private Room thisRoom;

    //Init data reference for this room
    private DatabaseReference thisRoomReference;

    //Init data as a hashmap for room stats listview
    private HashMap<String, String> statsRoom;

    //Init adapter for list view
    private StatsRoomAdapter statsRoomAdapter;

    public static HostRoomWaitingFragment newInstance() {
        HostRoomWaitingFragment fragment = new HostRoomWaitingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        mActivity = getActivity();
        mContext = getContext();
        sessionManager = new SessionManager(mContext);
        roomEntryRequester = new RoomEntryRequester(mContext);
        thisRoomReference = roomEntryRequester.find(sessionManager.getCurrentRoomKey());
        thisRoom = new Room();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_host_room_waiting, container, false);

        //Hook view
        waiterNameTv = view.findViewById(R.id.waiterNameTv);
        waiterPhoneTv = view.findViewById(R.id.waiterPhoneTv);
        waiterNumberTv = view.findViewById(R.id.waiterNumberTv);
        doneBtn = view.findViewById(R.id.doneBtn);
        skipBtn = view.findViewById(R.id.skipBtn);
        nextWaiterNameTv = view.findViewById(R.id.nextWaiterNameTv);
        nextWaterPhoneTv = view.findViewById(R.id.nextWaiterPhoneTv);
        viewListParticipantBtn = view.findViewById(R.id.viewListParticipantBtn);
        statsRoomListView = view.findViewById(R.id.statsRoomListView);

        //Add roomData value listener to update view when the data is change in firebase
        thisRoomReference.child(RoomDataEntry.ROOT_NAME).addValueEventListener(roomDataValueListener);

        //Create adapter and set to list view
        statsRoom = new HashMap<>();
        statsRoomAdapter = new StatsRoomAdapter(mContext, statsRoom);
        statsRoomListView.setAdapter(statsRoomAdapter);

        return view;
    }


    private final ValueEventListener roomDataValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            thisRoom.roomData = snapshot.getValue(RoomData.class);
            //Put value into a hash map for the adapter of listview stats
            statsRoom.put(StatsRoomDataContract.TOTAL_PARTICIPANT, thisRoom.roomData.totalParticipant + "/" + thisRoom.roomData.maxParticipant);
            statsRoom.put(StatsRoomDataContract.TOTAL_WAIT, thisRoom.roomData.totalParticipant - thisRoom.roomData.currentWait + "");
            statsRoom.put(StatsRoomDataContract.TOTAL_DONE, thisRoom.roomData.totalDone + "");
            statsRoom.put(StatsRoomDataContract.TOTAL_SKIP, thisRoom.roomData.totalSkip + "");
            statsRoom.put(StatsRoomDataContract.TOTAL_LEFT, thisRoom.roomData.totalLeft + "");
            statsRoomAdapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
}