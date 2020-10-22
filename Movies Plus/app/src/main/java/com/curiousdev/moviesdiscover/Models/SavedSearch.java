package com.curiousdev.moviesdiscover.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_search")
public class SavedSearch {
    @PrimaryKey(autoGenerate = true) private int id;
    private String query;

    public SavedSearch(String query) {
        this.query = query;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getQuery() {
        return query;
    }
}
