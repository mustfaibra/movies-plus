package com.curiousdev.moviesdiscover.Models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "movies_detail")
public class MovieDetail {

    @SerializedName("adult")
    @Expose
    public Boolean adult;
    @SerializedName("backdrop_path")
    @Expose
    public String backdropPath;
    @SerializedName("belongs_to_collection")
    @Expose
    @Ignore
    public Object belongsToCollection;
    @SerializedName("budget")
    @Expose
    public Integer budget;
    @SerializedName("genres")
    @Expose
    @Ignore
    public List<Genre> genres = null;
    @SerializedName("homepage")
    @Expose
    public String homepage;
    //this is the primary key of details table
    @SerializedName("id")
    @Expose
    @PrimaryKey
    public Integer id;
    @SerializedName("imdb_id")
    @Expose
    public String imdbId;
    @SerializedName("original_language")
    @Expose
    public String originalLanguage;
    @SerializedName("original_title")
    @Expose
    public String originalTitle;
    @SerializedName("overview")
    @Expose
    public String overview;
    @SerializedName("popularity")
    @Expose
    public Double popularity;
    @SerializedName("poster_path")
    @Expose
    public String posterPath;
    @SerializedName("release_date")
    @Expose
    public String releaseDate;
    @SerializedName("revenue")
    @Expose
    public Integer revenue;
    @SerializedName("runtime")
    @Expose
    public Integer runtime;
    @SerializedName("spoken_languages")
    @Expose
    @Ignore
    public List<Language> spokenLanguages = null;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("tagline")
    @Expose
    public String tagline;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("video")
    @Expose
    public Boolean video;
    @SerializedName("vote_average")
    @Expose
    public Double voteAverage;
    @SerializedName("vote_count")
    @Expose
    public Integer voteCount;
    @SerializedName("videos")
    @Expose
    @Ignore
    public Videos videos;
    @SerializedName("credits")
    @Expose
    @Ignore
    public Cast cast;

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setSpokenLanguages(List<Language> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public void setVideos(Videos videos) {
        this.videos = videos;
    }

    public void setCast(Cast cast) {
        this.cast = cast;
    }

    public Boolean getAdult() {
        return adult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }
    @Ignore
    public Object getBelongsToCollection() {
        return belongsToCollection;
    }

    public Integer getBudget() {
        return budget;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getHomepage() {
        return homepage;
    }

    public Integer getId() {
        return id;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public Double getPopularity() {
        return popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Integer getRevenue() {
        return revenue;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public List<Language> getSpokenLanguages() {
        return spokenLanguages;
    }

    public String getStatus() {
        return status;
    }

    public String getTagline() {
        return tagline;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getVideo() {
        return video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public Videos getVideos() {
        return videos;
    }

    public Cast getCast() {
        return cast;
    }


    public String getTrailer(){
        String trailer="";
        for (VideoItem video:getVideos().getResults()){
            if (video.getType().equalsIgnoreCase("trailer")){
                trailer=video.getKey();
                break;
            }
            else {
                System.out.println(video.getType());
            }
        }
        return trailer;
    }



    }

