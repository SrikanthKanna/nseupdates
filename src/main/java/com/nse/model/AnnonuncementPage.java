package com.nse.model;

/**
 * Created by srikanth on 17/10/17.
 */
public class AnnonuncementPage {

    private  String  zipUrl;
    private String  page;

    public String getZipUrl() {
        return zipUrl;
    }

    public void setZipUrl(String zipUrl) {
        this.zipUrl = zipUrl;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "AnnonuncementPage{" +
                "zipUrl='" + zipUrl + '\'' +
                ", page='" + page + '\'' +
                '}';
    }
}
