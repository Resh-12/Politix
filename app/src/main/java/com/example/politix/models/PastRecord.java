package com.example.politix.models;

public class PastRecord {
    String name,position,img;
    Long year;

    public PastRecord(String name, String position, Long year,String img) {
        this.name = name;
        this.position = position;
        this.year = year;
        this.img=img;
    }

    public PastRecord() {
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
