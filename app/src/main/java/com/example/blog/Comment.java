package com.example.blog;

public class Comment {

    private String name,photo,comment;

    public Comment(){

    }

    public Comment(String name, String photo, String comment) {
        this.name = name;
        this.photo = photo;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
