package com.android.bluetooths.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bluetooths.R;
import com.android.bluetooths.adapter.SearchResultAdapter;
import com.android.bluetooths.viewmodel.SearchViewModel;
import com.android.bluetooths.viewmodel.Searcher;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchFragment extends Fragment {

    private Activity mActivity;

    private SuggestionSearch mSuggestionSearch;
    private RecyclerView mSugListView;
    private SearchResultAdapter mAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mSugListView = view.findViewById(R.id.sug_list);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSearch();
    }

    @Override
    public void onStart() {
        super.onStart();

        final SearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);

        searchViewModel.searcherMutableLiveData.observe(this, new Observer<Searcher>() {
            @Override
            public void onChanged(Searcher searcher) {
                doSearch(searcher.getCity(), searcher.getKeyword());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSuggestionSearch.destroy();
    }

    private void initSearch() {

        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    return;
                }

                List<HashMap<String, String>> suggestList = new ArrayList<>();
                for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
                    if (info.getKey() != null && info.getDistrict() != null && info.getCity() != null) {
                        try {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("key", info.getKey());
                            map.put("city", info.getCity());
                            map.put("dis", info.getDistrict());
                            map.put("latitude", String.valueOf(info.pt.latitude));
                            map.put("longitude", String.valueOf(info.pt.longitude));
                            suggestList.add(map);
                        } catch (Exception e) {

                        }

                    }
                }
                mSugListView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));

                mAdapter = new SearchResultAdapter(suggestList);
                mSugListView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        HashMap<String, String> map = suggestList.get(position);

                        Bundle bundle = new Bundle();
                        bundle.putDouble(MainActivity.LAT_VALUE, Double.valueOf(map.get("latitude")));
                        bundle.putDouble(MainActivity.LNG_VALUE, Double.valueOf(map.get("longitude")));

                        NavController navController = Navigation.findNavController(mActivity, R.id.search_Fragment);

                        NavOptions navOptions = new NavOptions.Builder()
                                .setPopUpTo(navController.getGraph().getStartDestinationId(), false)
                                .setRestoreState(true)
                                .build();
                        navController.navigate(R.id.map_Fragment, null, navOptions);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void doSearch(String city, String keyword) {
        SuggestionSearchOption option = new SuggestionSearchOption();
        WeakReference<SuggestionSearchOption> weakReference = new WeakReference<>(option);

        SuggestionSearchOption s = weakReference.get();
        s.city(city);
        s.keyword(keyword);

        mSuggestionSearch.requestSuggestion(s);

    }


}
