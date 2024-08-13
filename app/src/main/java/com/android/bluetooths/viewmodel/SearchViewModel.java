package com.android.bluetooths.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchViewModel extends ViewModel {
    public final MutableLiveData<Searcher> searcherMutableLiveData = new MutableLiveData<>();

    public SearchViewModel() {
        searcherMutableLiveData.postValue(new Searcher("a", "b"));
    }


    public void input(String city, String keyword) {
        Searcher searcher = searcherMutableLiveData.getValue();
        if (searcher != null) {
            searcher.setCity(city);
            searcher.setKeyword(keyword);
            searcherMutableLiveData.setValue(searcher);
        }
    }





}
