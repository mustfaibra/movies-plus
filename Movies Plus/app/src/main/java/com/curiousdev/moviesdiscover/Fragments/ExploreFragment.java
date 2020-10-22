package com.curiousdev.moviesdiscover.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.curiousdev.moviesdiscover.Activities.FavGenres;
import com.curiousdev.moviesdiscover.Activities.MovieDetailActivity;
import com.curiousdev.moviesdiscover.Adapters.GenresRecyclerAdapter;
import com.curiousdev.moviesdiscover.Adapters.MoviesRecyclerAdapter;
import com.curiousdev.moviesdiscover.HelperClasses.GridScrollListenter;
import com.curiousdev.moviesdiscover.Models.Language;
import com.curiousdev.moviesdiscover.Models.Genre;
import com.curiousdev.moviesdiscover.Models.Movie;
import com.curiousdev.moviesdiscover.Models.MovieApi;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.Models.PreferenceManager;
import com.curiousdev.moviesdiscover.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.hostContext;
import static com.curiousdev.moviesdiscover.Functions.isNetworkAvailable;

public class ExploreFragment extends Fragment implements View.OnClickListener, MoviesRecyclerAdapter.OnCardClickListener, GenresRecyclerAdapter.onGenreClicked {
    //debugging tag
    private static final String TAG = "ExploreFragment";
    //Handler glabal
    private Handler handler;
    //widgets
    private ProgressBar loadingMore;
    //variables
    private int START_PAGE=1; //first page index that initiate the fragment
    private int RECOMMENDATION_TOTAL_PAGE; //TOTAL page
    private int RECOMMENDATION_CURRENT_PAGE=START_PAGE; //TOTAL page
    private boolean isLoading=false;
    private boolean isRecommendationLastPage=false;
    private List<Movie> recommendations;
    //retry snack bar
    private Snackbar snackbar;
    //a lists of filters info
    List<Genre> genres;
    List<Language> languages;
    List<String> languagesTitles;
    List<String> languagesShortcuts;
    List<Integer> selectedGenres;
    List<Integer> yearsList;
    List<Integer> ratesList;
    //containers
    private RelativeLayout parentContainer;
    private RecyclerView moviesRec;
    private RecyclerView genresRec;
    private LottieAnimationView lottieLoading;
    private SwipeRefreshLayout refreshSwiper;
    private RelativeLayout errorLayout;
    private RelativeLayout recommendLayout;
    private CardView filterContainer;
    private LinearLayout noResultContainer;
    //LayoutManager
    private GridLayoutManager gridLayout;
    private GridLayoutManager genreLayout;
    //adapters
    private MoviesRecyclerAdapter recommendationAdapter;
    //buttons
    private Button retryBtn,addGenresBtn,showFiltersBtn,applyFiltersBtn,cancelFilteringBtn;
    //filtering list items
        private CardView filterGenreBtn;
        private Spinner languagesMenu,yearsMenu,ratesMenu;
    //layout manage to get th position of scroll
    RecyclerView.LayoutManager layoutManager;
    //Retrofit instance
    private Retrofit api;
    //Movies Provider instance
    private MoviesProvider moviesProvider;
    //View of fragment
    private View view;
    //boolean to prevent page reolaoded
    boolean isReloaded=false;
    //boolean to check if fav genres is set or not
    private boolean isGenresSet=false;
    //height of the screen
    private int height=0;
    //a context of host activity
    private final Context context=hostContext;
    //initial variables to get recommendation
    private int currentMinYear;
    private int currentMinRate;
    private String currentLanguage;
    private StringBuilder currentGenres;

    private String deviceLang;
    //animation
    private Animation slideDown,slideUp;
    public static ExploreFragment exploreFragmentInstance;
    public static ExploreFragment getExploreFragmentInstance(){
        if(exploreFragmentInstance==null){
            exploreFragmentInstance=new ExploreFragment();
        }
        return exploreFragmentInstance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        height=getActivity().getWindowManager().getDefaultDisplay().getHeight();

        if (!PreferenceManager.getInstance(context).favGenresIsSet()){
            Log.d(TAG, "onViewCreated: setting fav genres");
            isGenresSet=false;
        }
        else {
            isGenresSet=true;
        }

        if (view==null){
            recommendations=new ArrayList<>();
            view = inflater.inflate(R.layout.fragment_explore, container, false);
            deviceLang=Locale.getDefault().getLanguage();
            Log.d(TAG, "onViewCreated: "+deviceLang);
            //iniate contaienrs
            parentContainer=view.findViewById(R.id.parent_container);
            errorLayout=view.findViewById(R.id.error_layout);
            lottieLoading=view.findViewById(R.id.lottie_loading);
            recommendLayout=view.findViewById(R.id.recommendations_container);
            filterContainer=view.findViewById(R.id.filter_options_container);
            moviesRec=view.findViewById(R.id.recommendation_recycler);
            genresRec=view.findViewById(R.id.filter_genres_recycler);
            refreshSwiper=view.findViewById(R.id.recommendation_swiper);
            noResultContainer=view.findViewById(R.id.no_result_container);
            initWidgets();

            recommendLayout.setVisibility(View.VISIBLE);
            initPage();
        }
        return view;
    }

