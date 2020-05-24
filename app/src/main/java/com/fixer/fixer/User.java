package com.fixer.fixer;

public class User {
    private String Name;
    private String Photo;


    public User() {
    }

    public User(String Name, String Photo) {
        this.Name = Name;
        this.Photo = Photo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }
}
