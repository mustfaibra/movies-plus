package com.curiousdev.moviesdiscover.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curiousdev.moviesdiscover.Models.SavedItem;
import com.curiousdev.moviesdiscover.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.QUALITY;

public class SavedMoviesAdapter extends RecyclerView.Adapter<SavedMoviesAdapter.MovieHolder> {
    //base url for all img path
    private String BASE_URL_IMG = "https://image.tmdb.org/t/p/w500";
    //adapter variables
    private Context context;
    private List<SavedItem> movies;
    private static final String TAG = "SavedMovieAdapter";
    //an object for interface
    private OnCardClickListener onCardClickListener;
    //inflater variable
    private LayoutInflater inflater;

    //an interface to catch user's click on the recycler's card
    public interface OnCardClickListener{
        void onCardClick(SavedItem movie);
    }
    public void setOnCardClickListener(OnCardClickListener listener){
        onCardClickListener=listener;
    }

    //our dear constuctor :)
    public SavedMoviesAdapter(Context context) {
        Log.d(TAG, "SavedListRecycler:constructing");
        this.context = context;
        this.movies =new ArrayList<>();
        inflater=LayoutInflater.from(context);
//        Paper.init(context);
//        quality=Paper.book().read("quality","original");
//        BASE_URL_IMG=BASE_URL_IMG.concat(quality);


    }
    //setter and getter methods to get and set movieslist as pagintaion index change


    public List<SavedItem> getMovies() {
        return movies;
    }

    public void setMovies(List<SavedItem> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: on create");

        //we gonna check if its LOADING_INDICATOR items indicator or an actual item
        Log.d(TAG, "onCreateViewHolder: its an "+viewType);
        View view=inflater.inflate(R.layout.saved_movie_rec_item,parent,false);
        return new MovieHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: start");
//        if (!quality.equalsIgnoreCase(Paper.book().read("quality"))){
//            BASE_URL_IMG=BASE_URL_IMG.replace(quality,Paper.book().read("quality"));
//            quality =Paper.book().read("quality");
//        }
        //first we get the movie on the position with index 'position'
        SavedItem movie=movies.get(position);
        //then we use the SavedItem object that we created above to give our dear placeHolder its widgets's values
        //first we begin with the cover using picasso library

        String posterLink=BASE_URL_IMG+movie.getMoviePoster();
//        if (!quality.equalsIgnoreCase("none")){
//        }
//        else {
//            Picasso.with(context).load(R.drawable.image_turned_off_simple).into(holder.cover);
//        }
        Picasso.with(context).load(posterLink).into(holder.cover);

        Log.d(TAG, "onBindViewHolder: cover done its :"+posterLink);

        holder.movieName.setText(movie.getMovieName());//we done with the name
        Log.d(TAG, "onBindViewHolder: title done");

        holder.movieRate.setText(""+movie.getMovieRate());//we are done with the rate
        Log.d(TAG, "onBindViewHolder: rate done");

        holder.movieOverview.setText(movie.getMovieOverview());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    //this is a helper methods for adapter to add new movie smoothly
    public void add(SavedItem movie){
        movies.add(movie);
        Log.d(TAG, "add: ");
        notifyItemInserted(movies.size()-1);
    }
    public void addAll(List<SavedItem> movies){
        Log.d(TAG, "addAll: "+movies.size());
        for (SavedItem movie:movies) {
            add(movie);
        }
    }

    public void remove(SavedItem movie){
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
    public SavedItem getMovie(int position){
        return movies.get(position);
    }


    public class MovieHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        TextView movieName,movieRate,movieOverview;

        public MovieHolder(final View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: viewholder constructor ");
            cover=itemView.findViewById(R.id.saved_movie_img);
            movieName=itemView.findViewById(R.id.saved_movie_name);
            movieRate=itemView.findViewById(R.id.saved_movie_rate);
            movieOverview=itemView.findViewById(R.id.saved_movie_overview);

            //click listener interface
            Log.d(TAG, "ViewHolder: now to listener");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: item clicked");
                    if (onCardClickListener!=null){
                        onCardClickListener.onCardClick(movies.get(getAdapterPosition()));
                    }
                }
            });
        }

    }

}

