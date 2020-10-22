package com.curiousdev.moviesdiscover.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.CastMember;
import com.curiousdev.moviesdiscover.Models.Genre;
import com.curiousdev.moviesdiscover.Models.Language;
import com.curiousdev.moviesdiscover.Models.MovieDetail;
import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.Models.Reviews;
import com.curiousdev.moviesdiscover.Models.VideoItem;
import com.curiousdev.moviesdiscover.Models.Videos;
import com.curiousdev.moviesdiscover.Respositories.MovieDetailRespo;

import java.util.List;


public class MovieDetailViewModel extends AndroidViewModel {
    private MutableLiveData<MovieDetail> details;
    private MutableLiveData<Movies> movies;
    private MovieDetailRespo detailRespo;
    private static final String TAG = "MovieDetailViewModel";
    public MovieDetailViewModel(@NonNull Application app) {
        super(app);
        details=new MutableLiveData<>();
        movies=new MutableLiveData<>();
        detailRespo=new MovieDetailRespo(app);
    }
    //getting from room
    public LiveData<MovieDetail> getMovieDetailFromRoom(int movieId){
        return detailRespo.getMovieDetails(movieId);
    }
    public LiveData<List<Genre>> getGenres(int movieId){
        return detailRespo.getGenres(movieId);
    }

    public LiveData<List<Language>> getLanguages(int movieId){
        return detailRespo.getLanguages(movieId);
    }

    public LiveData<List<CastMember>> getMembers(int movieId){
        return detailRespo.getMembers(movieId);
    }

    public LiveData<List<VideoItem>> getVideos(int movieId){
        return detailRespo.getVideos(movieId);
    }

    //getting from server
    public MutableLiveData<MovieDetail> getMovieDetail(int movieId){
        details=getMovieDetailFromApi(movieId);
        //now we have to save it to room
        return details;
    }

    public MutableLiveData<Movies> getSimilarMovies(int movieId,int currentPage){
        return getSimilarMoviesFromApi(movieId,currentPage);
    }

    public MutableLiveData<Reviews> getMovieReviews(int movieId,int page){
        return detailRespo.getReviews(movieId,page);
    }

    public MutableLiveData<Videos> getMovieDefaultVideos(int movieId){
        return getMovieDefaultVideosFromApi(movieId);
    }

    private MutableLiveData<MovieDetail> getMovieDetailFromApi(int movieId){
            return detailRespo.getDetail(movieId);
    }

    private MutableLiveData<Videos> getMovieDefaultVideosFromApi(int movieId){
        return detailRespo.getDefaultVideos(movieId);
    }
    private MutableLiveData<Movies> getSimilarMoviesFromApi(int movieId,int currentPage){
        return detailRespo.getSimilarMovies(movieId,currentPage);
    }


}
