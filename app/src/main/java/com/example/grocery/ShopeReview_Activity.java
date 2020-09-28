package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShopeReview_Activity extends AppCompatActivity {

    private String shop_uid;
    private ImageButton backbtn;
    private ImageView profile_review;
    private TextView shop_nam,rating_Tv;
    private RatingBar Rating_bare;
   private RecyclerView ReviewRv;
   private FirebaseAuth firebaseAuth;
   private ArrayList<ModelReview>modelReviewArrayList;
   private AdapterReview adapterReview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shope_review_);

        shop_uid=getIntent().getStringExtra("shop_uid");
        backbtn=findViewById(R.id.backbtn);
        profile_review=findViewById(R.id.profile_review);
        shop_nam=findViewById(R.id.shop_nam);
        Rating_bare=findViewById(R.id.Rating_bare);
        ReviewRv=findViewById(R.id.ReviewRv);
        rating_Tv=findViewById(R.id.rating_Tv);
        firebaseAuth=FirebaseAuth.getInstance();
        LoadShopDetails();//for shop name image
        loadRevies();//reviews lis for and Avg Rating

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }
    private float ratingSum=0;
    private void loadRevies() {
        modelReviewArrayList=new ArrayList<>();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(shop_uid).child("Rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelReviewArrayList.clear();
                ratingSum=0;
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    float rating=Float.parseFloat(""+ds.child("rating").getValue().toString());

                    ratingSum=ratingSum+rating;//for avg rating add all ratings,later will divide it by number of reviews

                    ModelReview modelReview=ds.getValue(ModelReview.class);
                    modelReviewArrayList.add(modelReview);
                }

                //setup Adapter
               adapterReview=new AdapterReview(modelReviewArrayList, ShopeReview_Activity.this);
                ReviewRv.setAdapter(adapterReview);

                long numberOfReviews=dataSnapshot.getChildrenCount();
                float avgrating=ratingSum/numberOfReviews;

                rating_Tv.setText(String.format("%.2f",avgrating)+"["+numberOfReviews+"]");//4.7[10]
                Rating_bare.setRating(avgrating);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LoadShopDetails() {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(shop_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ShopName=""+dataSnapshot.child("ShopeName").getValue().toString();
                String Image=""+dataSnapshot.child("profileImage").getValue().toString();


                try {
                    Picasso.get().load(Image).placeholder(R.drawable.ic_person_grey).into(profile_review);
                }catch (Exception e){
                    profile_review.setImageResource(R.drawable.ic_person_grey);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
