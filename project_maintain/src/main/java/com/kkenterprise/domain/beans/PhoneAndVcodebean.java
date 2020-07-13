package com.kkenterprise.domain.beans;

public class PhoneAndVcodebean {
    private String phoneNum;
    private String Vcode;
    private String AESkey;
    private String iv;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getVcode() {
        return Vcode;
    }

    public void setVcode(String vcode) {
        Vcode = vcode;
    }

    public String getAESkey() {
        return AESkey;
    }

    public void setAESkey(String AESkey) {
        this.AESkey = AESkey;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}
