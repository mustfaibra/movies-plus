package com.curiousdev.moviesdiscover.Respositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.CastMember;
import com.curiousdev.moviesdiscover.Models.Genre;
import com.curiousdev.moviesdiscover.Models.Language;
import com.curiousdev.moviesdiscover.Models.MovieApi;
import com.curiousdev.moviesdiscover.Models.RoomDao;
import com.curiousdev.moviesdiscover.Models.RoomDb;
import com.curiousdev.moviesdiscover.Models.Season;
import com.curiousdev.moviesdiscover.Models.TvShowDetails;
import com.curiousdev.moviesdiscover.Models.Series;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.Models.Reviews;
import com.curiousdev.moviesdiscover.Models.VideoItem;
import com.curiousdev.moviesdiscover.Models.Videos;
import com.curiousdev.moviesdiscover.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TvShowDetailRepo {
    private int showId;
    private Retrofit webClient;
    private MoviesProvider apiServices;
    Application application;
    MutableLiveData<TvShowDetails> details;
    MutableLiveData<Series> similarTvShows;
    MutableLiveData<Reviews> reviews;
    private RoomDao dao;

    public TvShowDetailRepo(Application app) {
        application=app;
        webClient= MovieApi.getRetrofitInstance();
        apiServices=webClient.create(MoviesProvider.class);
        details=new MutableLiveData<>();
        reviews=new MutableLiveData<>();
        dao= RoomDb.getRoomInstance(app.getApplicationContext()).getDao();
    }

    public MutableLiveData<TvShowDetails> getDetail(int id){
        showId=id;
        apiServices.getTvShowDetail(id,application.getString(R.string.tmdb_api), Locale.getDefault().getLanguage(), "videos,credits").enqueue(
                new Callback<TvShowDetails>() {
                    @Override
                    public void onResponse(Call<TvShowDetails> call, Response<TvShowDetails> response) {
                        if (response.isSuccessful()){
                            TvShowDetails detail=response.body();
                            saveShowDetails(detail);
                            saveShowGenres(detail.getGenres());
                            saveShowCast(detail.getCast().getCastMember());
                            //tv show use list of string instead of list of language object,so we gonna deal with it for now
                            List<Language> langs=new ArrayList<>();
                            for (String lang:detail.getLanguages()){
                                langs.add(new Language(lang));
                            }
                            saveShowLanguages(langs);
                            saveShowVideos(detail.getVideos().getResults());
                            saveShowSeasons(detail.getSeasons());
                            details.setValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<TvShowDetails> call, Throwable throwable) {
                        details.postValue(null);
                    }
                }
        );
        return details;
    }
    public MutableLiveData<Videos> getDefaultVideos(int id){
        MutableLiveData<Videos> videos=new MutableLiveData<>();
        apiServices.getTvShowVideos(id,application.getString(R.string.tmdb_api),"en").enqueue(
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

    public MutableLiveData<Series> getSimilarTvShows(int showId,int currentPage){
        similarTvShows=new MutableLiveData<>();
        apiServices.getSimilarTvShows(showId,currentPage,application.getString(R.string.tmdb_api),Locale.getDefault().getLanguage()).enqueue(
                new Callback<Series>() {
                    @Override
                    public void onResponse(Call<Series> call, Response<Series> response) {
                        similarTvShows.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<Series> call, Throwable throwable) {
                        similarTvShows.postValue(null);
                    }
                }
        );
        return similarTvShows;
    }
    public MutableLiveData<Reviews> getReviews(int id,int page){
        apiServices.getTvshowReviews(id,application.getString(R.string.tmdb_api),  Locale.getDefault().getLanguage(),page).enqueue(
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
    
    
    //saving show details to room
    public void saveShowDetails(TvShowDetails detail){
        new TvShowDetailRepo.SaveShowDetailAsyncTask(dao).execute(detail);
    }
    public void saveShowGenres(List<Genre> genres){
        for (Genre genre:genres){
            genre.setOwnerId(showId);
        }
        new MovieDetailRespo.SaveMovieGenresAsyncTask(dao).execute(genres);
    }
    public void saveShowLanguages(List<Language> languages){
        for (Language language:languages){
            language.setOwnerId(showId);
        }
        new MovieDetailRespo.SaveMovieLanguagesAsyncTask(dao).execute(languages);

    }
    public void saveShowCast(List<CastMember> members){
        for (CastMember member:members){
            member.setOwnerId(showId);
        }
        new MovieDetailRespo.SaveMovieCastAsyncTask(dao).execute(members);

    }
    public void saveShowVideos(List<VideoItem> videoItems){
        for(VideoItem video:videoItems){
            video.setOwnerId(showId);
        }
        new MovieDetailRespo.SaveMovieVideosAsyncTask(dao).execute(videoItems);

    }
    public void saveShowSeasons(List<Season> seasons){
        for(Season season:seasons){
            season.setShowId(showId);
        }
        new TvShowDetailRepo.SaveShowSeasonsAsyncTask(dao).execute(seasons);

    }

    //from room
    public LiveData<TvShowDetails> getShowDetails(int showId){
        return dao.getShowDetails(showId);
    }
    public LiveData<List<Genre>> getGenres(int showId){
        return dao.getSavedGenres(showId);
    }
    public LiveData<List<Language>> getLanguages(int showId){
        return dao.getSavedLanguages(showId);
    }
    public LiveData<List<CastMember>> getMembers(int showId){
        return dao.getSavedCast(showId);
    }
    public LiveData<List<VideoItem>> getVideos(int showId){
        return dao.getSavedVideos(showId);
    }
    public LiveData<List<Season>> getSeasons(int showId){
        return dao.getSeasons(showId);
    }


    public static class SaveShowDetailAsyncTask extends AsyncTask<TvShowDetails,Void,Void> {
        RoomDao dao;

        private SaveShowDetailAsyncTask(RoomDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(TvShowDetails... details) {
            dao.saveShowDetails(details[0]);
            return null;
        }
    }
    public static class SaveShowSeasonsAsyncTask extends AsyncTask<List<Season>,Void,Void> {
        RoomDao dao;

        private SaveShowSeasonsAsyncTask(RoomDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(List<Season>... seasons) {
            dao.saveShowSeasons(seasons[0]);
            return null;
        }
    }


}
