package com.curiousdev.moviesdiscover.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.Models.Series;
import com.curiousdev.moviesdiscover.Respositories.SearchMoviesRespo;
import com.curiousdev.moviesdiscover.Respositories.SearchSeriesRepo;

public class SearchSeriesViewModel extends AndroidViewModel {
    private static final String TAG = "SearchMoviesViewModel";
    private SearchSeriesRepo seriesRepo;
    private MutableLiveData<Series> series;
    private String prevQuery;

    public SearchSeriesViewModel(@NonNull Application application) {
        super(application);
        seriesRepo =new SearchSeriesRepo(application);
    }
    public MutableLiveData<Series> getSeries (String personName,int page){
        Log.d(TAG, "getMovies: its null,am gonna get it");
        prevQuery=personName;
        series=new MutableLiveData<>();
        series=getResults(personName,page);
        return series;
    }
    private MutableLiveData<Series> getResults(String pName, int page){
        return series= seriesRepo.loadResultsPage(pName,page);
    }


}
