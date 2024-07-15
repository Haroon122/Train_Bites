package com.example.trainbites;

public class User {
    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    private String Uid;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String userImage;

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String Uid, String username, String email, String phone, String password, String userImage) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password=password;
        this.userImage=userImage;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}

