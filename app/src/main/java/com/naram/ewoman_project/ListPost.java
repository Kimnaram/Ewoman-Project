package com.naram.ewoman_project;

import android.graphics.drawable.Drawable;
import android.media.Image;

public class ListPost {

    private int _id;
    private String title;
    private String name;
    private int like;

    public ListPost() {
        _id = 0;
        title = "";
        name = "";
        like = 0;
    }

    public ListPost(int _id, String title, String name, int like) {
        this._id = _id;
        this.title = title;
        this.name = name;
        this.like = like;
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

}