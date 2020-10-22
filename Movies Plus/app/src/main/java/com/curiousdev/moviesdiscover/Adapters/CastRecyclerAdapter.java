package com.curiousdev.moviesdiscover.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curiousdev.moviesdiscover.Models.CastMember;
import com.curiousdev.moviesdiscover.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.QUALITY;

public class CastRecyclerAdapter extends RecyclerView.Adapter<CastRecyclerAdapter.ViewHolder> {
    private static final String TAG = "CastRecyclerAdapter";
    private String BASE_URL_IMG = "https://image.tmdb.org/t/p/";

    //current quality
    private String quality=QUALITY;

    private Context context;
    private List<CastMember> castList;
    private ActorClicked listener;;

    public interface ActorClicked{
        public void onActorClicked(CastMember member);
    }

    public void setOnActorClicked(ActorClicked actorClicked){
        this.listener=actorClicked;
    }
    public CastRecyclerAdapter(Context context, List<CastMember> castMembers) {
        Paper.init(context);
        Log.d(TAG, "CastRecyclerAdapter: ");
        this.context = context;
        this.castList = castMembers;
        if (quality.equalsIgnoreCase("w500")){
            quality="h632";
        }
        BASE_URL_IMG=BASE_URL_IMG.concat(quality);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View view=LayoutInflater.from(context).inflate(R.layout.cast_rec_item,parent,false);
        CastRecyclerAdapter.ViewHolder viewHolder=new ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");

        if (!quality.equalsIgnoreCase("none")){
            Picasso.with(context).load(BASE_URL_IMG+castList.get(position).getProfilePath())
                    .into(holder.castImg, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.loading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
        else {
            Picasso.with(context).load(R.drawable.image_turned_off_simple).into(holder.castImg);
            holder.loading.setVisibility(View.GONE);
        }
        holder.actorName.setText(castList.get(position).getName());
        holder.asCharacter.setText(castList.get(position).getCharacter());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        return castList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView castImg;
        TextView actorName,asCharacter;
        ProgressBar loading;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: ");
            castImg=itemView.findViewById(R.id.cast_img);
            actorName=itemView.findViewById(R.id.actor_name);
            asCharacter=itemView.findViewById(R.id.actor_as);
            loading=itemView.findViewById(R.id.actor_img_progress);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onActorClicked(castList.get(getAdapterPosition()));
                }
            });
        }
    }
}


