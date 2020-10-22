package com.curiousdev.moviesdiscover.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.Respositories.SearchMoviesRespo;

public class SearchMoviesViewModel extends AndroidViewModel {
    private static final String TAG = "SearchMoviesViewModel";
    private SearchMoviesRespo SearchMoviesRespo;
    MutableLiveData<Movies> movies;
    String prevQuery;

    public SearchMoviesViewModel(@NonNull Application application) {
        super(application);
        SearchMoviesRespo =new SearchMoviesRespo(application);
    }
    public MutableLiveData<Movies> getMovies (String personName,int page,int year){
        Log.d(TAG, "getMovies: its null,am gonna get it");
        prevQuery=personName;
        movies=new MutableLiveData<>();
        movies=getResults(personName,page,year);
        return movies;
    }
    private MutableLiveData<Movies> getResults(String pName,int page,int year){
        return movies= SearchMoviesRespo.loadResultsPage(pName,page,year);
    }


}
