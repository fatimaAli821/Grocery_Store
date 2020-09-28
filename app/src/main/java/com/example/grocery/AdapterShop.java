package com.example.grocery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HoldeerShop>{

    private Context context;
    private ArrayList<ModelShop> shopslist;

    public AdapterShop(Context context, ArrayList<ModelShop> shopslist) {
        this.context = context;
        this.shopslist = shopslist;
    }

    @NonNull
    @Override
    public HoldeerShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view= LayoutInflater.from(context).inflate(R.layout.row_shop,null);
        return  new HoldeerShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HoldeerShop holder, int position) {
        // get data
        final ModelShop modelShop=shopslist.get(position);
        final String accountType=modelShop.getAccountType();
        String adress=modelShop.getAddress();
        String country=modelShop.getCountry();
        String city=modelShop.getCity();
        String state=modelShop.getState();
        String deliverfee=modelShop.getDelivery_fee();
        String email=modelShop.getEmail();
        String latitude=modelShop.getLatitude();
        String longitude=modelShop.getLongitude();
        String phone=modelShop.getPhone();
        final String name=modelShop.getName();
       final String uid=modelShop.getUuid();
        String onlion=modelShop.getOnlion();
        String timestamp=modelShop.getTimestamp();
        String ShopOpen=modelShop.getShopOpen();
        final String profileimg=modelShop.getProfileImage();
        final String ShopName=modelShop.getShopeName();

        loadRevies(modelShop,holder);//load avg rating set to rating bar



        //set data
        holder.ShopName.setText(ShopName);
        holder.Phone.setText(phone);
        holder.adress.setText(adress);
        // check onlion
        if (onlion.equals("true")){

            holder.onlionIv.setVisibility(View.VISIBLE);

        }else if (onlion.equals("false")){

            holder.onlionIv.setVisibility(View.GONE);

        }

        // check shop open
        if (ShopOpen.equals("true")){
            holder.closed.setVisibility(View.GONE);
        }else
            {
            holder.closed.setVisibility(View.VISIBLE);

        }

        try {
            Picasso.get().load(profileimg).placeholder(R.drawable.ic_person_grey).into(holder.ShopIv);
        }catch (Exception e){
            holder.ShopIv.setImageResource(R.drawable.ic_person_grey);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ShopDetail_Activity.class);
                intent.putExtra("ShopUid",uid);
                intent.putExtra("Shop",ShopName);
                Toast.makeText(context, ""+ShopName, Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            }
        });








    }
    private float ratingSum=0;
    private void loadRevies(ModelShop modelShop, final HoldeerShop holder) {

        String ShopUid=modelShop.getUuid();

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(ShopUid).child("Rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ratingSum=0;
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    float rating=Float.parseFloat(""+ds.child("rating").getValue().toString());

                    ratingSum=ratingSum+rating;//for avg rating add all ratings,later will divide it by number of reviews


                }


                long numberOfReviews=dataSnapshot.getChildrenCount();
                float avgrating=ratingSum/numberOfReviews;

               holder.ratingbar.setRating(avgrating);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return shopslist.size() ;
    }

    class HoldeerShop extends RecyclerView.ViewHolder{
        // hold the ui views
        private ImageView ShopIv,onlionIv;
        private TextView ShopName,Phone,adress,closed;
        private RatingBar ratingbar;
        public HoldeerShop(@NonNull View itemView) {
            super(itemView);
            ShopIv=itemView.findViewById(R.id.ShopIv);
            onlionIv=itemView.findViewById(R.id.onlionIv);
            ShopName=itemView.findViewById(R.id.ShopName);
            adress=itemView.findViewById(R.id.adress);
            Phone=itemView.findViewById(R.id.Phone);
            closed=itemView.findViewById(R.id.closed);
            ratingbar=itemView.findViewById(R.id.ratingbar);

        }
    }
}
