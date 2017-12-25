package com.developinggeek.blogapp;

/**
 * Created by DELL-PC on 7/28/2017.
 */

public class Blog
{

    private String title , desc , image , username , content ,userimage;

    public Blog() {}

    public Blog(String title, String desc, String image , String username ,String content ,String userimage) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.username = username;
        this.content = content;
        this.userimage = userimage;
    }

    public String getUserImage() {
        return userimage;
    }

    public void setUserImage(String userimage) {
        this.userimage = userimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
