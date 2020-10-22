package com.curiousdev.moviesdiscover.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.curiousdev.moviesdiscover.Models.Cast;
import com.curiousdev.moviesdiscover.Models.MovieDetail;
import com.curiousdev.moviesdiscover.Models.SavedItem;
import com.curiousdev.moviesdiscover.ViewModels.SavedItemsViewModel;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.curiousdev.moviesdiscover.Adapters.CastRecyclerAdapter;
import com.curiousdev.moviesdiscover.Adapters.ReviewsAdapter;
import com.curiousdev.moviesdiscover.Models.CastMember;
import com.curiousdev.moviesdiscover.Models.Genre;
import com.curiousdev.moviesdiscover.Models.Language;
import com.curiousdev.moviesdiscover.Models.Review;
import com.curiousdev.moviesdiscover.Models.VideoItem;
import com.curiousdev.moviesdiscover.Models.Videos;
import com.curiousdev.moviesdiscover.R;
import com.curiousdev.moviesdiscover.ViewModels.MovieDetailViewModel;
import com.squareup.picasso.Picasso;


import java.util.List;

import io.paperdb.Paper;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.QUALITY;


public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener, CastRecyclerAdapter.ActorClicked {
    //ads
    AdView adView;
    private static final String TAG = "MovieDetailActivity";
    private String BASE_URL_IMG = "https://image.tmdb.org/t/p/";

    //current quality
    private String quality=QUALITY;

    //animation
    Animation slideIn,slideOut;
    //widgets
    TextView genres,movieRate,releaseDate,spokenLanguages,noOverviewAvailable,seeAllReviews,seeSimliarMovies;
    Button watchTrailer,retryBtn;
    ImageView moviePoster,favIcon;
    Toolbar toolbar;
    LottieAnimationView lottieLoading;
    //containers
    RelativeLayout imgContainer;
    NestedScrollView scrollView;
    WebView movieDescripion,movieTrailer;
    RecyclerView castRec;
    LinearLayout infoContainer;
    //font
    Typeface blackJackFont;
    //variables
    int heightScrolled=0;
    int screenHeight,screenWidth;
    int imgBasicHeight=0;
    int imgFinalHeight=0;
    boolean isSaved=false;
    private String movieName;
    private int movieId;
    private String moviePosterUrl;
    private double rate;
    private String movieOverview;
    List<CastMember> castList;
    MovieDetailViewModel detailViewModel;
    SavedItemsViewModel saveviewModel;
    private String trailerUrl="";

    //booleans to control onChange repeatuation
    private boolean isFavClicked=false;
    private boolean isWatchedClicked=false;
    private boolean isToWatchClicked=false;
    //booleans to control onChange repeatuation
    private boolean isFaved=false;
    private boolean isWatched=false;
    private boolean isToWatch=false;

    //toolbar menu
    private Menu toolbarMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Paper.init(this);
        //getting data sent with intent
        movieName=getIntent().getExtras().getString("movie name");
        movieId=getIntent().getExtras().getInt("movie id");
        moviePosterUrl=getIntent().getExtras().getString("movie cover");
        rate =getIntent().getExtras().getDouble("movie rate");
        movieOverview =getIntent().getExtras().getString("movie overview");
        Log.d(TAG, "value got :name \n"+movieName+"id:\n"+movieId+"poster:\n"+moviePosterUrl+"rate:\n"+rate+"overview:\n"+movieOverview);


        //init activity
        initActivity();
        Log.d(TAG, "onCreate: movie poster url "+moviePosterUrl);
        BASE_URL_IMG=BASE_URL_IMG.concat(quality);
        String posterLink=BASE_URL_IMG+moviePosterUrl;


        if (!quality.equalsIgnoreCase("none")){
            Picasso.with(this).load(posterLink).error(R.color.light_gray).into(moviePoster);
        }
        else {
//            watchTrailer.setVisibility(View.GONE);
//            movieTrailer.setBackground(getDrawable(R.drawable.image_turned_off_simple));
            Picasso.with(this).load(R.drawable.image_turned_off_simple).into(moviePoster);
        }


        //view models
        saveviewModel=ViewModelProviders.of(this).get(SavedItemsViewModel.class);
        saveviewModel.isItemInFavList(movieId).observe(this, new Observer<SavedItem>() {
            @Override
            public void onChanged(SavedItem savedItem) {
                if (savedItem !=null){
                    Log.d(TAG, "onChanged: fav");
                    isFaved=true;
                }
                else {
                    isFaved=false;
                    Log.d(TAG, "onChanged: not fav");
                }
            }
        });
        saveviewModel.isItemInWatchedList(movieId).observe(this, new Observer<SavedItem>() {
            @Override
            public void onChanged(SavedItem savedItem) {
                if (savedItem !=null) {
                    isWatched=true;
                    Log.d(TAG, "onChanged: watched");
                }
                else {
                    isWatched=false;
                    Log.d(TAG, "onChanged: not watched");
                }
            }
        });
        saveviewModel.isItemInToWatchList(movieId).observe(this, new Observer<SavedItem>() {
            @Override
            public void onChanged(SavedItem savedItem) {
                if (savedItem !=null) {
                    isToWatch=true;
                    Log.d(TAG, "onChanged: to watched");
                }
                else {
                    isToWatch=false;
                    Log.d(TAG, "onChanged: not to watch");
                }
            }
        });
        detailViewModel= ViewModelProviders.of(this).get(MovieDetailViewModel.class);
        detailViewModel.getMovieDetailFromRoom(movieId).observe(this, details->{
            if (details==null){
                Log.d(TAG, "onCreate: not exist in room ");
                detailViewModel.getMovieDetail(movieId).observe(this, movieDetail -> {
                    if (movieDetail==null){
                        Log.d(TAG, "onCreate: details object is null");
                        Handler handler=new Handler();
                        handler.postDelayed(() ->{
                            lottieLoading.cancelAnimation();
                            lottieLoading.setVisibility(View.GONE);
                            retryBtn.setVisibility(View.VISIBLE);
                        },2000);
                    }
                    else {
                        upDateUi(movieDetail);
                    }
                });
            }
            else {
                Log.d(TAG, "onCreate: its exist in room");
                detailViewModel.getGenres(movieId).observe(this,savedGenres->{
                    detailViewModel.getLanguages(movieId).observe(this,languages -> {
                        detailViewModel.getMembers(movieId).observe(this,members -> {
                            detailViewModel.getVideos(movieId).observe(this,videoItems -> {
                                details.setGenres(savedGenres);
                                details.setCast(new Cast(members));
                                details.setVideos(new Videos(videoItems));
                                details.setSpokenLanguages(languages);
                                upDateUi(details);

                            });
                        });
                    });
                });
            }
        });


    }

    private void upDateUi(MovieDetail movieDetail) {
        lottieLoading.cancelAnimation();
        lottieLoading.setVisibility(View.GONE);
        infoContainer.setVisibility(View.VISIBLE);

        //set trailer
        trailerUrl=movieDetail.getTrailer();
        if (TextUtils.isEmpty(trailerUrl)){
            detailViewModel.getMovieDefaultVideos(movieId).observe(MovieDetailActivity.this, new Observer<Videos>() {
                @Override
                public void onChanged(Videos videos) {
                    if (videos!=null){
                        for (VideoItem video:videos.getResults()){
                            if (video.getType().equalsIgnoreCase("trailer")){
                                trailerUrl=video.getKey();
                            }
                        }
                    }
                }
            });
        }
        Log.d(TAG, "onCreate: trailer url"+trailerUrl);
        settingDescWebView(movieDetail.getOverview());

        String rateBuilder =String.valueOf(movieDetail.getVoteAverage());
        movieRate.setText(rateBuilder);
        releaseDate.setText(movieDetail.getReleaseDate());
//                if (movieDetail.getVideos().getResults().get(0)!=null){
//                    trailerUrl=movieDetail.getVideos().getResults().get(0).getKey();
//                }
        StringBuilder tempGenres=new StringBuilder();
        for (Genre genre:movieDetail.getGenres()) {
            tempGenres.append(genre.getGenreName());
            tempGenres.append("     ");
        }
        genres.setText(tempGenres);
        StringBuilder tempLanguage=new StringBuilder();
        for (int i=0;i<movieDetail.getSpokenLanguages().size();i++) {
            Language language=movieDetail.getSpokenLanguages().get(i);
            tempLanguage.append(language.getShortcut());
            if (i<movieDetail.getSpokenLanguages().size()-1){
                tempLanguage.append(" & ");
            }
        }
        spokenLanguages.setText(tempLanguage);
        //setting cast recycler images
        castList=movieDetail.getCast().getCastMember();
        setCastRecycler();
    }

    /*
    if the layout of all reviews is shown when pressing back,hide it
    else make the normal back
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initActivity() {
        //animation
        slideIn= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.reviews_slide_in);
        slideOut= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.reviews_slide_out);
        //font
        blackJackFont=Typeface.createFromAsset(getAssets(),"fonts/blackjack.otf");
        //toolbar
        toolbar=findViewById(R.id.movie_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(movieName);
        //textvview
//        movieName=findViewById(R.id.movie_name);
        //Imageview
        moviePoster=findViewById(R.id.movie_poster);
        //text views
        genres=findViewById(R.id.movie_genres);
        movieRate=findViewById(R.id.movie_rate);
        releaseDate=findViewById(R.id.release_date);
        spokenLanguages=findViewById(R.id.spoken_languages);
        noOverviewAvailable=findViewById(R.id.overview_not_available);
        seeAllReviews=findViewById(R.id.see_all_reviews);
        seeAllReviews.setOnClickListener(this);
        seeSimliarMovies=findViewById(R.id.similar_movies);
        seeSimliarMovies.setOnClickListener(this);

        //lottie
        lottieLoading=findViewById(R.id.lottie_loading);
        //buttons
        watchTrailer=findViewById(R.id.trailer_btn);
        retryBtn=findViewById(R.id.retry_button);
        watchTrailer.setOnClickListener(this);
        retryBtn.setOnClickListener(this);
        //containers
        imgContainer=findViewById(R.id.img_container);
        scrollView=findViewById(R.id.scroll);
        movieDescripion=findViewById(R.id.movie_description);
        movieTrailer=findViewById(R.id.movie_trailer);
        castRec=findViewById(R.id.cast_recycler);
        infoContainer=findViewById(R.id.movie_info);

        initLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.movie_detial_toolbar_item,menu);
        toolbarMenu=toolbar.getMenu();
        Log.d(TAG, "onCreateOptionsMenu: items num is "+toolbarMenu.size());
        if (isToWatch){
            toolbarMenu.getItem(2).setTitle(getString(R.string.remove_from_watch_later));
        }
        if(isWatched){
            toolbarMenu.getItem(1).setTitle(getString(R.string.remove_from_watched));
        }
        if (isFaved){
            toolbarMenu.getItem(0).setIcon(R.drawable.faved);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case android.R.id.home:{
                onBackPressed();
                break;
            }
            case R.id.movie_fav:{
                isFavClicked=true;
                //we have to check if its saved before so that we delete it if its already saved
                saveviewModel.isItemInFavList(movieId).observe(this, new Observer<SavedItem>() {
                    @Override
                    public void onChanged(SavedItem savedItem) {
                        if (isFavClicked){
                            isFavClicked=false;
                            //if returned object is null ,that mean its not saved before ,and we should save it now
                            if (savedItem ==null){
                                saveviewModel.saveItem(new SavedItem(movieId,movieName,moviePosterUrl,movieOverview,rate,"favorite","movie"));
                                toolbarMenu.getItem(0).setIcon(R.drawable.faved);
                            }
                            //already saved,we should delete it ,from our room
                            else {
                                saveviewModel.deleteItem(savedItem);
                                toolbarMenu.getItem(0).setIcon(R.drawable.heart);
                            }
                        }
                    }
                });
                break;
            }
            case R.id.movie_to_watch:{
                isToWatchClicked=true;
                //we have to check if its saved before so that we delete it if its already saved
                saveviewModel.isItemInToWatchList(movieId).observe(this, new Observer<SavedItem>() {
                    @Override
                    public void onChanged(SavedItem savedItem) {
                        if (isToWatchClicked){
                            isToWatchClicked=false;
                            //if returned object is null ,that mean its not saved before ,and we should save it now
                            if (savedItem ==null){
                                saveviewModel.saveItem(new SavedItem(movieId,movieName,moviePosterUrl,movieOverview,rate,"to watch","movie"));
                                toolbarMenu.getItem(1).setTitle(getString(R.string.remove_from_watched));
                                showSnackBar("Added to your to watch list");
                            }
                            //already saved,we should delete it ,from our room
                            else {
                                saveviewModel.deleteItem(savedItem);
                                toolbarMenu.getItem(1).setTitle(getString(R.string.add_to_watched));
                                showSnackBar("removed from your to watch list");
                            }
                        }
                    }
                });

                break;
            }
            case R.id.movie_watched:{
                isWatchedClicked=true;
                //we have to check if its saved before so that we delete it if its already saved
                saveviewModel.isItemInWatchedList(movieId).observe(this, new Observer<SavedItem>() {
                    @Override
                    public void onChanged(SavedItem savedItem) {
                        //to prevent repeatation we use a boolean
                        if(isWatchedClicked){
                            isWatchedClicked=false;
                            //if returned object is null ,that mean its not saved before ,and we should save it now
                            if (savedItem ==null){
                                saveviewModel.saveItem(new SavedItem(movieId,movieName,moviePosterUrl,movieOverview,rate,"watched","movie"));
                                toolbarMenu.getItem(1).setTitle(getString(R.string.remove_from_watch_later));
                                showSnackBar("Added to your watched list");
                            }
                            //already saved,we should delete it ,from our room
                            else {
                                saveviewModel.deleteItem(savedItem);
                                toolbarMenu.getItem(1).setTitle(getString(R.string.add_to_watch_later));
                                showSnackBar("removed from your watched list");
                            }
                        }
                    }
                });
                break;
            }
        }
        return true;
    }

    /*
    setting of movies cast's recyclerView
     */
    private void setCastRecycler() {
        CastRecyclerAdapter castRecyclerAdapter=new CastRecyclerAdapter(getApplicationContext(),castList);
        castRecyclerAdapter.setOnActorClicked(this);
        castRec.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));
        castRec.setAdapter(castRecyclerAdapter);

    }

    private void settingTrailerWebView(String url) {
        Log.d(TAG, "settingTrailerWebView: url is :"+url);
        String base_url="https://www.youtube.com/watch?v="+url;
        String fullUrl=base_url+url;
        String showTrailerInApp=Paper.book().read("trailer inside app","true");
        if (showTrailerInApp.equalsIgnoreCase("true")){
            Toast.makeText(this, "Youtube API is not ready yet !", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent youtubeAppIntent=new Intent(Intent.ACTION_VIEW,Uri.parse("vnd.youtube:"+url));
            Intent youtubeWebIntent=new Intent(Intent.ACTION_VIEW,Uri.parse(fullUrl));
            try{
                startActivity(youtubeAppIntent);
            }
            catch (ActivityNotFoundException ex){
                startActivity(youtubeWebIntent);
            }
        }

    }

    private void settingDescWebView(String desc) {
        if (!TextUtils.isEmpty(desc)){
            String txt="";
            txt+="<body style=\" background:#050320;\"><p align=\"justify\" style=\"color:#CEC8C8;font-size:12px; \">";
            txt+=desc;
            txt+="</p></body>";
            movieDescripion.loadData(txt ,"text/html; charset=UTF-8",null);
        }
        else {
            movieDescripion.setVisibility(View.GONE);
            noOverviewAvailable.setVisibility(View.VISIBLE);
        }
    }


    private void initLayout() {
        screenHeight=getWindowManager().getDefaultDisplay().getHeight();
        screenWidth=getWindowManager().getDefaultDisplay().getWidth();
        Log.d(TAG, "initLayout: width is "+screenWidth);
        Log.d(TAG, "initLayout: height is "+screenHeight);
        //give basic and final height of image thier actual values
        imgBasicHeight=screenHeight-(screenHeight/4);
        imgFinalHeight=imgBasicHeight/2;
        moviePoster.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth,imgBasicHeight));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.trailer_btn:{
                watchTrailer.setVisibility(View.GONE);
                movieTrailer.setVisibility(View.VISIBLE);
                settingTrailerWebView(trailerUrl);
                break;
            }
            case R.id.retry_button:{
                retryBtn.setVisibility(View.GONE);
                lottieLoading.setVisibility(View.VISIBLE);
                lottieLoading.playAnimation();
                detailViewModel.getMovieDetail(movieId);
                break;
            }
            case R.id.see_all_reviews:{
                Intent goToReviews=new Intent(this,ReviewsActivity.class);
                goToReviews.putExtra("type","movie");
                goToReviews.putExtra("id",movieId);
                startActivity(goToReviews);
                break;
            }
            case R.id.similar_movies:{
                Intent toSimilarMovies=new Intent(this,SimilarMoviesActivity.class);
                toSimilarMovies.putExtra("movie name",movieName);
                toSimilarMovies.putExtra("movie id",movieId);
                startActivity(toSimilarMovies);
                break;
            }
        }
    }

    private void showSnackBar(String msg){
        Snackbar.make(toolbar,msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onActorClicked(CastMember member) {
        Intent toCastMemberDetails=new Intent(this,PersonDetails.class);
        toCastMemberDetails.putExtra("person_id",member.getId());
        toCastMemberDetails.putExtra("person_name",member.getName());
        startActivity(toCastMemberDetails);
    }
}
