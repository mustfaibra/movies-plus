package com.curiousdev.moviesdiscover.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.curiousdev.moviesdiscover.Adapters.ReviewsAdapter;
import com.curiousdev.moviesdiscover.R;
import com.curiousdev.moviesdiscover.ViewModels.MovieDetailViewModel;
import com.curiousdev.moviesdiscover.ViewModels.ReviewViewModel;
import com.curiousdev.moviesdiscover.ViewModels.TvShowDetailViewModel;

import java.util.ArrayList;

public class ReviewsActivity extends AppCompatActivity {

    //reviews viewmodel
    ReviewViewModel reviewViewModel;
    RecyclerView reviewsRecycler;
    TextView noReviewsTxt;
    ReviewsAdapter adapter;

    private ProgressBar loading;

    //variables
    private int id;
    private String type;
    private int startPage=1;
    private int currentPage=startPage;
    private int pageCount;
    private boolean isLastPage=false;
    private boolean isLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        reviewViewModel= ViewModelProviders.of(this).get(ReviewViewModel.class);
        if (getIntent().getExtras()!=null){
            id=getIntent().getExtras().getInt("id");
            type=getIntent().getExtras().getString("type");
        }

        Toolbar toolbar = findViewById(R.id.reviews_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reviewsRecycler=findViewById(R.id.reviews_recycler);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        reviewsRecycler.setLayoutManager(layoutManager);
        adapter=new ReviewsAdapter(new ArrayList<>(),this);
        reviewsRecycler.setAdapter(adapter);
        noReviewsTxt=findViewById(R.id.no_reviews);
        loading=findViewById(R.id.reviews_progress);
        if (type.equalsIgnoreCase("movie")){
            getMovieReviews(startPage);
        }
        else if (type.equalsIgnoreCase("tv show")){
            getTvShowReviews(startPage);
        }
    }

    private void getTvShowReviews(int page){
        reviewViewModel.getTvShowReviews(id,page).observe(this,reviews->{
            loading.setVisibility(View.GONE);
            isLoading=false;
            if (reviews!=null){
                if (reviews.getTotalResults()==0){
                    noReviewsTxt.setVisibility(View.VISIBLE);
                }
                else {
                    if (page==1){
                        pageCount=reviews.getTotalPages();
                    }
                    isLastPage=pageCount<=currentPage;
                    adapter.addAll(reviews.getResults());
                }
            }
            else {
                reviewsRecycler.setVisibility(View.GONE);
            }

        });
    }
    private void getMovieReviews(int page){
        reviewViewModel.getMovieReviews(id,page).observe(this,reviews->{
            loading.setVisibility(View.GONE);
            isLoading=false;
            if (reviews!=null){
                if (reviews.getTotalResults()==0){
                    noReviewsTxt.setVisibility(View.VISIBLE);
                }
                else {
                    if (page==1){
                        pageCount=reviews.getTotalPages();
                    }
                    isLastPage=pageCount<=currentPage;
                    adapter.addAll(reviews.getResults());
                }
            }
            else {
                reviewsRecycler.setVisibility(View.GONE);
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return true;
    }
}
