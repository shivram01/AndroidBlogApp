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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mblog_list_recycleview;

    private DatabaseReference mdatabaseReference;

    //for authentication
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mauthStateListener;

    private Toolbar mToolbar;

    //for likes make boolean
    private boolean mprocess_likes = false;

    //database for likes
    private DatabaseReference mdatabase_likes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this helps us to get the child of the database
        mdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");

        //for the likes database reference
        mdatabase_likes = FirebaseDatabase.getInstance().getReference().child("Likes");

        //these is used to set the recycler
        mblog_list_recycleview = findViewById(R.id.blog_list_recyclerview);
        mblog_list_recycleview.setHasFixedSize(true);
        mblog_list_recycleview.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = findViewById(R.id.main_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Android BlogApp");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();


        //to track the user had signed in or out
        mauthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

//                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (firebaseAuth.getCurrentUser() == null) {

                    Intent start_intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(start_intent);
                    finish();
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

//        if (currentUser==null){
//
//            Intent start_intent = new Intent(MainActivity.this,WelcomeActivity.class);
//            startActivity(start_intent);
//            finish();
//        }


        //we have to create FirebaseRecyclerAdapter which takes two <Blog,BlogViewHolder>
        //these helps us to retrieve the data directly from the database and display into the recycler view

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>
                (Blog.class, R.layout.blog_row, BlogViewHolder.class, mdatabaseReference) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                //to get the position or the key of the imageview
                final String post_key = getRef(position).getKey();


                //these is used to set the title, descr and image

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDescr());
                viewHolder.setImage(model.getImage());
                viewHolder.setUsername(model.getUsername());

                //for changing the icon of the imagebutton
                viewHolder.setLikesButton(post_key);

                //these is when the user click on the imageview and it give the post_key
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(MainActivity.this, post_key, Toast.LENGTH_LONG).show();
                    }
                });


                //now when the likes button is pressed

                viewHolder.mlikes_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mprocess_likes = true;

                        //to read from the database we used addValueEventListener
                        mdatabase_likes.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (mprocess_likes) {

                                    //these is used to check that like is exist or not if it exist then if condition will run otherwise else condition
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        //these helps us to remove the likes when we double click on the likes imagebutton
                                        mdatabase_likes.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mprocess_likes =false;

                                    } else {

                                        //this is used to check the new user came to like
                                        mdatabase_likes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("Random");
                                        mprocess_likes = false;

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                });
            }
        };

        mblog_list_recycleview.setAdapter(firebaseRecyclerAdapter);
    }


    //we have to create BlogViewHolder which extends from RecyclerView.ViewHolder

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mview;

        //to store the likes in the database
        DatabaseReference mdatbaselikes;
        //for authentication
        FirebaseAuth mAuth;

        //for likes imagebutton
        ImageButton mlikes_button;

        public BlogViewHolder(View itemView) {

            super(itemView);
            mview = itemView;

            mlikes_button = itemView.findViewById(R.id.likes_image_button);

            mdatbaselikes = FirebaseDatabase.getInstance().getReference().child("Likes");

            mAuth = FirebaseAuth.getInstance();

        }

        public void setTitle(String title) {

            TextView textView_post_title = itemView.findViewById(R.id.post_title);
            textView_post_title.setText(title);
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

        public void setUsername(String username) {

            TextView textView_ussername = itemView.findViewById(R.id.post_username);
            textView_ussername.setText(username);

        }

        //these method is used for changing the icon of the likes
        public void setLikesButton(final String post_key){

            mdatbaselikes.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //to check the likes exist or not
                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                        //if the user exist then the change the icon of the image as red

                        mlikes_button.setImageResource(R.drawable.red);

                    }else {

                        //if the user new then the icon will be gray one
                        mlikes_button.setImageResource(R.drawable.gray);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


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

        if (item.getItemId() == R.id.action_account_setting) {

            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
}
