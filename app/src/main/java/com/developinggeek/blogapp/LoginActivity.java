package com.developinggeek.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity
{

    private TextInputLayout edtEmail , edtPass;
    private Button btnLogin , btnNew;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPass = (TextInputLayout) findViewById(R.id.login_password);
        edtEmail = (TextInputLayout) findViewById(R.id.login_email);
        btnLogin = (Button)findViewById(R.id.login_btn);
        btnNew = (Button)findViewById(R.id.login_btn_new);

        mAuth = FirebaseAuth.getInstance();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mProgress = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this , RegisterActivity.class);
                registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);

            }
        });

    }


    private void checkLogin()
    {
        String email = edtEmail.getEditText().getText().toString();
        String pass = edtPass.getEditText().getText().toString();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass))
        {
            mAuth.signInWithEmailAndPassword(email ,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                   if(task.isSuccessful())
                   {
                       checkUserOnDb();
                   }
                   else
                   {
                       Toast.makeText(LoginActivity.this, "Could not Sign in...", Toast.LENGTH_LONG).show();
                   }
                }
            });

        }

    }

    private void checkUserOnDb()
    {
        mProgress.setTitle("logging you in...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        final String current_user_id = mAuth.getCurrentUser().getUid();

        mUsersDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
               if(dataSnapshot.hasChild(current_user_id))
               {
                   mProgress.dismiss();

                   Intent loginIntent = new Intent(LoginActivity.this , MainActivity.class);
                   loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(loginIntent);

               }
               else {
                   mProgress.dismiss();

                   Intent setupIntent = new Intent(LoginActivity.this , SetupActivity.class);
                   setupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(setupIntent);
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
