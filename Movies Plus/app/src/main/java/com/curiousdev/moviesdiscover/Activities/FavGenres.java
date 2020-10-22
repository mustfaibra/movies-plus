package com.curiousdev.moviesdiscover.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.curiousdev.moviesdiscover.Adapters.GenresRecyclerAdapter;
import com.curiousdev.moviesdiscover.Models.Genre;
import com.curiousdev.moviesdiscover.Models.MovieApi;
import com.curiousdev.moviesdiscover.Models.MoviesProvider;
import com.curiousdev.moviesdiscover.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class FavGenres extends AppCompatActivity implements GenresRecyclerAdapter.onGenreClicked {
    //debugging const
    private static final String TAG = "FavGenres";
    RecyclerView genresRecycler;
    GridLayoutManager layoutManager;
    Toolbar toolbar;
    ///adapter
    GenresRecyclerAdapter genresAdapter;
    //list of genres
    List<Genre> genres;
    List<Integer> selectedGenres;
    //screen height
    int height=0;
    //confirm btn
    Button genresSelectedBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        height=getWindowManager().getDefaultDisplay().getHeight();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_genres);

        genresRecycler=findViewById(R.id.genres_selector_recycler);
        layoutManager=new GridLayoutManager(getApplicationContext(),2,GridLayoutManager.VERTICAL,false);
        toolbar=findViewById(R.id.genre_toolbar);

        //button
        genresSelectedBtn=findViewById(R.id.genre_selector_confirm);
        //we hide the confirmation button until user choose at least on genre
        genresSelectedBtn.setEnabled(false);
        genresSelectedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FavGenres.this, "All selected", Toast.LENGTH_SHORT).show();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.fav_genre));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectedGenres=new ArrayList<>();

        getAllGenresAvailable();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void buildCardRecycler(List<Genre> genres) {
        Log.d(TAG, "buildCardRecycler: ");
        genresAdapter=new GenresRecyclerAdapter(getApplicationContext(),genres,height);
        genresRecycler.setAdapter(genresAdapter);
        genresRecycler.setLayoutManager(layoutManager);
        genresAdapter.setOnGenreClicked(this);

    }

    public void getAllGenresAvailable() {
        Retrofit retrofit= MovieApi.getRetrofitInstance();
        MoviesProvider moviesProvider=retrofit.create(MoviesProvider.class);
        genres=new ArrayList<>();
        Call<Genre> genresCall=moviesProvider.getGenres(getString(R.string.tmdb_api),"en-US");
        genresCall.enqueue(new Callback<Genre>() {
            @Override
            public void onResponse(Call<Genre> call, Response<Genre> response) {
                Log.d(TAG, "onResponse: fetching genres successed");

                Genre genreInstance=response.body();

                genres=genreInstance.getGenres();
                buildCardRecycler(genres);
            }

            @Override
            public void onFailure(Call<Genre> call, Throwable throwable) {
                Log.d(TAG, "onFailure: fetching genres failed");
                getAllGenresAvailable();
            }
        });

    }

    @Override public void onGenreSelected(List<Integer> genresSelected) {
        selectedGenres=genresSelected;
        Log.d(TAG, "onGenreSelected: selected genres count is "+selectedGenres.size());
        genresSelectedBtn.setEnabled(genresSelected.size()>0);
    }

}
