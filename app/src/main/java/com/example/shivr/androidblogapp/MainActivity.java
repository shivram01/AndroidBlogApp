package com.example.shivr.androidblogapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mblog_list_recycleview;

    private DatabaseReference mdatabaseReference;

    //for authentication
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mauthStateListener;

    private Toolbar mToolbar;
//    private List<Blog> blogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this helps us to get the child of the database
        mdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");

        //these is used to set the recycler
        mblog_list_recycleview = findViewById(R.id.blog_list_recyclerview);
        mblog_list_recycleview.setHasFixedSize(true);
        mblog_list_recycleview.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = findViewById(R.id.main_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Android BlogApp");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();


        //to track wheneve the userr signs in or out
        mauthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    //user is signed in
//                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    Intent start_intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(start_intent);
                    finish();


                } else {

                    //user is signed out

                    Intent login_intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login_intent);


                }

            }
        };


//        mdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");

//        blogList = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mauthStateListener);

//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//        if (currentUser==null){
//
//            Intent start_intent = new Intent(MainActivity.this,WelcomeActivity.class);
//            startActivity(start_intent);
//            finish();
//        }


        //we have to create FirebaseRecyclerAdapter which takes two <Blog,BlogViewHolder>
        //these helps us to retrieve the data directly from the database

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>
                (Blog.class, R.layout.blog_row, BlogViewHolder.class, mdatabaseReference) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                //these is used to set the title, descr and image

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDescr());
                viewHolder.setImage(model.getImage());
            }
        };

        mblog_list_recycleview.setAdapter(firebaseRecyclerAdapter);
    }


    //we have to create BlogViewHolder which extends from RecyclerView.ViewHolder

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        //        TextView textView_title;
//        TextView textView_decription;
        View mview;

        public BlogViewHolder(View itemView) {

            super(itemView);
            mview = itemView;

//            textView_title = itemView.findViewById(R.id.post_title);
//            textView_decription = itemView.findViewById(R.id.post_descr);
        }

        public void setTitle(String title) {

            TextView textView_title = itemView.findViewById(R.id.post_title);
            textView_title.setText(title);
        }

        public void setDesc(String desc) {
            TextView textView_decription = itemView.findViewById(R.id.post_descr);
            textView_decription.setText(desc);
        }

        public void setImage(String image) {

            ImageView post_imageview = itemView.findViewById(R.id.post_image);

            //it is used to load image using the picasso dependency
            Picasso.get().load(image).into(post_imageview);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //this is used to create + icon in the toolbar
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add) {

            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.action_logout) {

            FirebaseAuth.getInstance().signOut();
            Intent start_intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(start_intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
