package com.journal.nn.school123.pojo;

import java.io.Serializable;
import java.util.Objects;

public class School implements Serializable {
    private String id;
    private String name;
    private String city;
    private String address;
    private boolean defaultValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        School school = (School) o;
        return defaultValue == school.defaultValue &&
                Objects.equals(id, school.id) &&
                Objects.equals(name, school.name) &&
                Objects.equals(city, school.city) &&
                Objects.equals(address, school.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, city, address, defaultValue);
    }
}
