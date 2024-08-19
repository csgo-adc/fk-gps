package com.android.nfc.system.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.nfc.system.R;
import com.android.nfc.system.listener.OnItemClickListener;

import java.util.HashMap;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchViewHold>{

    private List<HashMap<String, String>> mList;

    private OnItemClickListener mClickListener;

    public SearchResultAdapter(List<HashMap<String, String>> list) {
        mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;

    }

    @Override
    public SearchViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_search,parent,false);
        return new SearchViewHold(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(SearchViewHold holder, int position) {
        HashMap<String, String> map = mList.get(position);
        holder.citytextView.setText(map.get("city"));
        holder.keytextView.setText(map.get("key"));
        holder.distextview.setText(map.get("dis"));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    static class SearchViewHold extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView keytextView;
        TextView citytextView;
        TextView distextview;
        private OnItemClickListener mListener;
        public SearchViewHold(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);
            keytextView = itemView.findViewById(R.id.sug_key);
            citytextView = itemView.findViewById(R.id.sug_city);
            distextview = itemView.findViewById(R.id.sug_dis);
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
