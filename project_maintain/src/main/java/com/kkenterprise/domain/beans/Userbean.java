package com.kkenterprise.domain.beans;

public class Userbean {
    private int userId;
    private String phone;
    private String username;
    private boolean authentication;
    public Userbean(String phone ,String username)
    {
        this.phone = phone;
        this.username = username;
    }

    public Userbean() {
    }

    public boolean isAuthentication() {
        return authentication;
    }

    public void setAuthentication(boolean authentication) {
        this.authentication = authentication;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Userbean{" +
                "userId=" + userId +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", authentication=" + authentication +
                '}';
    }
}
