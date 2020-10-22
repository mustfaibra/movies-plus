package com.curiousdev.moviesdiscover.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.curiousdev.moviesdiscover.Models.PreferenceManager;
import com.curiousdev.moviesdiscover.R;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setBackgroundDrawable(getDrawable(R.drawable.movies_discover_splash));
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Log.d(TAG, "onCreate: ");
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: ");
                checkUser();
            }
        },3000);
    }

    private void checkUser() {
        if (PreferenceManager.getInstance(getApplicationContext()).isNewUser()){
            Log.d(TAG, "checkUser: new user");
            Intent goToNewUserPage=new Intent(getApplicationContext(), WelcomeScreen.class);
            startActivity(goToNewUserPage);
            finish();
        }
        else {
            Log.d(TAG, "checkUser: old user");
            Intent goToHome=new Intent(getApplicationContext(), FragmentsHost.class);
            startActivity(goToHome);
            finish();
        }
    }
}
