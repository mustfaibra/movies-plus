package com.curiousdev.moviesdiscover.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.curiousdev.moviesdiscover.Activities.MovieDetailActivity;
import com.curiousdev.moviesdiscover.Activities.TvShowDetailActivity;
import com.curiousdev.moviesdiscover.Adapters.SavedMoviesAdapter;
import com.curiousdev.moviesdiscover.Models.SavedItem;
import com.curiousdev.moviesdiscover.R;
import com.curiousdev.moviesdiscover.ViewModels.SavedItemsViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.hostContext;

public class FavoriteFragment extends Fragment implements SavedMoviesAdapter.OnCardClickListener {
    private static final String TAG = "FavoriteFragment";
    private RecyclerView favRec;
    private SavedMoviesAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    //view model
    private SavedItemsViewModel savedItemsViewModel;
    private View view;

    //lottie
    private LottieAnimationView lottieLoading;
    //textview
    private TextView noSavedMoviesTxt;
    List<SavedItem> movies;
    private Context context=hostContext;
    public static FavoriteFragment favoriteFragmentInstance;
    public static FavoriteFragment getFavoriteFragmentInstance(){
        if(favoriteFragmentInstance==null){
            favoriteFragmentInstance=new FavoriteFragment();
        }
        return favoriteFragmentInstance;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedItemsViewModel = ViewModelProviders.of(this).get(SavedItemsViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view=getLayoutInflater().inflate(R.layout.fragment_favorite,container,false);
            favRec=view.findViewById(R.id.fav_rec);
            lottieLoading=view.findViewById(R.id.lottie_loading);
            noSavedMoviesTxt=view.findViewById(R.id.no_saved_fav);
            linearLayoutManager=new LinearLayoutManager(context.getApplicationContext(),LinearLayoutManager.VERTICAL,false);
            favRec.setLayoutManager(linearLayoutManager);
            adapter=new SavedMoviesAdapter(context);
            favRec.setAdapter(adapter);
            adapter.setOnCardClickListener(this);
            movies=new ArrayList<>();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        savedItemsViewModel.getSavedItems("favorite").observe(this, new Observer<List<SavedItem>>() {
            @Override
            public void onChanged(List<SavedItem> savedItems) {
                lottieLoading.setVisibility(View.GONE);
                if (savedItems ==null){
                    Log.d(TAG, "onChanged: its null");
                }
                else if(savedItems.size()==0){
                    movies.clear();
                    adapter.clear();
                    Log.d(TAG, "onChanged: no movies faved");
                    noSavedMoviesTxt.setVisibility(View.VISIBLE);
                }
                else {
                    Log.d(TAG, "onChanged: movies faved exist");
                    favRec.setVisibility(View.VISIBLE);
                    noSavedMoviesTxt.setVisibility(View.GONE);
                    movies= savedItems;
                    buildRecycler(movies);
                }
            }
        });
    }

    private void buildRecycler(List<SavedItem> movies) {
        adapter.clear();
        adapter.addAll(movies);
    }

    @Override
    public void onCardClick(SavedItem item) {
        Log.d(TAG, "onCardClick: ");
        Intent goToDetailPage=new Intent();
        if (item.getItemType().equalsIgnoreCase("movie")){
            goToDetailPage=new Intent(context, MovieDetailActivity.class);
            goToDetailPage.putExtra("movie name",item.getMovieName());
            goToDetailPage.putExtra("movie id",item.getMovieId());
            goToDetailPage.putExtra("movie cover",item.getMoviePoster());
            goToDetailPage.putExtra("movie rate",item.getMovieRate());
            goToDetailPage.putExtra("movie overview",item.getMovieOverview());
        }
        else if (item.getItemType().equalsIgnoreCase("tv show")){
            goToDetailPage=new Intent(context, TvShowDetailActivity.class);
            goToDetailPage.putExtra("tv show name",item.getMovieName());
            goToDetailPage.putExtra("tv show id",item.getMovieId());
            goToDetailPage.putExtra("tv show cover",item.getMoviePoster());
            goToDetailPage.putExtra("tv show rate",item.getMovieRate());
            goToDetailPage.putExtra("tv show overview",item.getMovieOverview());
        }
        Log.d(TAG, "onCardClick: over rate and overveiw are :"+item.getMovieRate()+"\n"+item.getMovieOverview());
        startActivity(goToDetailPage);
    }
}
