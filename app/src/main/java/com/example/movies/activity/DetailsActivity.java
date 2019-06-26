package com.example.movies.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movies.R;
import com.example.movies.domain.Movie;
import com.example.movies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {
    private static Movie movie;
    public static final String MOVIE = "movie";

    @BindView(R.id.img_poster_detail)
    ImageView mMoviePoster;
    @BindView(R.id.txt_overview)
    TextView mTxtOverview;
    @BindView(R.id.txt_release_date)
    TextView mTxtReleaseDate;
    @BindView(R.id.txt_vote_average)
    TextView mTxtVoteAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();

        if (data != null) {
            movie = data.getParcelable(MOVIE);
            getSupportActionBar().setTitle(movie.getTitle());
            loadDetails(movie);
        }
    }

    private void loadDetails(Movie movie) {
        Picasso.get().load(NetworkUtils.getPosterUrl(movie.getPoster())).into(mMoviePoster);
        mTxtReleaseDate.setText(movie.getReleaseDate());
        mTxtVoteAverage.setText(movie.getVoteAverage());
        mTxtOverview.setText(movie.getOverview());
    }
}
