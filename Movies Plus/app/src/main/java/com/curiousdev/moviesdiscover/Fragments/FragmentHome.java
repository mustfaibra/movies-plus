package com.curiousdev.moviesdiscover.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.curiousdev.moviesdiscover.Activities.MovieDetailActivity;
import com.curiousdev.moviesdiscover.Activities.MoviesActivity;
import com.curiousdev.moviesdiscover.Activities.TvShowDetailActivity;
import com.curiousdev.moviesdiscover.Activities.TvShowsActivity;
import com.curiousdev.moviesdiscover.Adapters.MoviesRecyclerAdapter;
import com.curiousdev.moviesdiscover.Adapters.SeriesRecyclerAdapter;
import com.curiousdev.moviesdiscover.Models.Movie;
import com.curiousdev.moviesdiscover.Models.Series;
import com.curiousdev.moviesdiscover.Models.SeriesItem;
import com.curiousdev.moviesdiscover.R;
import com.curiousdev.moviesdiscover.ViewModels.MoviesViewModel;
import com.curiousdev.moviesdiscover.ViewModels.TvShowViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.hostContext;
import static com.curiousdev.moviesdiscover.Functions.isNetworkAvailable;

public class FragmentHome extends Fragment implements View.OnClickListener, MoviesRecyclerAdapter.OnCardClickListener, SeriesRecyclerAdapter.OnCardClickListener {
    private MoviesRecyclerAdapter popularMovieAdapter,upComingmoviesAdapter,newMoviesAdapter;
    private SeriesRecyclerAdapter popularTvShowAdapter,topRatedTvShowsAdapter,onAirTvShowAdapter;
    private List<Movie> moviesList;
    private List<SeriesItem> tvshowList;
    //View of fragment
    private View view;
    //contant context that use a static context variable form host activity
    private final Context context=hostContext;
    //Movies view model
    private MoviesViewModel moviesViewModel;
    //tv show view model
    private TvShowViewModel tvShowViewModel;
    //swiper refresh
    private SwipeRefreshLayout swipeRefresh;
    //recycler view
    private RecyclerView popularMoviesRec,upComingMoviesRec,newMoviesRec,popularTvShowsRec,topRatedTvShowRec,onAirTvShowRec;
    //shimmer layouts
    private ShimmerFrameLayout popularMoviesRecShimmer,upComingMoviesRecShimmer,newMoviesRecShimmer,popularTvShowsRecShimmer,topRatedTvShowRecShimmer,onAirTvShowRecShimmer;
    //relative
    private RelativeLayout errorRetryContainer;
    private TextView retryLoading;

