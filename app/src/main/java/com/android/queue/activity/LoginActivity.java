package com.android.queue.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.queue.R;
import com.android.queue.SessionManager;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract;
import com.android.queue.firebase.realtimedatabase.UserAccountsRequester;
import com.android.queue.models.UserAccounts;
import com.android.queue.utils.MD5Encode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    private MaterialButton loginBtn;
    private MaterialButton regBtn;
    TextInputLayout phoneTv;
    TextInputLayout passwordTv;
    String string_Pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    UserAccountsRequester userAccountsRequester;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Init all view in this activity
        phoneTv = findViewById(R.id.phoneTv);
        passwordTv = findViewById(R.id.passwordTv);
        loginBtn = findViewById(R.id.loginBtn);
        regBtn = findViewById(R.id.regBtn);

        userAccountsRequester = new UserAccountsRequester(this);
        sessionManager = new SessionManager(this);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogin();
            }
        });
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
//        loginPhone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, VerifyPhoneNumber.class);
//                LoginActivity.this.startActivity(intent);
//            }
//        });


        //Test thêm người chờ vào một phòng
//        RoomEntryRequester roomEntryRequester = new RoomEntryRequester(this);
//        Participant participant = new Participant("0123458698", "Tester8",  ParticipantListEntry.STATE_IS_WAIT);
//        roomEntryRequester.addParticipant(participant, "-MlwbMJc0itEekqKtOtT");
    }


    private void onClickLogin(){
        String phone = phoneTv.getEditText().getText().toString();
        String pass = passwordTv.getEditText().getText().toString();
        String encrypts = MD5Encode.endCode(pass);
        DatabaseReference databaseReference = userAccountsRequester.getmDatabase();
        Query query = databaseReference.orderByChild("phone").equalTo(phone.trim());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        UserAccounts userAccounts = user.getValue(UserAccounts.class);
                        if (userAccounts.password.equals(encrypts)) {
                            Toast.makeText(LoginActivity.this, " Đăng nhập thành công", Toast.LENGTH_LONG).show();
                            sessionManager.initUserSession(phone,userAccounts.fullName);
                            sessionManager.isLogin();
                            sendUserToHome();
                        } else {
                            Toast.makeText(LoginActivity.this, "Sai mật khẩu", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }





    private void sendUserToHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}