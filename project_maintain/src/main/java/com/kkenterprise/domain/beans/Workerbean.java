package com.kkenterprise.domain.beans;

public class Workerbean {
    private int workerId;
    private String phone;
    private String workername;
    private boolean authentication;

    public boolean isAuthentication() {
        return authentication;
    }

    public void setAuthentication(boolean authentication) {
        this.authentication = authentication;
    }

    public String getPhone() {
        return phone;
    }


    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWorkername() {
        return workername;
    }

    public void setWorkername(String workername) {
        this.workername = workername;
    }

    @Override
    public String toString() {
        return "Workerbean{" +
                "workerId=" + workerId +
                ", phone='" + phone + '\'' +
                ", workername='" + workername + '\'' +
                ", authentication=" + authentication +
                '}';
    }
}
