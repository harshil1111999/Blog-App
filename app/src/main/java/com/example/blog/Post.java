package com.example.blog;

public class Post {

    Long time;
    private String title, photo, description, email,person_photo;

    public Post(){

    }

    public Post(String title, String description, String photo, Long time, String email,String person_photo) {
        this.title = title;
        this.description = description;
        this.photo = photo;
        this.time = time;
        this.email = email;
        this.person_photo = person_photo;
//        this.comments = comments;
    }

//    public String getComments() {
//        return comments;
//    }
//
//    public void setComments(String comments) {
//        this.comments = comments;
//    }


    public String getPerson_photo() {
        return person_photo;
    }

    public void setPerson_photo(String person_photo) {
        this.person_photo = person_photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
