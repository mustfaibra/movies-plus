package com.curiousdev.moviesdiscover.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "seasons")
public class Season {

    public int showId;
    @SerializedName("air_date")
    @Expose
    public String airDate;
    @SerializedName("episode_count")
    @Expose
    public Integer episodeCount;
    @SerializedName("id")
    @Expose
    @PrimaryKey public Integer id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("overview")
    @Expose
    public String overview;
    @SerializedName("poster_path")
    @Expose
    public String posterPath;
    @SerializedName("season_number")
    @Expose
    public Integer seasonNumber;

    public String getAirDate() {
        return airDate;
    }

    public Integer getEpisodeCount() {
        return episodeCount;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }
}
