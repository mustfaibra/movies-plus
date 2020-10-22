package com.curiousdev.moviesdiscover.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.curiousdev.moviesdiscover.Adapters.MoviesRecyclerAdapter;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.curiousdev.moviesdiscover.HelperClasses.GridScrollListenter;
import com.curiousdev.moviesdiscover.Models.Movie;
import com.curiousdev.moviesdiscover.Models.MovieApi;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.Activities.MovieDetailActivity;
import com.curiousdev.moviesdiscover.R;
import com.curiousdev.moviesdiscover.ViewModels.MoviesViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.hostContext;
import static com.curiousdev.moviesdiscover.Functions.isNetworkAvailable;

public class TempFragmentHome extends Fragment implements View.OnClickListener, MoviesRecyclerAdapter.OnCardClickListener {
    //ads
    AdView adView;
    //debugging tag
    private static final String TAG = "FragmentHome";
    //Handler glabal
    private Handler handler;
    //widgets
    private TextView newTab,popularTab,upcomingTab,currentTab,oldCurrentTab;
    private ProgressBar loadingMore;
    //variables
    private ArrayList<TextView> tabs;
    private int START_PAGE=1; //first page index that initiate the fragment

    //POPULAR TAPS VARIABLES
    private boolean isLoading=false; //to check new movies loading
    private boolean isPopularLastPage=false; //to check if its the last page or not
    private int POPULAR_TOTAL_PAGE; //NUMBER OF PAGES AVAILABLE IN API
    private int POPULAR_CURRENT_PAGE=START_PAGE;
    //upcoming TAPS VARIABLES
    private boolean isupcomingLastPage=false; //to check if its the last page or not
    private int UPCOMING_TOTAL_PAGE; //NUMBER OF PAGES AVAILABLE IN API
    private int UPCOMING_CURRENT_PAGE=START_PAGE;
    //new movies TAPS VARIABLES
    private boolean isnewMoviesLastPage=false; //to check if its the last page or not
    private int NEW_TOTAL_PAGE; //NUMBER OF PAGES AVAILABLE IN API
    private int NEW_CURRENT_PAGE=START_PAGE;

    //containers
    private RelativeLayout parentContainer;
    private RecyclerView moviesRec;
    private LottieAnimationView lottieLoading;
    private SwipeRefreshLayout refreshSwiper;
    private RelativeLayout errorLayout;
    //Layout
    private GridLayoutManager gridLayout;
    //adapters
    private MoviesRecyclerAdapter popularAdapter;
    private MoviesRecyclerAdapter upcomingAdapter;
    private MoviesRecyclerAdapter newAdapter;

    //layout manage to get th position of scroll
    RecyclerView.LayoutManager layoutManager;
    //Retrofit instance
    private Retrofit api;
    //Movies Provider instance
    private MoviesProvider moviesProvider;
    //View of fragment
    private View view;
    //contant context that use a static context variable form host activity
    private final Context context=hostContext;
    //last call to the api so that we can cancel it any time :)
    private Call lastCall;

    //view model
    private MoviesViewModel moviesViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moviesViewModel= ViewModelProviders.of(this).get(MoviesViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view==null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);

            //iniate containers
            parentContainer = view.findViewById(R.id.parent_container);
            errorLayout = view.findViewById(R.id.error_layout);
            lottieLoading = view.findViewById(R.id.lottie_loading);
            moviesRec = view.findViewById(R.id.home_recycler);
            refreshSwiper = view.findViewById(R.id.home_swiper);
            refreshSwiper.setEnabled(false);


            //initate handler
            handler = new Handler();
            //getting height of the screen
            int height = getActivity().getWindowManager().getDefaultDisplay().getHeight();
            Log.d(TAG, "onCreateView: height is " + height);
            //iniaite and assign the adapter to recycler
            popularAdapter = new MoviesRecyclerAdapter(context);
            popularAdapter.setOnCardClickListener(this);//catch the click on card
            upcomingAdapter = new MoviesRecyclerAdapter(context);
            upcomingAdapter.setOnCardClickListener(this);//catch the click on card
            newAdapter = new MoviesRecyclerAdapter(context);
            newAdapter.setOnCardClickListener(this);//catch the click on card
            moviesRec.setAdapter(popularAdapter);


