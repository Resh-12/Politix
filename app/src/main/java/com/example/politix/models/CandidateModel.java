package com.example.politix.models;

public class CandidateModel {
    private String name;
    private String position;
    private String email;
    private String regno;
    private String imageUrl;
    private boolean approved;

    public CandidateModel(String name, String position, String email, String imageUrl, String registrationNumber) {
        this.name = name;
        this.position = position;
        this.email = email;
        this.imageUrl = imageUrl;
        this.regno = registrationNumber;
        this.approved = false;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getEmail() {
        return email;
    }

    public String getRegno() {
        return regno;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isApproved() {
        return approved;
    }

    public void approve() {
        this.approved = true;
    }
}