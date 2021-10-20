package com.android.queue.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.queue.R;
import com.android.queue.SessionManager;
import com.android.queue.adapter.ParticipantAdapter;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract;
import com.android.queue.firebase.realtimedatabase.RoomEntryRequester;
import com.android.queue.models.Participant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LinedUpActivity extends AppCompatActivity {

    private RoomEntryRequester roomEntryRequester;
    private DatabaseReference databaseReference;
    private TextView txtRoomName;
    private TextView txtHostName;
    private TextView txtHostPhone;
    private TextView txtAdress;
    private TextView txtWaiterNumber;
    private TextView txtTime;
    private Button skipBtn;
    private Button leavebtn;
    private SessionManager sessionManager;
    private static RecyclerView recyclerView;
    private static ParticipantAdapter mP_Adapter;
    private List<Participant> listParticipant;
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lined_up);

        txtRoomName = findViewById(R.id.rNameTv);
        txtHostName = findViewById(R.id.hostNameTv);
        txtHostPhone = findViewById(R.id.hostPhoneTv);
        txtAdress = findViewById(R.id.hostAdressTv);
        txtWaiterNumber = findViewById(R.id.sisoTv);
        txtTime = findViewById(R.id.time);
        skipBtn = findViewById(R.id.skipBtn);
        leavebtn = findViewById(R.id.leaveBtn);

        sessionManager = new SessionManager(this);
        
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
        //updateSTT();
        getListParticipant();

        leavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveRoom();
                getListParticipant();
            }
        });



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
                        if(longNumber!=-1){
                            mParticipant = new Participant(strPhone,strName,longNumber,strState);
                            listParticipant.add(mParticipant);
                        }
                    }
                    mP_Adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(mP_Adapter);
                    getDataRoomFromFirebase();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LinedUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDataRoomFromFirebase(){
        RoomEntryRequester tempRoomEntryRequeser=new RoomEntryRequester(LinedUpActivity.this);
        DatabaseReference tempDatabaseRefernce=tempRoomEntryRequeser.find(key);
        Query query = tempDatabaseRefernce.child("roomData");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String str1=snapshot.child("roomName").getValue(String.class);
                    String str2=snapshot.child("hostPhone").getValue(String.class);
                    String str3=snapshot.child("address").getValue(String.class);
                    Long longMax=snapshot.child("maxParticipant").getValue(Long.class);
                    double waitTime= snapshot.child("timeWait").getValue(double.class);
                    int count=mP_Adapter.getIsWaiterCount();
                    txtRoomName.setText("Tên phòng: "+str1);
                    txtHostPhone.setText(str2);
                    txtAdress.setText(str3);
                    txtWaiterNumber.setText("SS: "+String.valueOf(count) + " / "+longMax.toString());
                    double pos=findPositionUser();
                    double time=waitTime*(pos-1);
                    txtTime.setText(String.valueOf(time)+" phút");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LinedUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private double findPositionUser(){
        String waiterPhone=sessionManager.getUserData().getString(QueueDatabaseContract.UserEntry.PHONE_ARM);
        int index= 0;
        for (Participant item:listParticipant) {
            if(item.getWaiterPhone().equals(waiterPhone))
                return index+1;
            else index+=1;
        }
        return -1;
    }

    private void leaveRoom(){
        String waiterPhone=sessionManager.getUserData().getString(QueueDatabaseContract.UserEntry.PHONE_ARM);
        RoomEntryRequester tempRoomEntryRequeser=new RoomEntryRequester(LinedUpActivity.this);
        DatabaseReference tempDatabaseRefernce=tempRoomEntryRequeser.find(key);
        Query query = tempDatabaseRefernce.child("participantList");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                       String waiterKey=dataSnapshot.getKey();
                       String str=dataSnapshot.child("waiterPhone").getValue(String.class);
                       if(str.equals(waiterPhone)){
                           updateAfterLeave(waiterKey);
                           tempRoomEntryRequeser.updateTotalParticipantafterChange(key,mP_Adapter.getItemCount());
                           //long i=countTotalParticipant(key);
                           //tempRoomEntryRequeser.updateWaiterNumberafterChange(key,i);
                           //Intent intent=new Intent(LinedUpActivity.this,MainActivity.class);
                           //startActivity(intent);
                       }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LinedUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAfterLeave(String waiterKey){
        RoomEntryRequester tempRoomEntryRequeser=new RoomEntryRequester(LinedUpActivity.this);
        DatabaseReference tempDatabaseRefernce=tempRoomEntryRequeser.find(key);
        tempDatabaseRefernce.child("participantList").child(waiterKey).child("waiterNumber").setValue(-1);
        tempDatabaseRefernce.child("participantList").child(waiterKey).child("waiterState").setValue(QueueDatabaseContract.RoomEntry.ParticipantListEntry.STATE_IS_LEFT);
    }

    private void updateSTT(){
        RoomEntryRequester tempRoomEntryRequeser=new RoomEntryRequester(LinedUpActivity.this);
        DatabaseReference tempDatabaseRefernce=tempRoomEntryRequeser.find(key);
        long i=countTotalParticipant(key);
        //tempRoomEntryRequeser.updateWaiterNumberafterChange(key,i);
    }

    private long count;
    private long countTotalParticipant(String roomKey){
        RoomEntryRequester tempRoomEntryRequeser=new RoomEntryRequester(LinedUpActivity.this);
        DatabaseReference tempDatabaseRefernce=tempRoomEntryRequeser.find(key);
        Query query = tempDatabaseRefernce.child("roomData");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    count=snapshot.child("totalParticipant").getValue(long.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LinedUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        return count;
    }
}