package com.android.nfc.system.viewmodel;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Searcher implements Serializable {
    private String city;
    private String keyword;

    public Searcher(String c, String k) {
        this.city = c;
        this.keyword = k;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCity() {
        return city;
    }

    public String getKeyword() {
        return keyword;
    }

    @NonNull
    @Override
    public String toString() {
        return "c=" + city + ", k=" + keyword;
    }
}
