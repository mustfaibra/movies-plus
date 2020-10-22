package com.curiousdev.moviesdiscover.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.curiousdev.moviesdiscover.Adapters.MoviesRecyclerAdapter;
import com.curiousdev.moviesdiscover.Models.Movie;
import com.curiousdev.moviesdiscover.R;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class SavedFragment extends Fragment {
    //ads
    AdView adView;
    //debugging tag
    private static final String TAG = "SavedFragment";
    ArrayList<Movie> moviesList;
    //containers
    private RecyclerView moviesRec;
    private ShimmerFrameLayout shimmer;
    private SwipeRefreshLayout refreshSwiper;
    //handler
    private Handler handler;
    //adapter
    private MoviesRecyclerAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        //contaienrs
        shimmer=view.findViewById(R.id.shimmer);
        shimmer.startShimmer();
        moviesRec=view.findViewById(R.id.home_recycler);
        refreshSwiper=view.findViewById(R.id.home_swiper);
        refreshSwiper.setEnabled(false);
        //set the swipe listener
        refreshSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateTapContents();
            }
        });
        //set swiper color
        refreshSwiper.setColorSchemeColors(
                getResources().getColor(R.color.yellow),
                getResources().getColor(R.color.black)
        );

        //init the page
        initPage();
    }

    private void updateTapContents() {
        Log.d(TAG, "updateTapContents: ");
        moviesList.clear();
        getRecyclerData();
    }

    private void initPage() {
        getRecyclerData();
    }

    private void getRecyclerData() {
        Log.d(TAG, "getRecyclerData: ");
        buildHomeRecycler();
    }
    private void buildHomeRecycler() {
        refreshSwiper.setEnabled(true);
        moviesList=new ArrayList<>();
        refreshSwiper.setRefreshing(false);
        shimmer.setVisibility(View.GONE);
        moviesRec.setVisibility(View.VISIBLE);
        Log.d(TAG, "buildHomeRecycler: ");
        adapter=new MoviesRecyclerAdapter(getContext());
        moviesRec.setLayoutManager(new GridLayoutManager(getContext(),2));
        moviesRec.setAdapter(adapter);
    }

//    @Override
//    public void onCardClick(Movie movie) {
//        Log.d(TAG, "onCardClick: ");
//        Intent goToDetailPage=new Intent(, MovieDetailActivity.class);
//        goToDetailPage.putExtra("movie name",movie.getTitle());
//        goToDetailPage.putExtra("movie id",movie.getId());
//        goToDetailPage.putExtra("movie cover",movie.getPosterPath());
//        goToDetailPage.putExtra("movie rate",movie.getVoteAverage());
//        goToDetailPage.putExtra("movie overview",movie.getOverview());
//        startActivity(goToDetailPage);
//    }
}
