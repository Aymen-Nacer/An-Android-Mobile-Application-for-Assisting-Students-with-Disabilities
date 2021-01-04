package com.example.project;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String email;
    private String phoneN;
    private int numOfEvent;
    private String University_ID;
    private String Type_of_Disability;
    private String Degree_of_disability;
    private int age;
    private String gender;

    public User(){}
    public User(String name,String email, String phoneN,int numOfEvent ,String university_ID, String type_of_Disability, String degree_of_disability,int age,String gender) {
        this.name = name;
        this.email = email;
        this.phoneN = phoneN;
        this.numOfEvent=numOfEvent;
        University_ID = university_ID;
        Type_of_Disability = type_of_Disability;
        Degree_of_disability = degree_of_disability;
        this.age = age;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneN() {
        return phoneN;
    }

    public int getNumOfEvent() {
        return numOfEvent;
    }

    public int getAge() {
        return age;
    }

    public void setNumOfEvent(int numOfEvent) {
        this.numOfEvent = numOfEvent;
    }

    public String getUniversity_ID() {
        return University_ID;
    }

    public String getType_of_Disability() {
        return Type_of_Disability;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneN(String phoneN) {
        this.phoneN = phoneN;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setUniversity_ID(String university_ID) {
        University_ID = university_ID;
    }

    public void setType_of_Disability(String type_of_Disability) {
        Type_of_Disability = type_of_Disability;
    }

    public void setDegree_of_disability(String degree_of_disability) {
        Degree_of_disability = degree_of_disability;
    }

    public String getDegree_of_disability() {
        return Degree_of_disability;
    }
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}