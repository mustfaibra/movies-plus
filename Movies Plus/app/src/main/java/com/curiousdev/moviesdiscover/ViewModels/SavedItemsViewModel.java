package com.curiousdev.moviesdiscover.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.curiousdev.moviesdiscover.Models.SavedItem;
import com.curiousdev.moviesdiscover.Respositories.SavedItemsRepo;

import java.util.List;

public class SavedItemsViewModel extends AndroidViewModel {
    private static final String TAG = "SavedItemsViewModel";
    LiveData<List<SavedItem>> savedMovies;
    private SavedItemsRepo savedItemsRepo;

    public SavedItemsViewModel(@NonNull Application application) {
        super(application);
        savedItemsRepo =new SavedItemsRepo(application);
    }

    public LiveData<List<SavedItem>> getSavedItems(String type){
        return savedItemsRepo.getSavedItems(type);
    }

    public LiveData<SavedItem> isItemInWatchedList(int ItemId){
        return savedItemsRepo.isItemwatched(ItemId);
    }
    public LiveData<SavedItem> isItemInToWatchList(int ItemId){
        return savedItemsRepo.isItemToWatch(ItemId);
    }
    public LiveData<SavedItem> isItemInFavList(int ItemId){
        return savedItemsRepo.isItemfavorited(ItemId);
    }

    public void saveItem(SavedItem item){
        savedItemsRepo.saveItem(item);
    }
     public void deleteItem(SavedItem item){
        savedItemsRepo.deleteItem(item);
    }

}
