package com.micro.truck.truck.Models;

import java.io.Serializable;

public class Truck implements Serializable {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String driver_name;
    private boolean expanded;

    public Truck(int id, String driver_name, String plate_num, String location,String desc, int capacity, String model, int driver_phone, int company_phone, int status, int supplier_id, int price_km, int price_h, Double lat, Double lng, int rating, String image, Double distances, String expire_date, String licence_date,String supplier_name) {
        this.id = id;
        this.driver_name = driver_name;
        this.plate_num = plate_num;
        this.location = location;
        this.desc = desc;
        this.capacity = capacity;
        this.model = model;
        this.driver_phone = driver_phone;
        this.company_phone = company_phone;
        this.status = status;
        this.supplier_id = supplier_id;
        this.price_km = price_km;
        this.price_h = price_h;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.image = image;
        this.distances = distances;
        this.expire_date = expire_date;
        this.licence_date = licence_date;
        this.supplier_name = supplier_name;
    }

    public Truck(int id, String driver_name,String desc,int price_km, String image, Double distances) {
        this.id = id;
        this.driver_name = driver_name;
        this.desc = desc;
        this.image = image;
        this.distances = distances;
        this.price_km = price_km;
    }

    private  String plate_num;

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getPlate_num() {
        return plate_num;
    }

    public void setPlate_num(String plate_num) {
        this.plate_num = plate_num;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public int getDriver_phone() {
        return driver_phone;
    }

    public void setDriver_phone(int driver_phone) {
        this.driver_phone = driver_phone;
    }

    public int getCompany_phone() {
        return company_phone;
    }

    public void setCompany_phone(int company_phone) {
        this.company_phone = company_phone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public int getPrice_km() {
        return price_km;
    }

    public void setPrice_km(int price_km) {
        this.price_km = price_km;
    }

    public int getPrice_h() {
        return price_h;
    }

    public void setPrice_h(int price_h) {
        this.price_h = price_h;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getDistances() {
        double new_distances = distances/1000;
        return Math.round(new_distances * 100.0) / 100.0 ;
    }

    public void setDistances(Double distances) {
        this.distances = distances;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    public String getLicence_date() {
        return licence_date;
    }

    public void setLicence_date(String licence_date) {
        this.licence_date = licence_date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Truck() {
    }
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    private  String location;
    private String desc;

    private  int capacity;
    private  String model;
    private  String supplier_name;
    private  int driver_phone;
    private  int company_phone;
    private  int status;
    private  int supplier_id;
    private  int price_km;
    private  int price_h;
    private  Double lat;
    private  Double lng;
    private  int rating;
    private  String image;
    private  Double distances;
    private  String expire_date;
    private  String licence_date;

}