    public static FragmentHome homeInstance;
    public static FragmentHome getHomeInstance(){
        if(homeInstance==null){
            homeInstance=new FragmentHome();
        }
        return homeInstance;
    }
    //variables
    private int startPage = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moviesViewModel= ViewModelProviders.of(this).get(MoviesViewModel.class);
        tvShowViewModel= ViewModelProviders.of(this).get(TvShowViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //lists
        tvshowList=new ArrayList<>();
        moviesList=new ArrayList<>();

        if (view==null){
            view = inflater.inflate(R.layout.home_fragment, container, false);
            //relative
            errorRetryContainer=view.findViewById(R.id.error_bottom_snackbar);

            //refresh swiper
            swipeRefresh=view.findViewById(R.id.home_swiper);
            swipeRefresh.setOnRefreshListener(()->{
                getHomeData();
                swipeRefresh.setRefreshing(false);
                swipeRefresh.setEnabled(false);
            });
            //textViews
            retryLoading=view.findViewById(R.id.home_retry);
            retryLoading.setOnClickListener(this);
            TextView seeAllPopularMovies = view.findViewById(R.id.see_all_popular_movies);
            seeAllPopularMovies.setOnClickListener(this);
            TextView seeAllUpcomingMovies = view.findViewById(R.id.see_all_upcoming_movies);
            seeAllUpcomingMovies.setOnClickListener(this);
            TextView seeAllNewMovies = view.findViewById(R.id.see_all_new_movies);
            seeAllNewMovies.setOnClickListener(this);
            TextView seeAllPopularTvShow = view.findViewById(R.id.see_all_popular_tv_show);
            seeAllPopularTvShow.setOnClickListener(this);
            TextView seeAllTopRatedTvShow = view.findViewById(R.id.see_all_top_rated_tv_show);
            seeAllTopRatedTvShow.setOnClickListener(this);
            TextView seeAllOnAirTvShow = view.findViewById(R.id.see_all_on_air_tv_show);
            seeAllOnAirTvShow.setOnClickListener(this);
            //recyclers
            popularMoviesRec = view.findViewById(R.id.home_popular_movie_rec);
            upComingMoviesRec = view.findViewById(R.id.home_upcoming_movie_rec);
            newMoviesRec = view.findViewById(R.id.home_new_movies_rec);
            popularTvShowsRec = view.findViewById(R.id.home_popular_tv_show_rec);
            topRatedTvShowRec = view.findViewById(R.id.home_top_rated_tv_show_rec);
            onAirTvShowRec = view.findViewById(R.id.home_on_air_tv_show_rec);
            //shimmers
            popularMoviesRecShimmer = view.findViewById(R.id.home_popular_movie_rec_holder);
            upComingMoviesRecShimmer = view.findViewById(R.id.home_upcoming_movie_rec_holder);
            newMoviesRecShimmer = view.findViewById(R.id.home_new_movie_rec_holder);
            popularTvShowsRecShimmer = view.findViewById(R.id.home_popular_tv_show_rec_holder);
            topRatedTvShowRecShimmer = view.findViewById(R.id.home_top_rated_tv_show_rec_holder);
            onAirTvShowRecShimmer = view.findViewById(R.id.home_on_air_tv_show_rec_holder);
            //adapters
            popularMovieAdapter = new MoviesRecyclerAdapter(context);
            upComingmoviesAdapter = new MoviesRecyclerAdapter(context);
            newMoviesAdapter = new MoviesRecyclerAdapter(context);

            popularTvShowAdapter = new SeriesRecyclerAdapter(context);
            topRatedTvShowsAdapter = new SeriesRecyclerAdapter(context);
            onAirTvShowAdapter = new SeriesRecyclerAdapter(context);

            //setting adapters to recyclers
            popularMoviesRec.setAdapter(popularMovieAdapter);
            upComingMoviesRec.setAdapter(upComingmoviesAdapter);
            newMoviesRec.setAdapter(newMoviesAdapter);

            popularTvShowsRec.setAdapter(popularTvShowAdapter);
            topRatedTvShowRec.setAdapter(topRatedTvShowsAdapter);
            onAirTvShowRec.setAdapter(onAirTvShowAdapter);

            //setting adapters's items click event
            popularMovieAdapter.setOnCardClickListener(this);//catch the click on card
            upComingmoviesAdapter.setOnCardClickListener(this);//catch the click on card
            newMoviesAdapter.setOnCardClickListener(this);//catch the click on card

            popularTvShowAdapter.setOnCardClickListener(this);//catch the click on card
            topRatedTvShowsAdapter.setOnCardClickListener(this);//catch the click on card
            onAirTvShowAdapter.setOnCardClickListener(this);//catch the click on card
            getHomeData();
        }
        return view;
    }


    private void getHomeData(){
        getPopularMovies();
        getUpcomingMovies();
        getNewMovies();
        getPopularShow();
        getOnAirShow();
        getTopRatedShow();
    }

