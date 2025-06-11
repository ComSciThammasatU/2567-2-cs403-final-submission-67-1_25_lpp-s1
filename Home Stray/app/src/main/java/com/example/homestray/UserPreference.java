package com.example.homestray;

import java.util.List;

public class UserPreference {
    public String type;
    public List<String> breedGroup;
    public List<String> ageGroup;
    public boolean crippled;
    public boolean neutered;
    public boolean sick;
    public int range;

    public UserPreference() {} // Default constructor required for Firebase

    public UserPreference(String type, List<String> breedGroup, List<String> ageGroup, boolean crippled, boolean neutered, boolean sick, int range) {
        this.type = type;
        this.breedGroup = breedGroup;
        this.ageGroup = ageGroup;
        this.crippled = crippled;
        this.neutered = neutered;
        this.sick = sick;
        this.range = range;
    }
}

