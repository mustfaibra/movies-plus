package com.curiousdev.moviesdiscover.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.curiousdev.moviesdiscover.Models.Movies;
import com.curiousdev.moviesdiscover.Models.Series;
import com.curiousdev.moviesdiscover.Respositories.MoviesRepo;
import com.curiousdev.moviesdiscover.Respositories.TvShowRepo;

public class TvShowViewModel extends AndroidViewModel {
    private MutableLiveData<Series> popularTvShow;
    private MutableLiveData<Series> upComingTvShow;
    private MutableLiveData<Series> newTvShow;
    private TvShowRepo tvShowRepo;

    public TvShowViewModel(@NonNull Application application) {
        super(application);
        tvShowRepo=new TvShowRepo(application);
        popularTvShow=new MutableLiveData<>();
        upComingTvShow=new MutableLiveData<>();
        newTvShow=new MutableLiveData<>();
    }

    public MutableLiveData<Series> getPopularTvShow(int page){
        return tvShowRepo.getPopularTvShow(page);
    }
    public MutableLiveData<Series> getTopRatedTvShow(int page){
        return tvShowRepo.getTopRatedTvShow(page);
    }
    public MutableLiveData<Series> getOnAirTvShow(int page){
        return tvShowRepo.getOnAirTvShow(page);
    }

}
