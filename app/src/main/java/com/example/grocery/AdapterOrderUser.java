package com.example.grocery;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.HolderOrderUser> {

    private Context context;
    private ArrayList<ModelOrederUser>modelOrederUserList;

    public AdapterOrderUser(Context context, ArrayList<ModelOrederUser> modelOrederUserList) {
        this.context = context;
        this.modelOrederUserList = modelOrederUserList;
    }

    @NonNull
    @Override
    public HolderOrderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate layout
        View view= LayoutInflater.from(context).inflate(R.layout.row_order_user,parent,false);
        return new HolderOrderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderUser holder, int position) {
        // get data
        ModelOrederUser modelOrederUser=modelOrederUserList.get(position);
        final String orderId=modelOrederUser.getOrderId();
        String orderBy=modelOrederUser.getOrderBy();
        String orderStatus=modelOrederUser.getOrderStatus();
        String orderTime=modelOrederUser.getOrderTime();
        String ordercost=modelOrederUser.getOrderCost();
        final String orderTo=modelOrederUser.getOrderTo();

        //get ShopInfo
        loadShopInfo(modelOrederUser,holder);

        //set data
        try {
            holder.amountTv.setText("Amount $:"+ordercost);
            holder.orderStatus.setText(orderStatus);
            holder.orderTv.setText("Order Id"+orderId);

            //change order status textcolor

            if (orderStatus.equals("In progress")){
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
            if (orderStatus.equals("Completed")){
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.Greencolor));
            }
            if (orderStatus.equals("Cancelled")){
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.RedColor));
            }

            //convert timestamp to date format
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(orderTime));
            String dateformat= DateFormat.format("dd/MM/yyy",calendar).toString();

            holder.dateTv.setText(dateformat);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 //open order details,we need to keys there,orderId,order To;
                    Intent intent=new Intent(context,OrderDetailUsersActivity.class);
                    intent.putExtra("orderTo",orderTo);
                    intent.putExtra("orderId",orderId);
                    context.startActivity(intent);//now get these values through OrderDetailUsersActivity;

                }
            });

        }catch (NullPointerException ignored){

        }






    }

    private void loadShopInfo(ModelOrederUser modelOrederUser, final HolderOrderUser holder) {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(modelOrederUser.getOrderTo()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ShopName=dataSnapshot.child("ShopeName").getValue().toString();
                try {
                    holder.Shop_nameTv.setText(ShopName);
                }catch (NullPointerException ignored){

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelOrederUserList.size();
    }
    //hold the Ui Views

    class HolderOrderUser extends RecyclerView.ViewHolder{

       private TextView orderTv,dateTv,Shop_nameTv,amountTv,orderStatus;
       private ImageView next_Iv;


        public HolderOrderUser(@NonNull View itemView) {
            super(itemView);

            orderTv=itemView.findViewById(R.id.orderTv);
            dateTv=itemView.findViewById(R.id.dateTv);
            Shop_nameTv=itemView.findViewById(R.id.Shop_nameTv);
            amountTv=itemView.findViewById(R.id.amountTv);
            orderStatus=itemView.findViewById(R.id.orderStatus);
            next_Iv=itemView.findViewById(R.id.next_Iv);


        }
    }
}
