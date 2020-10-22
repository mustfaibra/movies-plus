package com.curiousdev.moviesdiscover.Respositories;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.MovieApi;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.Models.Person;
import com.curiousdev.moviesdiscover.R;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PersonDetailsRepo {
    private int personId;
    private Retrofit webClient;
    private MoviesProvider apiServices;
    Application application;
    MutableLiveData<Person> details;

    public PersonDetailsRepo(Application app) {
        application=app;
        webClient= MovieApi.getRetrofitInstance();
        apiServices=webClient.create(MoviesProvider.class);
        details=new MutableLiveData<>();
    }

    public MutableLiveData<Person> getDetail(int id){
        personId=id;
        apiServices.getPersonDetail(personId,application.getString(R.string.tmdb_api), Locale.getDefault().getLanguage(),"combined_credits").enqueue(
                new Callback<Person>() {
                    @Override
                    public void onResponse(Call<Person> call, Response<Person> response) {
                        if (response.isSuccessful()){
                            details.setValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<Person> call, Throwable throwable) {
                        details.postValue(null);
                    }
                }
        );
        return details;
    }
}
