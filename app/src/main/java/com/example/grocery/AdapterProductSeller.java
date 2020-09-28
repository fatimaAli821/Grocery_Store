package com.example.grocery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HplderProductSeller> {

    private Context context;
    public ArrayList<ModelProduct>productlist;
    public ArrayList<ModelProduct>productlist_All;
    public ArrayList<ModelProduct>filterlist;
  // private FilterProducts filter;

    public AdapterProductSeller( Context context,ArrayList<ModelProduct>productList) {
        this.context=context;
        this.productlist=productList;
      //  this.productlist_All=productList;
        //this.filterlist=productlist_All;

    }

    @NonNull
    @Override
    public HplderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layut
        View view= LayoutInflater.from(context).inflate(R.layout.row_product_seller,parent,false);
        return new HplderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HplderProductSeller holder, int position) {
        //get data
        try {
            final ModelProduct modelProduct=productlist.get(position);
            String product_id=modelProduct.getProduct_id();
            String uid=modelProduct.getUuid();
            String Title=modelProduct.getProduct_title();
            String Quantity=modelProduct.getProduct_Quantity();
            String timestamp=modelProduct.getTimestamp();
            String disscount_Avalible=modelProduct.getDisscount_Avalible();
            String disscount_note=modelProduct.getDiscoutnt_Note();
            String disscount_price=modelProduct.getDiscount_price();
            String orignal_price=modelProduct.getOrignal_price();
            String product_category=modelProduct.getProduct_Category();
            String product_discription=modelProduct.getProduct_Category();
            String icon=modelProduct.getProfile_Image();

            //set the values
            holder.tile.setText(Title);
            holder.disscount_note.setText(disscount_note);
            holder.discount_priceTv.setText('$'+disscount_price);

            holder.Orignal_price.setText('$'+orignal_price);
            holder.quantity.setText(Quantity);

                       if (disscount_Avalible.equals("true")){
                //product is on discount
                holder.discount_priceTv.setVisibility(View.VISIBLE);
                holder.disscount_note.setVisibility(View.VISIBLE);
                holder.Orignal_price.setPaintFlags(holder.Orignal_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);// add strike through on orignal price
            }else {
                //product is not on discount

                holder.discount_priceTv.setVisibility(View.GONE);
                holder.disscount_note.setVisibility(View.GONE);
            }

            try {
                Picasso.get().load(icon).placeholder(R.drawable.ic_person_grey).into(holder.product_icon);
            }catch (Exception e){
                holder.product_icon.setImageResource(R.drawable.ic_person_grey);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //handel item click,show item detail(in bottom sheet)
                    DetailBottomSheet(modelProduct);//here model product contain detail of clicked product
                }
            });


        }catch (NullPointerException ignored){

        }

    }

   private void DetailBottomSheet(final ModelProduct modelProduct) {
        //bottom sheet
        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);
        //inflate view for bottom sheet
        View view= LayoutInflater.from(context).inflate(R.layout.bs_product_detail_seller,null);
        //set view to bottom sheet
        bottomSheetDialog.setContentView(view);

        //init view of bottom sheet
        ImageButton backbtn=view.findViewById(R.id.backbtn);
        ImageButton Btn_delete=view.findViewById(R.id.Btn_delete);
        ImageButton Btn_edit=view.findViewById(R.id.Btn_edit);
        ImageView product_iconIv=view.findViewById(R.id.product_iconIv);
        TextView disscount_Note=view.findViewById(R.id.disscount_note);
        final TextView tile=view.findViewById(R.id.tile);
        TextView discriptionTv=view.findViewById(R.id.discriptionTv);
        TextView categoryTv=view.findViewById(R.id.categoryTv);
        TextView quantityTv=view.findViewById(R.id.quantityTv);
        TextView discount_priceTv=view.findViewById(R.id.discount_priceTv);
        TextView Orignal_priceTV=view.findViewById(R.id.Orignal_price);

        //get data

        final String product_id=modelProduct.getProduct_id();
        String uid=modelProduct.getUuid();
        final String Title=modelProduct.getProduct_title();
        String Quantity=modelProduct.getProduct_Quantity();
        String timestamp=modelProduct.getTimestamp();
        String disscount_Avalible=modelProduct.getDisscount_Avalible();
        String disscount_note=modelProduct.getDiscoutnt_Note();
        String disscount_price=modelProduct.getDiscount_price();
        String orignal_price=modelProduct.getOrignal_price();
        String product_category=modelProduct.getProduct_Category();
        String product_discription=modelProduct.getProduct_Description();
        String icon=modelProduct.getProfile_Image();

        //set data
        tile.setText(Title);
        discriptionTv.setText(product_discription);
        categoryTv.setText(product_category);
        quantityTv.setText(Quantity);
        disscount_Note.setText(disscount_note);
        discount_priceTv.setText("$"+disscount_price);
        Orignal_priceTV.setText("$"+orignal_price);

        if (disscount_Avalible.equals("true")){
            //product is on discount
            discount_priceTv.setVisibility(View.VISIBLE);
            disscount_Note.setVisibility(View.VISIBLE);
            Orignal_priceTV.setPaintFlags(Orignal_priceTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);// add strike through on orignal price
        }else {
            //product is not on discount

            discount_priceTv.setVisibility(View.GONE);
          disscount_Note.setVisibility(View.GONE);
        }

        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_person_grey).into(product_iconIv);
        }catch (Exception e){
            product_iconIv.setImageResource(R.drawable.ic_person_grey);
        }
        bottomSheetDialog.show();

        Btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context,EditProductActivity.class);
                intent.putExtra("productid",product_id);
                context.startActivity(intent);

            }
        });
        Btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete product"+" "+modelProduct.getProduct_title()+"?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteProduct(product_id);

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

            }
        });


    }

    private void deleteProduct(String product_id) {
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Products").child(product_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {

        if (productlist==null){
            return 0;
        }else {
            return productlist.size();
        }





    }

   /* @Override
    public Filter getFilter() {
        if (filter==null){
            filter=new FilterProducts(this,filterlist);
        }
        return filter;
    }*/


    class HplderProductSeller extends RecyclerView.ViewHolder{
            //holds views of recycler view
        private ImageView product_icon,nextIv;
        private TextView disscount_note,tile,quantity,discount_priceTv,Orignal_price;

        public HplderProductSeller(@NonNull View itemView) {
            super(itemView);
            product_icon=itemView.findViewById(R.id.product_icon);
            nextIv=itemView.findViewById(R.id.nextIv);
            disscount_note=itemView.findViewById(R.id.disscount_note);
            tile=itemView.findViewById(R.id.tile);
            quantity=itemView.findViewById(R.id.quantity);
            discount_priceTv=itemView.findViewById(R.id.discount_priceTv);
            Orignal_price=itemView.findViewById(R.id.Orignal_price);


        }
    }
}
