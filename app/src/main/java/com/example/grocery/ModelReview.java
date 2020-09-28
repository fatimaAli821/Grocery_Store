package com.example.grocery;

public class ModelReview {
    //use same spelling of variable as use in sending data to firBase

    private String uid,review,rating,timestamp;

    public ModelReview() {
    }

    public ModelReview(String uid, String review, String rating, String timestamp) {
        this.uid = uid;
        this.review = review;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
