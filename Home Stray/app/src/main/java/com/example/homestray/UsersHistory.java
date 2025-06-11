package com.example.homestray;

public class UsersHistory {

    String firstName, lastName, phone, line, salary, consent, address, freeTime;
    private Double lat, lng;

    public UsersHistory(){

    }

    public UsersHistory(String firstName, String lastName, String phone, String line, String salary, String freeTime, String consent, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.line = line;
        this.salary = salary;
        this.freeTime = freeTime;
        this.consent = consent;
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getLine() {
        return line;
    }
    public void setLine(String line) {
        this.line = line;
    }
    public String getSalary() {
        return salary;
    }
    public void setSalary(String salary) {
        this.salary = salary;
    }
    public String getConsent() {
        return consent;
    }
    public void setConsent(String consent) {
        this.consent = consent;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Double getLng() {return lng;}
    public void setLng(Double lng) {this.lng = lng;}
    public Double getLat() {return lat;}
    public void setLat(Double lat) {this.lat = lat;}
    public void setFreeTime(String freeTime) {this.freeTime = freeTime;}
    public String getFreeTime() {return freeTime;}
}