    private void initPage(){
        //initiate the Retrofit api and movies provider
        api=MovieApi.getRetrofitInstance();
        moviesProvider=api.create(MoviesProvider.class);
        height=getActivity().getWindowManager().getDefaultDisplay().getHeight();
        selectedGenres=new ArrayList<>();
        //initate handler
        handler=new Handler();
        //initiate the animation
        slideDown=AnimationUtils.loadAnimation(context,R.anim.slide_down);
        slideUp=AnimationUtils.loadAnimation(context,R.anim.slide_up);
        Log.d(TAG, "onCreateView: height is "+height);
        //get filtering info first
        getSavedRecommendData();
        getAllGenresAvailable();
        getAllLanguages();
        initateRecyclers();

        //setting the event handler listener
        setEventHandlers();
        //progress bar
        loadingMore=view.findViewById(R.id.loading_more);
        //init the activity
        if (recommendations.size()==0){
            loadRecommendationFirstPage();
        }
        else {
            onLoadingEnd();
            showFiltersBtn.setEnabled(true);
            recommendationAdapter.addAll(recommendations);
        }
    }

    private void getSavedRecommendData() {
        //its just an example for now,later will be got from sqlite db
        selectedGenres=new ArrayList<>();
        selectedGenres.add(28);
        currentMinRate=6;
        currentMinYear=2015;
        currentLanguage="";
    }

    private void initWidgets() {
        Log.d(TAG, "initWidgets: ");
        //initiate buttons
        //button will appear in case you didnt add fav genres and get back to recommendation page
        addGenresBtn=view.findViewById(R.id.set_fav_genres_btn);
        //show filtering recommendations options button
        showFiltersBtn=view.findViewById(R.id.filter_recommendation);
        applyFiltersBtn=view.findViewById(R.id.apply_filtering);
        cancelFilteringBtn=view.findViewById(R.id.cancel_filtering);
        //retry button
        retryBtn=view.findViewById(R.id.retry_button);
        //filtering buttons
        filterGenreBtn=view.findViewById(R.id.filter_genre);
        yearsMenu=view.findViewById(R.id.years_menu);
        ratesMenu=view.findViewById(R.id.rate_menu);
        languagesMenu=view.findViewById(R.id.language_menu);
    }

