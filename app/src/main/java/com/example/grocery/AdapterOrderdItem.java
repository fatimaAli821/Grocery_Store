package com.example.grocery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterOrderdItem extends RecyclerView.Adapter<AdapterOrderdItem.HlderOrderdItem>{

    private Context context;
    private ArrayList<ModelOrderdItem>modelOrderdItemList;

    public AdapterOrderdItem(Context context, ArrayList<ModelOrderdItem> modelOrderdItemList) {
        this.context = context;
        this.modelOrderdItemList = modelOrderdItemList;
    }







    @NonNull
    @Override
    public HlderOrderdItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view= LayoutInflater.from(context).inflate(R.layout.row_orderditems,parent,false);

        return new HlderOrderdItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HlderOrderdItem holder, int position) {
        //get data
        ModelOrderdItem modelOrderdItem=modelOrderdItemList.get(position);
        String Pid=modelOrderdItem.getProductId();
        String Name=modelOrderdItem.getName();
        String coast=modelOrderdItem.getCoastEach();
        String priceTotal=modelOrderdItem.getCoastTotal();
        String quantity=modelOrderdItem.getQuantity();

        //set data
        holder.itemTittle.setText(Name);
        holder.itemprice.setText(priceTotal);
        holder.itemPriceEachTv.setText("$"+coast);
        holder.itemquantityTv.setText("["+quantity+"]");

    }

    @Override
    public int getItemCount() {
        return modelOrderdItemList.size();
    }

    class HlderOrderdItem extends RecyclerView.ViewHolder{

        private TextView itemTittle,itemprice,itemPriceEachTv,itemquantityTv;

        public HlderOrderdItem(@NonNull View itemView) {
            super(itemView);
            itemTittle=itemView.findViewById(R.id.itemTittle);
            itemprice=itemView.findViewById(R.id.itemprice);
            itemPriceEachTv=itemView.findViewById(R.id.itemPriceEachTv);
            itemquantityTv=itemView.findViewById(R.id.itemquantityTv);
        }
    }
}
