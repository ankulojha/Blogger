package com.developinggeek.blogapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class FavouritesActivity extends AppCompatActivity
{

    private RecyclerView favList;
    private FirebaseAuth mAuth;
    private DatabaseReference mFavDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        mAuth = FirebaseAuth.getInstance();

        mFavDatabase = FirebaseDatabase.getInstance().getReference().child("Favourites").child(mAuth.getCurrentUser().getUid());

        favList = (RecyclerView)findViewById(R.id.favourites_list);
        favList.setLayoutManager(new LinearLayoutManager(this));
        favList.setHasFixedSize(true);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<Favorites,FavouriteViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Favorites, FavouriteViewHolder>
                (
                  Favorites.class,
                  R.layout.single_fav_item,
                  FavouriteViewHolder.class,
                  mFavDatabase
                ) {
            @Override
            protected void populateViewHolder(FavouriteViewHolder viewHolder, Favorites model, int position)
            {
                final String post_key = getRef(position).getKey();

                viewHolder.title.setText(model.getTitle());
                viewHolder.content.setText(model.getContent());
                viewHolder.userName.setText(model.getUserName());

                String imgUrl = model.getImage();
                Picasso.with(FavouritesActivity.this).load(imgUrl).into(viewHolder.img);

                viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        mFavDatabase.child(post_key).removeValue();

                        Toast.makeText(FavouritesActivity.this, "One Item Removed...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        favList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FavouriteViewHolder extends RecyclerView.ViewHolder
    {
        ImageView img;
        TextView userName , title , content;
        Button btnRemove;

        public FavouriteViewHolder(View itemView) {
            super(itemView);

            btnRemove = (Button)itemView.findViewById(R.id.single_fav_btn_remove);
            img = (ImageView)itemView.findViewById(R.id.single_fav_img);
            userName = (TextView)itemView.findViewById(R.id.single_fav_user_name);
            content = (TextView)itemView.findViewById(R.id.single_fav_content);
            title = (TextView)itemView.findViewById(R.id.single_fav_title);
        }
    }

}
