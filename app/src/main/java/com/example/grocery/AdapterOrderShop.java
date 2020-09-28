package com.example.grocery;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderShop extends RecyclerView.Adapter<AdapterOrderShop.HolderOrderShop> {

   private Context context;
   private ArrayList<ModelOrderShop>modelOrderShopArrayList;

    public AdapterOrderShop(Context context, ArrayList<ModelOrderShop> modelOrderShopArrayList) {
        this.context = context;
        this.modelOrderShopArrayList = modelOrderShopArrayList;
    }

    @NonNull
    @Override
    public HolderOrderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.row_order_seller,parent,false);
        return new HolderOrderShop(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull HolderOrderShop holder, int position) {
        // get data
        ModelOrderShop modelOrderShop=modelOrderShopArrayList.get(position);
        final String OrderId=modelOrderShop.getOrderId();
        String OrderTime=modelOrderShop.getOrderTime();
        String OrderStatus=modelOrderShop.getOrderStatus();
        String latitude=modelOrderShop.getLatitude();
        String longitude=modelOrderShop.getLongitude();
        String OrderCost=modelOrderShop.getOrderCost();
        String deliveryfee=modelOrderShop.getDeliveryfee();
        final String OrderBy=modelOrderShop.getOrderBy();
        String orderTo=modelOrderShop.getOrderTo();

        //load user(buyer)info
        loadUserInfo(modelOrderShop,holder);

        //set data
        holder.amountTv_Seller.setText("Amount :$"+OrderCost);
        holder.orderIdTV_Seller.setText(OrderId);
        holder.orderStatusTV_Seller.setText(OrderStatus);

        if (OrderStatus.equals("In Progress")){
            holder.orderStatusTV_Seller.setTextColor(context.getResources().getColor(R.color.colorPrimary));

        }else if (OrderStatus.equals("Completed")){
            holder.orderStatusTV_Seller.setTextColor(context.getResources().getColor(R.color.Greencolor));

        }else if (OrderStatus.equals("Cancelled")){
            holder.orderStatusTV_Seller.setTextColor(context.getResources().getColor(R.color.RedColor));

        }

        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(OrderTime));
        String formatdate= DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.orderDateTv_Seller.setText(formatdate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open orders detail
                Intent intent=new Intent(context,ShopDetailSellerActivity.class);
                intent.putExtra("Order_id",OrderId);//to load order info
                intent.putExtra("Order_By",OrderBy);//to load info of the user who placed order
                context.startActivity(intent);


            }
        });



    }

    private void loadUserInfo(ModelOrderShop modelOrderShop, final HolderOrderShop holder) {
        // to load user/buyer email:modelOrderShop.getOrderBy() contains the id of the user/buyer
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(modelOrderShop.getOrderBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String email=""+dataSnapshot.child("email").getValue().toString();
                holder.emilTv.setText(email);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelOrderShopArrayList.size();
    }


    class HolderOrderShop extends RecyclerView.ViewHolder{
        private TextView orderIdTV_Seller,orderDateTv_Seller,emilTv,amountTv_Seller,orderStatusTV_Seller;
        private ImageButton next_Iv_Seller;

        public HolderOrderShop(@NonNull View itemView) {
            super(itemView);

            orderIdTV_Seller=itemView.findViewById(R.id.orderIdTV_Seller);
            orderDateTv_Seller=itemView.findViewById(R.id.orderDateTv_Seller);
            emilTv=itemView.findViewById(R.id.emilTv);
            amountTv_Seller=itemView.findViewById(R.id.amountTv_Seller);
            orderStatusTV_Seller=itemView.findViewById(R.id.orderStatusTV_Seller);
            next_Iv_Seller=itemView.findViewById(R.id.next_Iv_Seller);
        }
    }


}
