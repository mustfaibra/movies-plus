package com.curiousdev.moviesdiscover.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.People;
import com.curiousdev.moviesdiscover.Respositories.SearchPeopleRespo;

public class PeopleViewModel extends AndroidViewModel {
    private static final String TAG = "PeopleViewModel";
    private SearchPeopleRespo searchPeopleRespo;
    MutableLiveData<People> people;
    String prevQuery;

    public PeopleViewModel(@NonNull Application application) {
        super(application);
        searchPeopleRespo =new SearchPeopleRespo(application);
    }
    public MutableLiveData<People> getPeople (String personName,int page){
            Log.d(TAG, "getPeople: its null,am gonna get it");
            prevQuery=personName;
            people=new MutableLiveData<>();
            people=getResults(personName,page);
        return people;
    }
    private MutableLiveData<People> getResults(String pName,int page){
        return people= searchPeopleRespo.loadResultsPage(pName,page);
    }


}
