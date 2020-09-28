package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Login_Activity extends AppCompatActivity {
    //UI views
    private EditText emailEt,passwordEt;
    private TextView forgotTv,noAccountTv;
    private Button BtnLogin;
   private FirebaseAuth firebaseAuth;
   private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        //init UI views

        emailEt=findViewById(R.id.emailEt);
        passwordEt=findViewById(R.id.passwordEt);
        forgotTv=findViewById(R.id.forgotTv);
        noAccountTv=findViewById(R.id.noAccountTv);
        BtnLogin=findViewById(R.id.BtnLogin);
        //initalize firebase
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();

            }
        });

        noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Login_Activity.this,RegisterUser_Activity.class);
                startActivity(intent);

            }
        });
        forgotTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Login_Activity.this,Forgatpassword_Activity.class);
                startActivity(intent);


            }
        });
    }
       private String email,password;
    private void loginUser() {
        progressDialog.setMessage("Login ....");
        progressDialog.show();
        email=emailEt.getText().toString().trim();
        password=passwordEt.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            emailEt.setError("enter the email");
            emailEt.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEt.setError("Invalid email address");
            emailEt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)){
            passwordEt.setError("enter the password");
            passwordEt.requestFocus();
            return;
        }
        if (password.length()<6){
            passwordEt.setError("Password must be 6 character long");
            passwordEt.requestFocus();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //logedin sucessfull

                MakemeOnlion();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed loggin
                progressDialog.dismiss();
                Toast.makeText(Login_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void MakemeOnlion() {
        // after login make user onlion
        progressDialog.setMessage("Checking user....");
        Map<String,Object>map=new HashMap<>();
        map.put("onlion","true");

        //update value to db
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                
                CheckuserType();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Login_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void CheckuserType() {
        //if user is seller start seller main activity
        //if user is buyer start buyer main activity
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uuid").equalTo(firebaseAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    String accountType=""+ds.child("accountType").getValue();
                    if (accountType.equals("Seller")){
                        progressDialog.dismiss();
                        // user is seller
                        Intent intent=new Intent(Login_Activity.this,MainSeller_Activity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        if (accountType.equals("User")){
                            progressDialog.dismiss();
                            // user is buyer
                            Intent intent=new Intent(Login_Activity.this,MainUser_Activity.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
