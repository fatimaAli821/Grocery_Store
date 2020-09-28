package com.example.grocery;

public class ModelPromotion {
  private   String id,timestamp,Code,p_Description,promoPrice,MiniOrder,
          expiredate;

    public ModelPromotion() {
    }

    public ModelPromotion(String id, String timestamp, String code, String p_Description, String promoPrice, String miniOrder, String expiredate) {
        this.id = id;
        this.timestamp = timestamp;
        Code = code;
        this.p_Description = p_Description;
        this.promoPrice = promoPrice;
        MiniOrder = miniOrder;
        this.expiredate = expiredate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getP_Description() {
        return p_Description;
    }

    public void setP_Description(String p_Description) {
        this.p_Description = p_Description;
    }

    public String getPromoPrice() {
        return promoPrice;
    }

    public void setPromoPrice(String promoPrice) {
        this.promoPrice = promoPrice;
    }

    public String getMiniOrder() {
        return MiniOrder;
    }

    public void setMiniOrder(String miniOrder) {
        MiniOrder = miniOrder;
    }

    public String getExpiredate() {
        return expiredate;
    }

    public void setExpiredate(String expiredate) {
        this.expiredate = expiredate;
    }
}
