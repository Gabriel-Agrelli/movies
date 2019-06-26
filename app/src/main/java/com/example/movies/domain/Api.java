package com.example.movies.domain;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    @GET("movie/popular")
    Observable<MovieResult> listMovies(@Query("page") int page, @Query("api_key") String key);
}
