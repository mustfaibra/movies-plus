package com.curiousdev.moviesdiscover.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.curiousdev.moviesdiscover.Adapters.SeriesRecyclerAdapter;
import com.curiousdev.moviesdiscover.HelperClasses.GridScrollListenter;
import com.curiousdev.moviesdiscover.Models.Series;
import com.curiousdev.moviesdiscover.Models.SeriesItem;
import com.curiousdev.moviesdiscover.R;
import com.curiousdev.moviesdiscover.ViewModels.TvShowViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.hostContext;
import static com.curiousdev.moviesdiscover.Functions.isNetworkAvailable;

public class TvShowsActivity extends AppCompatActivity implements SeriesRecyclerAdapter.OnCardClickListener {

    //TvShow view model
    private TvShowViewModel tvShowViewModel;
    //recycler and adapter and layout manager
    private RecyclerView tvShowRec;
    private SeriesRecyclerAdapter tvShowAdapter;
    private GridLayoutManager layoutManager;

    private RelativeLayout errorLayout;

    //widgets
    private LottieAnimationView lottieLoading;
    private TextView tvShowTitle;
    //variables
    private List<SeriesItem> series;
    private int startPage=1;
    private boolean isLoading=false; //to check new tv shows loading
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
        setContentView(R.layout.activity_tv_shows);
        typeSelected=getIntent().getExtras().getString("type");
        series=new ArrayList<>();
        tvShowViewModel=ViewModelProviders.of(this).get(TvShowViewModel.class);
        Toolbar toolbar=findViewById(R.id.tv_shows_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        errorLayout = findViewById(R.id.error_layout);

        tvShowRec=findViewById(R.id.tv_shows_recycler);
//        layoutManager=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
//        tvShowRec.setLayoutManager(layoutManager);
        layoutManager=(GridLayoutManager)tvShowRec.getLayoutManager();
        tvShowAdapter=new SeriesRecyclerAdapter(this);
        tvShowRec.setAdapter(tvShowAdapter);
        tvShowAdapter.setOnCardClickListener(this);
        tvShowTitle=findViewById(R.id.tv_shows_title);
        tvShowTitle.setText(typeSelected.toUpperCase());
        lottieLoading=findViewById(R.id.lottie_loading);

        tvShowRec.addOnScrollListener(new GridScrollListenter(layoutManager) {
            @Override
            public void loadMoreMovies() {
                currentPage++;
                loadTvShow(currentPage);
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
        loadTvShow(currentPage);
    }

    private void loadTvShow(int currentPage) {
        lottieLoading.setVisibility(View.VISIBLE);
        isLoading=true;
        if (typeSelected.equalsIgnoreCase(getString(R.string.popular))){
            tvShowViewModel.getPopularTvShow(currentPage).observe(this,result -> {
                showTvShow(result);
            });
        }
        else if (typeSelected.equalsIgnoreCase(getString(R.string.top_rated))){
            tvShowViewModel.getTopRatedTvShow(currentPage).observe(this,result -> {
                showTvShow(result);
            });
        }
        else if (typeSelected.equalsIgnoreCase(getString(R.string.on_air))){
            tvShowViewModel.getOnAirTvShow(currentPage).observe(this,result -> {
                showTvShow(result);
            });
        }
    }

    private void showTvShow(Series result) {
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
            series.addAll(result.getResults());
            tvShowAdapter.addAll(result.getResults());
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
                loadTvShow(currentPage);
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
    public void onTvShowClick(SeriesItem tvShow) {
        Intent goToDetailPage=new Intent(this, TvShowDetailActivity.class);
        goToDetailPage.putExtra("tv show name",tvShow.getName());
        goToDetailPage.putExtra("tv show id",tvShow.getId());
        goToDetailPage.putExtra("tv show cover",tvShow.getPosterPath());
        goToDetailPage.putExtra("tv show rate",tvShow.getVoteAverage());
        goToDetailPage.putExtra("tv show overview",tvShow.getOverview());
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
