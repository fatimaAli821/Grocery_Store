package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PromotionCodeActivity extends AppCompatActivity {

    private ImageButton backbtn,add_PromotionBtn,filterProductBtn2;
    private TextView filterTv,filterProductTv;
    private RecyclerView PromotionRv;
    private ArrayList<ModelPromotion>modelPromotionArrayList;
    private AdapterPromotionShop adapterPromotionShop;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_code);
        add_PromotionBtn=findViewById(R.id.add_PromotionBtn);
        backbtn=findViewById(R.id.backbtn);
        filterProductBtn2=findViewById(R.id.filterProductBtn2);
        filterTv=findViewById(R.id.filterTv);
        PromotionRv=findViewById(R.id.PromotionRv);
        filterProductTv=findViewById(R.id.filterProductTv);
        firebaseAuth=FirebaseAuth.getInstance();
        loadAllpromoCode();

        filterProductBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterDialouge();
            }
        });


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        add_PromotionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PromotionCodeActivity.this,AddPromotionCodeActivity.class);
                startActivity(intent);
            }
        });

    }

    private void FilterDialouge() {
        //show options
        String Options[]={"All","Expired","Not Expired"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Filter Promotion Code");
        builder.setItems(Options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    //All
                    filterProductTv.setText("All Promotion Code");
                    loadAllpromoCode();
                }else if (which==1){
                    //expired
                    filterProductTv.setText("Expired Promotion Code");
                    loadExpiredPromoCode();
                }else if (which==2){
                    //not expired
                    filterProductTv.setText("Not Expired Promotion Code");
                    loadNotexpiredPromocode();
                }
            }
        }).show();


    }

    private  void loadAllpromoCode(){
        //init list
        modelPromotionArrayList=new ArrayList<>();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Promotion Code").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clear list before adding data
                modelPromotionArrayList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelPromotion modelPromotion=ds.getValue(ModelPromotion.class);
                    modelPromotionArrayList.add(modelPromotion);
                }
                //setup adapter
                adapterPromotionShop=new AdapterPromotionShop(PromotionCodeActivity.this,modelPromotionArrayList);
                PromotionRv.setAdapter(adapterPromotionShop);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadExpiredPromoCode(){
        //get Current date;
        DecimalFormat decimalFormat=new DecimalFormat("00");
        Calendar calendar=Calendar.getInstance();
        int Year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int Day=calendar.get(Calendar.DAY_OF_MONTH);
        final String TodayDate=Day+"/"+month+"/"+Year;

        //init list
        modelPromotionArrayList=new ArrayList<>();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Promotion Code").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clear list before adding data
                modelPromotionArrayList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelPromotion modelPromotion=ds.getValue(ModelPromotion.class);

                    String expDate=modelPromotion.getExpiredate();

                    //check for expired
                    try {

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                        Date currentDate=simpleDateFormat.parse(TodayDate);
                        Date expireDate=simpleDateFormat.parse(expDate);

                        if (expireDate.compareTo(currentDate)>0){
                            //date 1 occur after date 2

                        }else if (expireDate.compareTo(currentDate)<0){
                            //date 1 occur before date 2

                            modelPromotionArrayList.add(modelPromotion);

                        }else if (expireDate.compareTo(currentDate)==0){
                            //both date equal
                        }



                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                //setup adapter
                adapterPromotionShop=new AdapterPromotionShop(PromotionCodeActivity.this,modelPromotionArrayList);
                PromotionRv.setAdapter(adapterPromotionShop);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadNotexpiredPromocode(){

        //get Current date;
        DecimalFormat decimalFormat=new DecimalFormat("00");
        Calendar calendar=Calendar.getInstance();
        int Year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int Day=calendar.get(Calendar.DAY_OF_MONTH);
        final String TodayDate=Day+"/"+month+"/"+Year;

        //init list
        modelPromotionArrayList=new ArrayList<>();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Promotion Code").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clear list before adding data
                modelPromotionArrayList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelPromotion modelPromotion=ds.getValue(ModelPromotion.class);

                    String expDate=modelPromotion.getExpiredate();

                    //check for expired
                    try {

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                        Date currentDate=simpleDateFormat.parse(TodayDate);
                        Date expireDate=simpleDateFormat.parse(expDate);

                        if (expireDate.compareTo(currentDate)>0){
                            //date 1 occur after date 2
                            modelPromotionArrayList.add(modelPromotion);


                        }else if (expireDate.compareTo(currentDate)<0){
                            //date 1 occur before date 2



                        }else if (expireDate.compareTo(currentDate)==0){
                            //both date equal
                            modelPromotionArrayList.add(modelPromotion);

                        }



                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                //setup adapter
                adapterPromotionShop=new AdapterPromotionShop(PromotionCodeActivity.this,modelPromotionArrayList);
                PromotionRv.setAdapter(adapterPromotionShop);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}