    private void getUpcomingMovies(){
        upComingMoviesRecShimmer.setVisibility(View.VISIBLE);
        upComingMoviesRecShimmer.startShimmer();
        upComingMoviesRec.setVisibility(View.GONE);
        upComingmoviesAdapter.clear();
        moviesViewModel.getUpComingMovies(startPage).observe(this, movies -> {
            swipeRefresh.setEnabled(true);
            if (movies!=null){
                upComingMoviesRecShimmer.setVisibility(View.GONE);
                upComingMoviesRec.setVisibility(View.VISIBLE);

                List<Movie> list=movies.getMovies();
                if (list.size()>6){
                    for (int i = 0; i <6 ; i++) {
                        moviesList.add(list.get(i));
                        upComingmoviesAdapter.add(list.get(i));
                    }
                }
                else {
                    moviesList.addAll(list);
                    upComingmoviesAdapter.addAll(list);
                }

            }
            else {
                upComingMoviesRecShimmer.stopShimmer();
                errorHandler();
            }
        });
    }
    private void getNewMovies(){
        newMoviesRecShimmer.setVisibility(View.VISIBLE);
        newMoviesRecShimmer.startShimmer();
        newMoviesRec.setVisibility(View.GONE);
        newMoviesAdapter.clear();
        moviesViewModel.getNewMovies(startPage).observe(this, movies -> {
            if (movies!=null){
                newMoviesRecShimmer.setVisibility(View.GONE);
                newMoviesRec.setVisibility(View.VISIBLE);
                List<Movie> list=movies.getMovies();
                if (list.size()>6){
                    for (int i = 0; i <6 ; i++) {
                        moviesList.add(list.get(i));
                        newMoviesAdapter.add(list.get(i));
                    }
                }
                else {
                    moviesList.addAll(list);
                    newMoviesAdapter.addAll(list);
                }

            }
            else {
                //its null
                newMoviesRecShimmer.stopShimmer();
                errorHandler();
            }
        });

    }
    private void getPopularMovies(){
        popularMoviesRecShimmer.setVisibility(View.VISIBLE);
        popularMoviesRecShimmer.startShimmer();
        popularMoviesRec.setVisibility(View.GONE);
        popularMovieAdapter.clear();
        moviesViewModel.getPopularMovies(startPage).observe(this, movies -> {
            if (movies!=null){
                popularMoviesRecShimmer.setVisibility(View.GONE);
                popularMoviesRec.setVisibility(View.VISIBLE);
                List<Movie> list=movies.getMovies();
                if (list.size()>6){
                    for (int i = 0; i <6 ; i++) {
                        moviesList.add(list.get(i));
                        popularMovieAdapter.add(list.get(i));
                    }
                }
                else {
                    moviesList.addAll(list);
                    popularMovieAdapter.addAll(list);
                }

            }
            else {
                //its null
                popularMoviesRecShimmer.stopShimmer();
                errorHandler();
            }
        });

    }

