package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.project.PlaceDetail;
import com.example.project.R;

import java.util.ArrayList;
import java.util.List;

public class PlacesListAdapter extends ArrayAdapter<PlaceDetail> {

    private static final String TAG = "PlacesListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView name;
        TextView type;
        TextView fullDistance;
    }

    /**
     * Default constructor for the PersonListAdapter
     */
    public PlacesListAdapter(Context context, int resource, ArrayList<PlaceDetail> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        String name = getItem(position).getName();
        String type = getItem(position).getType();
        String fullDistance = getItem(position).getFullDistance();

        //Create the person object with the information
        PlaceDetail place = new PlaceDetail(name,type,fullDistance);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.textView2);
            holder.type = (TextView) convertView.findViewById(R.id.textView3);
            holder.fullDistance = (TextView) convertView.findViewById(R.id.textView1);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        holder.name.setText(place.getName());
        holder.type.setText(place.getType());
        holder.fullDistance.setText(place.getFullDistance());


        return convertView;
    }
}







