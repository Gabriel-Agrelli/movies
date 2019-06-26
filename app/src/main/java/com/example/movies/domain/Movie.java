package com.example.movies.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {
    private String id;
    private String title;
    @SerializedName("poster_path")
    private String poster;
    @SerializedName("release_date")
    private String releaseDate;
    private String overview;
    @SerializedName("vote_average")
    private String voteAverage;

    public Movie(String id, String title, String poster, String releaseDate, String overview, String voteAverage) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.voteAverage = voteAverage;
    }

    protected Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        poster = in.readString();
        releaseDate = in.readString();
        overview = in.readString();
        voteAverage = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(releaseDate);
        dest.writeString(overview);
        dest.writeString(voteAverage);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }
}
