package com.android.bluetooths.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


object ViewModleMain : ViewModel() {
    //悬浮窗口创建 移除  基于无障碍服务
    var isShowWindow = MutableLiveData<Boolean>()
    //悬浮窗口创建 移除

    var isShowSuspendWindow = MutableLiveData<Boolean>()

    //悬浮窗口显示 隐藏
    var isVisible = MutableLiveData<Boolean>()

}
