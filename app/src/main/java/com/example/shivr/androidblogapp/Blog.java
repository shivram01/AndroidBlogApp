package com.example.shivr.androidblogapp;

/**
 * Created by shivr on 5/30/2018.
 */

public class Blog {

    //the variable filed name should be exactly same as in the firebase database
    String Name, Description, image, username;

    public Blog() {
    }

    public Blog(String title, String descr, String image, String username) {
        this.Name = title;
        this.Description = descr;
        this.image = image;
        this.username = username;

    }

    public String getTitle() {
        return Name;
    }

    public void setTitle(String title) {
        this.Name = title;
    }

    public String getDescr() {
        return Description;
    }

    public void setDescr(String descr) {
        this.Description = descr;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
