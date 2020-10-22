package com.curiousdev.moviesdiscover.Models;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "genres")
public class Genre {

    @PrimaryKey(autoGenerate = true) public int pk;
    public int ownerId;
    @SerializedName("genres")
    @Expose
    @Ignore
    public List<Genre> genres = null;

    @SerializedName("id")
    @Expose
    public Integer genreId;
    @SerializedName("name")
    @Expose
    public String genreName;

    //variables that used at selecting fav genres
    boolean isSelected=false;


    public void setPk(int pk) {
        this.pk = pk;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected(){
        return isSelected;
    }

}
