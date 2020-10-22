package com.curiousdev.moviesdiscover.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curiousdev.moviesdiscover.Adapters.MoviesRecyclerAdapter;
import com.curiousdev.moviesdiscover.HelperClasses.GridScrollListenter;
import com.curiousdev.moviesdiscover.Models.Movie;
import com.curiousdev.moviesdiscover.Models.Person;
import com.curiousdev.moviesdiscover.R;
import com.curiousdev.moviesdiscover.ViewModels.PersonDetailViewModel;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.QUALITY;
import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.hostContext;
import static com.curiousdev.moviesdiscover.Functions.isNetworkAvailable;

public class PersonDetails extends AppCompatActivity implements MoviesRecyclerAdapter.OnCardClickListener {
    //ads
    AdView adView;
    private static final String TAG = "PersonDetails";
    private String BASE_URL_IMG = "https://image.tmdb.org/t/p/";

    //current quality
    private String quality=QUALITY;

    private Toolbar toolbar;
    private RecyclerView /*knownForRec,*/moviesParticipatedRec;
    private Button loadMoreBtn;
    PersonDetailViewModel detailViewModel;

    //layouts
    LinearLayout detailContainer;
    private RelativeLayout errorLayout;

    //adapters and layoutManagers
    MoviesRecyclerAdapter /*knownForRecyclerAdapter,*/moviesParticipatedRecAdapter;
    GridLayoutManager gridLayoutManager;

    //widgets
    CircleImageView profileImg;
    TextView personName,personWork,personBirthPlace,personLifeDates;
    WebView personBiography;
    ProgressBar loading;

    private int startPage=1;
    private int pagecount;
    private int currentPage=startPage;
    private boolean isLastPage=false;
    private boolean isLoading;


