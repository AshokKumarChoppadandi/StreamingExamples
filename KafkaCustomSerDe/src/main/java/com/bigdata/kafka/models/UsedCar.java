package com.bigdata.kafka.models;

import java.io.Serializable;

/**
 * maker,model,mileage,manufacture_year,engine_displacement,engine_power,body_type,color_slug,stk_year,transmission,door_count,seat_count,fuel_type,date_created,date_last_seen,price_eur
 */

public class UsedCar implements Serializable {
    private String maker;
    private String model;
    private Integer mileage;
    private Integer manufacture_year;
    private String engine_displacement;
    private Integer engine_power;
    private String body_type;
    private String color_slug;
    private Integer stk_year;
    private String transmission;
    private String door_count;
    private Integer seat_count;
    private String fuel_type;
    private String date_created;
    private String date_last_seen;
    private Double price_eur;

    public UsedCar(String maker, String model, Integer mileage, Integer manufacture_year, String engine_displacement, Integer engine_power, String body_type, String color_slug, Integer stk_year, String transmission, String door_count, Integer seat_count, String fuel_type, String date_created, String date_last_seen, Double price_eur) {
        this.maker = maker;
        this.model = model;
        this.mileage = mileage;
        this.manufacture_year = manufacture_year;
        this.engine_displacement = engine_displacement;
        this.engine_power = engine_power;
        this.body_type = body_type;
        this.color_slug = color_slug;
        this.stk_year = stk_year;
        this.transmission = transmission;
        this.door_count = door_count;
        this.seat_count = seat_count;
        this.fuel_type = fuel_type;
        this.date_created = date_created;
        this.date_last_seen = date_last_seen;
        this.price_eur = price_eur;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public Integer getManufacture_year() {
        return manufacture_year;
    }

    public void setManufacture_year(Integer manufacture_year) {
        this.manufacture_year = manufacture_year;
    }

    public String getEngine_displacement() {
        return engine_displacement;
    }

    public void setEngine_displacement(String engine_displacement) {
        this.engine_displacement = engine_displacement;
    }

    public Integer getEngine_power() {
        return engine_power;
    }

    public void setEngine_power(Integer engine_power) {
        this.engine_power = engine_power;
    }

    public String getBody_type() {
        return body_type;
    }

    public void setBody_type(String body_type) {
        this.body_type = body_type;
    }

    public String getColor_slug() {
        return color_slug;
    }

    public void setColor_slug(String color_slug) {
        this.color_slug = color_slug;
    }

    public Integer getStk_year() {
        return stk_year;
    }

    public void setStk_year(Integer stk_year) {
        this.stk_year = stk_year;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getDoor_count() {
        return door_count;
    }

    public void setDoor_count(String door_count) {
        this.door_count = door_count;
    }

    public Integer getSeat_count() {
        return seat_count;
    }

    public void setSeat_count(Integer seat_count) {
        this.seat_count = seat_count;
    }

    public String getFuel_type() {
        return fuel_type;
    }

    public void setFuel_type(String fuel_type) {
        this.fuel_type = fuel_type;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDate_last_seen() {
        return date_last_seen;
    }

    public void setDate_last_seen(String date_last_seen) {
        this.date_last_seen = date_last_seen;
    }

    public Double getPrice_eur() {
        return price_eur;
    }

    public void setPrice_eur(Double price_eur) {
        this.price_eur = price_eur;
    }

    @Override
    public String toString() {
        return "UsedCar{" +
                "maker='" + maker + '\'' +
                ", model='" + model + '\'' +
                ", mileage=" + mileage +
                ", manufacture_year=" + manufacture_year +
                ", engine_displacement='" + engine_displacement + '\'' +
                ", engine_power=" + engine_power +
                ", body_type='" + body_type + '\'' +
                ", color_slug='" + color_slug + '\'' +
                ", stk_year=" + stk_year +
                ", transmission='" + transmission + '\'' +
                ", door_count='" + door_count + '\'' +
                ", seat_count=" + seat_count +
                ", fuel_type='" + fuel_type + '\'' +
                ", date_created='" + date_created + '\'' +
                ", date_last_seen='" + date_last_seen + '\'' +
                ", price_eur=" + price_eur +
                '}';
    }
}
