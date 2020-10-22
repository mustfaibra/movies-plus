package com.curiousdev.moviesdiscover.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Dao;

import com.curiousdev.moviesdiscover.Models.RoomDao;
import com.curiousdev.moviesdiscover.Models.RoomDb;
import com.curiousdev.moviesdiscover.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

import io.paperdb.Paper;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.QUALITY;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    //ads
    AdView adView;
    private AppCompatSpinner qualityMenu;
    private String qualitiesValues[];
    private String currentQuality;
    private String TAG="SettingActivity";
    private Switch trailerInsideApp;
    private TextView clearCache;
    private RoomDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Paper.init(this);
        currentQuality=QUALITY;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        dao= RoomDb.getRoomInstance(this).getDao();

        initActivity();
        setEventHandler();
    }

    private void initActivity() {
        qualityMenu = findViewById(R.id.quality_menu);
        Toolbar toolbar = findViewById(R.id.setting_toolbar);
        trailerInsideApp=findViewById(R.id.show_trailer_in_app);
        clearCache=findViewById(R.id.clear_cache);
        clearCache.setOnClickListener(this);

        qualitiesValues=getResources().getStringArray(R.array.quality_value);
        for (int i = 0; i < qualitiesValues.length; i++) {
            if(qualitiesValues[i].equalsIgnoreCase(currentQuality)){
                qualityMenu.setSelection(i);
            }
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.setting));
        String trailerInapp=Paper.book().read("trailer inside app","true");
        if(trailerInapp.equalsIgnoreCase("true"))
            trailerInsideApp.setChecked(true);
        else
            trailerInsideApp.setChecked(false);
    }

    @Override
    public void onBackPressed() {
        Paper.book().write("trailer inside app","" + trailerInsideApp.isChecked());
        super.onBackPressed();
    }

    private void setEventHandler() {
        qualityMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Paper.book().write("quality",qualitiesValues[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clear_cache:{
                new ClearData(dao).execute();
                Toast.makeText(this, getString(R.string.cleared), Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }


    public static class ClearData extends AsyncTask<Void,Void,Void> {
        RoomDao dao;
        public ClearData(RoomDao dao){
            this.dao=dao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            dao.clearSavedCast();
            dao.clearSavedGenres();
            dao.clearSavedItems();
            dao.clearSavedLanguages();
            dao.clearSavedMovieDetail();
            dao.clearSavedSearch();
            dao.clearSavedSeasons();
            dao.clearSavedVideos();
            dao.clearSavedShowDetail();
            return null;
        }
    }
}
