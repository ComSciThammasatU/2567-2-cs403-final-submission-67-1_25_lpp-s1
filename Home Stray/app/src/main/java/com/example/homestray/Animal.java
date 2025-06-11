package com.example.homestray;

public class Animal {
    private String id, name, breed, color, habits, medical, sickness, foundation, type, contact, history, address, imgURL, desc;
    private int age;
    private Double lat, lng;

    public Animal() {
    }

    public Animal(String id, String name, String imgURL, int age, String breed, String type, String color, String habits) {
        this.id = id;
        this.name = name;
        this.imgURL = imgURL;
        this.age = age;
        this.breed = breed;
        this.type = type;
        this.color = color;
        this.habits = habits;
    }

    public Animal(String id, String name, int age, String breed, String color, String habits, String medical
            , String sickness, String foundation, String type, String contact, String history, String address) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.color = color;
        this.habits = habits;
        this.medical = medical;
        this.sickness = sickness;
        this.foundation = foundation;
        this.type = type;
        this.contact = contact;
        this.history = history;
        this.address = address;
        this.age = age;
    }

    // Setter methods
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setBreed(String breed) { this.breed = breed; }
    public void setColor(String color) { this.color = color; }
    public void setHabits(String habits) { this.habits = habits; }
    public void setMedical(String medical) { this.medical = medical; }
    public void setSickness(String sickness) { this.sickness = sickness; }
    public void setFoundation(String foundation) { this.foundation = foundation; }
    public void setType(String type) { this.type = type; }
    public void setContact(String contact) { this.contact = contact; }
    public void setHistory(String history) { this.history = history; }
    public void setAddress(String address) { this.address = address; }
    public void setImgURL(String imgURL) { this.imgURL = imgURL; }
    public void setLat(Double lat) { this.lat = lat; }
    public void setLng(Double lng) { this.lng = lng; }

    // Getter methods
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getBreed() { return breed; }
    public String getColor() { return color; }
    public String getHabits() { return habits; }
    public String getMedical() { return medical; }
    public String getSickness() { return sickness; }
    public String getFoundation() { return foundation; }
    public String getType() { return type; }
    public String getContact() { return contact; }
    public String getHistory() { return history; }
    public String getAddress() { return address; }
    public String getImgURL() { return imgURL; }
    public Double getLat() { return lat; }
    public Double getLng() { return lng; }
}
