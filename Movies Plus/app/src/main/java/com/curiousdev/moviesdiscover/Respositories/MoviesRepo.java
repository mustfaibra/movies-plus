package com.curiousdev.moviesdiscover.Respositories;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.MovieApi;
import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.R;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MoviesRepo {

    private MoviesProvider apiServices;
    private Application application;
    public MoviesRepo(Application app){
        this.application=app;
        Retrofit retrofit= MovieApi.getRetrofitInstance();
        apiServices=retrofit.create(MoviesProvider.class);
    }

    public MutableLiveData<Movies> getPopularMovies(int page){
        MutableLiveData<Movies> movies=new MutableLiveData<>();

        apiServices.getPopularMovies(
            application.getApplicationContext().getString(R.string.tmdb_api),
            Locale.getDefault().getLanguage(),
            page)
            .enqueue(new Callback<com.curiousdev.moviesdiscover.Models.Movies>() {
                @Override
                public void onResponse(Call<com.curiousdev.moviesdiscover.Models.Movies> call, Response<com.curiousdev.moviesdiscover.Models.Movies> response) {
                    movies.setValue(response.body());
                }
                @Override
                public void onFailure(Call<com.curiousdev.moviesdiscover.Models.Movies> call, Throwable throwable) {
                    movies.postValue(null);
                }
            });
        return movies;
    }
    public MutableLiveData<Movies> getUpcomingMovies(int page){
         MutableLiveData<Movies> movies=new MutableLiveData<>();

        apiServices.getUpcomingMovies(
            application.getApplicationContext().getString(R.string.tmdb_api),
            Locale.getDefault().getLanguage(),
            page)
            .enqueue(new Callback<com.curiousdev.moviesdiscover.Models.Movies>() {
                @Override
                public void onResponse(Call<com.curiousdev.moviesdiscover.Models.Movies> call, Response<com.curiousdev.moviesdiscover.Models.Movies> response) {
                    movies.setValue(response.body());
                }
                @Override
                public void onFailure(Call<com.curiousdev.moviesdiscover.Models.Movies> call, Throwable throwable) {
                    movies.postValue(null);
                }
            });
        return movies;
    }
    public MutableLiveData<Movies> getNewMovies(int page){
        MutableLiveData<Movies> movies=new MutableLiveData<>();
        apiServices.getNewMovies(
            application.getApplicationContext().getString(R.string.tmdb_api),
            Locale.getDefault().getLanguage(),
            page)
            .enqueue(new Callback<com.curiousdev.moviesdiscover.Models.Movies>() {
                @Override
                public void onResponse(Call<com.curiousdev.moviesdiscover.Models.Movies> call, Response<com.curiousdev.moviesdiscover.Models.Movies> response) {
                    movies.setValue(response.body());
                }
                @Override
                public void onFailure(Call<com.curiousdev.moviesdiscover.Models.Movies> call, Throwable throwable) {
                    movies.postValue(null);
                }
            });
        return movies;
    }




}
