package com.curiousdev.moviesdiscover.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.curiousdev.moviesdiscover.Adapters.SeasonsAdapter;
import com.curiousdev.moviesdiscover.Models.Cast;
import com.curiousdev.moviesdiscover.Models.Language;
import com.curiousdev.moviesdiscover.Models.SavedItem;
import com.curiousdev.moviesdiscover.Models.Season;
import com.curiousdev.moviesdiscover.Models.TvShowDetails;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.curiousdev.moviesdiscover.Adapters.CastRecyclerAdapter;
import com.curiousdev.moviesdiscover.Models.CastMember;
import com.curiousdev.moviesdiscover.Models.Genre;
import com.curiousdev.moviesdiscover.Models.VideoItem;
import com.curiousdev.moviesdiscover.Models.Videos;
import com.curiousdev.moviesdiscover.R;
import com.curiousdev.moviesdiscover.ViewModels.TvShowDetailViewModel;
import com.curiousdev.moviesdiscover.ViewModels.SavedItemsViewModel;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.QUALITY;


public class TvShowDetailActivity extends AppCompatActivity implements
        View.OnClickListener,CastRecyclerAdapter.ActorClicked, SeasonsAdapter.OnCardClickListener {
    //ads
    AdView adView;
    private static final String TAG = "TvShowDetailActivity";
    private String BASE_URL_IMG = "https://image.tmdb.org/t/p/";

    //parent container
    CoordinatorLayout parentContainer;
    //animation
    Animation slideIn,slideOut;
    //widgets
    TextView genres,tvshowRate,votersCount,tvShowStatus,releaseDate,spokenLanguages,noOverviewAvailable,seeAllReviews,seeSimliarTvShows;
    Button watchTrailer,retryBtn;
    ImageView tvshowPoster,hideSeasonDetailArrow;
    Toolbar toolbar;
    LottieAnimationView lottieLoading;
    //containers
    RelativeLayout imgContainer;
    private LinearLayout seasonsContainer;
    NestedScrollView scrollView;
    WebView tvshowDescripion,tvshowTrailer;
    RecyclerView seasonsRec,castRec;
    LinearLayout infoContainer;
    //font
    Typeface blackJackFont;
    //variables
    int screenHeight,screenWidth;
    int imgBasicHeight=0;
    int imgFinalHeight=0;
    boolean isSaved=false;
    private String tvshowName;
    private int tvshowId;
    private String tvshowPosterUrl;
    private double rate;
    private String tvshowOverview;
    List<CastMember> castList;
    List<Season> seasons;
    TvShowDetailViewModel detailViewModel;
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
        setContentView(R.layout.activity_show_details);
        Paper.init(this);
        //getting data sent with intent
        tvshowName=getIntent().getExtras().getString("tv show name");
        tvshowId=getIntent().getExtras().getInt("tv show id");
        tvshowPosterUrl=getIntent().getExtras().getString("tv show cover");
        rate =getIntent().getExtras().getDouble("tv show rate");
        tvshowOverview =getIntent().getExtras().getString("tv show overview");


        //init activity
        initActivity();

        //current quality
        String quality=QUALITY;
        BASE_URL_IMG=BASE_URL_IMG.concat(quality);
        String posterLink=BASE_URL_IMG+tvshowPosterUrl;


        if (!quality.equalsIgnoreCase("none")){
            Picasso.with(this).load(posterLink).error(R.color.light_gray).into(tvshowPoster);
        }
        else {
//            watchTrailer.setVisibility(View.GONE);
//            tvshowTrailer.setBackground(getDrawable(R.drawable.image_turned_off_simple));
            Picasso.with(this).load(R.drawable.image_turned_off_simple).into(tvshowPoster);
        }


        //view models
        saveviewModel=ViewModelProviders.of(this).get(SavedItemsViewModel.class);
        saveviewModel.isItemInFavList(tvshowId).observe(this, new Observer<SavedItem>() {
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
        saveviewModel.isItemInWatchedList(tvshowId).observe(this, new Observer<SavedItem>() {
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
        saveviewModel.isItemInToWatchList(tvshowId).observe(this, new Observer<SavedItem>() {
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
        detailViewModel= ViewModelProviders.of(this).get(TvShowDetailViewModel.class);
        detailViewModel.getDetailsFromRoom(tvshowId).observe(this,detail->{
            if (detail==null){
                detailViewModel.getTvShowDetail(tvshowId).observe(this, tvshowDetail -> {
                    if (tvshowDetail==null){
                        Log.d(TAG, "onCreate: details object is null");
                        Handler handler=new Handler();
                        handler.postDelayed(() ->{
                            lottieLoading.cancelAnimation();
                            lottieLoading.setVisibility(View.GONE);
                            retryBtn.setVisibility(View.VISIBLE);
                        },2000);
                    }
                    else {
                        updateUi(tvshowDetail);
                    }
                });

            }
            else {
                detailViewModel.getGenres(tvshowId).observe(this,genres->{
                    detailViewModel.getVideos(tvshowId).observe(this,videos->{
                            detailViewModel.getMembers(tvshowId).observe(this,members -> {
                                detailViewModel.getSeasons(tvshowId).observe(this,seasons->{
                                    detailViewModel.getLanguages(tvshowId).observe(this,languages->{
                                        //we have to convert langauges list to string list
                                        List<String> langs=new ArrayList<>();
                                        for (Language language:languages){
                                            langs.add(language.getName());
                                        }
                                        detail.setLanguages(langs);
                                        detail.setCast(new Cast(members));
                                        detail.setVideos(new Videos(videos));
                                        detail.setGenres(genres);
                                        detail.setSeasons(seasons);
                                        updateUi(detail);
                                    });
                                });
                            });
                        });
                });
            }
        });


    }

    private void updateUi(TvShowDetails tvshowDetail) {
        lottieLoading.cancelAnimation();
        lottieLoading.setVisibility(View.GONE);
        infoContainer.setVisibility(View.VISIBLE);

        //set trailer
        trailerUrl=tvshowDetail.getTrailer();
        if (TextUtils.isEmpty(trailerUrl)){
            detailViewModel.getTvShowDefaultVideos(tvshowId).observe(TvShowDetailActivity.this, new Observer<Videos>() {
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
        settingDescWebView(tvshowDetail.getOverview());

        String rateBuilder =String.valueOf(tvshowDetail.getVoteAverage());
        tvshowRate.setText(rateBuilder);
        String allVoters = "( " + tvshowDetail.getVoteCount() + " )";
        votersCount.setText(allVoters);
        String status=tvshowDetail.getStatus();
        status=status.concat("\t\t"+tvshowDetail.getNumberOfEpisodes().toString());
        status=status.concat(" "+getString(R.string.episode));
        tvShowStatus.setText(status);
        releaseDate.setText(tvshowDetail.getFirstAirDate());
//                if (tvshowDetail.getVideos().getResults().get(0)!=null){
//                    trailerUrl=tvshowDetail.getVideos().getResults().get(0).getKey();
//                }
        StringBuilder tempGenres=new StringBuilder();
        for (Genre genre:tvshowDetail.getGenres()) {
            tempGenres.append(genre.getGenreName());
            tempGenres.append("     ");
        }
        genres.setText(tempGenres);
        StringBuilder tempLanguage=new StringBuilder();
        for (int i=0;i<tvshowDetail.getLanguages().size();i++) {
            String language=tvshowDetail.getLanguages().get(i);
            tempLanguage.append(language);
            if (i<tvshowDetail.getLanguages().size()-1){
                tempLanguage.append(" & ");
            }
        }
        spokenLanguages.setText(tempLanguage);
        //setting seasons recycler
        seasons=tvshowDetail.getSeasons();
        setSeasonsRecycler();
        //setting cast recycler images
        castList=tvshowDetail.getCast().getCastMember();
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
        toolbar=findViewById(R.id.tv_show_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(tvshowName);
        //textvview
//        tvshowName=findViewById(R.id.tvshow_name);
        //Imageview
        tvshowPoster=findViewById(R.id.tv_show_poster);
        //text views
        genres=findViewById(R.id.tv_show_genres);
        tvshowRate=findViewById(R.id.tv_show_rate);
        votersCount=findViewById(R.id.voters_count);
        tvShowStatus=findViewById(R.id.tv_show_status);
        releaseDate=findViewById(R.id.release_date);
        spokenLanguages=findViewById(R.id.spoken_languages);
        noOverviewAvailable=findViewById(R.id.overview_not_available);
        seeAllReviews=findViewById(R.id.see_all_reviews);
        seeAllReviews.setOnClickListener(this);
        seeSimliarTvShows=findViewById(R.id.similar_tv_shows);
        seeSimliarTvShows.setOnClickListener(this);
        //lottie
        lottieLoading=findViewById(R.id.lottie_loading);
        //buttons
        watchTrailer=findViewById(R.id.trailer_btn);
        retryBtn=findViewById(R.id.retry_button);
        watchTrailer.setOnClickListener(this);
        retryBtn.setOnClickListener(this);
        //containers
        parentContainer=findViewById(R.id.container);
        parentContainer.setOnClickListener(this);

        seasonsContainer=findViewById(R.id.seasons_container);
        imgContainer=findViewById(R.id.img_container);
        scrollView=findViewById(R.id.scroll);
        tvshowDescripion=findViewById(R.id.tv_show_description);
        tvshowTrailer=findViewById(R.id.tv_show_trailer);
        castRec=findViewById(R.id.cast_recycler);
        seasonsRec=findViewById(R.id.seasons_rec);
        infoContainer=findViewById(R.id.tv_show_info);

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
                saveviewModel.isItemInFavList(tvshowId).observe(this, new Observer<SavedItem>() {
                    @Override
                    public void onChanged(SavedItem savedItem) {
                        if (isFavClicked){
                            isFavClicked=false;
                            //if returned object is null ,that mean its not saved before ,and we should save it now
                            if (savedItem ==null){
                                saveviewModel.saveItem(new SavedItem(tvshowId,tvshowName,tvshowPosterUrl,tvshowOverview,rate,"favorite","tv show"));
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
                saveviewModel.isItemInToWatchList(tvshowId).observe(this, new Observer<SavedItem>() {
                    @Override
                    public void onChanged(SavedItem savedItem) {
                        if (isToWatchClicked){
                            isToWatchClicked=false;
                            //if returned object is null ,that mean its not saved before ,and we should save it now
                            if (savedItem ==null){
                                saveviewModel.saveItem(new SavedItem(tvshowId,tvshowName,tvshowPosterUrl,tvshowOverview,rate,"to watch","tv show"));
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
                saveviewModel.isItemInWatchedList(tvshowId).observe(this, new Observer<SavedItem>() {
                    @Override
                    public void onChanged(SavedItem savedItem) {
                        //to prevent repeatation we use a boolean
                        if(isWatchedClicked){
                            isWatchedClicked=false;
                            //if returned object is null ,that mean its not saved before ,and we should save it now
                            if (savedItem ==null){
                                saveviewModel.saveItem(new SavedItem(tvshowId,tvshowName,tvshowPosterUrl,tvshowOverview,rate,"watched","tv show"));
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
    setting of tvshows cast's recyclerView
     */
    private void setCastRecycler() {
            CastRecyclerAdapter castRecyclerAdapter=new CastRecyclerAdapter(getApplicationContext(),castList);
            castRecyclerAdapter.setOnActorClicked(this);
            castRec.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));
            castRec.setAdapter(castRecyclerAdapter);
    }

    /*
    setting of tvshows season's recyclerView
     */
    private void setSeasonsRecycler() {
        if (seasons!=null){
            Log.d(TAG, "setSeasonsRecycler: there are seasons "+seasons.size());
            SeasonsAdapter seasonsAdapter=new SeasonsAdapter(getApplicationContext(),seasons);
            seasonsAdapter.setOnCardClickListener(this);
            seasonsRec.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));
            seasonsRec.setAdapter(seasonsAdapter);
        }
        else {
            seasonsContainer.setVisibility(View.GONE);
        }


    }

    private void settingTrailerWebView(String url) {
        Log.d(TAG, "settingTrailerWebView: url is :"+url);
        String base_url="https://www.youtube.com/watch?v="+url;
        String fullUrl=base_url+url;
        String showTrailerInApp= Paper.book().read("trailer inside app","true");
        if (showTrailerInApp.equalsIgnoreCase("true")){
//            watchTrailer.setVisibility(View.GONE);
//            tvshowTrailer.setVisibility(View.VISIBLE);
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
            tvshowDescripion.loadData(txt ,"text/html; charset=UTF-8",null);
        }
        else {
            tvshowDescripion.setVisibility(View.GONE);
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
        tvshowPoster.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth,imgBasicHeight));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.trailer_btn:{

                settingTrailerWebView(trailerUrl);
                break;
            }
            case R.id.retry_button:{
                retryBtn.setVisibility(View.GONE);
                lottieLoading.setVisibility(View.VISIBLE);
                lottieLoading.playAnimation();
                detailViewModel.getTvShowDetail(tvshowId);
                break;
            }
            case R.id.see_all_reviews:{
                Intent goToReviews=new Intent(this,ReviewsActivity.class);
                goToReviews.putExtra("type","tv show");
                goToReviews.putExtra("id",tvshowId);
                startActivity(goToReviews);
                break;
            }
            case R.id.similar_tv_shows:{
                Intent toSimilarTvShows=new Intent(this,SimilarTvshowActivity.class);
                toSimilarTvShows.putExtra("tv show name",tvshowName);
                toSimilarTvShows.putExtra("tv show id",tvshowId);
                startActivity(toSimilarTvShows);
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

    @Override
    public void onCardClick(int position) {
        View seasonDialog=LayoutInflater.from(this).inflate(R.layout.season_detail_dialog,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(seasonDialog);
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        if(dialog.getWindow()!=null)  dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnim;
        dialog.show();
        Season season=seasons.get(position);
        ImageView seasonCover=seasonDialog.findViewById(R.id.dialog_season_img);
        TextView seasonName=seasonDialog.findViewById(R.id.dialog_season_name);
        TextView seasonAirDate=seasonDialog.findViewById(R.id.dialog_season_air_date);
        TextView seasonEpisodeNum=seasonDialog.findViewById(R.id.dialog_season_episode_num);
        TextView seasonOverview=seasonDialog.findViewById(R.id.dialog_season_over_view);
        Log.d(TAG, "onCardClick: season info "+season.getName()+" "+season.getAirDate()+season.getEpisodeCount()+" "+season.getOverview());
        Picasso.with(this).load(BASE_URL_IMG+seasons.get(position).getPosterPath()).into(seasonCover);
        seasonName.setText(season.getName());
        seasonAirDate.setText(season.getAirDate());
        seasonEpisodeNum.setText(String.valueOf(season.getEpisodeCount()).concat("  "+getString(R.string.episode)));
        seasonOverview.setText(season.getOverview());
    }
}
