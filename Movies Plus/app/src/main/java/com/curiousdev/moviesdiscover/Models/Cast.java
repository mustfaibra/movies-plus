package com.curiousdev.moviesdiscover.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cast {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("cast")
    @Expose
    private List<CastMember> cast = null;

    public Cast(List<CastMember> members){
        this.cast=members;
    }

    public Integer getId() {
        return id;
    }

    public List<CastMember> getCastMember() {
        return cast;
    }

}

