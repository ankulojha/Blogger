package com.developinggeek.blogapp;

/**
 * Created by DELL-PC on 9/14/2017.
 */

public class Favorites
{

    private String content;
    private String title;
    private String userName;
    private String image;

    public Favorites() {}

    public Favorites(String content, String title, String userName, String image) {
        this.content = content;
        this.title = title;
        this.userName = userName;
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
