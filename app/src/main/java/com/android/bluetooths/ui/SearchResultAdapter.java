package com.android.bluetooths.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bluetooths.R;
import com.android.bluetooths.database.LocationDao;
import com.android.bluetooths.database.LocationData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchViewHold>{

    List<HashMap<String, String>> mList;


    SearchResultAdapter(List<HashMap<String, String>> list) {
        mList = list;
    }

    @Override
    public SearchViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_search,parent,false);
        return new SearchViewHold(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHold holder, int position) {
        HashMap<String, String> map = mList.get(position);
        holder.citytextView.setText(map.get("city"));
        holder.keytextView.setText(map.get("key"));
        holder.distextview.setText(map.get("dis"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("latitude", map.get("latitude"));
                bundle.putString("longitude", map.get("longitude"));

                Navigation.findNavController(v).navigate(R.id.map_Fragment, bundle);

            }
        });

    }



    @Override
    public int getItemCount() {
        return mList.size();
    }


    static class SearchViewHold extends RecyclerView.ViewHolder{
        TextView keytextView;
        TextView citytextView;
        TextView distextview;
        public SearchViewHold(@NonNull View itemView) {
            super(itemView);
            keytextView = itemView.findViewById(R.id.sug_key);
            citytextView = itemView.findViewById(R.id.sug_city);
            distextview = itemView.findViewById(R.id.sug_dis);
        }
    }
}
