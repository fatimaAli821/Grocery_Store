package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddPromotionCodeActivity extends AppCompatActivity {
private EditText PromoCodeEt,PromoDescriptionEt,PromoPriceEt,minimumOrderPrice;
private TextView expiredateTv,TitlePromo;
private ImageButton backbtn;
private Button AddBtn;
private FirebaseAuth firebaseAuth;
private ProgressDialog progressDialog;
private String promoId;
private boolean isupdate=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion_code);



        PromoCodeEt=findViewById(R.id.PromoCodeEt);
        PromoDescriptionEt=findViewById(R.id.PromoDescriptionEt);
        PromoPriceEt=findViewById(R.id.PromoPriceEt);
        minimumOrderPrice=findViewById(R.id.minimumOrderPrice);
        expiredateTv=findViewById(R.id.expiredateTv);
        backbtn=findViewById(R.id.backbtn);
        AddBtn=findViewById(R.id.AddBtn);
        TitlePromo=findViewById(R.id.TitlePromo);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);

        //get promoId from intent
        Intent intent=getIntent();
        if (intent.getStringExtra("promoId")!=null){
            //came here from Adapter to update record;
            promoId=intent.getStringExtra("promoId");

            TitlePromo.setText("Update Promotion code");
            AddBtn.setText("Update");

            isupdate=true;

            loadpromoInfo();//load promotion code info to set in our views,so we can also update Single value


        }else{
            //came here from promo code list Activity to Add code

            TitlePromo.setText("Add Promotion code");
            AddBtn.setText("Add");

            isupdate=false;


        }

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                InPutData();

            }
        });
        expiredateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerdialouge();
            }
        });




    }

    private void loadpromoInfo() {

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Promotion Code").child(promoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id=dataSnapshot.child("id").getValue().toString();
                String timestamp=dataSnapshot.child("timestamp").getValue().toString();
                String Code=dataSnapshot.child("Code").getValue().toString();
                String p_Description=dataSnapshot.child("p_Description").getValue().toString();
                String promoPrice=dataSnapshot.child("promoPrice").getValue().toString();
                String MiniOrder=dataSnapshot.child("MiniOrder").getValue().toString();
                String expiredate=dataSnapshot.child("expiredate").getValue().toString();

                PromoCodeEt.setText(Code);
                PromoDescriptionEt.setText(p_Description);
                PromoPriceEt.setText(promoPrice);
                minimumOrderPrice.setText(MiniOrder);
                expiredateTv.setText(expiredate);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private String description,promoCode,promoPrice,MiniOrder,expiredate;
    private void InPutData() {
        
        //input data
        description=PromoDescriptionEt.getText().toString().trim();
        promoCode=PromoCodeEt.getText().toString().trim();
        promoPrice=PromoPriceEt.getText().toString().trim();
        MiniOrder=minimumOrderPrice.getText().toString().trim();
        expiredate=expiredateTv.getText().toString().trim();
        
        if (TextUtils.isEmpty(promoCode)){
            Toast.makeText(this, "Enter the discount code..", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)){
            Toast.makeText(this, "Enter the description..", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(promoPrice)){
            Toast.makeText(this, "Enter the Price..", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(MiniOrder)){
            Toast.makeText(this, "Enter the Minimum Order..", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(expiredate)){
            Toast.makeText(this, "Choose expire Date..", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isupdate){
            UpdateDataToDb();
        }else {
            AddingDatatodb();
        }

        





    }

    private void UpdateDataToDb() {
        progressDialog.setMessage("Adding Promotion Code");
        progressDialog.show();


        Map<String,Object>map=new HashMap<>();
        map.put("Code",promoCode);
        map.put("p_Description",description);
        map.put("promoPrice",promoPrice);
        map.put("MiniOrder",MiniOrder);
        map.put("expiredate",expiredate);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Promotion Code").child(promoId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddPromotionCodeActivity.this,"Updating..", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPromotionCodeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void AddingDatatodb() {
        progressDialog.setMessage("Adding Promotion Code");
        progressDialog.show();

        String timestamp=""+System.currentTimeMillis();
        Map<String,Object>map=new HashMap<>();
        map.put("id",timestamp);
        map.put("timestamp",timestamp);
        map.put("Code",promoCode);
        map.put("p_Description",description);
        map.put("promoPrice",promoPrice);
        map.put("MiniOrder",MiniOrder);
        map.put("expiredate",expiredate);

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Promotion Code").child(timestamp).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddPromotionCodeActivity.this, "Promotion Code added..", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPromotionCodeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void DatePickerdialouge() {
        //get current date to set on calendar

        Calendar c=Calendar.getInstance();
        int mYear=c.get(Calendar.YEAR);
        int mMonth=c.get(Calendar.MONTH);
        int mDay=c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                DecimalFormat decimalFormat=new DecimalFormat("00");
                String pday=decimalFormat.format(dayOfMonth);
                String pMonth=decimalFormat.format(month);
                String pYear=""+ year;
                String pDate=pday+"/"+pMonth+"/"+pYear;

                expiredateTv.setText(pDate);

            }
        },mYear,mMonth,mDay);
        //show dialouge
        datePickerDialog.show();
        //disable paste date selection on Calendar
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
    }
}