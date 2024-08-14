package com.android.nfc.system.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchViewModel extends ViewModel {
    public final MutableLiveData<Searcher> searcherMutableLiveData = new MutableLiveData<>();

    public SearchViewModel() {
        searcherMutableLiveData.postValue(new Searcher("北京市", ""));
    }


    public void input(String city, String keyword) {
        Searcher searcher = searcherMutableLiveData.getValue();
        if (searcher != null) {
            searcher.setCity(city);
            searcher.setKeyword(keyword);
            searcherMutableLiveData.postValue(searcher);
        }
    }

    public void setCity(String city) {
        Searcher searcher = searcherMutableLiveData.getValue();
        if (searcher != null) {
            searcher.setCity(city);
            searcherMutableLiveData.setValue(searcher);
        }
    }

    public String getCity() {
        Searcher searcher = searcherMutableLiveData.getValue();
        if (searcher != null) {
            return searcher.getCity();
        }
        return "e";
    }





}
