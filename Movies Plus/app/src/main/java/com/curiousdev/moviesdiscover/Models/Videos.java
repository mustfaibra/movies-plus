package com.curiousdev.moviesdiscover.Models;

import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Videos {
    @SerializedName("results")
    @Expose
    private List<VideoItem> trailers = null;

    public Videos(List<VideoItem> trailers) {
        this.trailers = trailers;
    }

    public List<VideoItem> getResults() {
        return trailers;
    }
}
