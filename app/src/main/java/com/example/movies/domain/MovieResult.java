package com.example.movies.domain;

import java.io.Serializable;
import java.util.List;

public class MovieResult implements Serializable {
    private List<Movie> results;

    public List<Movie> getResult() {
        return results;
    }
}
