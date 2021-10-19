package com.android.queue.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.queue.R;
import com.android.queue.adapter.ParticipantAdapter;
import com.android.queue.firebase.realtimedatabase.RoomEntryRequester;
import com.android.queue.models.Participant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LinedUpActivity extends AppCompatActivity {

    private RoomEntryRequester roomEntryRequester;
    private DatabaseReference databaseReference;
    private static RecyclerView recyclerView;
    private static ParticipantAdapter mP_Adapter;
    private List<Participant> listParticipant;
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lined_up);

        recyclerView = findViewById(R.id.rcv_participant);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        listParticipant = new ArrayList<>();
        mP_Adapter = new ParticipantAdapter(listParticipant);

        recyclerView.setAdapter(mP_Adapter);



        Intent intent=getIntent();
        key = intent.getStringExtra("keyRoom");

        roomEntryRequester = new RoomEntryRequester(LinedUpActivity.this);
        //databaseReference= roomEntryRequester.find(key).getDatabase().getReference("participantList");
        databaseReference= roomEntryRequester.find(key);
        getListParticipant();

    }

    private void getListParticipant(){
        Query query = databaseReference.child("participantList");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listParticipant.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                       //Participant mParticipant=dataSnapshot.getValue(Participant.class);
                        Participant mParticipant;
                        String strName = dataSnapshot.child("waiterName").getValue(String.class);
                        Long longNumber = dataSnapshot.child("waiterNumber").getValue(Long.class);
                        String strPhone = dataSnapshot.child("waiterPhone").getValue(String.class);
                        String strState = dataSnapshot.child("waiterState").getValue(String.class);
                        mParticipant = new Participant(strPhone,strName,longNumber,strState);
                        listParticipant.add(mParticipant);
                    }
                    mP_Adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(mP_Adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LinedUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }
}