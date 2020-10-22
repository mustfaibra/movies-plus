package com.curiousdev.moviesdiscover.Models;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieApi {
    public static Retrofit retrofitInstance=null;
    public static OkHttpClient getBuilder(){
        return  new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build();
    }
    public static synchronized Retrofit getRetrofitInstance() {
       if (retrofitInstance==null){
           retrofitInstance=new Retrofit.Builder()
                                .client(getBuilder())
                                .addConverterFactory(GsonConverterFactory.create())
                                .baseUrl("https://api.themoviedb.org/3/")
                                .build();
       }
       return retrofitInstance;
    }
}
