package com.example.movies.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
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
    public static final String CURRENT_PAGE = "current_page";
    public static final String TOTAL_PAGES = "total_pages";

    private GridLayoutManager gridLayoutManager;
    private int current_page = 1;
    private int total_pages;
    private boolean isScrolling = false;
    private int currentItems, totalItems, scrollOutItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMovieAdapter = new MovieAdapter(MainActivity.this);
        gridLayoutManager = new GridLayoutManager(this, 2);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            gridLayoutManager.setSpanCount(3);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    if (current_page < total_pages) {
                        isScrolling = false;
                        loadMovies(current_page + 1);
                    }
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            movies.clear();
            mMovieAdapter.setMovies(null);
            loadMovies(1);
        });

        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList(MOVIES);
            current_page = savedInstanceState.getInt(CURRENT_PAGE);
            total_pages = savedInstanceState.getInt(TOTAL_PAGES);
            mMovieAdapter.setMovies(movies);
        } else {
            loadMovies(current_page);
        }
    }

    private void loadMovies(int page) {
        if (NetworkUtils.isNetworkConnected(this)) {
            Retrofit retrofit = ApiService.getInstance();
            api = retrofit.create(Api.class);

            api.listMovies(page, BuildConfig.api_key)
                    .flatMap(movieResult -> {
                        current_page = Integer.parseInt(movieResult.getPage());
                        total_pages = Integer.parseInt(movieResult.getTotalPages());
                        return Observable.fromIterable(movieResult.getResult());
                    })
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
                            mSwipeRefreshLayout.setRefreshing(false);
                            mMovieAdapter.setMovies(movies);
                        }
                    });
        } else {
            showNetworkError();
        }
    }

    private void showNetworkError() {
        mSwipeRefreshLayout.setRefreshing(false);
        mTxtNetworkError.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES, movies);
        outState.putInt(CURRENT_PAGE, current_page);
        outState.putInt(TOTAL_PAGES, total_pages);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void OnClickHandler(Movie movie) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(MOVIE, movie);
        startActivity(intent);
    }
}
