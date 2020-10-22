package com.curiousdev.moviesdiscover.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Person {

    @SerializedName("popularity")
    @Expose
    private Double popularity;
    @SerializedName("combined_credits")
    @Expose
    private CombinedCredits combinedCredits;
    @SerializedName("known_for_department")
    @Expose
    private String knownForDepartment;
    @SerializedName("gender")
    @Expose
    private Integer gender;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("profile_path")
    @Expose
    private String profilePath;
    @SerializedName("adult")
    @Expose
    private Boolean adult;
    @SerializedName("known_for")
    @Expose
    private List<Movie> knownFor = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("deathday")
    @Expose
    private String deathday;
    @SerializedName("also_known_as")
    @Expose
    private List<String> alsoKnownAs = null;
    @SerializedName("biography")
    @Expose
    private String biography;
    @SerializedName("place_of_birth")
    @Expose
    private String placeOfBirth;
    @SerializedName("imdb_id")
    @Expose
    private String imdbId;
    @SerializedName("homepage")
    @Expose
    private String homepage;

    public Double getPopularity() {
        return popularity;
    }
    public String getKnownForDepartment() {
        return knownForDepartment;
    }

    public Integer getGender() {
        return gender;
    }

    public Integer getId() {
        return id;
    }

    public CombinedCredits getCombinedCredits() {
        return combinedCredits;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public Boolean getAdult() {
        return adult;
    }

    public List<Movie> getKnownFor() {
        return knownFor;
    }

    public String getName() {
        return name;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getDeathday() {
        return deathday;
    }

    public List<String> getAlsoKnownAs() {
        return alsoKnownAs;
    }

    public String getBiography() {
        return biography;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public void setCombinedCredits(CombinedCredits combinedCredits) {
        this.combinedCredits = combinedCredits;
    }

    public void setKnownForDepartment(String knownForDepartment) {
        this.knownForDepartment = knownForDepartment;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public void setKnownFor(List<Movie> knownFor) {
        this.knownFor = knownFor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setDeathday(String deathday) {
        this.deathday = deathday;
    }

    public void setAlsoKnownAs(List<String> alsoKnownAs) {
        this.alsoKnownAs = alsoKnownAs;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public class CombinedCredits {

        @SerializedName("cast")
        @Expose
        private List<Movie> movies = null;

        public List<Movie> getMovies() {
            return movies;
        }

    }
}
