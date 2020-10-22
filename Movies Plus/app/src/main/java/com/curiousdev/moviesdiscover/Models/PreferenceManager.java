package com.curiousdev.moviesdiscover.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class PreferenceManager {
    public static final String TAG = "PreferenceManager";
    Context mcontext;
    public static PreferenceManager prefManager;
    public static final String prefName="movies discover data";
    public static final String newUserPref="user_stat";
    public static final String userNamePref="user_name";
    public static final String userPassPref="user_password";
    public static final String userEmailPref="user_email";
    public static final String userImgpref="user_img";
    public static final String favGenres="fav_genre";


    public PreferenceManager(Context context) {
        mcontext=context;
    }
    
    public static PreferenceManager getInstance(Context context) {
        if (prefManager==null){
            prefManager=new PreferenceManager(context);
        }
        return prefManager;
    }

    public void setUserStat(){
        SharedPreferences sharedPreferences=mcontext.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(newUserPref,false);
        editor.apply();
    }

    public boolean isNewUser(){
        return mcontext.getSharedPreferences(prefName,Context.MODE_PRIVATE).getBoolean(newUserPref,true);
    }

    public void setFavGenresAsSet(){
        SharedPreferences sharedPreferences=mcontext.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(favGenres,true);
        editor.apply();

    }
    public boolean favGenresIsSet(){
        return mcontext.getSharedPreferences(prefName,Context.MODE_PRIVATE).getBoolean(favGenres,false);

    }
    public void login(User user) {
        Log.d(TAG, "login: user is "+user.getName());

        SharedPreferences sharedPreferences=mcontext.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(userEmailPref,user.getEmail());
        editor.putString(userPassPref,user.getPassword());
        editor.putString(userNamePref,user.getName());
        editor.putString(userImgpref,user.getImg());
        editor.apply();

    }
    public boolean isLogged(){
        SharedPreferences sharedPreferences=mcontext.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        return sharedPreferences.getString(userEmailPref,null)!=null;
    }
    public User getUser(){
        SharedPreferences pref=mcontext.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        User user= new User(
                pref.getString(userNamePref,"null"),
                pref.getString(userEmailPref,"null"),
                pref.getString(userPassPref,"null"),
                pref.getString(userImgpref,"null")
        );
        Log.d(TAG, "getUser: user img is "+user.getImg());
        return user;
    }
    public boolean logout(){
        SharedPreferences sharedPreferences=mcontext.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        setUserStat();

        Log.d(TAG, "logout: done");
        return true;
    }
}
