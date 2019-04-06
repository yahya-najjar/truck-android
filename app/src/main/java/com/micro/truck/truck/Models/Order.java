package com.micro.truck.truck.Models;
import java.io.Serializable;

public class Order implements Serializable  {

    private int id;
    private  Double lat;
    private  Double lng;
    private  int rating;
    private  int status;
    private  String location;
    private  String comment;
    private  int customer_id;
    private  int truck_id;
    private int driver_id;
    private String location_from;
    private String Location_to;
    private String location_current;
    private Double lat_from;
    private Double lng_from;
    private Double lat_to;
    private Double lng_to;
    private Double lat_current;
    private Double lng_current;
    private String current_driver;
    private String plate_num;
    private String desc;
    private Double truck_lat;
    private Double truck_lng;
    private int truck_status;
    private String image;
    private int order_status;
    private int price_km;
    private int price_h;
    private  Double distances;

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public String getLocation_from() {
        return location_from;
    }

    public void setLocation_from(String location_from) {
        this.location_from = location_from;
    }

    public String getLocation_to() {
        return Location_to;
    }

    public void setLocation_to(String location_to) {
        Location_to = location_to;
    }

    public String getLocation_current() {
        return location_current;
    }

    public void setLocation_current(String location_current) {
        this.location_current = location_current;
    }

    public Double getLat_from() {
        return lat_from;
    }

    public void setLat_from(Double lat_from) {
        this.lat_from = lat_from;
    }

    public Double getLng_from() {
        return lng_from;
    }

    public void setLng_from(Double lng_from) {
        this.lng_from = lng_from;
    }

    public Double getLat_to() {
        return lat_to;
    }

    public void setLat_to(Double lat_to) {
        this.lat_to = lat_to;
    }

    public Double getLng_to() {
        return lng_to;
    }

    public void setLng_to(Double lng_to) {
        this.lng_to = lng_to;
    }

    public Double getLat_current() {
        return lat_current;
    }

    public void setLat_current(Double lat_current) {
        this.lat_current = lat_current;
    }

    public Double getLng_current() {
        return lng_current;
    }

    public void setLng_current(Double lng_current) {
        this.lng_current = lng_current;
    }

    public String getCurrent_driver() {
        return current_driver;
    }

    public void setCurrent_driver(String current_driver) {
        this.current_driver = current_driver;
    }

    public String getPlate_num() {
        return plate_num;
    }

    public void setPlate_num(String plate_num) {
        this.plate_num = plate_num;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getTruck_lat() {
        return truck_lat;
    }

    public void setTruck_lat(Double truck_lat) {
        this.truck_lat = truck_lat;
    }

    public Double getTruck_lng() {
        return truck_lng;
    }

    public void setTruck_lng(Double truck_lng) {
        this.truck_lng = truck_lng;
    }

    public int getTruck_status() {
        return truck_status;
    }

    public void setTruck_status(int truck_status) {
        this.truck_status = truck_status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
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

    public Double getDistances() {
        double new_distances = distances/1000;
        return Math.round(new_distances * 100.0) / 100.0 ;
    }

    public void setDistances(Double distances) {
        this.distances = distances;
    }

    public Order(int id, Double lat, Double lng, int rating, int status, String location, String comment, int customer_id, int truck_id, int driver_id, String location_from, String location_to, String location_current, Double lat_from, Double lng_from, Double lat_to, Double lng_to, Double lat_current, Double lng_current, String current_driver, String plate_num, String desc, Double truck_lat, Double truck_lng, int truck_status, String image, int order_status, int price_km, int price_h, Double distances) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.status = status;
        this.location = location;
        this.comment = comment;
        this.customer_id = customer_id;
        this.truck_id = truck_id;
        this.driver_id = driver_id;
        this.location_from = location_from;
        Location_to = location_to;
        this.location_current = location_current;
        this.lat_from = lat_from;
        this.lng_from = lng_from;
        this.lat_to = lat_to;
        this.lng_to = lng_to;
        this.lat_current = lat_current;
        this.lng_current = lng_current;
        this.current_driver = current_driver;
        this.plate_num = plate_num;
        this.desc = desc;
        this.truck_lat = truck_lat;
        this.truck_lng = truck_lng;
        this.truck_status = truck_status;
        this.image = image;
        this.order_status = order_status;
        this.price_km = price_km;
        this.price_h = price_h;
        this.distances = distances;
    }

    public Order(int id, Double lat, Double lng, int rating, int status, String location, String comment, int customer_id, int truck_id) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.status = status;
        this.location = location;
        this.comment = comment;
        this.customer_id = customer_id;
        this.truck_id = truck_id;
    }

    public Order(int id, Double lat, Double lng, int rating, int status, String location, String comment, int customer_id, int truck_id,int driver_id,String location_from) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.status = status;
        this.location = location;
        this.comment = comment;
        this.customer_id = customer_id;
        this.truck_id = truck_id;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getTruck_id() {
        return truck_id;
    }

    public void setTruck_id(int truck_id) {
        this.truck_id = truck_id;
    }

    public Order() {

    }


}
