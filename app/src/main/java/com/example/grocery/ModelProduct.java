package com.example.grocery;

public class ModelProduct {
    private String uuid,Product_title,Product_Description,Product_Category,Product_Quantity,Orignal_price,Discount_price,
            product_id,Discoutnt_Note,timestamp,disscount_Avalible,profile_Image;

    public ModelProduct() {
    }

    public ModelProduct(String uuid, String product_title, String product_Description,
                        String product_Category, String product_Quantity, String orignal_price, String discount_price,
                        String product_id, String discoutnt_Note, String timestamp, String disscount_Avalible, String profile_Image) {
        this.uuid = uuid;
        Product_title = product_title;
        Product_Description = product_Description;
        Product_Category = product_Category;
        Product_Quantity = product_Quantity;
        Orignal_price = orignal_price;
        Discount_price = discount_price;
        this.product_id = product_id;
        Discoutnt_Note = discoutnt_Note;
        this.timestamp = timestamp;
        this.disscount_Avalible = disscount_Avalible;
        this.profile_Image = profile_Image;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getProduct_title() {
        return Product_title;
    }

    public void setProduct_title(String product_title) {
        Product_title = product_title;
    }

    public String getProduct_Description() {
        return Product_Description;
    }

    public void setProduct_Description(String product_Description) {
        Product_Description = product_Description;
    }

    public String getProduct_Category() {
        return Product_Category;
    }

    public void setProduct_Category(String product_Category) {
        Product_Category = product_Category;
    }

    public String getProduct_Quantity() {
        return Product_Quantity;
    }

    public void setProduct_Quantity(String product_Quantity) {
        Product_Quantity = product_Quantity;
    }

    public String getOrignal_price() {
        return Orignal_price;
    }

    public void setOrignal_price(String orignal_price) {
        Orignal_price = orignal_price;
    }

    public String getDiscount_price() {
        return Discount_price;
    }

    public void setDiscount_price(String discount_price) {
        Discount_price = discount_price;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getDiscoutnt_Note() {
        return Discoutnt_Note;
    }

    public void setDiscoutnt_Note(String discoutnt_Note) {
        Discoutnt_Note = discoutnt_Note;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDisscount_Avalible() {
        return disscount_Avalible;
    }

    public void setDisscount_Avalible(String disscount_Avalible) {
        this.disscount_Avalible = disscount_Avalible;
    }

    public String getProfile_Image() {
        return profile_Image;
    }

    public void setProfile_Image(String profile_Image) {
        this.profile_Image = profile_Image;
    }
}
