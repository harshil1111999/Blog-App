package com.example.blog;


class User {

    private String name;
    private String number;
    private String gender;
    private String age;
    private String photo;

    public User(){

    }

    public User(String name, String number,String gender,String age,String photo) {
        this.name = name;
        this.number = number;
        this.gender = gender;
        this.age = age;
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
