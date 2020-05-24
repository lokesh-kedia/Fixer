package com.fixer.fixer;

public class UserEdit {
    public String Name;
    public String Username;
    public String Website;
    public String Bio;
    public String Email;
    public String Phone;
    public String Gender;


    public UserEdit(String Name, String Username, String Website, String Bio, String Email, String Phone, String Gender) {
        this.Name = Name;
        this.Username = Username;
        this.Website = Website;
        this.Bio = Bio;
        this.Email = Email;
        this.Phone = Phone;
        this.Gender = Gender;

    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String Website) {
        this.Website = Website;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String Bio) {
        this.Bio = Bio;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }


}
