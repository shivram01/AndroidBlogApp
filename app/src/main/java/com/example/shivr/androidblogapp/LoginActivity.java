package com.example.shivr.androidblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";

    private TextInputLayout mlogin_email;
    private TextInputLayout mlogin_password;
    private Button  mlogin_btn;
    private Toolbar mToolbar;

    private ProgressDialog mProgress_dialog;

    private FirebaseAuth mAuth;

    //Firebase
    public FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mlogin_email = findViewById(R.id.login_email_text);
        mlogin_password = findViewById(R.id.login_user_password_text);
        mlogin_btn = findViewById(R.id.btn_login);

        mToolbar =  findViewById(R.id.login_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mProgress_dialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mlogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mlogin_email.getEditText().getText().toString();
                String password = mlogin_password.getEditText().getText().toString();



                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){

                    Toast.makeText(LoginActivity.this,"Please complete field",Toast.LENGTH_SHORT).show();

                }else {

                    setupFirebaseAuth();

//                    mProgress_dialog.setTitle("Logging In");
                    mProgress_dialog.setMessage("Logging In");
                    mProgress_dialog.setCanceledOnTouchOutside(false);
                    mProgress_dialog.show();

                    loginuser(email, password);


                }
            }
        });

    }

    /*
       ----------------------------- Firebase setup ---------------------------------
    */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    //check if email is verified
                    if(user.isEmailVerified()){
                        Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                        Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }else{
                        Toast.makeText(LoginActivity.this, "Email is not Verified\nCheck your Inbox", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
                // ...
            }
        };
    }

    private void loginuser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
//                            Log.w(TAG, "signInWithEmail:failed", task.getException());


                            mProgress_dialog.dismiss();

                            Toast.makeText(LoginActivity.this,"Login Sucessfully" ,Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();


                        }else {
                            mProgress_dialog.hide();

                            String error_message = task.getException().getMessage();
                            Toast.makeText(LoginActivity.this,"Error : " + error_message ,Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}
