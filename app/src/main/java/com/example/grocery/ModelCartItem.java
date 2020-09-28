package com.example.grocery;

public class ModelCartItem {
    private String item_id,productid,item_title,priceEach,totalPrice,quantity,uuid;

    public ModelCartItem() {
    }

    public ModelCartItem(String item_id, String productid, String item_title, String priceEach, String totalPrice, String quantity, String uuid) {
        this.item_id = item_id;
        this.productid = productid;
        this.item_title = item_title;
        this.priceEach = priceEach;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
        this.uuid = uuid;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getItem_title() {
        return item_title;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public String getPriceEach() {
        return priceEach;
    }

    public void setPriceEach(String priceEach) {
        this.priceEach = priceEach;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
