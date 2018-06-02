package com.example.shivr.androidblogapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogSingleActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private String mopost_key = null;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    private ImageView mBlogsingle_imageview;
    private TextView mBlogsingle_title;
    private TextView mBlogsingle_desc;
    private Button msingleButton_remove;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);

        mToolbar = findViewById(R.id.single_post__tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Android BlogApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBlogsingle_imageview = findViewById(R.id.post_single_image);
        mBlogsingle_title = findViewById(R.id.post_single_title);
        mBlogsingle_desc = findViewById(R.id.post_single_descr);
        msingleButton_remove = findViewById(R.id.remove_post);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mAuth = FirebaseAuth.getInstance();

        //to get the intent that send from the mainactivity
         mopost_key = getIntent().getExtras().getString("blog_id");

        //to get the detailed of the blog post
        mDatabase.child(mopost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title_name = (String) dataSnapshot.child("Name").getValue();
                String post_descc = (String) dataSnapshot.child("Description").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();


                //now to set the text
                mBlogsingle_title.setText(post_title_name);
                mBlogsingle_desc.setText(post_descc);

                Picasso.get().load(post_image).into(mBlogsingle_imageview);

                //this is used to checck the user who has logged in can only remove the post
                if (mAuth.getCurrentUser().getUid().equals(post_uid)){

                    msingleButton_remove.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        msingleButton_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //now to remove the post
                mDatabase.child(mopost_key).removeValue();
                startActivity(new Intent(BlogSingleActivity.this,MainActivity.class));
            }
        });
    }
}
