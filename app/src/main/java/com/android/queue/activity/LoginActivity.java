package com.android.queue.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.queue.R;
import com.android.queue.firebase.realtimedatabase.RoomEntryRequester;
import com.android.queue.models.Participant;
import com.android.queue.models.Room;
import com.android.queue.utils.TimestampHelper;
import com.google.android.material.button.MaterialButton;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.RoomEntry.ParticipantListEntry;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.RoomEntry;


public class LoginActivity extends AppCompatActivity {

    private MaterialButton loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Init all view in this activity
        loginBtn = findViewById(R.id.loginBtn);



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });


        //Test thêm người chờ vào một phòng
//        RoomEntryRequester roomEntryRequester = new RoomEntryRequester(this);
//        Participant participant = new Participant("072777777", "Tester7",  ParticipantListEntry.STATE_IS_WAIT);
//        roomEntryRequester.addParticipant(participant, "-MlwbMJc0itEekqKtOtT");


    }
}