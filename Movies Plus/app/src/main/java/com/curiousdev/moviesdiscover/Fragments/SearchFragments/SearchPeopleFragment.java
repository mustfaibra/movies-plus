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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.curiousdev.moviesdiscover.Activities.PersonDetails;
import com.curiousdev.moviesdiscover.Adapters.PeopleSearchRecyclerAdapter;
import com.curiousdev.moviesdiscover.HelperClasses.VerticalLinearScrollListener;
import com.curiousdev.moviesdiscover.Models.Person;
import com.curiousdev.moviesdiscover.Models.People;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.R;
import com.curiousdev.moviesdiscover.ViewModels.PeopleViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

import static com.curiousdev.moviesdiscover.Functions.isNetworkAvailable;

public class SearchPeopleFragment extends Fragment implements View.OnClickListener, PeopleSearchRecyclerAdapter.PersonSelectedListener {
    private String movieName;
    //fragment instance
    static SearchPeopleFragment searchPeopleFragment;
    //debugging tag
    private static final String TAG = "SearchPeopleFragment";
    //Handler global in case we need it
    private Handler handler;
    //widgets
    private ProgressBar loadingMore;
    private Toolbar toolbar;
    //variables
    private int START_PAGE=1; //first page index that initiate the fragment
    private boolean isLoading=true;

    //Actors tab VARIABLES
    private List<Person> actorsResults;
    private boolean isActorsLastPage=false; //to check if its the last page or not
    private int ACTORS_RESULT_TOTAL_PAGE; //NUMBER OF PAGES AVAILABLE IN API
    private int ACTORS_RESULT_CURRENT_PAGE=START_PAGE;
    //retry snack bar
    private Snackbar snackbar;
    //a lists of filters info
    private List<Integer> yearsList;
    private String searchQuery;
    //containers
    private RelativeLayout parentContainer;
    private RecyclerView peopleRec;
    private LottieAnimationView lottieSearching;
    private RelativeLayout errorLayout;
    private RelativeLayout resultLayout;
    private CardView filterContainer;
    private LinearLayout noResultContainer;
    //LayoutManager
    private LinearLayoutManager linearLayout;
    //adapters
    private PeopleSearchRecyclerAdapter peopleResultAdapter;
    //buttons
    private Button applyFiltersBtn;
    //image button
    private Button showFiltersBtn;
    private Spinner yearsMenu;
    //Retrofit instance
    private Retrofit api;
    //Movies Provider instance
    private MoviesProvider moviesProvider;
    //initial variables to get recommendation
    private int currentMinYear;
    //view
    View view;
    //fragments host context
    static Context hostContext;
    //filter container toggler
    private boolean isFilterVisible=false;
    private String preQuery;
    private PeopleViewModel peopleViewModel;
    public static SearchPeopleFragment getPeopleFragmentInstance(Context context) {
        hostContext=context;
        if (searchPeopleFragment ==null)
            searchPeopleFragment = new SearchPeopleFragment();
        return searchPeopleFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //search query
        if (getArguments()!=null)
            searchQuery = getArguments().getString("search query");
        //the view model
        peopleViewModel= ViewModelProviders.of(this).get(PeopleViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_search_actors, container, false);
            initFragment();

            isLoading = true;
            Log.d(TAG, "viewModelUpdate: ");
            actorsResults = new ArrayList<>();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lottieSearching.playAnimation();
        if (isLoading){
            peopleViewModel.getPeople(searchQuery, ACTORS_RESULT_CURRENT_PAGE).observe(this, new Observer<People>() {
                @Override
                public void onChanged(People people) {
                    if (people == null) {
                        Log.d(TAG, "onChanged: received null,look like an error happened");
                        isLoading = false;
                        loadingMore.setVisibility(View.GONE);
                        lottieSearching.setVisibility(View.GONE);
                        lottieSearching.cancelAnimation();lottieSearching.cancelAnimation();
                        processError();
                    } else {
                        if (people.getTotalResults()==0){
                            noResultContainer.setVisibility(View.VISIBLE);
                        }
                        Log.d(TAG, "onChanged: reicieved a result");
                        preQuery=searchQuery;
                        isLoading = false;
                        loadingMore.setVisibility(View.GONE);
                        lottieSearching.setVisibility(View.GONE);
                        lottieSearching.cancelAnimation();
                        peopleRec.setVisibility(View.VISIBLE);
                        errorLayout.setVisibility(View.GONE);

                        Log.d(TAG, "onChanged: search results is " + people.getTotalResults());
                        ACTORS_RESULT_TOTAL_PAGE = people.getTotalPages();
                        isActorsLastPage = ACTORS_RESULT_CURRENT_PAGE >= ACTORS_RESULT_TOTAL_PAGE;//check if its the last page of the search
                        actorsResults = people.getResults();
                        peopleResultAdapter.addAll(actorsResults);
                        peopleResultAdapter.notifyDataSetChanged();
                        Log.d(TAG, "lottieSearching is visible?" + (lottieSearching.getVisibility() == View.VISIBLE) + " and total page is "
                                + ACTORS_RESULT_TOTAL_PAGE + " and is it the last page? " + isActorsLastPage);
                        if (ACTORS_RESULT_CURRENT_PAGE == START_PAGE) {
                            playAnimations();
                        }
                    }
                }

            });
        }

    }

