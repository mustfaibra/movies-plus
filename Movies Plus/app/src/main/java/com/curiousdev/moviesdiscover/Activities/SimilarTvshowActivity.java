package com.curiousdev.moviesdiscover.Activities;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.curiousdev.moviesdiscover.Adapters.SeriesRecyclerAdapter;
import com.curiousdev.moviesdiscover.HelperClasses.GridScrollListenter;
import com.curiousdev.moviesdiscover.Models.SeriesItem;
import com.curiousdev.moviesdiscover.R;
import com.curiousdev.moviesdiscover.ViewModels.TvShowDetailViewModel;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import static com.curiousdev.moviesdiscover.Functions.isNetworkAvailable;

public class SimilarTvshowActivity extends AppCompatActivity implements View.OnClickListener, SeriesRecyclerAdapter.OnCardClickListener {
    //ads
    AdView adView;
    private static final String TAG = "SimilarTvShowActivity";
    private RecyclerView similarTvShowRec;
    GridLayoutManager gridLayoutManager;
    private List<SeriesItem> similarTvShowList;
    private SeriesRecyclerAdapter adapter;
    private LottieAnimationView loading;
    private ProgressBar loadingMore;
    private RelativeLayout errorLayout,noSimilarShowTxtContainer;

    private int tvShowId;
    private int startPage=1;
    private int currentPage=startPage;
    private int pageCount;
    private boolean isLastPage;
    private boolean isLoading;


    //Movie view model
    TvShowDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_tvshow);

        initActivity();

        if (getIntent().getExtras()!=null){
            String tvShowName=getIntent().getExtras().getString("tv show name");
            tvShowId =getIntent().getExtras().getInt("tv show id");
        }

        viewModel= ViewModelProviders.of(this).get(TvShowDetailViewModel.class);
        loadSimilarMovies(currentPage);
    }

    private void loadSimilarMovies(int currentPage){
        viewModel.getSimilarTvShows(tvShowId,currentPage).observe(this, series -> {
            Log.d(TAG, "onChanged: ");
            loading.setVisibility(View.GONE);
            loadingMore.setVisibility(View.GONE);
            isLoading=false;
            if (series!=null){
                Log.d(TAG, "onChanged: it's not null");
                if (series.getResults().size()>0){
                    Log.d(TAG, "onChanged: adding new series");
                    if (currentPage==1){
                        similarTvShowRec.setVisibility(View.VISIBLE);
                        pageCount=series.getTotalPages();
                    }
                    isLastPage= currentPage >= pageCount;
                    Log.d(TAG, "onChanged: is last page?"+isLastPage);
                    similarTvShowList.addAll(series.getResults());
                    adapter.addAll(series.getResults());
                }

                else {
                    if (similarTvShowList.size()==0){
                        Log.d(TAG, "onChanged: there are no similar series");
                        noSimilarShowTxtContainer.setVisibility(View.VISIBLE);
                    }
                    else {
                        Log.d(TAG, "onChanged: there are no more similar series");
                    }
                }
            }
            else{
                processError();
            }
        });

    }

    private void initActivity() {
        Toolbar toolbar=findViewById(R.id.similar_tv_show_toolbar);
        loading=findViewById(R.id.lottie_loading);
        errorLayout=findViewById(R.id.error_layout);
        noSimilarShowTxtContainer=findViewById(R.id.no_similar_container);
        loadingMore=findViewById(R.id.loading_more);
        adapter=new SeriesRecyclerAdapter(this);
        adapter.setOnCardClickListener(this);
        similarTvShowRec=findViewById(R.id.similar_recycler);
        gridLayoutManager=(GridLayoutManager)similarTvShowRec.getLayoutManager();
        similarTvShowList=new ArrayList<>();

        similarTvShowRec.setLayoutManager(gridLayoutManager);
        similarTvShowRec.setAdapter(adapter);
        similarTvShowRec.addOnScrollListener(new GridScrollListenter(gridLayoutManager) {
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
            Snackbar snackbar = Snackbar.make(similarTvShowRec, getString(R.string.loading_more_failed), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.retry), v -> {
                        isLoading = true;
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
    public void onTvShowClick(SeriesItem tvShow) {
        Log.d(TAG, "onCardClick: ");
        Intent goToDetailPage=new Intent(this, TvShowDetailActivity.class);
        goToDetailPage.putExtra("tv show name",tvShow.getName());
        goToDetailPage.putExtra("tv show id",tvShow.getId());
        goToDetailPage.putExtra("tv show cover",tvShow.getPosterPath());
        goToDetailPage.putExtra("tv show rate",tvShow.getVoteAverage());
        goToDetailPage.putExtra("tv show overview",tvShow.getOverview());
        startActivity(goToDetailPage);
    }
}