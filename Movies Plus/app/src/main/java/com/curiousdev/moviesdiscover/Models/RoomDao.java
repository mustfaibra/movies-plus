package com.curiousdev.moviesdiscover.Models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface RoomDao{

    //movies
    @Insert
    void saveItem(SavedItem item);
    @Delete
    void deleteItem(SavedItem movie);
    @Query("select * from saved_items where type=:type")
    LiveData<List<SavedItem>> getSavedItems(String type);

    @Query("select * from saved_items where movieId=:id and type=:type")
    LiveData<SavedItem> getSavedItem(int id, String type);
    //last search
    @Insert
    void saveSearch(SavedSearch search);
    @Query("select * from saved_search order by id desc")
    LiveData<List<SavedSearch>> getRecentSearch();

    //MovieDetail operations
    @Insert
    void saveMovieDetails(MovieDetail detail);
    @Insert
    void saveGenres(List<Genre> genres);
    @Insert
    void saveLanguages(List<Language> languages);
    @Insert
    void saveCast(List<CastMember> members);
    @Insert
    void saveVideos(List<VideoItem> videoItems);
    @Query("select * from movies_detail where id=:id")
    LiveData<MovieDetail> getMovieDetails(int id);

    //showDetails operations
    @Insert
    void saveShowDetails(TvShowDetails detail);
    @Insert
    void saveShowSeasons(List<Season> seasons);
    @Query("select * from tv_show_details where id=:id")
    LiveData<TvShowDetails> getShowDetails(int id);
    @Query("select * from seasons where showId=:id")
    LiveData<List<Season>> getSeasons(int id);

    @Query("select * from genres where ownerId=:id")
    LiveData<List<Genre>> getSavedGenres(int id);
    @Query("select * from languages where ownerId=:id")
    LiveData<List<Language>> getSavedLanguages(int id);
    @Query("select * from cast_members where ownerId=:id")
    LiveData<List<CastMember>> getSavedCast(int id);
    @Query("select * from videos where ownerId=:id")
    LiveData<List<VideoItem>> getSavedVideos(int id);


    //clear room
    @Query("delete from saved_items")
    void clearSavedItems();
    @Query("delete from saved_search")
    void clearSavedSearch();
    @Query("delete from movies_detail")
    void clearSavedMovieDetail();
    @Query("delete from tv_show_details")
    void clearSavedShowDetail();
    @Query("delete from seasons")
    void clearSavedSeasons();
    @Query("delete from videos")
    void clearSavedVideos();
    @Query("delete from cast_members")
    void clearSavedCast();
    @Query("delete from languages")
    void clearSavedLanguages();
    @Query("delete from genres")
    void clearSavedGenres();
}
