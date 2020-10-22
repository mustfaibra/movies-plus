package com.curiousdev.moviesdiscover.Respositories;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.MovieApi;
import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.Models.Series;
import com.curiousdev.moviesdiscover.R;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TvShowRepo {

    private MoviesProvider apiServices;
    private Application application;
    public TvShowRepo(Application app){
        this.application=app;
        Retrofit retrofit= MovieApi.getRetrofitInstance();
        apiServices=retrofit.create(MoviesProvider.class);
    }

    public MutableLiveData<Series> getPopularTvShow(int page){
        MutableLiveData<Series> series=new MutableLiveData<>();

        apiServices.getPopularTvShow(
                application.getApplicationContext().getString(R.string.tmdb_api),
                Locale.getDefault().getLanguage(),
                page)
                .enqueue(new Callback<com.curiousdev.moviesdiscover.Models.Series>() {
                    @Override
                    public void onResponse(Call<com.curiousdev.moviesdiscover.Models.Series> call, Response<com.curiousdev.moviesdiscover.Models.Series> response) {
                        series.setValue(response.body());
                    }
                    @Override
                    public void onFailure(Call<com.curiousdev.moviesdiscover.Models.Series> call, Throwable throwable) {
                        series.postValue(null);
                    }
                });
        return series;
    }
    public MutableLiveData<Series> getTopRatedTvShow(int page){
        MutableLiveData<Series> series=new MutableLiveData<>();

        apiServices.getTopRatedTvShow(
                application.getApplicationContext().getString(R.string.tmdb_api),
                Locale.getDefault().getLanguage(),
                page)
                .enqueue(new Callback<com.curiousdev.moviesdiscover.Models.Series>() {
                    @Override
                    public void onResponse(Call<com.curiousdev.moviesdiscover.Models.Series> call, Response<com.curiousdev.moviesdiscover.Models.Series> response) {
                        series.setValue(response.body());
                    }
                    @Override
                    public void onFailure(Call<com.curiousdev.moviesdiscover.Models.Series> call, Throwable throwable) {
                        series.postValue(null);
                    }
                });
        return series;
    }
    public MutableLiveData<Series> getOnAirTvShow(int page){
        MutableLiveData<Series> series=new MutableLiveData<>();
        apiServices.getOnAirTvShow(
                application.getApplicationContext().getString(R.string.tmdb_api),
                Locale.getDefault().getLanguage(),
                page)
                .enqueue(new Callback<com.curiousdev.moviesdiscover.Models.Series>() {
                    @Override
                    public void onResponse(Call<com.curiousdev.moviesdiscover.Models.Series> call, Response<com.curiousdev.moviesdiscover.Models.Series> response) {
                        series.setValue(response.body());
                    }
                    @Override
                    public void onFailure(Call<com.curiousdev.moviesdiscover.Models.Series> call, Throwable throwable) {
                        series.postValue(null);
                    }
                });
        return series;
    }




}
