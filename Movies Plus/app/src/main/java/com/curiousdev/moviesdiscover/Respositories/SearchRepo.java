package com.curiousdev.moviesdiscover.Respositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.curiousdev.moviesdiscover.Models.RoomDao;
import com.curiousdev.moviesdiscover.Models.RoomDb;
import com.curiousdev.moviesdiscover.Models.SavedSearch;

import java.util.List;

public class SearchRepo {
    RoomDao dao;
    LiveData<List<SavedSearch>> recentSearch;

    public SearchRepo(Application application) {
        RoomDb roomDb= RoomDb.getRoomInstance(application.getApplicationContext());
        dao=roomDb.getDao();
        recentSearch=dao.getRecentSearch();
    }

    public LiveData<List<SavedSearch>> getRecentSearch(){
        return dao.getRecentSearch();
    }

    public void saveMySearch(SavedSearch search){
        new SaveSearchAsyncTask(dao).execute(search);
    }

    public class SaveSearchAsyncTask extends AsyncTask<SavedSearch,Void,Void> {
        RoomDao dao;

        public SaveSearchAsyncTask(RoomDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(SavedSearch... savedSearches) {
            dao.saveSearch(savedSearches[0]);
            return null;
        }
    }
}
