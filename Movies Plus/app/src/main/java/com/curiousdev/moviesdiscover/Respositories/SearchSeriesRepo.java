package com.curiousdev.moviesdiscover.Respositories;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.MovieApi;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.Models.Series;
import com.curiousdev.moviesdiscover.R;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchSeriesRepo {
    private static final String TAG = "SearchSeriesRepo";
    private MoviesProvider apiServices;
    private Application application;
    private MutableLiveData<Series> series=new MutableLiveData<>();
    public SearchSeriesRepo(Application app){
        this.application=app;
        Retrofit retrofit= MovieApi.getRetrofitInstance();
        apiServices=retrofit.create(MoviesProvider.class);
    }

    public MutableLiveData<Series> loadResultsPage(String query,int page) {
        Log.d(TAG, "loadResultsFirstPage: ");
        getSearchSeriesCall(query,page).enqueue(new Callback<Series>() {
            @Override
            public void onResponse(Call<Series> call, Response<Series> response) {
                if (response.isSuccessful()){
                    series.setValue(response.body());
                }
                else{
                    Log.d(TAG, "onResponse: operation didn't success ,status code recived is : "+response.code());
                }

            }

            @Override
            public void onFailure(@NotNull Call<Series> call, @NotNull Throwable throwable) {
                Log.d(TAG, "onFailure: error accure :");
                series.postValue(null);
            }
        });
        return series;
    }

    private Call<Series> getSearchSeriesCall(String searchQuery, int page){

        Log.d(TAG, "getSearchMoviesCall: api key is "+application.getString(R.string.tmdb_api)+" and the page num is "+page);
        return apiServices.getSeriesSearchResults(
                application.getString(R.string.tmdb_api),
                searchQuery,
                false,
                Locale.getDefault().getLanguage(),
                page
        );
    }

}
