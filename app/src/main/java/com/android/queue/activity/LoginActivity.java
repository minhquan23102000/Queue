package com.android.queue.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.queue.R;
import com.android.queue.firebase.realtimedatabase.RoomEntryRequester;
import com.android.queue.models.Participant;
import com.android.queue.models.Room;
import com.android.queue.utils.TimestampHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.RoomEntry.ParticipantListEntry;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.RoomEntry;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private MaterialButton loginBtn;
    private MaterialButton loginPhone;
    private MaterialButton regBtn;

    TextInputLayout EmailTv;
    TextInputLayout passwordTv;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String string_Pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Init all view in this activity
        EmailTv = findViewById(R.id.mailTv);
        passwordTv = findViewById(R.id.passwordTv);
        loginBtn = findViewById(R.id.loginBtn);
        loginPhone = findViewById(R.id.loginPhone);
        regBtn = findViewById(R.id.regBtn);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithEmail();
            }
        });
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
        loginPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, VerifyPhoneNumber.class);
                LoginActivity.this.startActivity(intent);
            }
        });


        //Test thêm người chờ vào một phòng
//        RoomEntryRequester roomEntryRequester = new RoomEntryRequester(this);
//        Participant participant = new Participant("0123458698", "Tester8",  ParticipantListEntry.STATE_IS_WAIT);
//        roomEntryRequester.addParticipant(participant, "-MlwbMJc0itEekqKtOtT");
    }


    private void loginWithEmail(){
        String email = EmailTv.getEditText().getText().toString();
        String pass = passwordTv.getEditText().getText().toString();
        if (!email.matches(string_Pattern)){
            EmailTv.setError("Email không hợp lệ");
        }
        else if (pass.isEmpty() || pass.length()< 8){
            passwordTv.setError("Mật khẩu nhập vào không hợp lệ");
        }
        else{
            Toast.makeText(LoginActivity.this, "Đang đăng nhập...", Toast.LENGTH_SHORT).show();
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        sendUserToHome(email);
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToHome(String values) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("email", values);
        startActivity(intent);
    }
}