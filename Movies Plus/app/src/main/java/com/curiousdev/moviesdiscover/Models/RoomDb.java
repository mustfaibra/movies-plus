package com.curiousdev.moviesdiscover.Models;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {SavedItem.class,SavedSearch.class,MovieDetail.class,Language.class,Genre.class,CastMember.class,VideoItem.class,
                        TvShowDetails.class,Season.class},version = 1)
public abstract class RoomDb extends RoomDatabase{
    public static final String DATABASE_NAME="Movies Discover";
    public static RoomDb dbInstance;
    public  abstract RoomDao getDao();
    public static RoomDb getRoomInstance(Context context){
        if (dbInstance==null){
            dbInstance= Room
                    .databaseBuilder(context.getApplicationContext(),RoomDb.class,DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return dbInstance;
    }

    public static RoomDatabase.Callback callback=new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };
}
