package com.kkenterprise.domain.beans;

public class Authenticationbean {
    private String name;
    private String Idcard;
    private String phone;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdcard() {
        return Idcard;
    }

    public void setIdcard(String idcard) {
        Idcard = idcard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Authenticationbean{" +
                "name='" + name + '\'' +
                ", Idcard='" + Idcard + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
