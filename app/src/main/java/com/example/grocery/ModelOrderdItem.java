package com.example.grocery;

public class ModelOrderdItem {

    private String itemId,ProductId,CoastEach,CoastTotal,Quantity,
            Name;

    public ModelOrderdItem() {
    }

    public ModelOrderdItem(String itemId, String productId, String coastEach, String coastTotal, String quantity, String name) {
        this.itemId = itemId;
        ProductId = productId;
        CoastEach = coastEach;
        CoastTotal = coastTotal;
        Quantity = quantity;
        Name = name;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getCoastEach() {
        return CoastEach;
    }

    public void setCoastEach(String coastEach) {
        CoastEach = coastEach;
    }

    public String getCoastTotal() {
        return CoastTotal;
    }

    public void setCoastTotal(String coastTotal) {
        CoastTotal = coastTotal;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
