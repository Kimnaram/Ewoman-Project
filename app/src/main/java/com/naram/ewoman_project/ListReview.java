package com.naram.ewoman_project;

import android.graphics.drawable.Drawable;
import android.media.Image;

public class ListReview {

    private String title;
    private String userid;
    private String name;
    private int like;
    private String content;
    private Drawable image;

    public ListReview() {
        title = "";
        userid = "";
        name = "";
        like = 0;
        content = "";
        image = null;
    }

    public ListReview(String title, String userid, String name, int like, String content) {
        this.title = title;
        this.userid = userid;
        this.name = name;
        this.like = like;
        this.content = content;
    }

    public ListReview(String title, String userid, String name, int like, String content, Drawable image) {
        this.title = title;
        this.userid = userid;
        this.name = name;
        this.like = like;
        this.content = content;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

}
