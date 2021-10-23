package com.android.queue.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.queue.R;
import com.android.queue.SessionManager;
import com.android.queue.adapters.ParticipantAdapter;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract;
import com.android.queue.firebase.realtimedatabase.RoomEntryRequester;
import com.android.queue.models.Participant;
import com.android.queue.models.Room;
import com.android.queue.models.RoomData;
import com.android.queue.utils.NotificationDevice;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private TextView txtRoomName;
    private TextView txtHostName;
    private TextView txtHostPhone;
    private TextView txtAdress;
    private TextView txtTotalParticipant;
    private TextView txtTime;
    private Button skipBtn;
    private Button leavebtn;
    private SessionManager sessionManager;
    private static RecyclerView recyclerView;
    private static ParticipantAdapter mP_Adapter;
    private List<Participant> listParticipant;
    private String key;
    private long indexWaiter, indexWaiterAfterChange = -100;
    private boolean skip;
    CountDownTimer w;
    private boolean isTimer;

    private Room thisRoom = new Room();
    private Participant currentParticipant;


    //private long[] numberList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lined_up);

        txtRoomName = findViewById(R.id.hostNameTv);
        //txtHostName = findViewById(R.id.hostNameTv);
        txtHostPhone = findViewById(R.id.hostPhoneTv);
        txtAdress = findViewById(R.id.hostAdressTv);
        txtTotalParticipant = findViewById(R.id.sisoTv);
        txtTime = findViewById(R.id.time);
        skipBtn = findViewById(R.id.skipBtn);
        leavebtn = findViewById(R.id.leaveBtn);
        skip = false;//isTimer=false;

        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.rcv_participant);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        listParticipant = new ArrayList<>();
        mP_Adapter = new ParticipantAdapter(LinedUpActivity.this, listParticipant);

        recyclerView.setAdapter(mP_Adapter);


        Intent intent = getIntent();
        key = intent.getStringExtra("keyRoom");
        sessionManager.initKeyAfterUserJoinRoom(key);

        roomEntryRequester = new RoomEntryRequester(LinedUpActivity.this);
        databaseReference = roomEntryRequester.find(key);
        getListParticipant();


        leavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //w.cancel();
                onBackPressed();
                leaveRoom();
                //getListParticipant();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentParticipant.waiterNumber < thisRoom.roomData.totalParticipant) {
                    //w.cancel();
                    skip = true;
                    changeNumberAfterSkip();
                    getListParticipant();
                } else {
                    Toast.makeText(view.getContext(), "Bạn đã ở cuối hàng", Toast.LENGTH_SHORT).show();
                }
                //onBackPressed();

            }
        });
        //Value event listener for get room data
        RoomEntryRequester tempRoomEntryRequeser = new RoomEntryRequester(LinedUpActivity.this);
        DatabaseReference tempDatabaseRefernce = tempRoomEntryRequeser.find(key);
        Query roomListener = tempDatabaseRefernce.child("roomData");
        roomListener.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    thisRoom.roomData = snapshot.getValue(RoomData.class);
                    String str1 = thisRoom.roomData.roomName;
                    String str2 = thisRoom.roomData.hostPhone;
                    String str3 = thisRoom.roomData.address;
                    Long longMax = thisRoom.roomData.maxParticipant;
                    //int count = mP_Adapter.getIsWaiterCount();
                    txtRoomName.setText("Tên phòng: " + str1);
                    txtHostPhone.setText(str2);
                    txtAdress.setText(str3);
                    txtTotalParticipant.setText("SS: " + thisRoom.roomData.totalParticipant + " / " + longMax);
                    /*double pos=findPositionUser();
                    double waitTime= snapshot.child("timeWait").getValue(double.class);
                    double waitTimeDelay= snapshot.child("timeDelay").getValue(double.class);
                    long time=(long)(waitTime+waitTimeDelay)*(long)(pos-1);
                    txtTime.setText(String.valueOf(time)+" phút");*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LinedUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });


        //Value event listener to update waiter list and time count down
        Query query = databaseReference.child("participantList");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listParticipant.clear();
                    Bundle userData = sessionManager.getUserData();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        //Participant mParticipant=dataSnapshot.getValue(Participant.class);
                        Participant mParticipant;
                        String strName = dataSnapshot.child("waiterName").getValue(String.class);
                        Long longNumber = dataSnapshot.child("waiterNumber").getValue(Long.class);
                        String strPhone = dataSnapshot.child("waiterPhone").getValue(String.class);
                        String strState = dataSnapshot.child("waiterState").getValue(String.class);
                        if (longNumber != -1) {
                            mParticipant = new Participant(strPhone, strName, longNumber, strState);
                            listParticipant.add(mParticipant);
                            if (strPhone.equals(userData.getString(QueueDatabaseContract.UserEntry.PHONE_ARM))) {
                                Log.d("TEST", "onDataChange: " + "Tim thay " + userData.getString(QueueDatabaseContract.UserEntry.PHONE_ARM));
                                currentParticipant = mParticipant;
                            }
                        }


                    }
                    mP_Adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(mP_Adapter);
                    getDataRoomFromFirebase();
                    updateWaiterNumberAfterChange(key);
                    indexWaiter = (long) findPositionUser();
                    if (indexWaiter != -1) {
                        //w.cancel();
                        //onBackPressed();
                        if (skip != true) {
                            //w.cancel();
                            //onBackPressed();
                            if (indexWaiter != indexWaiterAfterChange) {
                                //onBackPressed();
                                if (indexWaiterAfterChange == -100) {
                                    isTimer = true;
                                    timeWaitCountDown();
                                    Log.d("TEST", "onDataChange: " + "Time on indexWaiterAfterChange == -100");
                                } else {
                                    onBackPressed();
                                    // w.cancel();
                                    isTimer = true;
                                    timeWaitCountDown();
                                    Log.d("TEST", "onDataChange: " + "Time on else");
                                }
                            }
                        } else {
                            onBackPressed();
                            Log.d("TEST", "onDataChange: " + "Time stop on else skip != true");
                        }
                    } else {
                        onBackPressed();
                        Log.d("TEST", "onDataChange: " + "Time stop on else indexWaiter != -1");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LinedUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getListParticipant() {

    }

    private void getDataRoomFromFirebase() {

    }

    private void timeWaitCountDown() {
        double waitTime = thisRoom.roomData.timeWait;
        double waitTimeDelay = thisRoom.roomData.timeDelay;
        long time = (long) (waitTime + waitTimeDelay) * (indexWaiter - 1);
        indexWaiterAfterChange = indexWaiter;
        timeContDown(time - 1);
    }

    public void onBackPressed() {
        if (w != null) {
            w.cancel();
            w = null;
            isTimer = false;
        }

        //finish();
    }

    private void timeContDown(long timeMinute) {
        if (isTimer != false) {
            if (timeMinute != -1) {
                w = new CountDownTimer(60000, 1000) {
                    public void onTick(long mil) {
                        txtTime.setText(timeMinute + " m " + mil / 1000 + " s");
                        if (indexWaiter == -1) {
                            //w.cancel();
                            onBackPressed();
                            Log.d("TEST", "onDataChange: " + "Time stop on timeCountDown on Ticj");
                        }
                        if (indexWaiter != indexWaiterAfterChange) {
                            onBackPressed();
                            //w.cancel();
                            Log.d("TEST", "onDataChange: " + "Time stop on timeCountDown On Tick");
                        }
                    }

                    public void onFinish() {
                        if (timeMinute == 30) {
                            NotificationDevice.headsUpNotification(LinedUpActivity.this);
                        }
                        if (timeMinute == 10) {
                            NotificationDevice.headsUpNotification(LinedUpActivity.this);
                        }
                        if (timeMinute == 5) {
                            NotificationDevice.headsUpNotification(LinedUpActivity.this);
                        }
                        timeContDown(timeMinute - 1);
                    }
                }.start();
            } else {
                onBackPressed();
                Log.d("TEST", "onDataChange: " + "Time stop on timeCountDown On asdasd");
                //w.cancel();
                Toast.makeText(LinedUpActivity.this, "Chờ gì lâu thế :v", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private double findPositionUser() {
        String waiterPhone = sessionManager.getUserData().getString(QueueDatabaseContract.UserEntry.PHONE_ARM);
        int index = 0;
        for (Participant item : listParticipant) {
            if (item.getWaiterPhone().equals(waiterPhone))
                return index + 1;
            else index += 1;
        }
        return -1;
    }

    private void leaveRoom() {
        String waiterPhone = sessionManager.getUserData().getString(QueueDatabaseContract.UserEntry.PHONE_ARM);
        RoomEntryRequester tempRoom = new RoomEntryRequester(LinedUpActivity.this);
        DatabaseReference tempDatabase = tempRoom.find(key);
        Query query = tempDatabase.child("participantList");
        query.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String waiterKey = snapshot.getKey();
                        String str = snapshot.child("waiterPhone").getValue(String.class);
                        if (str.equals(waiterPhone)) {
                            int i = mP_Adapter.getItemCount();
                            updateAfterLeave(waiterKey);
                            tempRoom.updateTotalParticipantafterChange(key);
                            sessionManager.clearKeyRoomAfterLeave();
                            //w.cancel();
                            onBackPressed();
                            indexWaiter = indexWaiterAfterChange = -100;
                            Intent intent = new Intent(LinedUpActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    private void updateAfterLeave(String waiterKey) {
        RoomEntryRequester tempRoomEntryRequeser = new RoomEntryRequester(LinedUpActivity.this);
        DatabaseReference tempDatabaseRefernce = tempRoomEntryRequeser.find(key);
        tempDatabaseRefernce.child("participantList").child(waiterKey).child("waiterNumber").setValue(-1);
        tempDatabaseRefernce.child("participantList").
                child(waiterKey).
                child("waiterState").setValue(QueueDatabaseContract.RoomEntry.ParticipantListEntry.STATE_IS_LEFT);
    }


    public void updateWaiterNumberAfterChange(String roomKey) {
        RoomEntryRequester tempRoomEntryRequeser = new RoomEntryRequester(LinedUpActivity.this);
        DatabaseReference tempDatabaseRefernce = tempRoomEntryRequeser.find(roomKey);
        Query query = tempDatabaseRefernce.child("participantList");
        query.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long num = 1;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String waiterKey = snapshot.getKey();
                        long ktr = snapshot.child("waiterNumber").getValue(long.class);
                        if (ktr != (-1)) {
                            tempDatabaseRefernce.child("participantList").child(waiterKey).child("waiterNumber").setValue(num);
                            num++;
                        }
                    }
                }
            }
        });
    }

    //Hàm thay đổi data sau khi tài khoản bỏ lượt
    private void changeNumberAfterSkip() {
        Log.d("TEST", "onDataChange: " + "Time stop on timeCountDown On asdasd");
        //onBackPressed();
        skip = true;
        //w.cancel();
        String waiterPhone = sessionManager.getUserData().getString(QueueDatabaseContract.UserEntry.PHONE_ARM);
        RoomEntryRequester tempRoomEntryRequeser = new RoomEntryRequester(LinedUpActivity.this);
        DatabaseReference tempDatabaseRefernce = tempRoomEntryRequeser.find(key);
        Query query = tempDatabaseRefernce.child("participantList");
        query.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //String waiterkey=snapshot.getKey();
                        String str = snapshot.child("waiterPhone").getValue(String.class);
                        if (str.equals(waiterPhone)) {
                            Long index = snapshot.child("waiterNumber").getValue(Long.class);
                            if (index != mP_Adapter.getItemCount()) {
                                String strName = snapshot.child("waiterName").getValue(String.class);
                                String strPhone = snapshot.child("waiterPhone").getValue(String.class);
                                String strState = snapshot.child("waiterState").getValue(String.class);
                                Participant waiterChange = new Participant(strPhone, strName, index, strState);
                                //getDataWaiter(index + 1);
                                changeWaiter(waiterChange);
                            } else {
                                Toast.makeText(LinedUpActivity.this, "Bạn đã cuối hàng đợi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });
    }

    private Participant participant;

    public void getDataWaiter(Long index) {
        RoomEntryRequester tempRoomEntryRequeser = new RoomEntryRequester(LinedUpActivity.this);
        DatabaseReference tempDatabaseRefernce = tempRoomEntryRequeser.find(key);
        Query query = tempDatabaseRefernce.child("participantList");
        query.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Long find = snapshot.child("waiterNumber").getValue(Long.class);
                        if (index == find) {
                            String strName = snapshot.child("waiterName").getValue(String.class);
                            String strPhone = snapshot.child("waiterPhone").getValue(String.class);
                            String strState = snapshot.child("waiterState").getValue(String.class);
                            participant = new Participant(strPhone, strName, find, strState);
                        }
                    }
                }
            }
        });
        //return participant;
    }

    private void changeWaiter(Participant a) {
        Log.d("TEST", "onDataChange: " + "Time stop  On changeWaiter");
        onBackPressed();

        //w.cancel();
        //String waiterPhone=sessionManager.getUserData().getString(QueueDatabaseContract.UserEntry.PHONE_ARM);
        RoomEntryRequester tempRoomEntryRequeser = new RoomEntryRequester(LinedUpActivity.this);
        DatabaseReference tempDatabaseRefernce = tempRoomEntryRequeser.find(key);
        Query query = tempDatabaseRefernce.child("participantList");
        query.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String waiterKey = snapshot.getKey();
                        Long numChange = snapshot.child("waiterNumber").getValue(Long.class);
                        if (numChange == a.waiterNumber) {
                            Participant p = listParticipant.get(currentParticipant.waiterNumber.intValue());
                            String str1 = p.waiterName;
                            String str2 = p.waiterPhone;
                            String str3 = p.waiterState;
                            tempDatabaseRefernce.child("participantList").child(waiterKey).child("waiterName").setValue(str1);
                            tempDatabaseRefernce.child("participantList").child(waiterKey).child("waiterPhone").setValue(str2);
                            tempDatabaseRefernce.child("participantList").child(waiterKey).child("waiterState").setValue(str3);
                        }
                        if (numChange == currentParticipant.waiterNumber + 1) {
                            tempDatabaseRefernce.child("participantList").child(waiterKey).child("waiterName").setValue(a.waiterName);
                            tempDatabaseRefernce.child("participantList").child(waiterKey).child("waiterPhone").setValue(a.waiterPhone);
                            tempDatabaseRefernce.child("participantList").child(waiterKey).child("waiterState").setValue(a.waiterState);
                        }
                    }
                }
            }
        });
        skip = false;
        //getListParticipant();
    }

}