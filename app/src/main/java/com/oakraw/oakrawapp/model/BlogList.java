package com.oakraw.oakrawapp.model;

/**
 * Created by Rawipol on 4/24/15 AD.
 */
public class BlogList {
    public int id;
    public String title;
    public String date;
    public String feature_img;
    public String categories;
    public int page_view;

    public BlogList(int id, String title, String date, String feature_img, String categories, int page_view) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.feature_img = feature_img;
        this.categories = categories;
        this.page_view = page_view;
    }
}
