package com.example.shivr.androidblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mUser_name;
    private TextInputLayout memail;
    private TextInputLayout mpassword;
    private TextInputLayout conform_password;
    private Button mCreate_btn;

    private Toolbar mToolbar;
    private ProgressDialog mProgress_dialoge;

    //for user authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //the databasereference is needed to store the user in the database uniquely
    private DatabaseReference mdatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUser_name =findViewById(R.id.reg_username_text);
        memail = findViewById(R.id.reg_email_text);
        mpassword = findViewById(R.id.reg_password_text);
        conform_password = findViewById(R.id.reg_conform_password_text);
        mCreate_btn =findViewById(R.id.btn_register);

        mProgress_dialoge = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


        // These line is used to create tool bar and back button
        mToolbar = findViewById(R.id.register_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init(){

        mCreate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = mUser_name.getEditText().getText().toString();
                String email = memail.getEditText().getText().toString();
                String password = mpassword.getEditText().getText().toString();
                String cpassword = conform_password.getEditText().getText().toString();


                if (checkInputs(email, display_name, password,cpassword)) {
                    if(doStringsMatch(password, cpassword)){
                        register_user(display_name,email, password);
                        mProgress_dialoge.setTitle("Registering User");
                        mProgress_dialoge.setMessage("Please wait while we create your account");
                        mProgress_dialoge.setCanceledOnTouchOutside(false);
                        mProgress_dialoge.show();
                    }else{
                        Toast.makeText(getApplicationContext(), "passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "All field must be filled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * Return true if @param 's1' matches @param 's2'
     * @param s1
     * @param s2
     * @return
     */
    private boolean doStringsMatch(String s1, String s2){
        return s1.equals(s2);
    }

    /**
     * Checks all the input fields for null
     * @param email
     * @param username
     * @param password
     * @return
     */
    private boolean checkInputs(String email, String username, String password, String confirmPassword){
//        Log.d(TAG, "checkInputs: checking inputs for null values");
        if(email.equals("") || username.equals("") || password.equals("") || confirmPassword.equals("")){
            Toast.makeText(getApplicationContext(), "All field must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {

                            // these helps us to get the user_id from the authentication
                            String user_id = mAuth.getCurrentUser().getUid();

                            //and then we store the user in another child
                            DatabaseReference current_user_data = mdatabaseReference.child(user_id);

                            //set the name of the user and image as default
                            current_user_data.child("Name").setValue(display_name);
                            current_user_data.child("image").setValue("default");

//                            sendVerificationEmail();
                            mProgress_dialoge.dismiss();
                            Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                            Toast.makeText(RegisterActivity.this,"Sucessfully Registered" ,Toast.LENGTH_LONG).show();
                        }else {
                            mProgress_dialoge.hide();

                            String error_message = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this,"Error : " + error_message ,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
