package com.curiousdev.moviesdiscover.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curiousdev.moviesdiscover.Models.SavedSearch;
import com.curiousdev.moviesdiscover.R;

import java.util.List;

public class RecentSearchRecyclerAdapter extends RecyclerView.Adapter<RecentSearchRecyclerAdapter.SearchHolder> {
    List<SavedSearch> savedSearches;
    Context context;

    RecentSearch listener;
    public interface RecentSearch{
        public void onRecentSearchClicked(String query);
    }

    public void setOnRecentSearchClicked(RecentSearch listener){
        this.listener=listener;
    }
    public RecentSearchRecyclerAdapter(List<SavedSearch> savedSearches, Context context) {
        this.savedSearches = savedSearches;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recent_search_item,parent,false);
        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
        holder.query.setText(savedSearches.get(position).getQuery());
    }

    @Override
    public int getItemCount() {
        return savedSearches.size();
    }

    public class SearchHolder extends RecyclerView.ViewHolder{
        TextView query;
        public SearchHolder(@NonNull View itemView) {
            super(itemView);
            query=itemView.findViewById(R.id.recent_search_query);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRecentSearchClicked(savedSearches.get(getAdapterPosition()).getQuery());
                }
            });
        }
    }
}
