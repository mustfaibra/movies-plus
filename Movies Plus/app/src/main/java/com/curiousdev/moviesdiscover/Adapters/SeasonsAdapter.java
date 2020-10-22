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

import com.curiousdev.moviesdiscover.Models.Season;
import com.curiousdev.moviesdiscover.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.QUALITY;

public class SeasonsAdapter extends RecyclerView.Adapter<SeasonsAdapter.SeasonViewHolder> {
    //base url for all img path
    private String BASE_URL_IMG = "https://image.tmdb.org/t/p/";

    //current quality
    private String quality=QUALITY;
    //adapter variables
    private Context context;
    private List<Season> seasons;
    private static final String TAG = "seasonsRecyclerAdapter";
    //an object for interface
    private OnCardClickListener onCardClickListener;
    //inflater variable
    private LayoutInflater inflater;

    //an interface to catch user's click on the recycler's card
    public interface OnCardClickListener{
        void onCardClick(int position);
    }

    public void setOnCardClickListener(OnCardClickListener listener){
        onCardClickListener=listener;
    }

    //our dear constuctor :)
    public SeasonsAdapter(Context context,List<Season> seasons) {
        Paper.init(context);
        this.context = context;
        this.seasons =seasons;
        inflater=LayoutInflater.from(context);
        BASE_URL_IMG = BASE_URL_IMG.concat(quality);
    }

    @NonNull
    @Override
    public SeasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.seasons_rec_item,parent,false);
        return new SeasonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonViewHolder holder, int position) {
        //first we get the season on the position with index 'position'
        Season season=seasons.get(position);
        //then we use the Season object that we created above to give our dear placeHolder its widgets's values
        //first we begin with the cover using picasso library
        String posterLink=BASE_URL_IMG+season.getPosterPath();
        if (!quality.equalsIgnoreCase("none")){
            Picasso.with(context).load(posterLink).into(holder.cover);
        }
        else {
            Picasso.with(context).load(R.drawable.image_turned_off_simple).into(holder.cover);
        }
        Log.d(TAG, "onBindViewHolder: cover done its :"+posterLink);

        String name=season.getName();
        if (TextUtils.isEmpty(name)){
            name=season.getName();
        }
        holder.tvshowName.setText(name);//we done with the name
        Log.d(TAG, "onBindViewHolder: title done :"+name);

    }

    @Override
    public int getItemCount() {
        return seasons.size();
    }

    //this is a helper methods for adapter to add new season smoothly
    public void add(Season season){
        seasons.add(season);
        notifyItemInserted(seasons.size()-1);
    }
    public void addAll(List<Season> seasons){
        Log.d(TAG, "addAll: "+seasons.size());
        for (Season season:seasons) {
            add(season);
        }
    }


    public void remove(Season season){
        seasons.remove(season);
        notifyItemRemoved(seasons.indexOf(season));
    }
    public void clear(){
        notifyDataSetChanged();
        if (!isEmpty()){
            while (getItemCount()>0){
                remove(getseasonsItem(0));
            }
        }
    }
    public boolean isEmpty(){
        return getItemCount()==0;
    }
    private Season getseasonsItem(int position){
        return seasons.get(position);
    }
    class SeasonViewHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        TextView tvshowName;

        SeasonViewHolder(final View itemView) {
            super(itemView);
            cover=itemView.findViewById(R.id.season_thumbnail);
            tvshowName=itemView.findViewById(R.id.season_name);

            itemView.setOnClickListener(v -> {
                if (onCardClickListener!=null){
                    onCardClickListener.onCardClick(getAdapterPosition());
                }
            });
        }

    }
}
