package com.curiousdev.moviesdiscover.HelperClasses;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class GridScrollListenter extends RecyclerView.OnScrollListener {
    //debuging cons
    private static final String TAG = "PagintaionScrollListent";
    GridLayoutManager gridLayoutMgr;

    protected GridScrollListenter(GridLayoutManager layoutManager) {
        this.gridLayoutMgr = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        Log.d(TAG, "onScrolled: start");
        int visibleItemCount=gridLayoutMgr.getChildCount();
        int totalItemcount=gridLayoutMgr.getItemCount();
        int firstVisibleItemPosition=gridLayoutMgr.findFirstVisibleItemPosition();
        if(!isLoading()&&!isLastPage()){
            Log.d(TAG, "onScrolled: its not loading and not the last page,so we gonna load more movie");
            Log.d(TAG, "onScrolled: items seen "+visibleItemCount);
            Log.d(TAG, "onScrolled: first item is at "+firstVisibleItemPosition);
            Log.d(TAG, "onScrolled: all item num "+totalItemcount);
            if ((visibleItemCount+firstVisibleItemPosition+1)>=totalItemcount&&firstVisibleItemPosition>0){
                Log.d(TAG, "onScrolled: its time mi amigo");
                loadMoreMovies();
            }
        }
    }
    public abstract void loadMoreMovies();
    public abstract boolean isLoading();
    public abstract boolean isLastPage();
    public abstract int getTotalPagesCount();

}
