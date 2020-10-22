package com.curiousdev.moviesdiscover.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_items")
public class SavedItem {
    @PrimaryKey(autoGenerate = true) private int id;
    private int movieId;
    private String type; //type refer to saved movie list,it can be favorite,watched,toWatch
    private String itemType;
    private String movieName;
    private String moviePoster;
    private String movieOverview;
    private double movieRate;

    public SavedItem(int movieId, String movieName, String moviePoster, String movieOverview, double movieRate, String type,String itemType) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.moviePoster = moviePoster;
        this.type=type;
        this.itemType=itemType;
        this.movieOverview=movieOverview;
        this.movieRate = movieRate;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getItemType() {
        return itemType;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getMoviePoster() {
        return moviePoster;
    }
    public String getType() {
        return type;
    }

    public double getMovieRate() {
        return movieRate;
    }
}
