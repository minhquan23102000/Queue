package com.android.queue.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.queue.firebase.realtimedatabase.UserAccountsRequester;
import com.android.queue.models.UserAccounts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.android.queue.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    TextInputLayout phoneTv;
    TextInputLayout nameTv;
    TextInputLayout passwordTv;
    TextInputLayout re_passwordTv;
    private MaterialButton regBtn;
    private UserAccountsRequester userAccountsRequester;


    FirebaseAuth mAuth;
    FirebaseUser mUser;

    String string_Pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        phoneTv = findViewById(R.id.phoneTv);
        nameTv = findViewById(R.id.nameTv);
        passwordTv = findViewById(R.id.passwordTv);
        re_passwordTv = findViewById(R.id.re_passwordTv);
        regBtn = findViewById(R.id.regBtn);

        userAccountsRequester = new UserAccountsRequester(this);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRegister();
            }
        });
    }


    private void onClickRegister() {
        String phone = phoneTv.getEditText().getText().toString();
        String name = nameTv.getEditText().getText().toString();
        String pass = passwordTv.getEditText().getText().toString();
        String re_pass = re_passwordTv.getEditText().getText().toString();

        //check data
        boolean validData = true;
        if (phone.matches("")){
            phoneTv.setError("Số điện thoại không được để trống");
            validData = false;
        }
        else{
            phoneTv.setError("");
        }
        if (phone.matches("[0-9]+") && phone.length() > 2) {
            validData = true;
            phoneTv.setError("");
        }
        else{
            validData = false;
            phoneTv.setError("Số điện thoại không hợp lệ");
        }
        if (name.matches("")){
            nameTv.setError("Họ tên không được để trống");
            validData = false;
        }
        else{
            nameTv.setError("");
        }
        if (pass.matches("")){
            passwordTv.setError("Mật khẩu không được để trống");
            validData = false;
        }
        else{
            passwordTv.setError("");
        }
        if (pass.length() < 6){
            passwordTv.setError("Mật khẩu tối thiểu phải 6 kí tự");
            validData = false;
        }
        else{
            passwordTv.setError("");
        }
        if (!re_pass.equals(pass)){
            re_passwordTv.setError("Mật khẩu không trùng khớp");
            validData = false;
        }
        else{
            re_passwordTv.setError("");
        }

        if (validData){
            UserAccounts account = new UserAccounts(name, phone ,pass);
            String accKey = userAccountsRequester.createAnUserAccount(account);
            if (accKey != null){
                Toast.makeText(this,"Đăng kí thành công", Toast.LENGTH_SHORT).show();
                sendUserToLogin();
            }
            else{
                Toast.makeText(this,"Đăng kí thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }





    private void sendUserToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}