    private void getPopularShow(){
        popularTvShowsRecShimmer.setVisibility(View.VISIBLE);
        popularTvShowsRecShimmer.startShimmer();
        popularTvShowsRec.setVisibility(View.GONE);
        popularTvShowAdapter.clear();
        tvShowViewModel.getPopularTvShow(startPage).observe(this, series -> {
            if (series!=null){
                popularTvShowsRecShimmer.setVisibility(View.GONE);
                popularTvShowsRec.setVisibility(View.VISIBLE);
                List<SeriesItem> list=series.getResults();
                if (list.size()>6){
                    for (int i = 0; i <6 ; i++) {
                        tvshowList.add(list.get(i));
                        popularTvShowAdapter.add(list.get(i));
                    }
                }
                else {
                    tvshowList.addAll(list);
                    popularTvShowAdapter.addAll(list);
                }

            }
            else {
                //its null
                popularTvShowsRecShimmer.stopShimmer();
                errorHandler();
            }
        });

    }
    private void getTopRatedShow(){
        topRatedTvShowRecShimmer.setVisibility(View.VISIBLE);
        topRatedTvShowRecShimmer.startShimmer();
        topRatedTvShowRec.setVisibility(View.GONE);
        topRatedTvShowsAdapter.clear();
        tvShowViewModel.getTopRatedTvShow(startPage).observe(this, series -> {
            if (series!=null){
                topRatedTvShowRecShimmer.setVisibility(View.GONE);
                topRatedTvShowRec.setVisibility(View.VISIBLE);
                List<SeriesItem> list=series.getResults();

                if (list.size()>6){
                    for (int i = 0; i <6 ; i++) {
                        tvshowList.add(list.get(i));
                        topRatedTvShowsAdapter.add(list.get(i));
                    }
                }
                else {
                    tvshowList.addAll(list);
                    topRatedTvShowsAdapter.addAll(list);
                }
            }
            else {
                // its null
                topRatedTvShowRecShimmer.stopShimmer();
                errorHandler();
            }
        });

    }
    private void getOnAirShow(){
        onAirTvShowRecShimmer.setVisibility(View.VISIBLE);
        onAirTvShowRecShimmer.startShimmer();
        onAirTvShowRec.setVisibility(View.GONE);
        onAirTvShowAdapter.clear();
        tvShowViewModel.getOnAirTvShow(startPage).observe(this, series -> {
            if (series!=null){
                onAirTvShowRecShimmer.setVisibility(View.GONE);
                onAirTvShowRec.setVisibility(View.VISIBLE);
                List<SeriesItem> list=series.getResults();
                if (list.size()>6) {
                    for (int i = 0; i < 6; i++) {
                        tvshowList.add(list.get(i));
                        onAirTvShowAdapter.add(list.get(i));
                    }
                }
                else {
                    tvshowList.addAll(list);
                    onAirTvShowAdapter.addAll(list);
                }
            }
            else {
                //its null
                onAirTvShowRecShimmer.stopShimmer();
                errorHandler();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_retry:{
                errorRetryContainer.setVisibility(View.GONE);
                getHomeData();
                break;
            }
            case R.id.see_all_popular_movies:{
                Intent goToMoviesActivity=new Intent(hostContext,MoviesActivity.class);
                goToMoviesActivity.putExtra("type",hostContext.getString(R.string.popular));
                startActivity(goToMoviesActivity);
                break;
            }
            case R.id.see_all_upcoming_movies:{
                Intent goToMoviesActivity=new Intent(hostContext,MoviesActivity.class);
                goToMoviesActivity.putExtra("type",hostContext.getString(R.string.upcoming));
                startActivity(goToMoviesActivity);
                break;
            }
            case R.id.see_all_new_movies:{
                Intent goToMoviesActivity=new Intent(hostContext,MoviesActivity.class);
                goToMoviesActivity.putExtra("type",hostContext.getString(R.string.new_txt));
                startActivity(goToMoviesActivity);
                break;
            }
            case R.id.see_all_popular_tv_show:{
                Intent goToTvshowActivity=new Intent(hostContext, TvShowsActivity.class);
                goToTvshowActivity.putExtra("type",hostContext.getString(R.string.popular));
                startActivity(goToTvshowActivity);
                break;
            }
            case R.id.see_all_top_rated_tv_show:{
                Intent goToTvshowActivity=new Intent(hostContext, TvShowsActivity.class);
                goToTvshowActivity.putExtra("type",hostContext.getString(R.string.top_rated));
                startActivity(goToTvshowActivity);
                break;
            }
            case R.id.see_all_on_air_tv_show:{
                Intent goToTvshowActivity=new Intent(hostContext, TvShowsActivity.class);
                goToTvshowActivity.putExtra("type",hostContext.getString(R.string.on_air));
                startActivity(goToTvshowActivity);
                break;
            }
        }
    }

    private void errorHandler(){

        errorRetryContainer.setVisibility(View.VISIBLE);
//
//        if (!isNetworkAvailable(hostContext)){
//        }
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent goToDetailPage=new Intent(hostContext, MovieDetailActivity.class);
        goToDetailPage.putExtra("movie name",movie.getTitle());
        goToDetailPage.putExtra("movie id",movie.getId());
        goToDetailPage.putExtra("movie cover",movie.getPosterPath());
        goToDetailPage.putExtra("movie rate",movie.getVoteAverage());
        goToDetailPage.putExtra("movie overview",movie.getOverview());
        startActivity(goToDetailPage);
    }

    @Override
    public void onTvShowClick(SeriesItem tvShow) {
        Intent goToDetailPage=new Intent(hostContext, TvShowDetailActivity.class);
        goToDetailPage.putExtra("tv show name",tvShow.getName());
        goToDetailPage.putExtra("tv show id",tvShow.getId());
        goToDetailPage.putExtra("tv show cover",tvShow.getPosterPath());
        goToDetailPage.putExtra("tv show rate",tvShow.getVoteAverage());
        goToDetailPage.putExtra("tv show overview",tvShow.getOverview());
        startActivity(goToDetailPage);
    }



}
