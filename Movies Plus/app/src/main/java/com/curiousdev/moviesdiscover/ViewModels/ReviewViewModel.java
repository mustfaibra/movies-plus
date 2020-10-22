package com.curiousdev.moviesdiscover.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.Reviews;
import com.curiousdev.moviesdiscover.Respositories.MovieDetailRespo;
import com.curiousdev.moviesdiscover.Respositories.TvShowDetailRepo;

public class ReviewViewModel extends AndroidViewModel {
    private MutableLiveData<Reviews> movieReviews;
    private MutableLiveData<Reviews> tvShowReviews;
    private MovieDetailRespo movieDetailrepo;
    private TvShowDetailRepo tvShowDetailrepo;

    public ReviewViewModel(@NonNull Application application) {
        super(application);
        movieDetailrepo=new MovieDetailRespo(application);
        tvShowDetailrepo=new TvShowDetailRepo(application);
        movieReviews=new MutableLiveData<>();
        tvShowReviews=new MutableLiveData<>();
    }

    public MutableLiveData<Reviews> getMovieReviews(int movieId,int page){
        movieReviews=getMovieReviewsFromApi(movieId,page);
        return movieReviews;
    }
    private MutableLiveData<Reviews> getMovieReviewsFromApi(int movieId,int page){
        return movieDetailrepo.getReviews(movieId,page);
    }
    public MutableLiveData<Reviews> getTvShowReviews(int showId,int page){
        tvShowReviews=getTvShowReviewsFromApi(showId,page);
        return tvShowReviews;
    }
    private MutableLiveData<Reviews> getTvShowReviewsFromApi(int showId,int page){
        return tvShowDetailrepo.getReviews(showId,page);
    }
}
