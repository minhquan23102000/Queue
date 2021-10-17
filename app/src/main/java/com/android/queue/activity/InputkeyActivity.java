package com.android.queue.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.queue.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class InputkeyActivity extends AppCompatActivity {

    ImageButton scanBtn;
    TextInputLayout txtKey;
    Button btnJoin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputkey);
        scanBtn = findViewById(R.id.scanBtn);
        txtKey = findViewById(R.id.iKey);
        btnJoin = findViewById(R.id.joinBtn);

        Intent intent=getIntent();
        String str = intent.getStringExtra("Key");
        //txtKey.setText(str);
        txtKey.getEditText().setText(str);


        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String key= txtKey.getText().toString();
                //Kiểm tra key với data firebase
                //Chuyển qua activity xếp hàng
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
}