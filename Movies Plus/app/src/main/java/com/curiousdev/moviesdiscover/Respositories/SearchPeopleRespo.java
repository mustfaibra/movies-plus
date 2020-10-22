package com.curiousdev.moviesdiscover.Respositories;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.MovieApi;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.Models.People;
import com.curiousdev.moviesdiscover.R;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchPeopleRespo {
    private static final String TAG = "SearchPeopleRespo";
    private MoviesProvider apiServices;
    private Application application;
    private MutableLiveData<People> people=new MutableLiveData<>();
    public SearchPeopleRespo(Application app){
        this.application=app;
        Retrofit retrofit= MovieApi.getRetrofitInstance();
        apiServices=retrofit.create(MoviesProvider.class);
    }


    public MutableLiveData<People> loadResultsPage(String query,int page) {
        Log.d(TAG, "loadResultsFirstPage: ");
        getSearchActorsCall(query,page).enqueue(new Callback<People>() {
            @Override
            public void onResponse(Call<People> call, Response<People> response) {
                if (response.isSuccessful()){
                    people.setValue(response.body());
                }
                else{
                    Log.d(TAG, "onResponse: operation didn't success ,status code recived is : "+response.code());
                }

            }

            @Override
            public void onFailure(@NotNull Call<People> call, @NotNull Throwable throwable) {
                Log.d(TAG, "onFailure: error accure :");
                people.postValue(null);
            }
        });
       return people;
    }

    private Call<People> getSearchActorsCall(String searchQuery,int page){

        Log.d(TAG, "getSearchActorsCall: api key is "+application.getString(R.string.tmdb_api)+" and the page num is "+page);
        return apiServices.getActorsSearchResults(
                application.getString(R.string.tmdb_api),
                searchQuery,
                false,
                Locale.getDefault().getLanguage(),
                page
        );
    }

}