    private void initFragment() {
        Log.d(TAG, "initFragment: ");
        resultLayout=view.findViewById(R.id.results_container);
        errorLayout=view.findViewById(R.id.error_layout);
        lottieSearching=view.findViewById(R.id.lottie_loading);
        filterContainer=view.findViewById(R.id.filter_options_container);
        noResultContainer=view.findViewById(R.id.no_result_container);
        //init handler
        handler=new Handler();
        //loading more progress bar
        loadingMore=view.findViewById(R.id.loading_more);
        //initiate buttons
        //show filtering movieResults options button
        showFiltersBtn=view.findViewById(R.id.filter_search_result);
        applyFiltersBtn=view.findViewById(R.id.apply_filtering);
        //filtering buttons
        yearsMenu=view.findViewById(R.id.years_menu);
        initateRecyclers();
    }

    private void initateRecyclers(){
        Log.d(TAG, "initateRecyclers: ");
        //initiate and assign the adapter to recycler
        peopleRec=view.findViewById(R.id.search_result_rec);
        peopleResultAdapter=new PeopleSearchRecyclerAdapter(hostContext);
        peopleResultAdapter.setOnPersonSelected(this);
        linearLayout=new LinearLayoutManager(hostContext,LinearLayoutManager.VERTICAL,false);
        peopleRec.setLayoutManager(linearLayout);
        peopleRec.addOnScrollListener(new VerticalLinearScrollListener(linearLayout) {
            @Override
            public void loadMore() {
                Log.d(TAG, "loadMore: ");
                ACTORS_RESULT_CURRENT_PAGE++;
                isLoading=true;
                loadingMore.setVisibility(View.VISIBLE);
                peopleViewModel.getPeople(searchQuery,ACTORS_RESULT_CURRENT_PAGE);
            }

            @Override
            public boolean isLastPage() {
                return isActorsLastPage;
            }

            @Override
            public int getTotalPage() {
                return ACTORS_RESULT_TOTAL_PAGE;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        peopleRec.setAdapter(peopleResultAdapter);
    }

    public void updateSearch(String newSearchQuery){
        Log.d(TAG, "updateSearch: ");
        searchQuery=newSearchQuery;
        ACTORS_RESULT_CURRENT_PAGE=1;
        peopleResultAdapter.clear();
        actorsResults.clear();
        loadingMore.setVisibility(View.GONE);
        noResultContainer.setVisibility(View.GONE);
        lottieSearching.setVisibility(View.VISIBLE);
        lottieSearching.playAnimation();
        isLoading=true;
        peopleViewModel.getPeople(searchQuery,ACTORS_RESULT_CURRENT_PAGE);
    }

    private void processError() {
        Log.d(TAG, "processError: ");
        if (ACTORS_RESULT_CURRENT_PAGE==1){
            Button retryBtn;
            peopleRec.setVisibility(View.GONE);
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
                peopleRec.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                noResultContainer.setVisibility(View.GONE);
                lottieSearching.setVisibility(View.VISIBLE);
                lottieSearching.playAnimation();
                isLoading=true;
                peopleViewModel.getPeople(searchQuery,ACTORS_RESULT_CURRENT_PAGE);
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
                        peopleViewModel.getPeople(searchQuery,ACTORS_RESULT_CURRENT_PAGE);
                    }).setActionTextColor(hostContext.getResources().getColor(R.color.yellow)).setDuration(3000);
            Snackbar.SnackbarLayout snackbarLayout=(Snackbar.SnackbarLayout) snackbar.getView();
            snackbarLayout.setPadding(0,0,0,0);
            snackbar.show();
        }
    }

    @Override
    public void onClick(View v) {
        int clickedViewId=v.getId();
        switch (clickedViewId){
            case R.id.retry_button:{
                updateSearch(searchQuery);
                break;
            }
        }
    }

//    private void applyCurrentFilters() {
//        Log.d(TAG, "applyCurrentFilters: year selected is "+yearsList.get(yearsMenu.getSelectedItemPosition()));
//
//        currentMinYear =yearsList.get(yearsMenu.getSelectedItemPosition());
//        filteringOptionsToggler();
//        loadResultsFirstPage();
//    }
//    private void filteringOptionsToggler() {
//        Log.d(TAG, "filteringOptionsToggler: it shown now");
//        if (isFilterVisible){
//            isFilterVisible=false;
//            filterContainer.setVisibility(View.GONE);
//            filterContainer.startAnimation(slideDown);
//        }
//        else{
//            isFilterVisible=true;
//            filterContainer.setVisibility(View.VISIBLE);
//            filterContainer.startAnimation(slideUp);
//        }
//    }
    private void playAnimations(){
        Log.d(TAG, "playAnimations: ");
        LayoutAnimationController layoutAnimationController= AnimationUtils.loadLayoutAnimation(hostContext,R.anim.home_recycler_layout_animation);
        peopleRec.setLayoutAnimation(layoutAnimationController);
    }


    @Override
    public void onPersonSelectd(Person person) {

        Intent toPersonDetails=new Intent(hostContext, PersonDetails.class);
        toPersonDetails.putExtra("person_id",person.getId());
        toPersonDetails.putExtra("person_name",person.getName());

//
        startActivity(toPersonDetails);
    }
}
