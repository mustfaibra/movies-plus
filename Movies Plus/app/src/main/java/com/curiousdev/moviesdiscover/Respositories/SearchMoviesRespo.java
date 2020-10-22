package com.curiousdev.moviesdiscover.Respositories;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.MovieApi;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.R;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchMoviesRespo {
    private static final String TAG = "SearchMoviesRespo";
    private MoviesProvider apiServices;
    private Application application;
    private MutableLiveData<Movies> Movies=new MutableLiveData<>();
    public SearchMoviesRespo(Application app){
        this.application=app;
        Retrofit retrofit= MovieApi.getRetrofitInstance();
        apiServices=retrofit.create(MoviesProvider.class);
    }

    public MutableLiveData<Movies> loadResultsPage(String query,int page,int year) {
        Log.d(TAG, "loadResultsFirstPage: ");
        getSearchMoviesCall(query,page,year).enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                if (response.isSuccessful()){
                    Movies.setValue(response.body());
                }
                else{
                    Log.d(TAG, "onResponse: operation didn't success ,status code recived is : "+response.code());
                }

            }

            @Override
            public void onFailure(@NotNull Call<Movies> call, @NotNull Throwable throwable) {
                Log.d(TAG, "onFailure: error accure :");
                Movies.postValue(null);
            }
        });
        return Movies;
    }

    private Call<Movies> getSearchMoviesCall(String searchQuery,int page,int year){

        Log.d(TAG, "getSearchMoviesCall: api key is "+application.getString(R.string.tmdb_api)+" and the page num is "+page);
        return apiServices.getMoviesSearchResults(
                application.getString(R.string.tmdb_api),
                searchQuery,
                false,
                year,
                Locale.getDefault().getLanguage(),
                page
        );
    }

}
