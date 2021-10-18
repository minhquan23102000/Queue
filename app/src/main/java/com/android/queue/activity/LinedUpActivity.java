package com.android.queue.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.queue.R;
import com.android.queue.firebase.realtimedatabase.RoomEntryRequester;
import com.android.queue.models.Participant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class LinedUpActivity extends AppCompatActivity {

    private TextView textView;
    private RoomEntryRequester roomEntryRequester;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lined_up);
        textView = findViewById(R.id.Participant);


        Intent intent=getIntent();
        String key = intent.getStringExtra("keyRoom");

        roomEntryRequester = new RoomEntryRequester(LinedUpActivity.this);
        databaseReference= roomEntryRequester.find(key);
        Query query = databaseReference.child("participantList");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    textView.append(dataSnapshot.child("waiterName").getValue(String.class)+"/n");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}