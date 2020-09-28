package com.example.grocery;

public class ModelShop {
    String uuid,email,name,ShopeName,phone,delivery_fee,latitude,longitude,
            timestamp,country,State,city,ShopOpen,address,accountType,
            onlion,profileImage;

    public ModelShop() {
    }

    public ModelShop(String uuid, String email, String name, String shopeName, String phone, String delivery_fee, String latitude, String longitude, String timestamp, String country, String state, String city, String shopOpen, String address, String accountType, String onlion, String profileImage) {
        this.uuid = uuid;
        this.email = email;
        this.name = name;
        ShopeName = shopeName;
        this.phone = phone;
        this.delivery_fee = delivery_fee;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.country = country;
        State = state;
        this.city = city;
        ShopOpen = shopOpen;
        this.address = address;
        this.accountType = accountType;
        this.onlion = onlion;
        this.profileImage = profileImage;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopeName() {
        return ShopeName;
    }

    public void setShopeName(String shopeName) {
        ShopeName = shopeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(String delivery_fee) {
        this.delivery_fee = delivery_fee;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getShopOpen() {
        return ShopOpen;
    }

    public void setShopOpen(String shopOpen) {
        ShopOpen = shopOpen;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getOnlion() {
        return onlion;
    }

    public void setOnlion(String onlion) {
        this.onlion = onlion;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
