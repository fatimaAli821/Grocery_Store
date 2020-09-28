package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Write_review_Activity extends AppCompatActivity {

    private String s_id;
   private ImageButton backbtn;
   private ImageView profile_shop;
   private TextView Shop_Name,comment;
   private RatingBar rating_bar;
   private EditText reviewEt;
  private FloatingActionButton submit_btn;
  private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review_);

        s_id=getIntent().getStringExtra("s_id");
        backbtn=findViewById(R.id.backbtn);
        profile_shop=findViewById(R.id.profile_shop);
        Shop_Name=findViewById(R.id.Shop_Name);
        comment=findViewById(R.id.comment);
        rating_bar=findViewById(R.id.rating_bar);
        reviewEt=findViewById(R.id.reviewEt);
        submit_btn=findViewById(R.id.submit_btn);
        firebaseAuth=FirebaseAuth.getInstance();
        loadMyReview();
        loadShopInfo();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private void loadShopInfo() {

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(s_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String Shopname=dataSnapshot.child("ShopeName").getValue().toString();
                String Profile=dataSnapshot.child("profileImage").getValue().toString().trim();

                Shop_Name.setText(Shopname);

                try {
                    Picasso.get().load(Profile).placeholder(R.drawable.ic_person_grey).into(profile_shop);
                }catch (Exception e){
                    profile_shop.setImageResource(R.drawable.ic_person_grey);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMyReview() {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(s_id).child("Rating").child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    // my review is available in this Shop

                    // get review details
                     String uid=""+dataSnapshot.child("uid").getValue().toString();
                     String ratings=""+dataSnapshot.child("rating").getValue().toString();
                     String review=""+dataSnapshot.child("review").getValue().toString();
                     String timestamp=""+dataSnapshot.child("timestamp").getValue().toString();

                     //set data
                    float myrating=Float.parseFloat(ratings);
                    rating_bar.setRating(myrating);
                    reviewEt.setText(review);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inputData() {

        String rating=""+rating_bar.getRating();
        String review=reviewEt.getText().toString().trim();

        String timeStamp=""+System.currentTimeMillis();//for time of review.

        Map<String,Object>map=new HashMap<>();
        map.put("uid",firebaseAuth.getUid());
        map.put("review",review);//e.g good
        map.put("rating",rating);
        map.put("timestamp",timeStamp);

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(s_id).child("Rating").child(firebaseAuth.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Write_review_Activity.this, "Review Publish Successfully...", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Write_review_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }
}
