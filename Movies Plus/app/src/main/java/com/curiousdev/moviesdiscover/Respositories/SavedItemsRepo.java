package com.curiousdev.moviesdiscover.Respositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.curiousdev.moviesdiscover.Models.RoomDao;
import com.curiousdev.moviesdiscover.Models.RoomDb;
import com.curiousdev.moviesdiscover.Models.SavedItem;

import java.util.List;

public class SavedItemsRepo {
    RoomDao dao;
    LiveData<List<SavedItem>> savedItems;

    public SavedItemsRepo(Application application) {
        RoomDb database= RoomDb.getRoomInstance(application.getApplicationContext());
        dao=database.getDao();
    }

    public LiveData<List<SavedItem>> getSavedItems(String type){
        return dao.getSavedItems(type);
    }
    public LiveData<SavedItem> isItemwatched(int itemId){
        return dao.getSavedItem(itemId,"watched");
    }
    public LiveData<SavedItem> isItemToWatch(int itemId){
        return dao.getSavedItem(itemId,"to watch");
    }
    public LiveData<SavedItem> isItemfavorited(int itemId){
        return dao.getSavedItem(itemId,"favorite");
    }
    
    public void saveItem(SavedItem item){
        new SaveMovieAsyncTask(dao).execute(item);
    }
    public void deleteItem(SavedItem item){
        new DeleteMovieAsyncTask(dao).execute(item);
    }

    public static class SaveMovieAsyncTask extends AsyncTask<SavedItem,Void,Void> {
        RoomDao dao;

        public SaveMovieAsyncTask(RoomDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(SavedItem... savedItems) {
            dao.saveItem(savedItems[0]);
            return null;
        }
    }
    public static class DeleteMovieAsyncTask extends AsyncTask<SavedItem,Void,Void> {
        RoomDao dao;

        public DeleteMovieAsyncTask(RoomDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(SavedItem... savedItems) {
            dao.deleteItem(savedItems[0]);
            return null;
        }
    }
}
