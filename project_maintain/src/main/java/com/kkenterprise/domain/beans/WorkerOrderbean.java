package com.kkenterprise.domain.beans;

import java.util.Date;

public class WorkerOrderbean {


    //订单号
    private String order_num;
    //订单状态
    private String order_status;
    //订单金额
    private String order_amount;
    //实际支付金额
    private String paid_amount;
    //产品ID
    private String product_id;
    //购买数量
    private int buy_counts;
    //订单创建时间
    private Date create_time;
    //交易结束时间
    private Date gmtClose;
    //用户id
    private Integer userId;
    //工人id
    private Integer workerId;
    //地址 经度
    private double longitude;
    //地址 纬度
    private double latitude;
    //详细地址
    private String address;
    //备注
    private String remarks;
    //图片
    private String picture;


    public String getOrder_num() {
        return order_num;
    }

    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(String order_amount) {
        this.order_amount = order_amount;
    }

    public String getPaid_amount() {
        return paid_amount;
    }

    public void setPaid_amount(String paid_amount) {
        this.paid_amount = paid_amount;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getBuy_counts() {
        return buy_counts;
    }

    public void setBuy_counts(int buy_counts) {
        this.buy_counts = buy_counts;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getGmtClose() {
        return gmtClose;
    }

    public void setGmtClose(Date gmtClose) {
        this.gmtClose = gmtClose;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
