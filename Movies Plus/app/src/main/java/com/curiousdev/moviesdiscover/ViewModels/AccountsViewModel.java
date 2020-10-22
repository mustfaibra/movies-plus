package com.curiousdev.moviesdiscover.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.curiousdev.moviesdiscover.Models.MovieApi;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.Models.User;

import retrofit2.Retrofit;

public class AccountsViewModel extends AndroidViewModel {
    Retrofit webClient;
    MoviesProvider apiServices;
    User loggedUserInfo,newUserInfo;
    public AccountsViewModel(@NonNull Application application) {
        super(application);
        webClient= MovieApi.getRetrofitInstance();
        apiServices=webClient.create(MoviesProvider.class);

    }
    public User login(String email,String password){
        return loggedUserInfo;
    }
    public User signUp(String userName,String email,String password,String confirmPassword){
        return newUserInfo;
    }
    private User checkLoginAuth(){
        return loggedUserInfo;
    }
    private User checkSignUpValidity(){
        return newUserInfo;
    }
}
