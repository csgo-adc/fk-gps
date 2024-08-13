package com.android.bluetooths.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bluetooths.R;
import com.android.bluetooths.database.LocationData;
import com.android.bluetooths.ui.OnItemClickListener;

import java.util.ArrayList;

public class CoordinateAdapter extends RecyclerView.Adapter<CoordinateAdapter.CoordinateViewHolder> {


    private ArrayList<LocationData> mList;
    private OnItemClickListener mClickListener;


    public CoordinateAdapter(ArrayList<LocationData> list) {
        mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;

    }


    @NonNull
    @Override
    public CoordinateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_coordinate,parent,false);
        return new CoordinateViewHolder(view, mClickListener);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CoordinateViewHolder holder, int position) {
        LocationData locationData = mList.get(position);
        holder.lat_tv.setText(String.valueOf(locationData.getLatitude()));
        holder.lon_tv.setText(String.valueOf(locationData.getLongitude()));
        holder.pos_name_tv.setText(locationData.getPositionName());

    }



    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class CoordinateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView pos_name_tv;

        TextView lon_tv;
        TextView lat_tv;
        private final OnItemClickListener mListener;

        public CoordinateViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            lon_tv = itemView.findViewById(R.id.lon_text);
            lat_tv = itemView.findViewById(R.id.lat_text);
            pos_name_tv = itemView.findViewById(R.id.pos_name);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(v, getLayoutPosition());

        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onItemLongClick(v, getLayoutPosition());
            return true;
        }
    }
}
