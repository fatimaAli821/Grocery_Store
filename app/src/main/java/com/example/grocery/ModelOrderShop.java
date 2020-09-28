package com.example.grocery;

public class ModelOrderShop {
    private String OrderId,OrderTime,OrderStatus,latitude,longitude,OrderCost,deliveryfee,OrderBy,orderTo;

    public ModelOrderShop() {
    }

    public ModelOrderShop(String orderId, String orderTime, String orderStatus, String latitude, String longitude, String orderCost, String deliveryfee, String orderBy, String orderTo) {
        OrderId = orderId;
        OrderTime = orderTime;
        OrderStatus = orderStatus;
        this.latitude = latitude;
        this.longitude = longitude;
        OrderCost = orderCost;
        this.deliveryfee = deliveryfee;
        OrderBy = orderBy;
        this.orderTo = orderTo;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(String orderTime) {
        OrderTime = orderTime;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOrderCost() {
        return OrderCost;
    }

    public void setOrderCost(String orderCost) {
        OrderCost = orderCost;
    }

    public String getDeliveryfee() {
        return deliveryfee;
    }

    public void setDeliveryfee(String deliveryfee) {
        this.deliveryfee = deliveryfee;
    }

    public String getOrderBy() {
        return OrderBy;
    }

    public void setOrderBy(String orderBy) {
        OrderBy = orderBy;
    }

    public String getOrderTo() {
        return orderTo;
    }

    public void setOrderTo(String orderTo) {
        this.orderTo = orderTo;
    }
}
