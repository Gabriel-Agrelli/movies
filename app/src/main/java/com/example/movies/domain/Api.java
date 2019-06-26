package com.example.movies.domain;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    @GET("movie/popular?api_key=3459ec896c901e452a28dac87bf98b34")
    Observable<MovieResult> listMovies(@Query("api_key") String key);
}
