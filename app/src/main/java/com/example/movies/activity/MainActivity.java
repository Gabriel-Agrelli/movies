package com.example.movies.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.movies.BuildConfig;
import com.example.movies.R;
import com.example.movies.adapter.MovieAdapter;
import com.example.movies.domain.Movie;
import com.example.movies.domain.Api;
import com.example.movies.domain.ApiService;
import com.example.movies.utils.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnClickHandler {
    private Api api;
    private ArrayList<Movie> movies = new ArrayList<>();
    private MovieAdapter mMovieAdapter;

    @BindView(R.id.txt_error_fetch_data)
    TextView mTxtNetworkError;
    @BindView(R.id.rv_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeToRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static final String MOVIES = "movies";
    public static final String MOVIE = "movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSwipeRefreshLayout.setOnRefreshListener(() -> listMovies());

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES)) {
            movies = savedInstanceState.getParcelableArrayList(MOVIES);
            setMoviesAdapter();
        } else {
            listMovies();
        }
    }

    private void listMovies() {
        if (NetworkUtils.isNetworkConnected(this)) {
            Retrofit retrofit = ApiService.getInstance();
            api = retrofit.create(Api.class);

            api.listMovies(BuildConfig.api_key)
                    .flatMap(movieResult -> Observable.fromIterable(movieResult.getResult()))
                    .flatMap(movie -> Observable.just(new Movie(movie.getId(), movie.getTitle(), movie.getPoster(), movie.getReleaseDate(), movie.getOverview(), movie.getVoteAverage())))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Movie>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mTxtNetworkError.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mSwipeRefreshLayout.setRefreshing(true);
                        }

                        @Override
                        public void onNext(Movie movie) {
                            movies.add(movie);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onComplete() {
                            setMoviesAdapter();
                        }
                    });
        } else {
            showNetworkError();
        }
    }

    private void setMoviesAdapter() {
        mMovieAdapter = new MovieAdapter(movies, MainActivity.this);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMovieAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showNetworkError() {
        mSwipeRefreshLayout.setRefreshing(false);
        mTxtNetworkError.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES, movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void OnClickHandler(Movie movie) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(MOVIE, movie);
        startActivity(intent);
    }
}
