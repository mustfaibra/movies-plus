package com.curiousdev.moviesdiscover.Adapters;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curiousdev.moviesdiscover.Models.Movie;
import com.curiousdev.moviesdiscover.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.QUALITY;

public class MoviesRecyclerAdapter extends RecyclerView.Adapter<MoviesRecyclerAdapter.MovieHolder> {
    //base url for all img path
    private String BASE_URL_IMG = "https://image.tmdb.org/t/p/";

    //current quality
    private String quality=QUALITY;

    //adapter variables
    private Context context;
    private List<Movie> movies;
    private static final String TAG = "MoviesRecycler";
    //an object for interface
    private OnCardClickListener onCardClickListener;
    //inflater variable
    private LayoutInflater inflater;

    //an interface to catch user's click on the recycler's card
    public interface OnCardClickListener{
        void onMovieClick(Movie movie);
    }

    public void setOnCardClickListener(OnCardClickListener listener){
        onCardClickListener=listener;
    }

    //our dear constuctor :)
    public MoviesRecyclerAdapter(Context context) {
        Paper.init(context);
        this.context = context;
        this.movies =new ArrayList<>();
        inflater=LayoutInflater.from(context);
        BASE_URL_IMG = BASE_URL_IMG.concat(quality);
    }

    //setter and getter methods to get and set movieslist as pagintaion index change
    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.home_rec_item,parent,false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        //first we get the movie on the position with index 'position'
        Movie movie=movies.get(position);
        //then we use the Movie object that we created above to give our dear placeHolder its widgets's values
        //first we begin with the cover using picasso library
        String posterLink=BASE_URL_IMG+movie.getPosterPath();
        if (!quality.equalsIgnoreCase("none")){
            Picasso.with(context).load(posterLink).into(holder.cover);
        }
        else {
            Picasso.with(context).load(R.drawable.image_turned_off_simple).into(holder.cover);
        }
        Log.d(TAG, "onBindViewHolder: cover done its :"+posterLink);

        String name=movie.getTitle();
        if (TextUtils.isEmpty(name)){
            name=movie.getName();
        }
        holder.movieName.setText(name);//we done with the name
        Log.d(TAG, "onBindViewHolder: title done :"+name);

        holder.movieRate.setText(String.valueOf(movie.getVoteAverage()));//we are done with the rate

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    //this is a helper methods for adapter to add new movie smoothly
    public void add(Movie movie){
        movies.add(movie);
        notifyItemInserted(movies.size()-1);
    }
    public void addAll(List<Movie> movies){
        Log.d(TAG, "addAll: "+movies.size());
        for (Movie movie:movies) {
            add(movie);
        }
    }


    public void remove(Movie movie){
        movies.remove(movie);
        notifyItemRemoved(movies.indexOf(movie));
    }
    public void clear(){
        notifyDataSetChanged();
        if (!isEmpty()){
            while (getItemCount()>0){
                remove(getMovie(0));
            }
        }
    }
    public boolean isEmpty(){
        return getItemCount()==0;
    }
    private Movie getMovie(int position){
        return movies.get(position);
    }
    class MovieHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        TextView movieName,movieRate;

        MovieHolder(final View itemView) {
            super(itemView);
            cover=itemView.findViewById(R.id.movie_thumbnail);
            movieName=itemView.findViewById(R.id.movie_name);
            movieRate=itemView.findViewById(R.id.movie_rate);

            itemView.setOnClickListener(v -> {
                if (onCardClickListener!=null){
                    onCardClickListener.onMovieClick(movies.get(getAdapterPosition()));
                }
            });
        }

    }
}
