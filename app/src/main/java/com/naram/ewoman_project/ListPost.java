package com.naram.ewoman_project;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;

public class ListPost {

    private int _id;
    private String category;
    private Bitmap thumbnail;
    private String title;
    private String name;
    private String content;
    private String detailurl;
    private int like;

    public ListPost() {
        _id = 0;
        title = "";
        name = "";
        like = 0;
    }

    public ListPost(Bitmap thumbnail, String title, String content, String detailurl) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.content = content;
        this.detailurl = detailurl;
    }

    public ListPost(int _id, String category, String title, String name, int like) {
        this._id = _id;
        this.category = category;
        this.title = title;
        this.name = name;
        this.like = like;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int get_id() {
        return _id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetailurl() {
        return detailurl;
    }

    public void setDetailurl(String detailurl) {
        this.detailurl = detailurl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}