    private void setEventHandlers() {
        retryBtn.setOnClickListener(this);
        //setting the scrolling listener to the recycler
        moviesRec.addOnScrollListener(new GridScrollListenter(gridLayout) {
            @Override
            public void loadMoreMovies() {
                isLoading=true;
                loadingMore.setVisibility(View.VISIBLE);
                RECOMMENDATION_CURRENT_PAGE++;
                loadRecommendationNextPage();
            }

            @Override
            public boolean isLoading() {
                Log.d(TAG, "isLoading? "+isLoading);
                return isLoading;
            }

            @Override
            public boolean isLastPage() {
                return isRecommendationLastPage;
            }

            @Override
            public int getTotalPagesCount() {
                Log.d(TAG, "getTotalPagesCount: ");
                return RECOMMENDATION_TOTAL_PAGE;
            }
        });
        //set the swipe listener
        refreshSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: refreshing");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateContents();
                    }
                },2000);

            }
        });

        //set swiper color
        refreshSwiper.setColorSchemeColors(
                context.getResources().getColor(R.color.yellow),
                context.getResources().getColor(R.color.black)
        );
        addGenresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, FavGenres.class));
            }
        });
        showFiltersBtn.setOnClickListener(this);
        applyFiltersBtn.setOnClickListener(this);
        filterGenreBtn.setOnClickListener(this);
        cancelFilteringBtn.setOnClickListener(this);
    }

    private void initateRecyclers(){
        Log.d(TAG, "initateRecyclers: ");
        //iniaite and assign the adapter to recycler
        recommendationAdapter=new MoviesRecyclerAdapter(context);
        gridLayout=(GridLayoutManager)moviesRec.getLayoutManager();
        moviesRec.setAdapter(recommendationAdapter);
        recommendationAdapter.setOnCardClickListener(this);
    }


    private void updateContents() {
        Log.d(TAG, "updateContents: ");
        //process the refreshing
        getAllLanguages();
        getAllGenresAvailable();
        recommendationAdapter.clear();
        loadRecommendationFirstPage();
    }
    private void loadRecommendationFirstPage() {
        Log.d(TAG, "loadRecommendationFirstPage: ");
        RECOMMENDATION_CURRENT_PAGE=1;
        recommendations.clear();
        recommendationAdapter.clear();
        onLoadingStart();
        getRecommendationMovieCall().enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                if (response.isSuccessful()){
                    //now the filtering option can be used
                    showFiltersBtn.setEnabled(true);
                    Log.d(TAG, "onResponse: we got some response");
                    recommendations=fetchRecommendationResult(response);
                    onLoadingEnd();
                    if (recommendations.size()==0){
                        noResultContainer.setVisibility(View.VISIBLE);
                    }
                    recommendationAdapter.addAll(recommendations);
                    recommendations.addAll(recommendationAdapter.getMovies());
                    playAnimations();
                    //check if its the last page
                    isRecommendationLastPage=RECOMMENDATION_TOTAL_PAGE<=RECOMMENDATION_CURRENT_PAGE; //its the last page only if current equal total page
                }
                else{
                    Log.d(TAG, "onResponse: operation didn't success ,status code recived is : "+response.code());
                }

            }

            @Override
            public void onFailure(@NotNull Call<Movies> call, @NotNull Throwable throwable) {
                Log.d(TAG, "onFailure: error accure :");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showErrorLayout(throwable);
                    }
                },2000);
            }
        });
    }
    private void loadRecommendationNextPage() {
        Log.d(TAG, "loadRecommendationNextPage: ");
        onLoadingStart();
        getRecommendationMovieCall().enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                if (response.code()==200){
                    onLoadingEnd();

                    recommendationAdapter.addAll(fetchRecommendationResult(response));
                    recommendations=recommendationAdapter.getMovies();
                    //check if its the last page
                    isRecommendationLastPage=RECOMMENDATION_CURRENT_PAGE>=RECOMMENDATION_TOTAL_PAGE;
                }
                else{
                    Log.d(TAG, "onResponse: operation didn't success ,status code recived is : "+response.code());
                }

            }

            @Override
            public void onFailure(Call<Movies> call, Throwable throwable) {
                Log.d(TAG, "onFailure: error accure :");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showErrorLayout(throwable);
                    }
                },1000);
            }
        });
    }
    private void onLoadingStart(){
        //in this method we are gonna show all temp widger in screen
        //like lottieLoading effect and boolean variables that control some stuff
        Log.d(TAG, "onLoadingStart: ");
        //now to disable refreshing
        refreshSwiper.setEnabled(false);
        refreshSwiper.setRefreshing(false);
        //showing our recycler,its neccessary only for the first page loading
        if (RECOMMENDATION_CURRENT_PAGE==1){
            lottieLoading.setVisibility(View.VISIBLE);
            lottieLoading.playAnimation();
            moviesRec.setVisibility(View.GONE);
        }
        errorLayout.setVisibility(View.GONE);
        noResultContainer.setVisibility(View.GONE);
        //indicate that we are start loading data
        isLoading=true;
    }
    private void onLoadingEnd(){
        //in this method we are gonna hide all temp widger in screen
        //like lottieLoading effect and boolean variables that control some stuff
        Log.d(TAG, "onDataFetched: ");
        //now to enable refreshing
        refreshSwiper.setEnabled(true);
        refreshSwiper.setRefreshing(false);
        //hidding lottieLoading effect and showing our recycler after getting data
        lottieLoading.setVisibility(View.GONE);
        lottieLoading.cancelAnimation();
        moviesRec.setVisibility(View.VISIBLE);
        //indicate that we are finish loading data
        loadingMore.setVisibility(View.GONE);
        isLoading=false;

    }
    private void showErrorLayout(Throwable throwable) {
        Log.d(TAG, "showErrorLayout: ");
        TextView errorHeader=view.findViewById(R.id.error_header);
        TextView errorTxt=view.findViewById(R.id.error_txt);
        isLoading=false;
        refreshSwiper.setEnabled(true);
        refreshSwiper.setRefreshing(false);

        if (RECOMMENDATION_CURRENT_PAGE>1){
            loadingMore.setVisibility(View.GONE);
            //here we will process loading more failing
            snackbar=Snackbar.make(parentContainer,context.getString(R.string.loading_more_failed),Snackbar.LENGTH_INDEFINITE)
                    .setAction(context.getString(R.string.retry), v -> {
                        isLoading=true;
                        loadingMore.setVisibility(View.VISIBLE);
                        loadRecommendationNextPage();
                    }).setActionTextColor(context.getResources().getColor(R.color.yellow)).setDuration(3000);
            Snackbar.SnackbarLayout snackbarLayout=(Snackbar.SnackbarLayout) snackbar.getView();
            snackbarLayout.setPadding(0,0,0,0);
            snackbar.show();
        }
        else {
            //here processing first page and refresh error
            errorLayout.setVisibility(View.VISIBLE);

            if (!isNetworkAvailable(context)){
                errorHeader.setText(context.getString(R.string.no_connection_header));
                errorTxt.setText(context.getString(R.string.no_connection_txt));
            }
            else if(throwable instanceof TimeoutException){
                errorHeader.setText(context.getString(R.string.server_timeout_error_header));
                errorTxt.setText(context.getString(R.string.server_timeout_error_txt));
            }
            else {
                errorHeader.setText(context.getString(R.string.unknown_error_header));
                errorTxt.setText(context.getString(R.string.unknown_error_txt));
            }
        }
    }
    @Override public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        int clickedViewId=v.getId();
        switch (clickedViewId){

            case R.id.retry_button:{
                updateContents();
                break;
            }
            case R.id.filter_recommendation:{
                filterContainer.setVisibility(View.VISIBLE);
                genresRec.setVisibility(View.GONE);
                filterContainer.startAnimation(slideDown);
                break;
            }
            case R.id.apply_filtering:{
                applyCurrentFilters();
                break;
            }
            case R.id.cancel_filtering:{
                filterContainer.setVisibility(View.GONE);
                filterContainer.startAnimation(slideUp);
                break;
            }
            case R.id.filter_genre:{
                showGenres();
                break;
            }


        }
    }
    private void applyCurrentFilters() {
        Log.d(TAG, "applyCurrentFilters: rate selected is "+ratesList.get(ratesMenu.getSelectedItemPosition()));
        Log.d(TAG, "applyCurrentFilters: year selected is "+yearsList.get(yearsMenu.getSelectedItemPosition()));
        Log.d(TAG, "applyCurrentFilters: language selected is "+languagesShortcuts.get(languagesMenu.getSelectedItemPosition()));
        for (int i = 0; i < selectedGenres.size(); i++) {
            for (int j = 0; j <genres.size() ; j++) {
                if (genres.get(j).getGenreId()==selectedGenres.get(i)){
                    Log.d(TAG, "applyCurrentFilters: genre selected : "+genres.get(j).getGenreName()+" and id is "+genres.get(j).getGenreId());
                }
            }
        }
        currentMinYear =yearsList.get(yearsMenu.getSelectedItemPosition());
        currentMinRate=ratesList.get(ratesMenu.getSelectedItemPosition());
        //checking the language ,if its selected as no_language option then we make it default to any language
        currentLanguage=languagesShortcuts.get(languagesMenu.getSelectedItemPosition());
        if(currentLanguage.equalsIgnoreCase("xx")){
            currentLanguage="";
        }
        cancelFilteringBtn.performClick();
        loadRecommendationFirstPage();
    }

    private void showGenres(){
        Log.d(TAG, "showGenres: ");
        int visibilty=genresRec.getVisibility();
        genresRec.setVisibility((visibilty==0)?View.GONE:View.VISIBLE);
    }
    private void buildGenreRec(){
        genreLayout=new GridLayoutManager(context,3,GridLayoutManager.VERTICAL,false);
        genresRec.setLayoutManager(genreLayout);
        GenresRecyclerAdapter genresdapter = new GenresRecyclerAdapter(context, genres, height);
        genresRec.setAdapter(genresdapter);
        genresdapter.setOnGenreClicked(this);
    }
    private void buildMenus(){
        //languages spinner
        ArrayAdapter<String> langAdapter=new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,languagesTitles);
        languagesMenu.setAdapter(langAdapter);
        //get and sort years
        int[] years=context.getResources().getIntArray(R.array.years);
        yearsList=new ArrayList<>();
        for (int year:years) {
            yearsList.add(year);
        }
        ArrayAdapter<Integer> yearsAdapter=new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,yearsList);
        yearsMenu.setAdapter(yearsAdapter);
        //rate menu
        int[] rates=context.getResources().getIntArray(R.array.rates);
        ratesList=new ArrayList<>();
        for (int rate:rates) {
            ratesList.add(rate);
        }
        ArrayAdapter<Integer> ratesAdapter=new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,ratesList);
        ratesMenu.setAdapter(ratesAdapter);
    }
    @Override public void onMovieClick(Movie movie) {
        Log.d(TAG, "onCardClick: ");
        Intent goToDetailPage=new Intent(context, MovieDetailActivity.class);
        goToDetailPage.putExtra("movie name",movie.getTitle());
        goToDetailPage.putExtra("movie id",movie.getId());
        goToDetailPage.putExtra("movie cover",movie.getPosterPath());
        goToDetailPage.putExtra("movie rate",movie.getVoteAverage());
        goToDetailPage.putExtra("movie overview",movie.getOverview());
        startActivity(goToDetailPage);
    }
    //all methods that are unlikely to edit much are placed here...
    private Call<Movies> getRecommendationMovieCall(){
        Log.d(TAG, "getRecommendationMovieCall: ");
        currentGenres=new StringBuilder();
        if (selectedGenres.size()>0){
            currentGenres.append(selectedGenres.get(0));
            //we start for loop from one cause we already take index one as init
            for (int i = 1; i <selectedGenres.size() ; i++) {
                currentGenres.append(",");
                currentGenres.append(selectedGenres.get(i));
            }
        }
        Log.d(TAG, "getRecommendationMovieCall: genres is "+currentGenres.toString());
        Log.d(TAG, "getRecommendationMovieCall: api key is "+context.getString(R.string.tmdb_api)+" and the page num is "+RECOMMENDATION_CURRENT_PAGE);
        return

                moviesProvider.getRecommendations(
                        context.getString(R.string.tmdb_api),
                        currentLanguage,
                        currentGenres.toString(),
                        false,
                        currentMinYear,
                        currentMinRate,
                        deviceLang,
                        RECOMMENDATION_CURRENT_PAGE
                );
    }
    private List<Movie> fetchRecommendationResult(Response<Movies> popularResponse){
        Log.d(TAG, "fetchRecommendationResult: ");
        Movies movies =popularResponse.body();
        RECOMMENDATION_TOTAL_PAGE= movies.getTotalPages();
        Log.d(TAG, "fetchRecommendationResult: total page is "+RECOMMENDATION_TOTAL_PAGE);
        return movies.getMovies();
    }
    private void playAnimations(){
        LayoutAnimationController layoutAnimationController=AnimationUtils.loadLayoutAnimation(context,R.anim.home_recycler_layout_animation);
        moviesRec.setLayoutAnimation(layoutAnimationController);
    }
    private void getAllGenresAvailable() {
        Retrofit retrofit= MovieApi.getRetrofitInstance();
        MoviesProvider moviesProvider=retrofit.create(MoviesProvider.class);
        genres=new ArrayList<>();
        Call<Genre> genresCall=moviesProvider.getGenres(context.getString(R.string.tmdb_api),deviceLang);
        genresCall.enqueue(new Callback<Genre>() {
            @Override
            public void onResponse(Call<Genre> call, Response<Genre> response) {
                Genre genreInstance=response.body();
                genres=genreInstance.getGenres();
                buildGenreRec();
            }

            @Override
            public void onFailure(Call<Genre> call, Throwable throwable) {
                Log.d(TAG, "onFailure: fetching genres failed");
                if (isNetworkAvailable(hostContext)){
                    getAllGenresAvailable();
                }
            }
        });
    }
    private void getAllLanguages(){
        Retrofit retrofit= MovieApi.getRetrofitInstance();
        MoviesProvider moviesProvider=retrofit.create(MoviesProvider.class);
        languages=new ArrayList<>();
        languagesTitles=new ArrayList<>();
        languagesShortcuts=new ArrayList<>();
        Call<List<Language>> countriesCall=moviesProvider.getLanguages(context.getString(R.string.tmdb_api));
        countriesCall.enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(Call<List<Language>> call, Response<List<Language>> response) {
                Log.d(TAG, "onResponse: got the countreis");
                languages=response.body();
                for (Language language:languages) {
                    languagesTitles.add(language.getEnglishName());
                    languagesShortcuts.add(language.getShortcut());
                    if(genres.size()>0){
                        buildMenus();
                    }
                    else{
                        getAllGenresAvailable();
                        buildMenus();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Language>> call, Throwable throwable) {
                Log.d(TAG, "onFailure: fetching countries failed");
                if (isNetworkAvailable(hostContext)){
                    getAllLanguages();
                }
            }
        });
    }
    @Override public void onGenreSelected(List<Integer> genresSelected) {
        Log.d(TAG, "onGenreSelected: ");

        this.selectedGenres=genresSelected;
    }
}

