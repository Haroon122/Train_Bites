package com.example.trainbites;

public class Chef {
    private String username;
    private String email;
    private String phone;
    private String password;

    public Chef() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Chef(String username, String email, String phone) {
        this.username = username;
        this.email = email;
        this.phone = phone;
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
