package com.android.queue.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.queue.R;
import com.android.queue.SessionManager;
import com.google.android.material.button.MaterialButton;
<<<<<<< HEAD
import com.google.android.material.textview.MaterialTextView;
=======
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.UserEntry;
>>>>>>> origin/master

public class MainActivity extends AppCompatActivity {

    private MaterialTextView usernameTv;

    private MaterialButton logoutBtn;
    private MaterialButton createRoomBtn;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Init session manager
        sessionManager = new SessionManager(this);
        //Init all view in this activity
        usernameTv = findViewById(R.id.usernameTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        createRoomBtn = findViewById(R.id.hostBtn);

        Intent intent = getIntent();
        String mailOfUser = intent.getStringExtra("email");
        usernameTv.setText(mailOfUser);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateRoomActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //If user is already create or join a room, navigate to room layout
        Bundle userData = sessionManager.getUserData();
        if (userData.getString(UserEntry.CURRENT_ROOM_ARM, null) != null) {
            if (userData.getBoolean(UserEntry.IS_HOST_ARM, false)) {
                Intent intent = new Intent(MainActivity.this, HostActivity.class);
                MainActivity.this.startActivity(intent);
            }
        }
    }
}