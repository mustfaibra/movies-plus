package com.curiousdev.moviesdiscover.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curiousdev.moviesdiscover.Models.Movie;
import com.curiousdev.moviesdiscover.Models.Person;
import com.curiousdev.moviesdiscover.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.QUALITY;

public class PeopleSearchRecyclerAdapter extends RecyclerView.Adapter<PeopleSearchRecyclerAdapter.PersonHolder> {
    private static final String TAG = "PeopleSearchRecyclerAda";
    //base url for all img path
    private String BASE_URL_IMG = "https://image.tmdb.org/t/p/";

    //current quality
    private String quality=QUALITY;

    private Context context;
    private List<Person> people;
    private LayoutInflater inflater;
    private PersonSelectedListener listener;

    public void setOnPersonSelected(PersonSelectedListener myListener){
        this.listener=myListener;
    }

    public interface PersonSelectedListener{
        public void onPersonSelectd(Person person);
    }

    public PeopleSearchRecyclerAdapter(Context context) {
        this.context = context;
        people=new ArrayList<>();
        inflater=LayoutInflater.from(context);
        Paper.init(context);
        BASE_URL_IMG=BASE_URL_IMG.concat(quality);
    }


    @NonNull
    @Override
    public PersonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.actors_recycler_item,parent,false);
        return new PersonHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonHolder holder, int position) {
        //first we set person image

        String posterLink=BASE_URL_IMG+people.get(position).getProfilePath();

        if (!quality.equalsIgnoreCase("none")){
            Picasso.with(context).load(posterLink).into(holder.personImg);
        }
        else {
            Picasso.with(context).load(R.drawable.image_turned_off_simple).into(holder.personImg);
        }

        //name
        holder.personName.setText(people.get(position).getName());
        //work
        holder.personWork.setText(people.get(position).getKnownForDepartment());
        //known for
        StringBuilder knownFor=new StringBuilder();
        List<Movie> knownForMovies=people.get(position).getKnownFor();
        knownFor.append(context.getString(R.string.known_for));
        knownFor.append(" : ");

        for (Movie movie:knownForMovies) {
            if (movie.getOriginalTitle()!=null){
                knownFor.append(movie.getOriginalTitle());
                //if its not the last movie
                if (movie!=knownForMovies.get(knownForMovies.size()-1)){
                    knownFor.append(" , ");
                }
            }
            else {
                knownFor.append(movie.getOriginalName());
                //if its not the last movie
                if (movie!=knownForMovies.get(knownForMovies.size()-1)){
                    knownFor.append(" , ");
                }
            }
        }
        holder.personKnownFor.setText(knownFor);

    }

    @Override
    public int getItemCount() {
        return people.size();
    }


    public void add(Person person){
        people.add(person);
        notifyItemInserted(people.size()-1);
    }
    public void addAll(List<Person> people){
        for (Person person:people) {
            add(person);
        }
    }
    public void delete(Person person){
        people.remove(person);
        notifyItemRemoved(people.indexOf(person));
    }
    public void clear(){
        notifyDataSetChanged();
        if (!isEmpty()){
            while (getItemCount()>0){
                delete(getPerson(0));
            }
        }
    }
    public boolean isEmpty(){
        return getItemCount()==0;
    }

    public Person getPerson(int position){
         return people.get(position);
    }
    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }

    class PersonHolder extends RecyclerView.ViewHolder {
        private ImageView personImg;
        private TextView personName;
        private TextView personWork;
        private TextView personKnownFor;

        public PersonHolder(@NonNull View itemView) {
            super(itemView);
            personImg=itemView.findViewById(R.id.person_img);
            personName=itemView.findViewById(R.id.person_name);
            personWork=itemView.findViewById(R.id.person_work);
            personKnownFor=itemView.findViewById(R.id.person_known_for);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPersonSelectd(people.get(getAdapterPosition()));
                }
            });
        }
    }
}
