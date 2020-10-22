package com.curiousdev.moviesdiscover.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curiousdev.moviesdiscover.Models.Genre;
import com.curiousdev.moviesdiscover.R;

import java.util.ArrayList;
import java.util.List;

public class GenresRecyclerAdapter extends RecyclerView.Adapter<GenresRecyclerAdapter.CardHolder> {
    Context context;
    List<Genre> genres;
    List<Integer> selectedGenres;
    LayoutInflater layoutInflater;
    onGenreClicked onGenreClicked;
    private int height;

    public interface onGenreClicked{
        public void onGenreSelected(List<Integer> selectedGenres);
    }

    public void setOnGenreClicked(GenresRecyclerAdapter.onGenreClicked onGenreClicked) {
        this.onGenreClicked = onGenreClicked;
    }

    public GenresRecyclerAdapter(Context context, List<Genre> genres,int height) {
        Log.d("GenresRecyclerAdapter", "GenresRecyclerAdapter: ");
        this.context = context;
        this.genres = genres;
        this.selectedGenres=new ArrayList<>();
        this.height=height;
        Log.d("GenresRecyclerAdapter", "genres is : "+genres.size()+" and height is "+height);

        layoutInflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("GenreRecyclerAdapter", "onCreateViewHolder: ");
        View view=layoutInflater.inflate(R.layout.genre_item,parent,false);
        view.getLayoutParams().height=height/8;
        return new CardHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        holder.bindGenre(genres.get(position));
    }

    @Override public int getItemCount() {
        return genres.size();
    }

    class CardHolder extends RecyclerView.ViewHolder {
        TextView genreName;
        ImageView genreImg;
        public CardHolder(@NonNull View itemView) {
            super(itemView);
            genreName=itemView.findViewById(R.id.genre_name);
            genreImg=itemView.findViewById(R.id.genre_img);

        }
        private void bindGenre(Genre genre){
            float alpha=(genre.isSelected())?1f:0.5f;
            genreImg.setAlpha(alpha);
            genreName.setText(genre.getGenreName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    genre.setSelected(!genre.isSelected());
                    float alpha=(genre.isSelected())?1f:0.5f;
                    if(alpha==1)
                        selectedGenres.add(genre.getGenreId());
                    else
                        selectedGenres.remove(genre.getGenreId());

                    onGenreClicked.onGenreSelected(selectedGenres);
                    genreImg.setAlpha(alpha);
                    Log.d("genreAdapter", "onClick: selected items are "+selectedGenres.size());
                    for (Integer i:selectedGenres) {
                        Log.d("genreAdapter", "you selected "+i);
                    }
                }
            });
        }

    }
}
