package com.curiousdev.moviesdiscover.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.curiousdev.moviesdiscover.Models.SavedSearch;
import com.curiousdev.moviesdiscover.Respositories.SearchRepo;

import java.util.List;


public class SearchViewModel extends AndroidViewModel {
    SearchRepo searchRepo;
    public SearchViewModel(@NonNull Application application) {
        super(application);
        searchRepo=new SearchRepo(application);
    }

    public LiveData<List<SavedSearch>> getRecentSearch(){
        return searchRepo.getRecentSearch();
    }

    public void saveRecentSearch(SavedSearch search){
        searchRepo.saveMySearch(search);
    }
}
