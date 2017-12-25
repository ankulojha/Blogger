package com.developinggeek.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity
{

    private TextView mUserName , mUserStatus;
    private CircleImageView mUserImg;
    private TextInputLayout mEdtStatus;
    private Button mBtnStatus , mBtnImg;
    private Uri mImageUri = null;
    private DatabaseReference mUsersDatabase;
    private StorageReference mStorageImage;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mUserName = (TextView)findViewById(R.id.setup_name);
        mUserImg = (CircleImageView)findViewById(R.id.setup_image);
        mBtnImg = (Button)findViewById(R.id.setup_submit_btn);
        mUserStatus = (TextView)findViewById(R.id.setup_user_status);
        mBtnStatus = (Button)findViewById(R.id.setup_btn_img);
        mEdtStatus = (TextInputLayout)findViewById(R.id.setup_edt_status);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mStorageImage = FirebaseStorage.getInstance().getReference().child("Profile_images");

        mAuth = FirebaseAuth.getInstance();

        String user_id = mAuth.getCurrentUser().getUid();

        mProgress = new ProgressDialog(this);

        mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                String userName = dataSnapshot.child("name").getValue().toString();
                mUserName.setText(userName);

                String userStatus = dataSnapshot.child("status").getValue().toString();
                mUserStatus.setText(userStatus);

                String imageUrl = dataSnapshot.child("image").getValue().toString();
                if(!imageUrl.equals("default"))
                {
                    Picasso.with(getApplicationContext()).load(imageUrl).into(mUserImg);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });

        mBtnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , 1);

            }
        });

        mBtnStatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String user_id = mAuth.getCurrentUser().getUid();

                mProgress.setMessage("saving the changes...");
                mProgress.show();

                String newStatus = mEdtStatus.getEditText().getText().toString();

                if(!TextUtils.isEmpty(newStatus))
                {
                    mUsersDatabase.child(user_id).child("status").setValue(newStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SetupActivity.this, "Status Updated Successfully", Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                        }
                    });
                }
            }
        });

    }

    private void setupAccount()
    {
        final String user_id = mAuth.getCurrentUser().getUid();

        if(mImageUri!=null)
        {
            mProgress.setMessage("saving the changes...");
            mProgress.show();

            StorageReference filePath = mStorageImage.child(mImageUri.getLastPathSegment());
            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mUsersDatabase.child(user_id).child("image").setValue(downloadUri);

                    mProgress.dismiss();

                    Intent mainIntent = new Intent(SetupActivity.this , MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK)
        {
             mImageUri = data.getData();
             mUserImg.setImageURI(mImageUri);

             setupAccount();
        }

    }


}
