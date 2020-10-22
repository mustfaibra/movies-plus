package com.curiousdev.moviesdiscover.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curiousdev.moviesdiscover.Adapters.RecentSearchRecyclerAdapter;
import com.curiousdev.moviesdiscover.Fragments.SearchFragments.SearchMoviesFragment;
import com.curiousdev.moviesdiscover.Fragments.SearchFragments.SearchPeopleFragment;
import com.curiousdev.moviesdiscover.Fragments.SearchFragments.SearchSeriesFragment;
import com.curiousdev.moviesdiscover.Models.SavedSearch;
import com.curiousdev.moviesdiscover.R;
import com.curiousdev.moviesdiscover.ViewModels.SearchViewModel;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, RecentSearchRecyclerAdapter.RecentSearch {
    //ads
    AdView adView;
    //debugging tag
    private static final String TAG = "SearchActivity";
    //toolbar
    private Toolbar toolbar;
    private String searchQuery;
    //containers
    private RelativeLayout searchContainer;
    private RelativeLayout recentSearchContainer;
    //widget
    private Spinner yearsMenu;
    private int currentMinYear;
    //search view
    private SearchView searchView;
    //search sub fragments
    private SearchSeriesFragment searchSeriesFragment;
    private SearchMoviesFragment searchMoviesFragment;
    private SearchPeopleFragment searchPeopleFragment;
    private Fragment currentFragment;
    //fragment manager and transactionn
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    //Textviews as tabs
    private TextView tvShowsTab,moviesTab,peopleTab,noRecentSearches;
    private List<TextView> tabs;
    //views as underlines
    View tvShowsTabUnderLine,moviesTabUnderLine,peopleTabUnderline;
    List<View> underLines;
    List<String> searches;
    //rec and adapter and layoutManger
    RecyclerView recentSearchRec;
    RecentSearchRecyclerAdapter adapter;
    LinearLayoutManager layoutManager;
    private boolean isPeopleFragmentCreated=false;
    private boolean isMoviesFragmentCreated=false;
    //animation
    Animation recentSearchSlideIn,toolbarSlideIn;

    //viewModel
    SearchViewModel searchViewModel;
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: saving state");
        if (currentFragment== searchSeriesFragment){
            outState.putString("current fragment","series");
        }
        if (currentFragment== searchMoviesFragment){
            outState.putString("current fragment","movies");
        }
        if (currentFragment== searchPeopleFragment){
            outState.putString("current fragment","people");
        }
        //and also putting the last searched query
        outState.putString("search query",searchQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: ");
        
        String currentFragmentStr=savedInstanceState.getString("current fragment");
        //getting the last searched query
        searchQuery=savedInstanceState.getString("search query");
        initFragments();
        searchContainer.setVisibility(View.VISIBLE);

        if (currentFragmentStr.equalsIgnoreCase("series")){
            Log.d(TAG, "onRestoreInstanceState: back to series fragment");
            fragmentController(searchSeriesFragment);
        }
        else if (currentFragmentStr.equalsIgnoreCase("movies")){
            Log.d(TAG, "onRestoreInstanceState: back to movies fragment");
            fragmentController(searchMoviesFragment);
        }
        else if (currentFragmentStr.equalsIgnoreCase("people")){
            Log.d(TAG, "onRestoreInstanceState: back to people fragment");
            fragmentController(searchPeopleFragment);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchViewModel= ViewModelProviders.of(this).get(SearchViewModel.class);

        //iniate contaienrs
        fragmentManager=getSupportFragmentManager();
        searchContainer=findViewById(R.id.search_container);
        recentSearchContainer=findViewById(R.id.recent_search_container);
        //animation
        recentSearchSlideIn= AnimationUtils.loadAnimation(this,R.anim.recent_search_slide_in);
        toolbarSlideIn= AnimationUtils.loadAnimation(this,R.anim.toolbar_slide_in);
//        initiate widgets
        initWidgets();
        setEventHandler();
        setToolbar();
        searchViewModel.getRecentSearch().observe(this, new Observer<List<SavedSearch>>() {
            @Override
            public void onChanged(List<SavedSearch> savedSearches) {
                recentSearchContainer.startAnimation(recentSearchSlideIn);
                setRecentSearchRec(savedSearches);
            }
        });

    }

    private void setRecentSearchRec(List<SavedSearch> savedSearches) {
        if(savedSearches!=null){
            searches=new ArrayList<>();
            for (SavedSearch savedSearch:savedSearches){
                searches.add(savedSearch.getQuery());
            }
            noRecentSearches.setVisibility(View.GONE);
            recentSearchRec.setVisibility(View.VISIBLE);
            adapter=new RecentSearchRecyclerAdapter(savedSearches,this);
            layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
            recentSearchRec.setLayoutManager(layoutManager);
            recentSearchRec.setAdapter(adapter);
            adapter.setOnRecentSearchClicked(this);
        }
        else{
            noRecentSearches.setVisibility(View.VISIBLE);
        }
        
        
    }

    private void initWidgets() {
        //tabs
        tvShowsTab=findViewById(R.id.series_tab);
        moviesTab=findViewById(R.id.movies_tab);
        peopleTab=findViewById(R.id.actors_tab);
        noRecentSearches=findViewById(R.id.no_recent_search_txt);
        //listveiw
        recentSearchRec=findViewById(R.id.recent_search_rec);
        tabs=new ArrayList<>();
        tabs.add(tvShowsTab);
        tabs.add(moviesTab);
        tabs.add(peopleTab);
        //views underlines
        tvShowsTabUnderLine=findViewById(R.id.series_tab_underline);
        moviesTabUnderLine=findViewById(R.id.movies_tab_underline);
        peopleTabUnderline=findViewById(R.id.actors_tab_underline);
        underLines=new ArrayList<>();
        underLines.add(tvShowsTabUnderLine);
        underLines.add(moviesTabUnderLine);
        underLines.add(peopleTabUnderline);
    }

    private void setEventHandler(){
        tvShowsTab.setOnClickListener(this);
        moviesTab.setOnClickListener(this);
        peopleTab.setOnClickListener(this);
    }

    private void initFragments() {
        searchSeriesFragment = SearchSeriesFragment.getSeriesFragmentInstance(getApplicationContext());
        searchMoviesFragment = SearchMoviesFragment.getMoviesFragmentInstance(getApplicationContext());
        searchPeopleFragment = SearchPeopleFragment.getPeopleFragmentInstance(getApplicationContext());
    }

    private void updateTapsLooks(){
        Log.d(TAG, "updateTapsLooks: ");
        for (int i = 0; i <tabs.size() ; i++) {
            tabs.get(i).setTextColor(getResources().getColor(R.color.med_gray));
            underLines.get(i).setVisibility(View.INVISIBLE);
        }
        if (currentFragment== searchSeriesFragment){
            tvShowsTab.setTextColor(getResources().getColor(R.color.white));
            underLines.get(0).setVisibility(View.VISIBLE);
        }
        else if (currentFragment== searchMoviesFragment){
            moviesTab.setTextColor(getResources().getColor(R.color.white));
            underLines.get(1).setVisibility(View.VISIBLE);
        }
        else if (currentFragment== searchPeopleFragment){
            peopleTab.setTextColor(getResources().getColor(R.color.white));
            underLines.get(2).setVisibility(View.VISIBLE);
        }
    }

    private void setToolbar() {
        toolbar=findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.startAnimation(toolbarSlideIn);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.search_toolbar_items,menu);
        searchView=(SearchView)menu.findItem(R.id.search_view_bar).getActionView();
        searchView.setQuery(searchQuery,false);

        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!searches.contains(query)){
                    searchViewModel.saveRecentSearch(new SavedSearch(query));
                }
                onQuerySubmitted(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void onQuerySubmitted(String query) {
        searchQuery=query;
        searchView.clearFocus();
        searchContainer.setVisibility(View.VISIBLE);

        if (currentFragment!=null){
            /*
            if current fragment is not null ,it mean movies fragment is created
            so we check if the people fragment is created too so that we can update the search in both of them
            */

            if (isMoviesFragmentCreated&&isPeopleFragmentCreated){
                searchSeriesFragment.updateSearch(query);
                searchPeopleFragment.updateSearch(query);
                searchMoviesFragment.updateSearch(query);
            }
            else if(isMoviesFragmentCreated){
                searchSeriesFragment.updateSearch(query);
                searchMoviesFragment.updateSearch(query);
            }
            else if(isPeopleFragmentCreated){
                searchSeriesFragment.updateSearch(query);
                searchPeopleFragment.updateSearch(query);
            }
            else {
                searchSeriesFragment.updateSearch(query);
            }
        }
        //if current fragment is null,its mean this is first search done by user
        else {
//                    we gonna initial it
            initFragments();
            fragmentController(searchSeriesFragment);
        }
    }

    private void fragmentController(Fragment fragment) {
        recentSearchContainer.setVisibility(View.GONE);
        if (fragment== searchPeopleFragment &&!isPeopleFragmentCreated){
            isPeopleFragmentCreated=true;
        }
        else if (fragment== searchMoviesFragment &&!isMoviesFragmentCreated){
            isMoviesFragmentCreated=true;
        }
        fragmentTransaction=fragmentManager.beginTransaction();
        currentFragment=fragment;
        updateTapsLooks();
        Log.d(TAG, "fragmentController: current is "+fragment.toString());
        Bundle argument=new Bundle();
        argument.putString("search query",searchQuery);
        fragment.setArguments(argument);
        fragmentTransaction.replace(R.id.search_fragments_container,currentFragment);
        fragmentTransaction.commit();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        int clickedViewId=v.getId();
        fragmentManager.saveFragmentInstanceState(currentFragment);
        switch (clickedViewId){
            case R.id.series_tab:{
                currentFragment= searchSeriesFragment;
                updateTapsLooks();
                fragmentController(searchSeriesFragment);
                break;
            }
            case R.id.movies_tab:{
                currentFragment= searchMoviesFragment;
                updateTapsLooks();
                fragmentController(searchMoviesFragment);
                break;
            }
            case R.id.actors_tab:{
                currentFragment= searchPeopleFragment;
                updateTapsLooks();
                fragmentController(searchPeopleFragment);
                break;
            }
        }
    }
 
    @Override
    public void onRecentSearchClicked(String recentQuery) {
        Log.d(TAG, "onRecentSearchClicked: ");
        searchView.setQuery(recentQuery,false);
        onQuerySubmitted(recentQuery);
    }
}


