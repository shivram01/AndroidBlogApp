package com.example.shivr.androidblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    public static final int GALLERY_INTENT = 1;

    private ImageButton mselect_image;

    private EditText mpost_title;
    private EditText mpost_description;
    private Button msubmit_button;


//    private  Uri image_uri;

    //to store the images in the firebase

    StorageReference mstorage;


    private ProgressDialog progressDialog;

    private DatabaseReference mdatabaseReference;

    private Uri downloadUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        mselect_image = findViewById(R.id.insert_image);

        mpost_title = findViewById(R.id.mpost_title);
        mpost_description = findViewById(R.id.mpost_description);
        msubmit_button = findViewById(R.id.sumbit_button);


        progressDialog = new ProgressDialog(this);

        //get the instance of the storagereference

        mstorage = FirebaseStorage.getInstance().getReference();

        //get the instance of the Databaserefernece
        mdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");

        mselect_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//

                //to open the gallery we can do CropImage.activity or making Intent

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);


            }
        });


        msubmit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Posting to Blog....");
                progressDialog.show();

                String title_post = mpost_title.getText().toString();
                String descr_post = mpost_description.getText().toString();

                if (TextUtils.isEmpty(title_post) || TextUtils.isEmpty(descr_post )||(mselect_image==null)){
                    Toast.makeText(PostActivity.this,"Please complete the field",Toast.LENGTH_LONG).show();

                }else {

                    //now to store title and description in the database we have to make the DatbaseReference

                    DatabaseReference new_post = mdatabaseReference.push();

                    new_post.child("Name").setValue(title_post);
                    new_post.child("Description").setValue(descr_post);
                    new_post.child("image").setValue(downloadUrl.toString());

                    Toast.makeText(PostActivity.this,"Sucessfully Posted",Toast.LENGTH_LONG).show();

                    startActivity(new Intent(PostActivity.this,MainActivity.class));

                    progressDialog.dismiss();


//                   String image  = mdatabaseReference.child("image").setValue(downloadUrl).toString();
//
//                    HashMap<String,String> data_map = new HashMap<String, String>();
//                    data_map.put("title",title_post);
//                    data_map.put("descr",descr_post);
//                    data_map.put("image",image);
//
//                    progressDialog.setTitle("Posting to Blog....");
//                    progressDialog.show();
//
//                    mdatabaseReference.push().setValue(data_map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()){
//                                progressDialog.dismiss();
//                                Toast.makeText(PostActivity.this,"Sucessfully Posted",Toast.LENGTH_LONG).show();
//                            }else {
//                                progressDialog.hide();
//                                String error = task.getException().getMessage();
//                                Toast.makeText(PostActivity.this,"Error: " + error ,Toast.LENGTH_LONG).show();
//
//                            }
//                        }
//                    });
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            progressDialog.setMessage("Uploading image");
            progressDialog.show();


            //these two line of code will just take the images and display to the user

            Uri download_uri = data.getData();
//
//            mselect_image.setImageURI(download_uri);

//
            //now we create a folder name upload_images which we upload images in the folder name

            StorageReference filepath = mstorage.child("upload_images").child(download_uri.getLastPathSegment());

            //now we have to put file
            filepath.putFile(download_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    progressDialog.dismiss();

                    //when the user click ok in the gallery then it load images into the image_view to do we have to give the Uri

                     downloadUrl = taskSnapshot.getDownloadUrl();

                    //then afterwords we have to implement the Picasso

                    Picasso.get().load(downloadUrl).into(mselect_image);

                    Toast.makeText(PostActivity.this, "Uploaded sucessfully", Toast.LENGTH_LONG).show();
                }
            });

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = data.getError();

        }
    }


    private void startPosting() {

        //to show progress diaglog to the user





    }
}


//

