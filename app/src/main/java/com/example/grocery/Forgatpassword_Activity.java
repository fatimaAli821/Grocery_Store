package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgatpassword_Activity extends AppCompatActivity {


    //Ui views
    private ImageButton backbtn;
    private EditText emailEt;
    private Button recovryBtn;
   private FirebaseAuth firebaseAuth;
   private ProgressDialog progressDialog;

 // private   ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgatpassword_);

        //init ui views
        backbtn=findViewById(R.id.backbtn);
        emailEt=findViewById(R.id.emailEt);
        recovryBtn=findViewById(R.id.recovryBtn);
        //initalize firebase
        firebaseAuth=FirebaseAuth.getInstance();
       // progressBar = findViewById(R.id.progressBar);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait....");
        progressDialog.setCanceledOnTouchOutside(false);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




       recovryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // progressBar.setVisibility(View.VISIBLE);
                final String email=emailEt.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    emailEt.setError("enter email");
                    emailEt.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEt.setError("Invalid email address");
                    emailEt.requestFocus();
                    return;
                }
                progressDialog.setMessage("sending instruction to reset password");
                progressDialog.show();
                try {


                    firebaseAuth.sendPasswordResetEmail(emailEt.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Forgatpassword_Activity.this, "password reset instruction sent to email", Toast.LENGTH_SHORT).show();

                                    } else {
                                       progressDialog.dismiss();
                                        Toast.makeText(Forgatpassword_Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }catch (NullPointerException ignored){

                }

            }
        });

    }

   /* private void recoverable(String email) {
        try {
            email=emailEt.getText().toString().trim();


            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailEt.setError("Invalid email address");
                emailEt.requestFocus();
                return;
            }
            progressDialog.setMessage("sending instruction to reset password");
            progressDialog.show();
            firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //instruction sent
                    progressDialog.dismiss();
                    Toast.makeText(Forgatpassword_Activity.this, "password reset instruction sent to email", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //sending instruction failed
                    progressDialog.dismiss();
                    Toast.makeText(Forgatpassword_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }catch (NullPointerException ignored){

        }


    }*/
}
