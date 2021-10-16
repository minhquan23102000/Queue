package com.android.queue.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.queue.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {

    private MaterialTextView usernameTv;

    private MaterialButton logoutBtn;
    private MaterialButton createRoomBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}