package com.kkenterprise.domain.beans;

public class Productsbean {
    private String name ;
    private String price;
    private Integer id;
    private String describeInfo;

    @Override
    public String toString() {
        return "Productsbean{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", id=" + id +
                ", describeInfo='" + describeInfo + '\'' +
                '}';
    }

    public String getDescribeInfo() {
        return describeInfo;
    }

    public void setDescribeInfo(String describeInfo) {
        this.describeInfo = describeInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
