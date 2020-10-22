package com.curiousdev.moviesdiscover.Respositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.CastMember;
import com.curiousdev.moviesdiscover.Models.Genre;
import com.curiousdev.moviesdiscover.Models.Language;
import com.curiousdev.moviesdiscover.Models.Movie;
import com.curiousdev.moviesdiscover.Models.MovieApi;
import com.curiousdev.moviesdiscover.Models.MovieDetail;
import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.Models.Reviews;
import com.curiousdev.moviesdiscover.Models.RoomDao;
import com.curiousdev.moviesdiscover.Models.RoomDb;
import com.curiousdev.moviesdiscover.Models.VideoItem;
import com.curiousdev.moviesdiscover.Models.Videos;
import com.curiousdev.moviesdiscover.R;

import java.security.PublicKey;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MovieDetailRespo {
    public int movieId;
    public Retrofit webClient;
    public MoviesProvider apiServices;
    Application application;
    public MutableLiveData<MovieDetail> details;
    public MutableLiveData<Movies> similarMovies;
    public MutableLiveData<Reviews> reviews;
    public RoomDao dao;
    public static final String TAG = "MovieDetailRespo";

    public MovieDetailRespo(Application app) {
        application=app;
        webClient= MovieApi.getRetrofitInstance();
        apiServices=webClient.create(MoviesProvider.class);
        details=new MutableLiveData<>();
        reviews=new MutableLiveData<>();
        dao= RoomDb.getRoomInstance(app.getApplicationContext()).getDao();
    }
    
    public MutableLiveData<MovieDetail> getDetail(int id){
        movieId=id;
        apiServices.getMovieDetail(movieId,application.getString(R.string.tmdb_api), Locale.getDefault().getLanguage(), "videos,credits").enqueue(
                new Callback<MovieDetail>() {
                    @Override
                    public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                        if (response.isSuccessful()){
                            MovieDetail detail=response.body();
                            saveMovieDetails(detail);
                            saveMovieGenres(detail.getGenres());
                            saveMovieCast(detail.getCast().getCastMember());
                            saveMovieLanguages(detail.getSpokenLanguages());
                            saveMovieVideos(detail.getVideos().getResults());
                            details.setValue(response.body());

                        }
                    }

                    @Override
                    public void onFailure(Call<MovieDetail> call, Throwable throwable) {
                        details.postValue(null);
                    }
                }
        );

        return details;
    }

    public MutableLiveData<Videos> getDefaultVideos(int id){
        movieId=id;
        MutableLiveData<Videos> videos=new MutableLiveData<>();
        apiServices.getMovieVideos(movieId,application.getString(R.string.tmdb_api),"en").enqueue(
                new Callback<Videos>() {
                    @Override
                    public void onResponse(Call<Videos> call, Response<Videos> response) {
                        if (response.isSuccessful()){
                            videos.setValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<Videos> call, Throwable throwable) {
                        videos.postValue(null);
                    }
                }
        );
        return videos;
    }
    public MutableLiveData<Movies> getSimilarMovies(int movieId,int currentPage){
        similarMovies=new MutableLiveData<>();
        apiServices.getSimilarMovies(movieId,currentPage,application.getString(R.string.tmdb_api),Locale.getDefault().getLanguage()).enqueue(
                new Callback<Movies>() {
                    @Override
                    public void onResponse(Call<Movies> call, Response<Movies> response) {
                        similarMovies.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<Movies> call, Throwable throwable) {
                        similarMovies.postValue(null);
                    }
                }
        );
        return similarMovies;
    }
    public MutableLiveData<Reviews> getReviews(int id,int page){
        movieId=id;
        apiServices.getReviews(movieId,application.getString(R.string.tmdb_api),  Locale.getDefault().getLanguage(),page).enqueue(
                new Callback<Reviews>() {
                    @Override
                    public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                        if (response.isSuccessful()){
                            reviews.setValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<Reviews> call, Throwable throwable) {
                        details.postValue(null);
                    }
                }
        );
        return reviews;
    }




    //save MovieDetails to room
    public void saveMovieDetails(MovieDetail detail){
        new SaveMovieDetailAsyncTask(dao).execute(detail);
    }
    public void saveMovieGenres(List<Genre> genres){
        for (Genre genre:genres){
            genre.setOwnerId(movieId);
        }
        new SaveMovieGenresAsyncTask(dao).execute(genres);
    }
    public void saveMovieLanguages(List<Language> languages){
        for (Language language:languages){
            language.setOwnerId(movieId);
        }
        new SaveMovieLanguagesAsyncTask(dao).execute(languages);

    }
    public void saveMovieCast(List<CastMember> members){
        for (CastMember member:members){
            member.setOwnerId(movieId);
        }
        new SaveMovieCastAsyncTask(dao).execute(members);

    }
    public void saveMovieVideos(List<VideoItem> videoItems){
        for(VideoItem video:videoItems){
            video.setOwnerId(movieId);
        }
        new SaveMovieVideosAsyncTask(dao).execute(videoItems);

    }
    //getMovieDetails from room
    public LiveData<MovieDetail> getMovieDetails(int movieId){
        return dao.getMovieDetails(movieId);
    }

    public LiveData<List<Genre>> getGenres(int movieId){
        return dao.getSavedGenres(movieId);
    }

    public LiveData<List<Language>> getLanguages(int movieId){
        return dao.getSavedLanguages(movieId);
    }

    public LiveData<List<CastMember>> getMembers(int movieId){
        return dao.getSavedCast(movieId);
    }

    public LiveData<List<VideoItem>> getVideos(int movieId){
        return dao.getSavedVideos(movieId);
    }


    public static class SaveMovieDetailAsyncTask extends AsyncTask<MovieDetail,Void,Void> {
        RoomDao dao;

        public SaveMovieDetailAsyncTask(RoomDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(MovieDetail... details) {
            dao.saveMovieDetails(details[0]);
            return null;
        }
    }
    public static class SaveMovieGenresAsyncTask extends AsyncTask<List<Genre>,Void,Void> {
        RoomDao dao;

        public SaveMovieGenresAsyncTask(RoomDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(List<Genre>... genres) {
            dao.saveGenres(genres[0]);
            return null;
        }
    }
    public static class SaveMovieLanguagesAsyncTask extends AsyncTask<List<Language>,Void,Void> {
        RoomDao dao;

        public SaveMovieLanguagesAsyncTask(RoomDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(List<Language>... langs) {
            dao.saveLanguages(langs[0]);
            return null;
        }
    }
    public static class SaveMovieCastAsyncTask extends AsyncTask<List<CastMember>,Void,Void> {
        RoomDao dao;

        public SaveMovieCastAsyncTask(RoomDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(List<CastMember>... members) {
            dao.saveCast(members[0]);
            return null;
        }
    }
    public static class SaveMovieVideosAsyncTask extends AsyncTask<List<VideoItem>,Void,Void> {
        RoomDao dao;

        public SaveMovieVideosAsyncTask(RoomDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(List<VideoItem>... videos) {
            dao.saveVideos(videos[0]);
            return null;
        }
    }


}
