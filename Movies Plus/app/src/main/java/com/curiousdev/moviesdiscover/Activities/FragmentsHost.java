package com.curiousdev.moviesdiscover.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.curiousdev.moviesdiscover.Fragments.AccountsFragment;
import com.curiousdev.moviesdiscover.Fragments.ExploreFragment;
import com.curiousdev.moviesdiscover.Fragments.FavoriteFragment;
import com.curiousdev.moviesdiscover.Fragments.FragmentHome;
import com.curiousdev.moviesdiscover.Fragments.SavedFragment;
import com.curiousdev.moviesdiscover.Fragments.ToWatchFragment;
import com.curiousdev.moviesdiscover.Fragments.WatchedFragment;
import com.curiousdev.moviesdiscover.R;

import java.util.Arrays;

import io.paperdb.Paper;

public class FragmentsHost extends AppCompatActivity {
    InterstitialAd interstitialAd;
    //container
    RelativeLayout hostContainer;
    FrameLayout fragmentContainer;
    //host widgets
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggler;
    //Fragment and Fragment Transaction act as global
    Fragment activeFragment;
    FragmentTransaction fTransaction;
    //all the possible destinations
    FragmentHome fragmentHome;
    SavedFragment savedFragment;
    ExploreFragment exploreFragment;
    AccountsFragment accountsFragment;
    FavoriteFragment favoriteFragment;
    ToWatchFragment toWatchFragment;
    WatchedFragment watchedFragment;
    //indicator int variable to indicate the current fragment
    int activeFragmentIndicator = 0;
    Menu fragmentsMenu;
    
    private boolean isSearching = false;
    //img view
    ImageView searchIcon;
    //textview
    TextView toolbarTitle;
    //debugging const
    private static final String TAG = "Host";
    //static refernce
    public static Context hostContext = null;
    //an const to animate the drawer
    final float drawerScale=0.9f;
    //global variable that indicate the quality of image
    public static String QUALITY;

    //booleans to double click effect
    private boolean clickedOnce;

    @Override
    protected void onRestart() {
        super.onRestart();
        AdRequest adRequest=new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (interstitialAd.isLoaded()){
                    interstitialAd.show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdRequest adRequest=new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (interstitialAd.isLoaded()){
                    interstitialAd.show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hostContext = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments_host);
        //ads
        interstitialAd=new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.between_activities_unitId));
        AdRequest adRequest=new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (interstitialAd.isLoaded()){
                    interstitialAd.show();
                }
            }
        });
        Paper.init(this);
        QUALITY=Paper.book().read("quality","w185");

        Log.d(TAG, "onCreate: ");
        //reference all fragments
        fragmentHome = FragmentHome.getHomeInstance();
        savedFragment = new SavedFragment();
        exploreFragment =ExploreFragment.getExploreFragmentInstance();
        accountsFragment=AccountsFragment.getAccountsFragmentInstance();
        favoriteFragment=FavoriteFragment.getFavoriteFragmentInstance();
        watchedFragment=WatchedFragment.getWatchedInstance();
        toWatchFragment=ToWatchFragment.getToWatchInstance();
        //check whether or not there is an active fragmentz
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: its nulll");
            activeFragment = fragmentHome;
            initHostActivity();
        } else {
            initWidget();
            setToolbar(); 
            Log.d(TAG, "onCreate: it not null");
            activeFragmentIndicator=savedInstanceState.getInt("current fragment");
            navigationView=findViewById(R.id.navigation);
            fragmentsMenu=navigationView.getMenu();
            onDrawerItemSelected(fragmentsMenu.getItem(activeFragmentIndicator));

        }

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("current fragment", activeFragmentIndicator);
        super.onSaveInstanceState(outState);
    }

    private void initHostActivity() {
        initWidget();

        setToolbar();
        initFragment(fragmentHome);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //initilize the fragments host with home
        navigationView.getMenu().getItem(0).setChecked(true);
        fragmentsMenu=navigationView.getMenu();

        toggler = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer);
        toggler.setDrawerIndicatorEnabled(true);
        toggler.syncState();
        drawer.addDrawerListener(toggler);

        //add listener to navigatin drawer
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            for (int i = 0; i < fragmentsMenu.size(); i++) {
                if (fragmentsMenu.getItem(i)==menuItem){
                    Log.d(TAG, "setToolbar: switching to fragment with index "+i);
                    activeFragmentIndicator=i;
                }
            }
            onDrawerItemSelected(menuItem);
            return true;
        });
        navigationView.getMenu().findItem(R.id.drawer_home).setChecked(true);

