
package com.curiousdev.moviesdiscover.Models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "tv_show_details")
public class TvShowDetails {
    @SerializedName("backdrop_path")
    @Expose
    public String backdropPath;
    @SerializedName("created_by")
    @Expose
    @Ignore
    public List<CreatedBy> createdBy = null;
    @SerializedName("episode_run_time")
    @Expose
    @Ignore
    public List<Integer> episodeRunTime = null;
    @SerializedName("first_air_date")
    @Expose
    public String firstAirDate;
    @SerializedName("genres")
    @Expose
    @Ignore
    public List<Genre> genres = null;
    @SerializedName("homepage")
    @Expose
    public String homepage;
    @SerializedName("id")
    @Expose
    @PrimaryKey public Integer id;
    @SerializedName("in_production")
    @Expose
    public Boolean inProduction;
    @SerializedName("languages")
    @Expose
    @Ignore
    public List<String> languages = null;
    @SerializedName("last_air_date")
    @Expose
    public String lastAirDate;
    @SerializedName("last_episode_to_air")
    @Expose
    @Ignore
    public LastEpisodeToAir lastEpisodeToAir;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("next_episode_to_air")
    @Expose
    @Ignore
    public Object nextEpisodeToAir;
    @SerializedName("networks")
    @Expose
    @Ignore
    public List<Network> networks = null;
    @SerializedName("number_of_episodes")
    @Expose
    public Integer numberOfEpisodes;
    @SerializedName("number_of_seasons")
    @Expose
    public Integer numberOfSeasons;
    @SerializedName("origin_country")
    @Expose
    @Ignore
    public List<String> originCountry = null;
    @SerializedName("original_language")
    @Expose
    public String originalLanguage;
    @SerializedName("original_name")
    @Expose
    public String originalName;
    @SerializedName("overview")
    @Expose
    public String overview;
    @SerializedName("popularity")
    @Expose
    public Double popularity;
    @SerializedName("poster_path")
    @Expose
    public String posterPath;
    @SerializedName("production_companies")
    @Expose
    @Ignore
    public List<ProductionCompany> productionCompanies = null;
    @SerializedName("seasons")
    @Expose
    @Ignore
    public List<Season> seasons = null;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("type")
    @Expose
    public String type;
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

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public void setVideos(Videos videos) {
        this.videos = videos;
    }

    public void setCast(Cast cast) {
        this.cast = cast;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public List<CreatedBy> getCreatedBy() {
        return createdBy;
    }

    public List<Integer> getEpisodeRunTime() {
        return episodeRunTime;
    }

    public String getFirstAirDate() {
        return firstAirDate;
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

    public Boolean getInProduction() {
        return inProduction;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public LastEpisodeToAir getLastEpisodeToAir() {
        return lastEpisodeToAir;
    }

    public String getName() {
        return name;
    }

    public Object getNextEpisodeToAir() {
        return nextEpisodeToAir;
    }

    public List<Network> getNetworks() {
        return networks;
    }

    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public List<String> getOriginCountry() {
        return originCountry;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOriginalName() {
        return originalName;
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

    public List<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
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
        }
        return trailer;
    }
}
