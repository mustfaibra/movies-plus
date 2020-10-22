package com.curiousdev.moviesdiscover.Fragments.SearchFragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.curiousdev.moviesdiscover.Activities.TvShowDetailActivity;
import com.curiousdev.moviesdiscover.Adapters.SeriesRecyclerAdapter;
import com.curiousdev.moviesdiscover.Models.Series;
import com.curiousdev.moviesdiscover.Models.SeriesItem;
import com.curiousdev.moviesdiscover.ViewModels.SearchSeriesViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.curiousdev.moviesdiscover.Activities.MovieDetailActivity;
import com.curiousdev.moviesdiscover.HelperClasses.GridScrollListenter;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

import static com.curiousdev.moviesdiscover.Functions.isNetworkAvailable;

public class SearchSeriesFragment extends Fragment implements View.OnClickListener, SeriesRecyclerAdapter.OnCardClickListener {
    //fragment instance
    static SearchSeriesFragment searchSeriesFragment;
    //debugging tag
    private static final String TAG = "SearchSeriesFragment";
    //handler
    private Handler handler;
    //widgets
    private ProgressBar loadingMore;
    private Toolbar toolbar;
    //variables
    private int START_PAGE=1; //first page index that initiate the fragment
    private boolean isLoading=true;

    //Actors tab VARIABLES
    private List<SeriesItem> seriesResults;
    private boolean isSeriesLastPage=false; //to check if its the last page or not
    private int SERIES_RESULT_TOTAL_PAGE; //NUMBER OF PAGES AVAILABLE IN API
    private int SERIES_RESULT_CURRENT_PAGE=START_PAGE;
    //retry snack bar
    private Snackbar snackbar;
    //a lists of filters info
    private List<Integer> yearsList;
    private String searchQuery;
    //containers
    private RelativeLayout parentContainer;
    private RecyclerView seriesRec;
    private LottieAnimationView lottieSearching;
    private RelativeLayout errorLayout;
    private RelativeLayout resultLayout;
    private CardView filterContainer;
    private LinearLayout noResultContainer;
    //LayoutManager
    private GridLayoutManager gridLayout;
    //adapters
    private SeriesRecyclerAdapter seriesResultAdapter;
    //buttons
    private Button applyFiltersBtn;
    //image button
    private Button showFiltersBtn;
    private Spinner yearsMenu;
    //Retrofit instance
    private Retrofit api;
    //Movies Provider instance
    private MoviesProvider seriesProvider;
    //initial variables to get recommendation
    private int currentMinYear;
    //animation
    private Animation slideDown,slideUp;
    //view
    View view;
    //fragments host context
    static Context hostContext;
    //filter container toggler
    private boolean isFilterVisible=false;
    private SearchSeriesViewModel searchSeriesViewModel;

    public static SearchSeriesFragment getSeriesFragmentInstance(Context context) {
        hostContext=context;

        if (searchSeriesFragment ==null)
            searchSeriesFragment = new SearchSeriesFragment();

        return searchSeriesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //search query
        if (getArguments()!=null)
            searchQuery = getArguments().getString("search query");
        //the view model
        searchSeriesViewModel = ViewModelProviders.of(this).get(SearchSeriesViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_series, container, false);
        initFragment();

        isLoading=true;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lottieSearching.playAnimation();
        if (isLoading){
            searchSeriesViewModel.getSeries(searchQuery,SERIES_RESULT_CURRENT_PAGE).observe(this, new Observer<Series>() {
                @Override
                public void onChanged(Series series) {
                    if (series == null) {
                        Log.d(TAG, "onChanged: received null,look like an error happened");
                        isLoading = false;
                        loadingMore.setVisibility(View.GONE);
                        lottieSearching.setVisibility(View.GONE);
                        lottieSearching.cancelAnimation();
                        processError();
                    } else {
                        if (series.getTotalResults()==0){
                            noResultContainer.setVisibility(View.VISIBLE);
                        }
                        isLoading = false;
                        loadingMore.setVisibility(View.GONE);
                        lottieSearching.setVisibility(View.GONE);
                        lottieSearching.cancelAnimation();
                        seriesRec.setVisibility(View.VISIBLE);
                        errorLayout.setVisibility(View.GONE);

                        Log.d(TAG, "onChanged: search results is " + series.getTotalResults());
                        SERIES_RESULT_TOTAL_PAGE = series.getTotalPages();
                        isSeriesLastPage = SERIES_RESULT_CURRENT_PAGE >= SERIES_RESULT_TOTAL_PAGE;//check if its the last page of the search
                        seriesResults = series.getResults();
                        seriesResultAdapter.addAll(seriesResults);
                        seriesResultAdapter.notifyDataSetChanged();
                        Log.d(TAG, "lottieSearching is visible?" + (lottieSearching.getVisibility() == View.VISIBLE )+" and total page is "
                                +SERIES_RESULT_TOTAL_PAGE +" and is it the last page? "+isSeriesLastPage);
                        if (SERIES_RESULT_CURRENT_PAGE==START_PAGE){
                            playAnimations();
                        }
                    }
                }

            });
        }
    }

