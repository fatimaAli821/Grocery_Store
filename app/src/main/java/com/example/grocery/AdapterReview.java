package com.example.grocery;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.HolderReview>{
    private ArrayList<ModelReview>modelReviewArrayList;
    private Context context;

    public AdapterReview(ArrayList<ModelReview> modelReviewArrayList, ShopeReview_Activity context) {
        this.modelReviewArrayList = modelReviewArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HolderReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_review,parent,false);
        return new HolderReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReview holder, int position) {
        // get data
        ModelReview modelReview=modelReviewArrayList.get(position);
        String uid=modelReview.getUid();
        String rating=modelReview.getRating();
        String TimeStamp=modelReview.getTimestamp();
        String Review=modelReview.getReview();
        
        loadUserDetail(modelReview,holder);//we also need (profileImg,name)of user who wrote the review:we can do it using
        //uid of user
        //set data
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(TimeStamp));
        String dateformat= DateFormat.format("dd,MM.yyy",calendar).toString();

        holder.rating_barReview.setRating(Float.parseFloat(rating));
        holder.user_Review.setText(Review);
        holder.date_review.setText(dateformat);

    }

    private void loadUserDetail(ModelReview modelReview, final HolderReview holder) {

        //uid of user who wrote review
        String uid=modelReview.getUid();

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get user info use same key as FireBase
                String name=""+dataSnapshot.child("name").getValue().toString();
                String ProfileImage=""+dataSnapshot.child("profileImage").getValue().toString();

                //set data
                holder.name_review.setText(name);

                try {
                    Picasso.get().load(ProfileImage).placeholder(R.drawable.ic_person_grey).into(holder.pic_Review);
                }catch (Exception e){
                    holder.pic_Review.setImageResource(R.drawable.ic_person_grey);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelReviewArrayList.size();
    }

    class HolderReview extends RecyclerView.ViewHolder{
        private ImageView pic_Review;
       private TextView name_review,date_review,user_Review;
       private RatingBar rating_barReview;



        public HolderReview(@NonNull View itemView) {
            super(itemView);
            pic_Review=itemView.findViewById(R.id.pic_Review);
            name_review=itemView.findViewById(R.id.name_review);
            date_review=itemView.findViewById(R.id.date_review);
            user_Review=itemView.findViewById(R.id.user_Review);
            rating_barReview=itemView.findViewById(R.id.rating_barReview);
        }
    }
}
