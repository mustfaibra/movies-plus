package com.curiousdev.moviesdiscover.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.curiousdev.moviesdiscover.Models.PreferenceManager;
import com.curiousdev.moviesdiscover.R;

public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener {
    //containers
    RelativeLayout firstView,secondView,thirdView,dotsContainer;
    //widgets
    Button startAppBtn,next;
    View dot1,dot2,dot3;
    //animation
    Animation slideFromBottom,fadeIn,fadeOut;
//    //debugging const
    private static final String TAG = "WelcomeScreen";
    int switcherIndex=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        Log.d(TAG, "onCreate: ");
        //containers
        firstView=findViewById(R.id.first_view);
        secondView=findViewById(R.id.second_view);
        thirdView=findViewById(R.id.third_view);
        dotsContainer=findViewById(R.id.dots_container);
        //init the references
        startAppBtn=findViewById(R.id.start_app);
        next=findViewById(R.id.next_btn);
        slideFromBottom= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in);
        fadeIn= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_up);
        fadeOut= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_down);
        dot1=findViewById(R.id.dot1);
        dot2=findViewById(R.id.dot2);
        dot3=findViewById(R.id.dot3);
        //init the view by sliding in the first view
        firstView.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);
        firstView.startAnimation(fadeIn);
        next.startAnimation(fadeIn);

        //change view by button click
        next.setOnClickListener(this);
        startAppBtn.setOnClickListener(this);
    }

    private void changeTheView() {
        switcherIndex++;
        if (switcherIndex<3){
            if (switcherIndex==1){
                firstView.setVisibility(View.GONE);
                secondView.setVisibility(View.VISIBLE);
                firstView.startAnimation(fadeOut);
                secondView.startAnimation(fadeIn);
                dot2.setBackground(getDrawable(R.drawable.circle_dot_filled));
                dot1.setBackground(getDrawable(R.drawable.circle_dot_empty));
            }
            else if (switcherIndex==2){
                secondView.setVisibility(View.GONE);
                thirdView.setVisibility(View.VISIBLE);
                secondView.startAnimation(fadeOut);
                thirdView.startAnimation(fadeIn);
                dot3.setBackground(getDrawable(R.drawable.circle_dot_filled));
                dot2.setBackground(getDrawable(R.drawable.circle_dot_empty));
            }
        }
        else {
            dotsContainer.setAnimation(fadeOut);
            dotsContainer.setVisibility(View.GONE);
            thirdView.setVisibility(View.GONE);
            thirdView.startAnimation(fadeOut);
            next.startAnimation(fadeOut);
            next.setVisibility(View.GONE);
            Log.d(TAG, "run: time to show start button");
            startAppBtn.setVisibility(View.VISIBLE);
            startAppBtn.startAnimation(slideFromBottom);
        }
    }

    @Override
    public void onClick(View v) {
        int clickedViewId=v.getId();
        switch (clickedViewId){
            case R.id.start_app:{
                PreferenceManager.getInstance(getApplicationContext()).setUserStat();
                Log.d(TAG, "onClick: you are an old user now");
                Intent goToHome=new Intent(getApplicationContext(), FragmentsHost.class);
                startActivity(goToHome);
                finish();
                break;
            }
            case R.id.next_btn:{
                changeTheView();
                break;
            }
        }
    }
}