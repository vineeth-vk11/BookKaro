package com.example.bookkaro.helper;

public class ServicesData {
    private String name;
    private String image;

    public ServicesData() {
    }

    public ServicesData(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}