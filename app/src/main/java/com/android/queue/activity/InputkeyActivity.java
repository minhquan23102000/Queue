package com.android.queue.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.queue.R;
import com.android.queue.SessionManager;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract;
import com.android.queue.firebase.realtimedatabase.RoomEntryRequester;
import com.android.queue.models.Participant;
import com.android.queue.models.RoomData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.RoomEntry;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.RoomEntry.RoomDataEntry;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.RoomEntry.ParticipantListEntry;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class InputkeyActivity extends AppCompatActivity {

    private ImageButton scanBtn;
    private TextInputLayout txtKey;
    private MaterialTextView roomName;
    private MaterialTextView roomTotal;
    private Button btnJoin;
    private RoomEntryRequester roomEntryRequester;
    private DatabaseReference databaseReference;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputkey);
        scanBtn = findViewById(R.id.scanBtn);
        txtKey = findViewById(R.id.iKey);
        btnJoin = findViewById(R.id.joinBtn);
        roomName = findViewById(R.id.welcomeRoom);
        roomTotal = findViewById(R.id.statusRoom);


        Intent intent=getIntent();
        String str = intent.getStringExtra("Key");

        txtKey.getEditText().setText(str);

        setWelcomeRoomName(str);
        sessionManager = new SessionManager(this);


        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String key= txtKey.getEditText().getText().toString();
                //Kiểm tra key với data firebase and add Participant
                if(key!=null){
                    roomEntryRequester = new RoomEntryRequester(InputkeyActivity.this);
                    databaseReference= roomEntryRequester.find(key);
                    Query query = databaseReference.child("roomData");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                //Add Partipant
                                String waiterPhone=sessionManager.getUserData().getString(QueueDatabaseContract.UserEntry.PHONE_ARM);
                                String waiterName=sessionManager.getUserData().getString(QueueDatabaseContract.UserEntry.FULL_NAME_ARM);
                                String waiterState= "IsWait";
                                Participant participant = new Participant(waiterPhone,waiterName,waiterState);
                                RoomData thisRoom=snapshot.getValue(RoomData.class);
                                roomEntryRequester.addParticipant(participant,key);
                                //Chuyển qua activity xếp hàng
                                Intent intent = new Intent(InputkeyActivity.this,LinedUpActivity.class);
                                intent.putExtra("keyRoom",key);
                                startActivity(intent);
                            }else{
                                roomName.setText("Phòng không tồn tại");
                                roomTotal.setText("");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }else{
                    txtKey.setError("Khóa không được để trống");
                }
            }
        });
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(InputkeyActivity.this,ScanQRActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setWelcomeRoomName(String str){
        if(str!=null){
            RoomEntryRequester tempRoom = new RoomEntryRequester(InputkeyActivity.this);
            DatabaseReference tempData= tempRoom.find(str);
            Query query = tempData.child("roomData");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            RoomData thisRoom=snapshot.getValue(RoomData.class);

                            roomName.setText("Welcome "+thisRoom.roomName);
                            roomTotal.setText(thisRoom.totalParticipant.toString()+"/"+thisRoom.maxParticipant.toString());
                        }else{
                            roomName.setText("Phòng không tồn tại");
                            roomTotal.setText("");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
    }
