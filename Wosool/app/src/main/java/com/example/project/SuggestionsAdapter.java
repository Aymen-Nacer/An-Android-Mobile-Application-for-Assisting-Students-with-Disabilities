package com.example.project;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;

//Simple adapter that is passed to ExpandableHeightGridView so it shows suggestions
public class SuggestionsAdapter extends ArrayAdapter<String> {
    Context mContext;
    ArrayList<String> mCities;

    public SuggestionsAdapter(@NonNull Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mCities = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.suggestions_template, parent, false);
        }
        TextView tv = convertView.findViewById(R.id.suggestion_text);
        tv.setText(getItem(position));
        return convertView;
    }
}

