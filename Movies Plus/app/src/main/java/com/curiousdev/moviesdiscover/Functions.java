package com.curiousdev.moviesdiscover;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.curiousdev.moviesdiscover.Models.Genre;
import java.util.List;


public class Functions {
    private static final String TAG = "Functions";
    private static Context mcontext;
    //genres list
    List<Genre> genres;
    public Functions(Context context){
        this.mcontext=context;
    }
    public static boolean isNetworkAvailable(Context context){
        boolean connectedToWifi=false;
        boolean connectedToMobile=false;
        if (context==null){
            return false;
        }
        ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info=cm.getAllNetworkInfo();
        for (NetworkInfo n:info){
            if (n.getTypeName().equalsIgnoreCase("wifi")){
                if (n.isConnected()){
                    connectedToWifi=true;
                }
            }
            else if (n.getTypeName().equalsIgnoreCase("mobile")){
                if (n.isConnected()){
                    connectedToMobile=true;
                }
            }
        }
        return connectedToMobile||connectedToWifi;
    }



}
