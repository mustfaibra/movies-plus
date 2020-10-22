package com.curiousdev.moviesdiscover.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.CastMember;
import com.curiousdev.moviesdiscover.Models.Genre;
import com.curiousdev.moviesdiscover.Models.Language;
import com.curiousdev.moviesdiscover.Models.Reviews;
import com.curiousdev.moviesdiscover.Models.Season;
import com.curiousdev.moviesdiscover.Models.TvShowDetails;
import com.curiousdev.moviesdiscover.Models.Series;
import com.curiousdev.moviesdiscover.Models.VideoItem;
import com.curiousdev.moviesdiscover.Models.Videos;
import com.curiousdev.moviesdiscover.Respositories.TvShowDetailRepo;

import java.util.List;


public class TvShowDetailViewModel extends AndroidViewModel {
    private MutableLiveData<TvShowDetails> details;
    private TvShowDetailRepo detailRespo;
    public TvShowDetailViewModel(@NonNull Application app) {
        super(app);
        details=new MutableLiveData<>();
        detailRespo=new TvShowDetailRepo(app);
    }
    //from room
    public LiveData<TvShowDetails> getDetailsFromRoom(int showId){
        return detailRespo.getShowDetails(showId);
    }
    public LiveData<List<Genre>> getGenres(int showId){
        return detailRespo.getGenres(showId);
    }

    public LiveData<List<Language>> getLanguages(int showId){
        return detailRespo.getLanguages(showId);
    }

    public LiveData<List<CastMember>> getMembers(int showId){
        return detailRespo.getMembers(showId);
    }

    public LiveData<List<Season>> getSeasons(int showId){
        return detailRespo.getSeasons(showId);
    }

    public LiveData<List<VideoItem>> getVideos(int showId){
        return detailRespo.getVideos(showId);
    }
    //from server
    public MutableLiveData<TvShowDetails> getTvShowDetail(int showId){
        details=getTvShowDetailFromApi(showId);
        return details;
    }


    private MutableLiveData<TvShowDetails> getTvShowDetailFromApi(int showId){
        return detailRespo.getDetail(showId);
    }

    public MutableLiveData<Series> getSimilarTvShows(int movieId,int currentPage){
        return getSimilarTvShowsFromApi(movieId,currentPage);
    }

    public MutableLiveData<Videos> getTvShowDefaultVideos(int showId){
        return getTvShowDefaultVideosFromApi(showId);
    }

    public MutableLiveData<Reviews> getReviews(int showId,int page){
        return detailRespo.getReviews(showId,page);
    }

    private MutableLiveData<Videos> getTvShowDefaultVideosFromApi(int showId){
        return detailRespo.getDefaultVideos(showId);
    }
    private MutableLiveData<Series> getSimilarTvShowsFromApi(int showId,int currentPage){
        return detailRespo.getSimilarTvShows(showId,currentPage);
    }


}
