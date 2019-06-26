package com.example.movies.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.domain.Movie;
import com.example.movies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MoviesViewHolder> {
    private List<Movie> mMoviesArray;
    private OnClickHandler mClickHandler;

    public MovieAdapter(OnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_movies, parent, false);

        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        holder.mTxtMovieTitle.setText(mMoviesArray.get(position).getTitle());
        Picasso.get().load(NetworkUtils.getPosterUrl(mMoviesArray.get(position).getPoster())).into(holder.mMoviePoster);

        holder.view.setOnClickListener(onClick ->
                mClickHandler.OnClickHandler(mMoviesArray.get(position)));
    }

    @Override
    public int getItemCount() {
        if (mMoviesArray == null) return 0;
        return mMoviesArray.size();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder {
        private View view;
        @BindView(R.id.txt_movie_name)
        TextView mTxtMovieTitle;
        @BindView(R.id.img_movie)
        ImageView mMoviePoster;

        public MoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }
    }

    public interface OnClickHandler {
        void OnClickHandler(Movie Movie);
    }

    public void setMovies(List<Movie> movies) {
        mMoviesArray = movies;
        notifyDataSetChanged();
    }
}
