package com.android.queue.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.android.queue.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    TextInputLayout EmailTv;
    TextInputLayout passwordTv;
    TextInputLayout re_passwordTv;
    private MaterialButton regBtn;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    String string_Pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EmailTv = findViewById(R.id.EmailTv);
        passwordTv = findViewById(R.id.passwordTv);
        re_passwordTv = findViewById(R.id.re_passwordTv);
        regBtn = findViewById(R.id.regBtn);

        //khoi tao firebase auth
        mAuth = FirebaseAuth.getInstance();

        mUser = mAuth.getCurrentUser();
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerWithEmail();
            }
        });
    }


    private void registerWithEmail() {
        String Email = EmailTv.getEditText().getText().toString();
        String pass = passwordTv.getEditText().getText().toString();
        String re_pass = re_passwordTv.getEditText().getText().toString();

        if (!Email.matches(string_Pattern)){
            EmailTv.setError("Email không hợp lệ");
        }
        else if (pass.isEmpty() || pass.length()< 8){
            passwordTv.setError("Mật khẩu nhập vào không hợp lệ");
        }
        else if (!re_pass.equals(pass)){

                re_passwordTv.setError("Mật khẩu nhập không trùng khớp");
        }
        else{
            System.out.println("create");
            mAuth.createUserWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        sendUserToLogin();
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }



    private void sendUserToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}