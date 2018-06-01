package com.example.shivr.androidblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountActivity extends AppCompatActivity {

    private CircleImageView circleImageView;

    private Button msubmit_btn;
    private Button mChange_image_btn;
    private Toolbar mToolbar;
    private EditText user_name_text;

    public static final int GALLERY_PICK = 1;

    //to takae the images of the Uri mImage_uri
    private Uri mImage_uri = null;

    //to store in the database
    private DatabaseReference mdatabase_user;

    //it is for the authentication for the user id
    private FirebaseAuth mAuth;

    //it used to store images in the database
    private StorageReference mstorageImage;

    //progress dialogue
    private ProgressDialog mprogressdiaog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);


        mToolbar = findViewById(R.id.users_account_setting);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mprogressdiaog  =new ProgressDialog(this);


        circleImageView = findViewById(R.id.profile_image);
        msubmit_btn = findViewById(R.id.msubmit_btn);
        mChange_image_btn = findViewById(R.id.change_image);
        user_name_text = findViewById(R.id.user_name_text);


        mdatabase_user = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        //it is used to store the images making the folder upload_images
        mstorageImage = FirebaseStorage.getInstance().getReference().child("upload_images");

        mChange_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery_intent = new Intent();
                gallery_intent.setType("image/*");
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery_intent, "Select Images"), GALLERY_PICK);

                // start picker to get image for cropping and then use the image in cropping activity

//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(SettingsActivity.this);
            }
        });


        msubmit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startsetupAccount();
            }
        });


    }

    private void startsetupAccount() {

        final String user_name = user_name_text.getText().toString();

        //it is used to get the current user id who logged in
        final String user_id = mAuth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(user_name) && (mImage_uri != null)) {


            mprogressdiaog.setMessage("Finishing setup....");
            mprogressdiaog.show();

            //the folder is already created name upload_images in which the images is stored
            StorageReference filepath = mstorageImage.child(mImage_uri.getLastPathSegment());


            //now we have to put file
            filepath.putFile(mImage_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                    progressDialog.dismiss();

                    //when the user click ok in the gallery then it load images into the image_view to do we have to give the Uri

                    String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                    mdatabase_user.child(user_id).child("Name").setValue(user_name);
                    mdatabase_user.child(user_id).child("image").setValue(downloadUrl);

                    mprogressdiaog.dismiss();

                    Intent main_intent = new Intent(AccountActivity.this,MainActivity.class);
                    main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(main_intent);
                    finish();

                }
            });

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            //these helps us to get the images by the help of Uri
            Uri image_uri = data.getData();

            CropImage.activity(image_uri)
                    .setAspectRatio(1, 1)
                    .start(this);

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //after cropping the images we have to take the crop images and giving the Uri name mIamge_uri
                mImage_uri = result.getUri();

                //to set the images in the circleimageview
                circleImageView.setImageURI(mImage_uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}

