package com.vogella.mockito;

public class User {
    private String email;
    private String name;
    private boolean verified;

    public User(String email, String name) {
        this.email = email;
        this.name = name;
        this.verified = false;
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}