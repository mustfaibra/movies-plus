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

import com.curiousdev.moviesdiscover.Models.SeriesItem;
import com.curiousdev.moviesdiscover.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.QUALITY;

public class SeriesRecyclerAdapter extends RecyclerView.Adapter<SeriesRecyclerAdapter.SeriesItemHolder> {
    //base url for all img path
    private String BASE_URL_IMG = "https://image.tmdb.org/t/p/";

    //current quality
    private String quality=QUALITY;
    //adapter variables
    private Context context;
    private List<SeriesItem> series;
    private static final String TAG = "SeriesRecycler";
    //an object for interface
    private OnCardClickListener onCardClickListener;
    //inflater variable
    private LayoutInflater inflater;

    //an interface to catch user's click on the recycler's card
    public interface OnCardClickListener{
        void onTvShowClick(SeriesItem tvShow);
    }

    public void setOnCardClickListener(OnCardClickListener listener){
        onCardClickListener=listener;
    }

    //our dear constuctor :)
    public SeriesRecyclerAdapter(Context context) {
        Paper.init(context);
        this.context = context;
        this.series =new ArrayList<>();
        inflater=LayoutInflater.from(context);
        BASE_URL_IMG = BASE_URL_IMG.concat(quality);
    }

    //setter and getter methods to get and set serieslist as pagintaion index change
    public List<SeriesItem> getSeriesItems() {
        return series;
    }

    public void setSeriesItems(List<SeriesItem> series) {
        this.series = series;
    }

    @NonNull
    @Override
    public SeriesItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.home_rec_item,parent,false);
        return new SeriesItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesItemHolder holder, int position) {
        //first we get the movie on the position with index 'position'
        SeriesItem tvshow=series.get(position);
        //then we use the SeriesItem object that we created above to give our dear placeHolder its widgets's values
        //first we begin with the cover using picasso library
        String posterLink=BASE_URL_IMG+tvshow.getPosterPath();
        if (!quality.equalsIgnoreCase("none")){
            Picasso.with(context).load(posterLink).into(holder.cover);
        }
        else {
            Picasso.with(context).load(R.drawable.image_turned_off_simple).into(holder.cover);
        }
        Log.d(TAG, "onBindViewHolder: cover done its :"+posterLink);

        String name=tvshow.getName();
        if (TextUtils.isEmpty(name)){
            name=tvshow.getOriginalName();
        }
        holder.tvshowName.setText(name);//we done with the name
        Log.d(TAG, "onBindViewHolder: title done :"+name);

        holder.tvshowRate.setText(String.valueOf(tvshow.getVoteAverage()));//we are done with the rate

    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    //this is a helper methods for adapter to add new tvshow smoothly
    public void add(SeriesItem tvshow){
        series.add(tvshow);
        notifyItemInserted(series.size()-1);
    }
    public void addAll(List<SeriesItem> series){
        Log.d(TAG, "addAll: "+series.size());
        for (SeriesItem tvshow:series) {
            add(tvshow);
        }
    }


    public void remove(SeriesItem tvshow){
        series.remove(tvshow);
        notifyItemRemoved(series.indexOf(tvshow));
    }
    public void clear(){
        notifyDataSetChanged();
        if (!isEmpty()){
            while (getItemCount()>0){
                remove(getSeriesItem(0));
            }
        }
    }
    public boolean isEmpty(){
        return getItemCount()==0;
    }
    private SeriesItem getSeriesItem(int position){
        return series.get(position);
    }
    class SeriesItemHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        TextView tvshowName,tvshowRate;

        SeriesItemHolder(final View itemView) {
            super(itemView);
            cover=itemView.findViewById(R.id.movie_thumbnail);
            tvshowName=itemView.findViewById(R.id.movie_name);
            tvshowRate=itemView.findViewById(R.id.movie_rate);

            itemView.setOnClickListener(v -> {
                if (onCardClickListener!=null){
                    onCardClickListener.onTvShowClick(series.get(getAdapterPosition()));
                }
            });
        }

    }
}
