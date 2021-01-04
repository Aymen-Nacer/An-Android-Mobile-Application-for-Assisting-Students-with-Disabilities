package com.example.project;

public class PlaceDetail {
    public String name;
    public String type;
    public String fullDistance;

    public PlaceDetail(String name, String type, String distance) {
        this.name = name;
        this.type = type;
        fullDistance = distance;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getFullDistance() {
        return fullDistance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFullDistance(String distance) {
        fullDistance = distance;
    }
}
