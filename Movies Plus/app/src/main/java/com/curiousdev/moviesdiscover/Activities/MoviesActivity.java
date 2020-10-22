package com.curiousdev.moviesdiscover.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.curiousdev.moviesdiscover.Adapters.MoviesRecyclerAdapter;
import com.curiousdev.moviesdiscover.HelperClasses.GridScrollListenter;
import com.curiousdev.moviesdiscover.Models.Movie;
import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.R;
import com.curiousdev.moviesdiscover.ViewModels.MoviesViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;

import static com.curiousdev.moviesdiscover.Functions.isNetworkAvailable;

public class MoviesActivity extends AppCompatActivity implements MoviesRecyclerAdapter.OnCardClickListener {
    private AdView adView;
    //Movies view model
    private MoviesViewModel moviesViewModel;
    //recycler and adapter and layout manager
    private RecyclerView moviesRec;
    private MoviesRecyclerAdapter movieAdapter;
    private GridLayoutManager layoutManager;

    private RelativeLayout errorLayout;

    //widgets
    private LottieAnimationView lottieLoading;
    private TextView moviesTitle;
    //variables
    private List<Movie> movies;
    private int startPage=1;
    private boolean isLoading=false; //to check new movies loading
    private boolean isLastPage=false; //to check if its the last page or not
    private int totalPage; //NUMBER OF PAGES AVAILABLE IN API
    private int currentPage=startPage;

    //intent variables
    private String typeSelected;
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("current page",currentPage);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adView=findViewById(R.id.movies_act_banner);
        AdRequest adRequest=new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
        typeSelected=getIntent().getExtras().getString("type");
        movies=new ArrayList<>();
        moviesViewModel=ViewModelProviders.of(this).get(MoviesViewModel.class);
        Toolbar toolbar=findViewById(R.id.movies_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        errorLayout = findViewById(R.id.error_layout);
        moviesRec=findViewById(R.id.movies_recycler);
        layoutManager=(GridLayoutManager)moviesRec.getLayoutManager();
        movieAdapter=new MoviesRecyclerAdapter(this);
        moviesRec.setAdapter(movieAdapter);
        movieAdapter.setOnCardClickListener(this);
        moviesTitle=findViewById(R.id.movies_title);
        moviesTitle.setText(typeSelected.toUpperCase());
        lottieLoading=findViewById(R.id.lottie_loading);

        moviesRec.addOnScrollListener(new GridScrollListenter(layoutManager) {
            @Override
            public void loadMoreMovies() {
                currentPage++;
                loadMovies(currentPage);
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public int getTotalPagesCount() {
                return totalPage;
            }
        });
        loadMovies(currentPage);
    }

    private void loadMovies(int currentPage) {
        lottieLoading.setVisibility(View.VISIBLE);
        isLoading=true;
        if (typeSelected.equalsIgnoreCase(getString(R.string.popular))){
            moviesViewModel.getPopularMovies(currentPage).observe(this,result -> {
                showMovies(result);
            });
        }
        else if (typeSelected.equalsIgnoreCase(getString(R.string.upcoming))){
            moviesViewModel.getUpComingMovies(currentPage).observe(this,result -> {
                showMovies(result);
            });
        }
        else if (typeSelected.equalsIgnoreCase(getString(R.string.new_txt))){
            moviesViewModel.getNewMovies(currentPage).observe(this,result -> {
                showMovies(result);
            });
        }
    }

    private void showMovies(Movies result) {
        isLoading=false;
        lottieLoading.setVisibility(View.GONE);
        if (result==null){
            //its null
            processError();
        }
        else {
            if(currentPage==1){
                totalPage=result.getTotalPages();
            }
            movies.addAll(result.getMovies());
            movieAdapter.addAll(result.getMovies());
            isLastPage=totalPage<=currentPage;
        }
    }

    private void processError(){

        if (currentPage>1){
            Toast.makeText(this, getString(R.string.loading_more_failed), Toast.LENGTH_SHORT).show();
        }
        else {
            TextView errorHeader=findViewById(R.id.error_header);
            TextView errorTxt=findViewById(R.id.error_txt);
            errorLayout.setVisibility(View.VISIBLE);
            Button retryBtn = findViewById(R.id.retry_button);
            retryBtn.setOnClickListener(view->{
                errorLayout.setVisibility(View.GONE);
                loadMovies(currentPage);
            });

            /*it make a fatal exception leading app to crash
            so in order to fix this problem i made a static context in the host activity use it in
            the fragment instead of getActivity() and getContext()
            */
            if (!isNetworkAvailable(this)){
                errorHeader.setText(getString(R.string.no_connection_header));
                errorTxt.setText(getString(R.string.no_connection_txt));
            }
            else {
                errorHeader.setText(getString(R.string.unknown_error_header));
                errorTxt.setText(getString(R.string.unknown_error_txt));
            }
        }

    }


    @Override
    public void onMovieClick(Movie movie) {
        Intent goToDetailPage=new Intent(this, MovieDetailActivity.class);
        goToDetailPage.putExtra("movie name",movie.getTitle());
        goToDetailPage.putExtra("movie id",movie.getId());
        goToDetailPage.putExtra("movie cover",movie.getPosterPath());
        goToDetailPage.putExtra("movie rate",movie.getVoteAverage());
        goToDetailPage.putExtra("movie overview",movie.getOverview());
        startActivity(goToDetailPage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return true;
    }
}
