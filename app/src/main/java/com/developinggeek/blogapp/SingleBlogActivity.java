package com.developinggeek.blogapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SingleBlogActivity extends AppCompatActivity
{

    private String post_key;
    private ImageView blogImg ,btnFav;
    private TextView title , desc , userName , content;
    private DatabaseReference mDatabase , mFavDatabase;
    private Button btnRemove;
    private FirebaseAuth mAuth;
    private String postTitle, postDesc , postUserName , postImg , postContent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_blog);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        mFavDatabase = FirebaseDatabase.getInstance().getReference().child("Favourites");

        mAuth =FirebaseAuth.getInstance();

        post_key = getIntent().getStringExtra("blog_id");


        blogImg = (ImageView)findViewById(R.id.single_blog_img);
        title = (TextView)findViewById(R.id.single_blog_title);
        desc = (TextView)findViewById(R.id.single_blog_desc);
        userName = (TextView)findViewById(R.id.single_blog_user_name);
        btnRemove = (Button)findViewById(R.id.single_blog_remove);
        content = (TextView)findViewById(R.id.single_blog_content);
        btnFav = (ImageView)findViewById(R.id.btn_fav);

        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()) {

                    postTitle = dataSnapshot.child("title").getValue().toString();
                    postDesc = dataSnapshot.child("desc").getValue().toString();
                    postUserName = dataSnapshot.child("username").getValue().toString();
                    postImg = dataSnapshot.child("image").getValue().toString();
                    String postUid = dataSnapshot.child("uid").getValue().toString();
                    postContent = dataSnapshot.child("content").getValue().toString();

                    title.setText(postTitle);
                    desc.setText(postDesc);
                    userName.setText(postUserName);
                    Picasso.with(SingleBlogActivity.this).load(postImg).into(blogImg);
                    content.setText(postContent);

                    if (mAuth.getCurrentUser().getUid().equals(postUid)) {
                        btnRemove.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mDatabase.child(post_key).removeValue();

                Toast.makeText(SingleBlogActivity.this, "Blog removed...", Toast.LENGTH_SHORT).show();

                Intent mainIntent = new Intent(SingleBlogActivity.this , MainActivity.class);
                startActivity(mainIntent);
            }
        });

        mFavDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(post_key))
                {
                    btnFav.setImageResource(R.drawable.star_blue);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                HashMap favMap = new HashMap();
                favMap.put("title",postTitle);
                favMap.put("content",postContent);
                favMap.put("userName",postUserName);
                favMap.put("image",postImg);

               mFavDatabase.child(mAuth.getCurrentUser().getUid()).child(post_key).setValue(favMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task)
                   {
                       if(task.isSuccessful())
                       {
                           Toast.makeText(SingleBlogActivity.this, "Added to Favourites", Toast.LENGTH_SHORT).show();
                           btnFav.setImageResource(R.drawable.star_blue);
                       }
                       else
                       {
                           Toast.makeText(SingleBlogActivity.this, "UnSuccessful... check your connection", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
            }
        });

    }

}
