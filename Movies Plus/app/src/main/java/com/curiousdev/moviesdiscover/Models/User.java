package com.curiousdev.moviesdiscover.Models;

public class User {
    private String name=null;
    private String password=null;
    private String email=null;
    private String img=null;

    public User(){

    }

     public User(String uname, String uemail, String upassword ,String uimg) {
        this.name = uname;
        this.password = upassword;
        this.email = uemail;
        this.img = uimg;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

     public String getPassword() {
        return password;
    }

     public String getEmail() {
        return email;
    }

     public String getImg() {
        return img;
    }
}
