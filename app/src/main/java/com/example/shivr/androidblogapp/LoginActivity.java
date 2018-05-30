package com.example.shivr.androidblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mlogin_email;
    private TextInputLayout mlogin_password;
    private Button mlogin_btn;
    private Toolbar mToolbar;

    private ProgressDialog mProgress_dialog;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mlogin_email =  findViewById(R.id.login_email_text);
        mlogin_password =  findViewById(R.id.login_user_password_text);
        mlogin_btn =  findViewById(R.id.btn_login);

        mToolbar = findViewById(R.id.login_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mProgress_dialog = new ProgressDialog(this);


        mlogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_email = mlogin_email.getEditText().getText().toString();
                String user_password = mlogin_email.getEditText().getText().toString();


                if (TextUtils.isEmpty(user_email) || TextUtils.isEmpty(user_password)) {

                    Toast.makeText(LoginActivity.this, "Please complete field", Toast.LENGTH_SHORT).show();

                } else {

//                    setupFirebaseAuth();

//                    mProgress_dialog.setTitle("Logging In");
                    mProgress_dialog.setMessage("Logging In........");
                    mProgress_dialog.setCanceledOnTouchOutside(false);
                    mProgress_dialog.show();

                    login_user(user_email, user_password);


                }
            }
        });

    }

    private void login_user(String user_email, String user_password) {


        mAuth.signInWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (task.isSuccessful()) {
//                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    mProgress_dialog.dismiss();

                    Intent login_intent = new Intent(LoginActivity.this,MainActivity.class);
                    login_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(login_intent);
                    finish();

                    Toast.makeText(LoginActivity.this,"Login Sucessfully" ,Toast.LENGTH_LONG).show();

                }else {
                    mProgress_dialog.hide();

                    String error_message = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this,"Error : " + error_message ,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean checkinputuser(String user_email, String user_password) {
        if(user_email.equals("") || user_password.equals("")){
            Toast.makeText(getApplicationContext(), "All field must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
