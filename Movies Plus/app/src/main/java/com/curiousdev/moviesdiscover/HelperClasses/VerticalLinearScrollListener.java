package com.curiousdev.moviesdiscover.HelperClasses;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class VerticalLinearScrollListener extends RecyclerView.OnScrollListener {
    private static final String TAG = "VerticalLinearScrollLis";
    LinearLayoutManager linearLayoutManager;

    public VerticalLinearScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        Log.d(TAG, "onScrolled: ");
        int visibleItems=linearLayoutManager.getChildCount();
        int totalItems=linearLayoutManager.getItemCount();
        int firstVisibleItem=linearLayoutManager.findFirstVisibleItemPosition();
        if (!isLastPage()&&!isLoading()){
            if (visibleItems+firstVisibleItem>=totalItems){
                Log.d(TAG, "onScrolled: time to load more people");
                loadMore();
            }
        }
    }

    public abstract void loadMore();
    public abstract boolean isLastPage();
    public abstract int getTotalPage();
    public abstract boolean isLoading();

}
