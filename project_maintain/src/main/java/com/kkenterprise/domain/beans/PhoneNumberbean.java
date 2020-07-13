package com.kkenterprise.domain.beans;

public class PhoneNumberbean {

    private String phoneNum;


    @Override
    public String toString() {
        return "PhoneNumberbean{" +
                "phoneNum='" + phoneNum + '\'' +
                '}';
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