    private void initFragment() {
        seriesResults=new ArrayList<>();
        Log.d(TAG, "initFragment: ");
        resultLayout=view.findViewById(R.id.results_container);
        errorLayout=view.findViewById(R.id.error_layout);
        lottieSearching=view.findViewById(R.id.lottie_loading);
        filterContainer=view.findViewById(R.id.filter_options_container);
        noResultContainer=view.findViewById(R.id.no_result_container);
        //init handler
        handler=new Handler();
        //animation
        slideDown=AnimationUtils.loadAnimation(hostContext,R.anim.slide_down);
        slideUp=AnimationUtils.loadAnimation(hostContext,R.anim.slide_up);
        //loading more progress bar
        loadingMore=view.findViewById(R.id.loading_more);
        //initiate buttons
        //show filtering serieResults options button
        showFiltersBtn=view.findViewById(R.id.filter_search_result);
        applyFiltersBtn=view.findViewById(R.id.apply_filtering);
        //filtering buttons
        yearsMenu=view.findViewById(R.id.years_menu);
        initateRecyclers();
    }

    private void initateRecyclers(){
        Log.d(TAG, "initateRecyclers: ");
        //initiate and assign the adapter to recycler
        seriesRec=view.findViewById(R.id.search_result_rec);
        seriesResultAdapter=new SeriesRecyclerAdapter(hostContext);
        seriesResultAdapter.setOnCardClickListener(this);
        gridLayout=(GridLayoutManager)seriesRec.getLayoutManager();
        seriesRec.addOnScrollListener(new GridScrollListenter(gridLayout) {
            @Override
            public void loadMoreMovies() {
                Log.d(TAG, "loadMore: ");
                SERIES_RESULT_CURRENT_PAGE++;
                isLoading=true;
                loadingMore.setVisibility(View.VISIBLE);
                searchSeriesViewModel.getSeries(searchQuery,SERIES_RESULT_CURRENT_PAGE);
            }

            @Override
            public boolean isLastPage() {
                return isSeriesLastPage;
            }

            @Override
            public int getTotalPagesCount() {
                return SERIES_RESULT_TOTAL_PAGE;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        seriesRec.setAdapter(seriesResultAdapter);
    }

    public void updateSearch(String newSearchQuery){
        Log.d(TAG, "updateSearch: ");
        searchQuery=newSearchQuery;
        SERIES_RESULT_CURRENT_PAGE=1;
        seriesResultAdapter.clear();
        seriesResults.clear();
        loadingMore.setVisibility(View.GONE);
        noResultContainer.setVisibility(View.GONE);
        lottieSearching.setVisibility(View.VISIBLE);
        lottieSearching.playAnimation();
        isLoading=true;
        searchSeriesViewModel.getSeries(searchQuery,SERIES_RESULT_CURRENT_PAGE);
    }

    private void processError() {
        Log.d(TAG, "processError: ");
        if (SERIES_RESULT_CURRENT_PAGE==1){
            Button retryBtn;
            seriesRec.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);

            TextView errorHeader=view.findViewById(R.id.error_header);
            TextView errorTxt=view.findViewById(R.id.error_txt);
            retryBtn=view.findViewById(R.id.retry_button);

            if(!isNetworkAvailable(hostContext)){
                errorHeader.setText(hostContext.getString(R.string.no_connection_header));
                errorTxt.setText(hostContext.getString(R.string.no_connection_txt));
            }
            else {
                errorHeader.setText(hostContext.getString(R.string.unknown_error_header));
                errorTxt.setText(hostContext.getString(R.string.unknown_error_txt));
            }
            retryBtn.setOnClickListener(v -> {
                seriesRec.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                noResultContainer.setVisibility(View.GONE);
                lottieSearching.setVisibility(View.VISIBLE);
                lottieSearching.playAnimation();
                isLoading=true;
                searchSeriesViewModel.getSeries(searchQuery,SERIES_RESULT_CURRENT_PAGE);
            });
        }
        else {
            loadingMore.setVisibility(View.GONE);
            loadingMore.setVisibility(View.GONE);
            //here we will process loading more failing
            snackbar=Snackbar.make(errorLayout,hostContext.getString(R.string.loading_more_failed),Snackbar.LENGTH_INDEFINITE)
                    .setAction(hostContext.getString(R.string.retry), v -> {
                        isLoading=true;
                        loadingMore.setVisibility(View.VISIBLE);
                        searchSeriesViewModel.getSeries(searchQuery,SERIES_RESULT_CURRENT_PAGE);
                    }).setActionTextColor(hostContext.getResources().getColor(R.color.yellow)).setDuration(3000);
            Snackbar.SnackbarLayout snackbarLayout=(Snackbar.SnackbarLayout) snackbar.getView();
            snackbarLayout.setPadding(0,0,0,0);
            snackbar.show();
        }
    }

    private void setFilterOptions() {
        yearsList=new ArrayList<>();
        //get and sort years
        int[] years=hostContext.getResources().getIntArray(R.array.years);
        yearsList=new ArrayList<>();
        for (int year:years) {
            yearsList.add(year);
        }
        ArrayAdapter<Integer> yearsAdapter=new ArrayAdapter<>(hostContext,android.R.layout.simple_spinner_dropdown_item,yearsList);
        yearsMenu.setAdapter(yearsAdapter);
    }

    @Override
    public void onClick(View v) {
        int clickedViewId=v.getId();
        switch (clickedViewId){
            case R.id.retry_button:{
                updateSearch(searchQuery);
                break;
            }
            case R.id.filter_search_result:{
                filteringOptionsToggler();
                break;
            }
            case R.id.apply_filtering:{
                applyCurrentFilters();
                break;
            }
        }
    }

    private void applyCurrentFilters() {
        Log.d(TAG, "applyCurrentFilters: year selected is "+yearsList.get(yearsMenu.getSelectedItemPosition()));

        currentMinYear =yearsList.get(yearsMenu.getSelectedItemPosition());
        filteringOptionsToggler();
        searchSeriesViewModel.getSeries(searchQuery,SERIES_RESULT_CURRENT_PAGE);
    }
    private void filteringOptionsToggler() {
        Log.d(TAG, "filteringOptionsToggler: it shown now");
        if (isFilterVisible){
            isFilterVisible=false;
            filterContainer.setVisibility(View.GONE);
            filterContainer.startAnimation(slideDown);
        }
        else{
            isFilterVisible=true;
            filterContainer.setVisibility(View.VISIBLE);
            filterContainer.startAnimation(slideUp);
        }
    }
    private void playAnimations(){
        Log.d(TAG, "playAnimations: ");
        LayoutAnimationController layoutAnimationController= AnimationUtils.loadLayoutAnimation(hostContext,R.anim.home_recycler_layout_animation);
        seriesRec.setLayoutAnimation(layoutAnimationController);
    }

    @Override
    public void onTvShowClick(SeriesItem tvShow) {
        Log.d(TAG, "onCardClick: ");
        Intent goToDetailPage=new Intent(hostContext, TvShowDetailActivity.class);
        goToDetailPage.putExtra("tv show name",tvShow.getName());
        goToDetailPage.putExtra("tv show id",tvShow.getId());
        goToDetailPage.putExtra("tv show cover",tvShow.getPosterPath());
        goToDetailPage.putExtra("tv show rate",tvShow.getVoteAverage());
        goToDetailPage.putExtra("tv show overview",tvShow.getOverview());
        startActivity(goToDetailPage);
    }
}