    private int personId;
    private List<Movie> moviesParticipated;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("current page",currentPage);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);

        moviesParticipated=new ArrayList<>();
        Paper.init(this);
        BASE_URL_IMG=BASE_URL_IMG.concat(quality);
        if (quality.equalsIgnoreCase("w500")){
            //actors profile didn't have a resolution w500 so we gonna replace it with h632
            quality="h632";
        }
        Log.d(TAG, "onCreate: ");
        //getting entent data
        Bundle bundle=getIntent().getExtras();
        personId=bundle.getInt("person_id");
        String pName=bundle.getString("person_name");
        initActivity();
        if (pName != null) {
            setToolbar(pName);
        }
        detailViewModel=new PersonDetailViewModel(getApplication());
        detailViewModel.getPersonDetail(personId).observe(this, new Observer<Person>() {
            @Override
            public void onChanged(Person person) {
                loading.setVisibility(View.GONE);
                if(person!=null){
                    Log.d(TAG, "onChanged: ");
                    detailContainer.setVisibility(View.VISIBLE);
                    if (!quality.equalsIgnoreCase("none")){
                        Picasso.with(getApplicationContext())
                                .load(BASE_URL_IMG+person.getProfilePath())
                                .into(profileImg);
                    }
                    else{
                        Picasso.with(getApplicationContext()).load(R.drawable.image_turned_off_simple).into(profileImg);
                    }
                    personName.setText(person.getName());
                    personWork.setText(person.getKnownForDepartment());
                    personBirthPlace.setText(person.getPlaceOfBirth());
                    String deathdate=person.getDeathday();
                    String birthdate=person.getBirthday();
                    if (deathdate==null){
                        deathdate="Still alive";
                    }
                    if (birthdate==null){
                        birthdate="unknown";
                    }
                    Log.d(TAG, "onChanged: after death");
                    personLifeDates.setText(birthdate.concat(" - ").concat(deathdate));
                    setBiographyWebView(person.getBiography());
                    moviesParticipated=person.getCombinedCredits().getMovies();

                    moviesParticipatedRec.setVisibility(View.VISIBLE);
                    loadParticipatedMovies();

                }
                else {
                    processError();
                }
            }
        });

    }


    private void loadParticipatedMovies() {
        if (moviesParticipated.size()>0){
            double count=Math.ceil(moviesParticipated.size()/20.0);
            Log.d(TAG, "loadParticipatedMovies: page count is "+count);
            pagecount=(int)count;
            Log.d(TAG, "loadParticipatedMovies: page count is "+pagecount+" results count is "+moviesParticipated.size());

            loadMovies(currentPage);
        }


    }

    private void initActivity() {
        Log.d(TAG, "initActivity: ");
        toolbar=findViewById(R.id.person_detail_toolbar);
//        knownForRec=findViewById(R.id.known_for_movies);
        moviesParticipatedRec=findViewById(R.id.career_movies);
        loadMoreBtn=findViewById(R.id.load_more);
        loadMoreBtn.setOnClickListener(v -> {
            currentPage++;
            loadMovies(currentPage);
        });
        profileImg=findViewById(R.id.person_img);
        personName=findViewById(R.id.person_name);
        personWork=findViewById(R.id.person_work);
        personBirthPlace=findViewById(R.id.person_birth_place);
        personLifeDates=findViewById(R.id.person_life_dates);
        personBiography=findViewById(R.id.person_biography);
        loading=findViewById(R.id.loading);
        //containers
        detailContainer=findViewById(R.id.detail_container);
        errorLayout = findViewById(R.id.error_layout);
        moviesParticipatedRecAdapter=new MoviesRecyclerAdapter(this);
        moviesParticipatedRecAdapter.setOnCardClickListener(this);
        moviesParticipatedRec.setAdapter(moviesParticipatedRecAdapter);

    }

    private void setToolbar(String personName) {
        Log.d(TAG, "setToolbar: ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(personName.concat("'s Details"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setBiographyWebView(String biography) {
        Log.d(TAG, "setBiographyWebView: ");
        String txt="";
        txt+="<body style=\" background:#050320;\"><p align=\"justify\" style=\"color:#CEC8C8;font-size:13px; \">";
        txt+=biography;
        txt+="</p></body>";
        personBiography.loadData(txt ,"text/html; charset=UTF-8",null);
    }

//    private void setKnownForRec(List<Movie> movies){
//        knownForRecyclerAdapter.addAll(movies);
//        knownForRec.setAdapter(knownForRecyclerAdapter);
//    }

    private void loadMovies(int page){
        Log.d(TAG, "loadMovies: loading ");
        isLastPage=pagecount<=currentPage;
        if (isLastPage){
            loadMoreBtn.setVisibility(View.GONE);
        }
        int moviesAtTime=20;
        int startFrom=page*moviesAtTime-moviesAtTime;
        int endAt=startFrom+moviesAtTime;
        if (moviesParticipated.size()<endAt){
            for (int i = startFrom; i <moviesParticipated.size() ; i++) {
                moviesParticipatedRecAdapter.add(moviesParticipated.get(i));
            }
        }
        else {
            for (int i = startFrom; i <endAt ; i++) {
                moviesParticipatedRecAdapter.add(moviesParticipated.get(i));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return true;

    }

    private void processError(){
        TextView errorHeader=findViewById(R.id.error_header);
        TextView errorTxt=findViewById(R.id.error_txt);
        errorLayout.setVisibility(View.VISIBLE);
        Button retryBtn = findViewById(R.id.retry_button);
        retryBtn.setOnClickListener(view->{
            errorLayout.setVisibility(View.GONE);
            detailViewModel.getPersonDetail(personId);
        });

            /*it make a fatal exception leading app to crash
            so in order to fix this problem i made a static context in the host activity use it in
            the fragment instead of getActivity() and getContext()
            */
        if (!isNetworkAvailable(this)){
            errorHeader.setText(getString(R.string.no_connection_header));
            errorTxt.setText(getString(R.string.no_connection_txt));
        }
        else {
            errorHeader.setText(getString(R.string.unknown_error_header));
            errorTxt.setText(getString(R.string.unknown_error_txt));
        }
    }

    @Override
    public void onMovieClick(Movie movie) {
        Log.d(TAG, "onCardClick: ");
        Intent goToDetailPage=new Intent();
        if (movie.getMediaType().equalsIgnoreCase("tv")){
            goToDetailPage=new Intent(this, TvShowDetailActivity.class);
            goToDetailPage.putExtra("tv show name",movie.getTitle());
            goToDetailPage.putExtra("tv show id",movie.getId());
            goToDetailPage.putExtra("tv show cover",movie.getPosterPath());
            goToDetailPage.putExtra("tv show rate",movie.getVoteAverage());
            goToDetailPage.putExtra("tv show overview",movie.getOverview());
        }
        else if (movie.getMediaType().equalsIgnoreCase("movie")){
            goToDetailPage=new Intent(this, MovieDetailActivity.class);
            goToDetailPage.putExtra("movie name",movie.getTitle());
            goToDetailPage.putExtra("movie id",movie.getId());
            goToDetailPage.putExtra("movie cover",movie.getPosterPath());
            goToDetailPage.putExtra("movie rate",movie.getVoteAverage());
            goToDetailPage.putExtra("movie overview",movie.getOverview());
        }

        startActivity(goToDetailPage);
    }
}