            //iniate and set layout manager for the recycler
            int width=checkConfigration();
            gridLayout = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
            moviesRec.setLayoutManager(gridLayout);

            //init activity with popualr tap
            currentTab = popularTab;
            oldCurrentTab = popularTab;

            //retry button and event handler
            //retry button on error
            Button retryBtn = view.findViewById(R.id.retry_button);
            retryBtn.setOnClickListener(this);
            //initiate the Retrofit api and movies provider
            api = MovieApi.getRetrofitInstance();
            moviesProvider = api.create(MoviesProvider.class);
            //popular movie instance to get total page count
            Movies moviesInstance = new Movies();

            //set the swipe listener
            refreshSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.d(TAG, "onRefresh: refreshing " + currentTab.getText());
                    updateTapContents(currentTab);
                }
            });

            //set swiper color
            refreshSwiper.setColorSchemeColors(
                    getResources().getColor(R.color.yellow),
                    getResources().getColor(R.color.black)
            );

            //progress bar
            loadingMore = view.findViewById(R.id.loading_more);
            //set top tabs actions
            newTab = view.findViewById(R.id.new_tap);
            popularTab = view.findViewById(R.id.popular_tap);
            upcomingTab = view.findViewById(R.id.upcoming_tap);
            tabs = new ArrayList<>();
            tabs.add(newTab);
            tabs.add(popularTab);
            tabs.add(upcomingTab);
            newTab.setOnClickListener(this);
            popularTab.setOnClickListener(this);
            upcomingTab.setOnClickListener(this);
            currentTab = popularTab;

            Log.d(TAG, "onViewCreated: before loading the first page");

            //init the page with current tab which is popular tab currently
            updateTapsLooks();

            //setting the scrolling listener to the recycler
            moviesRec.addOnScrollListener(new GridScrollListenter(gridLayout) {
                @Override
                public void loadMoreMovies() {
                    isLoading = true;
                    loadingMore.setVisibility(View.VISIBLE);
                    Log.d(TAG, "loadMoreMovies: current tab is " + currentTab.getText().toString());
                    if (currentTab == popularTab) {
                        POPULAR_CURRENT_PAGE++;
                        loadPopular(POPULAR_CURRENT_PAGE);
                    } else if (currentTab == upcomingTab) {
                        UPCOMING_CURRENT_PAGE++;
                        loadUpcoming(UPCOMING_CURRENT_PAGE);
                    } else {
                        //for new tab
                        NEW_CURRENT_PAGE++;
                        loadNew(NEW_CURRENT_PAGE);
                    }
                }

                @Override
                public boolean isLoading() {
                    Log.d(TAG, "isLoading? " + isLoading);
                    return isLoading;
                }

                @Override
                public boolean isLastPage() {
                    Log.d(TAG, "isLastPage: ?" + isPopularLastPage);
                    if (currentTab == popularTab) {
                        return isPopularLastPage;
                    } else if (currentTab == upcomingTab) {
                        return isupcomingLastPage;
                    } else {
                        //for new tab
                        return isnewMoviesLastPage;
                    }
                }

                @Override
                public int getTotalPagesCount() {
                    Log.d(TAG, "getTotalPagesCount: ");
                    if (currentTab == popularTab) {
                        return POPULAR_TOTAL_PAGE;
                    } else if (currentTab == upcomingTab) {
                        return UPCOMING_TOTAL_PAGE;
                    } else {
                        //for new tab
                        return NEW_TOTAL_PAGE;
                    }
                }
            });
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void playAnimations(){
        LayoutAnimationController layoutAnimationController=AnimationUtils.loadLayoutAnimation(hostContext,R.anim.home_recycler_layout_animation);
        moviesRec.setLayoutAnimation(layoutAnimationController);

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void updateTapsLooks(){
        loadingMore.setVisibility(View.GONE);
        Log.d(TAG, "updateTapsLooks: ");
        for (int i = 0; i <3 ; i++) {
            tabs.get(i).setTextColor(getResources().getColor(R.color.med_gray));
            tabs.get(i).setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.tab_text_size));
        }
        currentTab.setTextColor(Color.WHITE);

        //now time to change to the current active tab adapter 
        showActiveAdapter();
    }

    private void showActiveAdapter() {
        Log.d(TAG, "showActiveAdapter: ");

        if (currentTab == popularTab) {
            moviesRec.swapAdapter(popularAdapter,true);
            if (popularAdapter.isEmpty()){
                updateTapContents(currentTab);
            }
//            layoutManager.scrollToPosition(moviesRec.getChildAdapterPosition(moviesRec.getChildAt()));
        }

        if (currentTab == upcomingTab) {
            moviesRec.swapAdapter(upcomingAdapter,true);
            if (upcomingAdapter.isEmpty()){
                updateTapContents(currentTab);
            }

        }

        if (currentTab == newTab) {
            moviesRec.swapAdapter(newAdapter,true);
            if (newAdapter.isEmpty()){
                updateTapContents(currentTab);
            }

        }

    }

    private void updateTapContents(TextView tabToUpdate) {
        lottieLoading.setVisibility(View.VISIBLE);
        lottieLoading.playAnimation();
        //process the refreshing
        Log.d(TAG, "updateTapContents: tabToUpdate is "+tabToUpdate.getText());
        if (tabToUpdate==popularTab){
            Log.d(TAG, "updateTapContents: current is popular tab");
            popularAdapter.clear();
            POPULAR_CURRENT_PAGE=START_PAGE;
            loadPopular(POPULAR_CURRENT_PAGE);
        }
        if (tabToUpdate==upcomingTab){
            Log.d(TAG, "updateTapContents: current is upcoming tab");
            upcomingAdapter.clear();
            UPCOMING_CURRENT_PAGE=START_PAGE;
            loadUpcoming(UPCOMING_CURRENT_PAGE);
        }
        if (tabToUpdate==newTab){
            Log.d(TAG, "updateTapContents: current is new movies tab");
            newAdapter.clear();
            NEW_CURRENT_PAGE=START_PAGE;
            loadNew(NEW_CURRENT_PAGE);
        }
    }

    private void loadPopular(int page){
        onLoadingStart();
        moviesViewModel.getPopularMovies(page).observe(this,movies -> {
            if (movies!=null){

                onLoadingEnd();
                if (page==1){
                    //secondly we play animation
                    playAnimations();
                    POPULAR_TOTAL_PAGE=movies.getTotalPages();
                }
                popularAdapter.addAll(movies.getMovies());
                //check if its the last page
                isPopularLastPage=POPULAR_TOTAL_PAGE<=POPULAR_CURRENT_PAGE; //its the last page only if current equal total page
            }
            else {
                handler.postDelayed(() -> showErrorLayout(),2000);
            }
        });
    }

    private void loadUpcoming(int page){
        onLoadingStart();
        moviesViewModel.getUpComingMovies(page).observe(this,movies -> {
            if (movies!=null){
                onLoadingEnd();
                if (page==1){
                    UPCOMING_TOTAL_PAGE=movies.getTotalPages();
                    //secondly we play animation
                    playAnimations();
                }
                upcomingAdapter.addAll(movies.getMovies());
                //check if its the last page
                isupcomingLastPage=UPCOMING_TOTAL_PAGE<=UPCOMING_CURRENT_PAGE; //its the last page only if current equal total page
            }
            else {
                handler.postDelayed(() -> showErrorLayout(),2000);
            }
        });
    }

    private void loadNew(int page){
        onLoadingStart();
        moviesViewModel.getNewMovies(page).observe(this,movies -> {
            if (movies!=null){
                //setting the recycler's animation layout
                onLoadingEnd();
                if (page==1){
                    NEW_TOTAL_PAGE=movies.getTotalPages();
                    //secondly we play animation
                    playAnimations();
                }
                newAdapter.addAll(movies.getMovies());
                //check if its the last page
                isnewMoviesLastPage=NEW_TOTAL_PAGE<=NEW_CURRENT_PAGE; //its the last page only if current equal total page
            }
            else {
                handler.postDelayed(() -> showErrorLayout(),2000);
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
        moviesRec.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
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

    private void showErrorLayout() {
        Log.d(TAG, "showErrorLayout: ");
        // first we save the old tab that the error happened in
        oldCurrentTab=currentTab;
        TextView errorHeader=view.findViewById(R.id.error_header);
        TextView errorTxt=view.findViewById(R.id.error_txt);
        isLoading=false;
        refreshSwiper.setEnabled(true);
        refreshSwiper.setRefreshing(false);
        loadingMore.setVisibility(View.GONE);
        if (POPULAR_CURRENT_PAGE>1&&currentTab==popularTab&& popularAdapter.getMovies().size()>0
                ||UPCOMING_CURRENT_PAGE>1&&currentTab==upcomingTab&& upcomingAdapter.getMovies().size()>0
                ||NEW_CURRENT_PAGE>1&&currentTab==newTab&& newAdapter.getMovies().size()>0){
            //here we will process loading more failing
            //retry snack bar
            Snackbar snackbar = Snackbar.make(parentContainer, hostContext.getString(R.string.loading_more_failed), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.retry), v -> {
                        isLoading = true;
                        loadingMore.setVisibility(View.VISIBLE);
                        if (currentTab == popularTab) {
                            loadPopular(POPULAR_CURRENT_PAGE);
                        } else if (currentTab == upcomingTab) {
                            loadUpcoming(UPCOMING_CURRENT_PAGE);
                        } else if (currentTab == newTab) {
                            loadNew(NEW_CURRENT_PAGE);
                        }
                    }).setActionTextColor(hostContext.getResources().getColor(R.color.yellow)).setDuration(3000);
            Snackbar.SnackbarLayout snackbarLayout=(Snackbar.SnackbarLayout) snackbar.getView();
            snackbarLayout.setPadding(0,0,0,0);
            snackbar.show();
        }
        else {
            //here processing first page and refresh error
            errorLayout.setVisibility(View.VISIBLE);

            /*it make a fatal exception leading app to crash
            so in order to fix this problem i made a static context in the host activity use it in
            the fragment instead of getActivity() and getContext()
            */
            if (!isNetworkAvailable(hostContext)){
                errorHeader.setText(hostContext.getString(R.string.no_connection_header));
                errorTxt.setText(hostContext.getString(R.string.no_connection_txt));
            }
            else {
                errorHeader.setText(hostContext.getString(R.string.unknown_error_header));
                errorTxt.setText(hostContext.getString(R.string.unknown_error_txt));
            }
        }
    }
    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        int clickedViewId=v.getId();
        if (clickedViewId==R.id.retry_button){
            updateTapContents(oldCurrentTab);
        }
        else {
            lottieLoading.setVisibility(View.GONE);
            lottieLoading.cancelAnimation();
            currentTab=view.findViewById(clickedViewId);
            updateTapsLooks();
        }
    }

    @Override
    public void onMovieClick(Movie clickedMovie) {
        Log.d(TAG, "onCardClick: ");
        List<Movie> movies=new ArrayList<>();
        if (currentTab==popularTab){
            movies=popularAdapter.getMovies();
        }
        else if (currentTab==upcomingTab){
            movies=upcomingAdapter.getMovies();
        }
        else if (currentTab==newTab){
            movies=newAdapter.getMovies();
        }

        Movie movie=clickedMovie;
        Intent goToDetailPage=new Intent(context, MovieDetailActivity.class);
        goToDetailPage.putExtra("movie name",movie.getTitle());
        goToDetailPage.putExtra("movie id",movie.getId());
        goToDetailPage.putExtra("movie cover",movie.getPosterPath());
        goToDetailPage.putExtra("movie rate",movie.getVoteAverage());
        goToDetailPage.putExtra("movie overview",movie.getOverview());
        startActivity(goToDetailPage);
    }

    private int checkConfigration(){
        int width=0;
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            width=getResources().getConfiguration().screenWidthDp;
        }
        return width;
    }
}
