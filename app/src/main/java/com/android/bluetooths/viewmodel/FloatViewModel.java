package com.android.bluetooths.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FloatViewModel extends ViewModel {

    public final MutableLiveData<Boolean> isShowWindow = new MutableLiveData<>();

    public final MutableLiveData<Boolean> isShowSuspendWindow = new MutableLiveData<>();

    public final MutableLiveData<Boolean> isVisible = new MutableLiveData<>();
}
