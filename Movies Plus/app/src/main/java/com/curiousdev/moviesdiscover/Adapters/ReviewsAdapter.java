package com.curiousdev.moviesdiscover.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curiousdev.moviesdiscover.Models.Review;
import com.curiousdev.moviesdiscover.R;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {
    private List<Review> reviews;
    Context context;

    public ReviewsAdapter(List<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.reviews_recycler_item,parent,false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        holder.authorName.setText(reviews.get(position).getAuthor());
        String txt="";
        txt+="<body style=\" background:#050320;\"><p align=\"justify\" style=\"color:#797878;font-size:13px; \">";
        txt+=reviews.get(position).getContent();
        txt+="</p></body>";
        holder.reviewWebView.loadData(txt ,"text/html; charset=UTF-8",null);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void add(Review review){
        reviews.add(review);
        notifyItemInserted(reviews.size()-1);
    }
    public void addAll(List<Review> reviews){
        for (Review review:reviews){
            add(review);
        }
    }
    class ReviewHolder extends RecyclerView.ViewHolder{
        WebView reviewWebView;
        TextView authorName;
        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            reviewWebView=itemView.findViewById(R.id.movie_review);
            authorName=itemView.findViewById(R.id.author_name);
        }
    }
}
