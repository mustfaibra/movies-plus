package com.curiousdev.moviesdiscover.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.Respositories.MoviesRepo;

public class MoviesViewModel extends AndroidViewModel {
    private MutableLiveData<Movies> popularMovies;
    private MutableLiveData<Movies> upComingMovies;
    private MutableLiveData<Movies> newMovies;
    private MoviesRepo moviesRepo;

    public MoviesViewModel(@NonNull Application application) {
        super(application);
        moviesRepo=new MoviesRepo(application);
        popularMovies=new MutableLiveData<>();
        upComingMovies=new MutableLiveData<>();
        newMovies=new MutableLiveData<>();
    }

    public MutableLiveData<Movies> getPopularMovies(int page){
        return moviesRepo.getPopularMovies(page);
    }
    public MutableLiveData<Movies> getUpComingMovies(int page){
        return moviesRepo.getUpcomingMovies(page);
    }
    public MutableLiveData<Movies> getNewMovies(int page){
        return moviesRepo.getNewMovies(page);
    }

}
