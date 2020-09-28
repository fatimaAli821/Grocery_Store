package com.example.grocery;

import android.widget.Filter;

import androidx.constraintlayout.widget.Constraints;

import java.util.ArrayList;

public class FilterProducts extends Filter {
    AdapterProductSeller adapter;
    ArrayList<ModelProduct> filterlist;

    public FilterProducts(AdapterProductSeller adapter, ArrayList<ModelProduct> filterlist) {
        this.adapter = adapter;
        this.filterlist = filterlist;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults results=new FilterResults();

        // validate data for search query
        if (constraint.toString().isEmpty()){
            filterlist.addAll(adapter.productlist_All);

        }else if (constraint!=null&&constraint.length()>0){
            // search field not empty,searching something,perform search
            //change to case upper case to make case insenstive
            constraint= constraint.toString().toUpperCase();
            //store our filterd list
            ArrayList<ModelProduct> filterModel=new ArrayList<>();
            for (int i=0;i<=filterlist.size();i++){
                //check, search by title and category
                if (filterlist.get(i).getProduct_title().toUpperCase().contains(constraint)||
                        filterlist.get(i).getProduct_Category().toUpperCase().contains(constraint)){
                    filterModel.add(filterlist.get(i));
                }
            }

            results.count=filterModel.size();
            results.values=filterModel;
        }else {

            // search field empty,not searching,retrun orignal/all/complete list
            results.count=filterlist.size();
            results.values=filterlist;
        }
        return results;
    }


    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

       /* adapter.productlist=(ArrayList<ModelProduct>)results.values;
        //refresh adapter
        adapter.notifyDataSetChanged();*/

    }
}
