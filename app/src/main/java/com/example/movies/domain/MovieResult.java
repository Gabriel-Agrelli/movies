package com.example.movies.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MovieResult implements Serializable {
    private String page;
    @SerializedName("total_pages")
    private String totalPages;
    private List<Movie> results;

    public String getPage() {
        return page;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public List<Movie> getResult() {
        return results;
    }
}