//        //temp hide the profile and the logout optionn from drawer menu until login is ready and user has logged in
//        navigationView.getMenu().findItem(R.id.drawer_log_out).setVisible(false);
//        navigationView.getMenu().findItem(R.id.drawer_account).setVisible(false);

        //set event handler to search icon
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSearch=new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(toSearch);
            }
        });
    }

    private void initWidget(){
        //container
        hostContainer=findViewById(R.id.host_container);
        fragmentContainer=findViewById(R.id.frame_container);

        //textview
        toolbarTitle=findViewById(R.id.home_toolbar_title);
        //navigation view
        navigationView = findViewById(R.id.navigation);
        //toolbar
        toolbar = findViewById(R.id.toolbar);
        //set up drawer with its toggler
        drawer = findViewById(R.id.drawer);
        //search icon
        searchIcon=toolbar.findViewById(R.id.search_movie_icon);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(activeFragment==fragmentHome){
                if (clickedOnce){
                    super.onBackPressed();
                }
                else{
                    clickedOnce=true;
                    Toast.makeText(hostContext, getString(R.string.click_again), Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(() -> {
                    clickedOnce=false;
                },2000);

            }
            else {
                onDrawerItemSelected(fragmentsMenu.getItem(0));
            }
        }

    }

//    private void setDrawerAnimation(){
//        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                LinearLayout hostContainer=findViewById(R.id.host_container);
//                final float diffScaleOffset=slideOffset*(1-drawerScale);
//                final float offsetScale=1-diffScaleOffset;
//                hostContainer.setScaleX(offsetScale);
//                hostContainer.setScaleY(offsetScale);
//                //now move the view based on scale width
//                final float xOffSet=drawer.getWidth()*slideOffset;
//                final float xOffsetDiff=hostContainer.getWidth()*diffScaleOffset*4;
//                final float xTransition=xOffSet-xOffsetDiff;
//                hostContainer.setTranslationX(xTransition);
//            }
//        });
//    }
    private void onDrawerItemSelected(MenuItem menuItem) {
        if (drawer!=null){
            drawer.closeDrawer(GravityCompat.START);
        }
        Menu navMenu=navigationView.getMenu();
        //making sure that we unchecked all the previous selections
        for (int i = 0; i < navMenu.size(); i++) {
            navMenu.getItem(i).setChecked(false);
        }
        //mark the new selected item as checked
        menuItem.setChecked(true);
        if(menuItem.isChecked()){
            Log.d(TAG, "onDrawerItemSelected: current is "+menuItem.getTitle());
        }
        switch (menuItem.getItemId()) {
            case R.id.drawer_home: {
                //set title of the toolbar to indicate the page
                toolbarTitle.setText(getString(R.string.home));
                changeFragment(fragmentHome);
                break;
            }
            case R.id.drawer_explore: {
                //set title of the toolbar to indicate the page
                toolbarTitle.setText(getString(R.string.explore));
                changeFragment(exploreFragment);
                break;
            }
            case R.id.drawer_favorite: {
                //set title of the toolbar to indicate the page
                toolbarTitle.setText(getString(R.string.fav));
                changeFragment(favoriteFragment);
                break;
            }
            case R.id.drawer_to_watch: {
                //set title of the toolbar to indicate the page
                toolbarTitle.setText(getString(R.string.to_watch));
                changeFragment(toWatchFragment);
                break;
            }
            case R.id.drawer_watched: {
                //set title of the toolbar to indicate the page
                toolbarTitle.setText(getString(R.string.watched));
                changeFragment(watchedFragment);
                break;
            }
//
//            case R.id.drawer_join: {
//                //set title of the toolbar to indicate the page
//                toolbarTitle.setText("Join TMDb");
//                changeFragment(accountsFragment);
//                break;
//            }
            case R.id.drawer_setting: {
                Intent goToSetting=new Intent(this,SettingActivity.class);
                startActivity(goToSetting);
                break;
            }
            case R.id.drawer_about: {
                Intent goToAbout=new Intent(this,AboutActivity.class);
                startActivity(goToAbout);
                break;
            }
            case R.id.drawer_rate_us:{
                Toast.makeText(hostContext, "We are working on this right now .", Toast.LENGTH_SHORT).show();
            }
            }

    }

//    @Override
//    public void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        toggler.syncState();
//    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggler.onConfigurationChanged(newConfig);
    }

    //opening and closing the drawer
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggler.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home: {
                drawer.openDrawer(GravityCompat.START);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initFragment(Fragment home) {
        Log.d(TAG, "initFrgment: ");
        activeFragment = home;
        toolbarTitle.setText(getString(R.string.home));

        fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.replace(R.id.frame_container, activeFragment);
        fTransaction.commit();
        Log.d(TAG, "initFragment: updated");
    }

    private void changeFragment(Fragment fragment) {
        Log.d(TAG, "updateFragment: ");
        fTransaction = getSupportFragmentManager().beginTransaction();
        activeFragment=fragment;
        fTransaction.replace(R.id.frame_container, fragment);
        fTransaction.commit();
        Log.d(TAG, "updateFragment: updated");
    }


}
