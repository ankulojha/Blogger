package com.developinggeek.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity
{

    private TextInputLayout edtName , edtPass ,edtEmail;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmail = (TextInputLayout) findViewById(R.id.edt_email);
        edtName = (TextInputLayout) findViewById(R.id.edt_name);
        edtPass = (TextInputLayout) findViewById(R.id.edt_pass);
        btnRegister = (Button) findViewById(R.id.btn_register);

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               registerUser();
            }
        });

    }

    private void registerUser()
    {
        final String name = edtName.getEditText().getText().toString();
        String email = edtEmail.getEditText().getText().toString();
        String pass = edtPass.getEditText().getText().toString();

       if(!TextUtils.isEmpty(name) &&  !TextUtils.isEmpty(email) &&!TextUtils.isEmpty(pass))
       {
           mProgress.setMessage("Signing in... ");
           mProgress.show();

           mAuth.createUserWithEmailAndPassword(email , pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task)
               {
                   FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                   String userId = currentUser.getUid();

                   DatabaseReference currentUserDb = mDatabase.child(userId);

                   currentUserDb.child("name").setValue(name);
                   currentUserDb.child("image").setValue("default");
                   currentUserDb.child("status").setValue("Hey there I am on blog app");

                   mProgress.dismiss();

                   Intent mainIntent = new Intent(RegisterActivity.this , MainActivity.class);
                   mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(mainIntent);
               }
           });

       }
       else
       {
           Toast.makeText(this, "Enter all the text fields", Toast.LENGTH_SHORT).show();
           mProgress.dismiss();
       }

    }


}
