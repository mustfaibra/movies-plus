package com.curiousdev.moviesdiscover.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.curiousdev.moviesdiscover.Adapters.MoviesRecyclerAdapter;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.curiousdev.moviesdiscover.HelperClasses.GridScrollListenter;
import com.curiousdev.moviesdiscover.Models.Movie;
import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.R;
import com.curiousdev.moviesdiscover.ViewModels.MovieDetailViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.curiousdev.moviesdiscover.Functions.isNetworkAvailable;

public class SimilarMoviesActivity extends AppCompatActivity implements View.OnClickListener, MoviesRecyclerAdapter.OnCardClickListener {
    //ads
    AdView adView;
    private static final String TAG = "SimilarMoviesActivity";
    private RecyclerView similarMoviesRec;
    GridLayoutManager gridLayoutManager;
    private List<Movie> similarMoviesList;
    private MoviesRecyclerAdapter adapter;
    private LottieAnimationView loading;
    private ProgressBar loadingMore;
    private RelativeLayout errorLayout,noSimilarMoviesTxtContainer;

    private int movieId;
    private int startPage=1;
    private int currentPage=startPage;
    private int pageCount;
    private boolean isLastPage;
    private boolean isLoading;
    private Snackbar snackbar;
    

    //Movie view model
    MovieDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_movies);

        initActivity();

        if (getIntent().getExtras()!=null){
            String movieName=getIntent().getExtras().getString("movie name");
            movieId =getIntent().getExtras().getInt("movie id");
        }

        viewModel= ViewModelProviders.of(this).get(MovieDetailViewModel.class);
        loadSimilarMovies(currentPage);
    }

    private void loadSimilarMovies(int currentPage){
        viewModel.getSimilarMovies(movieId,currentPage).observe(this, new Observer<Movies>() {
            @Override
            public void onChanged(Movies movies) {
                Log.d(TAG, "onChanged: ");
                loading.setVisibility(View.GONE);
                loadingMore.setVisibility(View.GONE);
                isLoading=false;
                if (movies!=null){
                    Log.d(TAG, "onChanged: it's not null");
                    if (movies.getMovies().size()>0){
                        Log.d(TAG, "onChanged: adding new movies");
                        if (currentPage==1){
                            similarMoviesRec.setVisibility(View.VISIBLE);
                            pageCount=movies.getTotalPages();
                        }
                        isLastPage= currentPage >= pageCount;
                        Log.d(TAG, "onChanged: is last page?"+isLastPage);
                        similarMoviesList.addAll(movies.getMovies());
                        adapter.addAll(movies.getMovies());
                    }

                    else {
                        if (similarMoviesList.size()==0){
                            Log.d(TAG, "onChanged: there are no similar movies");
                            noSimilarMoviesTxtContainer.setVisibility(View.VISIBLE);
                        }
                        else {
                            Log.d(TAG, "onChanged: there are no more similar movies");
                        }
                    }
                }
                else{
                    processError();
                }
            }
        });

    }

    private void initActivity() {
        Toolbar toolbar=findViewById(R.id.similar_movies_toolbar);
        loading=findViewById(R.id.lottie_loading);
        errorLayout=findViewById(R.id.error_layout);
        noSimilarMoviesTxtContainer=findViewById(R.id.no_similar_container);
        loadingMore=findViewById(R.id.loading_more);
        adapter=new MoviesRecyclerAdapter(this);
        adapter.setOnCardClickListener(this);
        similarMoviesRec=findViewById(R.id.similar_recycler);
        gridLayoutManager=(GridLayoutManager)similarMoviesRec.getLayoutManager();
        similarMoviesList=new ArrayList<>();

        similarMoviesRec.setAdapter(adapter);
        similarMoviesRec.addOnScrollListener(new GridScrollListenter(gridLayoutManager) {
            @Override
            public void loadMoreMovies() {
                Log.d(TAG, "loadMoreMovies: ");
                isLoading=true;
                currentPage++;
                loadingMore.setVisibility(View.VISIBLE);
                loadSimilarMovies(currentPage);
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
                return pageCount;
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void processError() {

        isLoading=false;

        if (currentPage>1){
            loadingMore.setVisibility(View.GONE);
            //here we will process loading more failing
            snackbar= Snackbar.make(similarMoviesRec,getString(R.string.loading_more_failed),Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.retry), v -> {
                        isLoading=true;
                        loadingMore.setVisibility(View.VISIBLE);
                        loadSimilarMovies(currentPage);
                    }).setActionTextColor(getResources().getColor(R.color.yellow)).setDuration(3000);
            Snackbar.SnackbarLayout snackbarLayout=(Snackbar.SnackbarLayout) snackbar.getView();
            snackbarLayout.setPadding(0,0,0,0);
            snackbar.show();
        }
        else {
            TextView errorHeader=findViewById(R.id.error_header);
            TextView errorTxt=findViewById(R.id.error_txt);
            Button retry=findViewById(R.id.retry_button);
            retry.setOnClickListener(this);
            //here processing first page and refresh error
            errorLayout.setVisibility(View.VISIBLE);

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.retry_button:{
                errorLayout.setVisibility(View.GONE);
                loadSimilarMovies(currentPage);
                break;
            }
        }
    }

    @Override
    public void onMovieClick(Movie movie) {
        Log.d(TAG, "onCardClick: ");
        Intent goToDetailPage=new Intent(this, MovieDetailActivity.class);
        goToDetailPage.putExtra("movie name",movie.getTitle());
        goToDetailPage.putExtra("movie id",movie.getId());
        goToDetailPage.putExtra("movie cover",movie.getPosterPath());
        goToDetailPage.putExtra("movie rate",movie.getVoteAverage());
        goToDetailPage.putExtra("movie overview",movie.getOverview());
        startActivity(goToDetailPage);
    }
}
