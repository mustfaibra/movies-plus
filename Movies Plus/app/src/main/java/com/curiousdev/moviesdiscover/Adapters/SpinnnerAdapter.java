package com.curiousdev.moviesdiscover.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.curiousdev.moviesdiscover.R;

import java.util.List;

public class SpinnnerAdapter extends ArrayAdapter {
    public SpinnnerAdapter (Context context,List<String> items){

        super(context,0,items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initSpinnerItem(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initSpinnerItem(position, convertView, parent);
    }

    public View initSpinnerItem(int position, View convertView, ViewGroup parent){
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.spinner_item,parent,false);
        }
        TextView item=convertView.findViewById(R.id.spinner_item_title);
        item.setText(getItem(position).toString());
        return convertView;
    }
}
