package com.curiousdev.moviesdiscover.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "languages")
public class Language {
    @PrimaryKey (autoGenerate = true) public int pk;
    public int ownerId;
    @SerializedName("iso_639_1")
    @Expose
    public String shortcut;
    @SerializedName("english_name")
    @Expose
    public String englishName;
    @SerializedName("name")
    @Expose
    public String name;

    //in case we want to use it with tv show where its a list of string
    public Language(String name) {
        this.name = name;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getShortcut() {
        return shortcut;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getName() {
        return name;
    }
}
