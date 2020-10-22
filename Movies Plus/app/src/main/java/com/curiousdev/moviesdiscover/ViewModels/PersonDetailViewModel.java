package com.curiousdev.moviesdiscover.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.Person;
import com.curiousdev.moviesdiscover.Respositories.PersonDetailsRepo;

public class PersonDetailViewModel extends AndroidViewModel {
    private MutableLiveData<Person> details;
    private PersonDetailsRepo detailRespo;
    public PersonDetailViewModel(@NonNull Application app) {
        super(app);
        details=new MutableLiveData<>();
        detailRespo=new PersonDetailsRepo(app);
    }
    public MutableLiveData<Person> getPersonDetail(int movieId){
        details=getPersonDetailFromApi(movieId);
        return details;
    }
    private MutableLiveData<Person> getPersonDetailFromApi(int movieId){
        return detailRespo.getDetail(movieId);
    }
}
