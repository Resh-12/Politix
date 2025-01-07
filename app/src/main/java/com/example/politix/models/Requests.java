package com.example.politix.models;

public class Requests {
    private String id; // Unique ID for the candidate
    private String name;
    private String email;
    private String regno;
    private String cgpa;
    private String position;
    private String image,candidateId;


    // Default constructor required for calls to DataSnapshot.getValue(Requests.class)
    public Requests() {
    }

    public Requests(String id, String name, String email, String regno, String cgpa, String position, String image,String candidateId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.regno = regno;
        this.cgpa = cgpa;
        this.position = position;
        this.image = image;
        this.candidateId=candidateId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    // Getter and Setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter for regno
    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    // Getter and Setter for cgpa
    public String getCgpa() {
        return cgpa;
    }

    public void setCgpa(String cgpa) {
        this.cgpa = cgpa;
    }

    // Getter and Setter for position
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    // Getter and Setter for image
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
