package com.kkenterprise.domain.beans;

public class TandPublicKeyBean {
    private String publickey;
    private String time;

    public TandPublicKeyBean(String publickey, String time) {
        this.publickey = publickey;
        this.time = time;
    }

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    public String toString() {
        return "TandPublicKeyBean{" +
                "publickey='" + publickey + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
