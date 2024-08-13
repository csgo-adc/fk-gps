package com.android.bluetooths

import android.annotation.SuppressLint
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.lifecycle.LifecycleService
import com.android.bluetooths.utils.Util
import com.android.bluetooths.viewmodel.FloatViewModel
import com.android.bluetooths.viewmodel.ViewModleMain


class SuspendwindowService : LifecycleService() {
    private lateinit var windowManager: WindowManager
    private var floatRootView: View? = null//悬浮窗View


    override fun onCreate() {
        super.onCreate()
        Log.e("aaaaa", "onservers ce")
        initObserve()
    }

    private fun initObserve() {
        ViewModleMain.apply {
            isVisible.observe(this@SuspendwindowService, {
                floatRootView?.visibility = if (it) View.VISIBLE else View.GONE
            })
            isShowSuspendWindow.observe(this@SuspendwindowService, {
                if (it) {
                    Log.e("aaaaa", "showWindow")

                    showWindow()
                } else {
                    if (!Util.isNull(floatRootView)) {
                        if (!Util.isNull(floatRootView?.windowToken)) {
                            if (!Util.isNull(windowManager)) {
                                windowManager?.removeView(floatRootView)
                            }
                        }
                    }
                }
            })
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showWindow() {
        Log.e("aaaaa", "showWindow in")

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(outMetrics)
        var layoutParam = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            format = PixelFormat.RGBA_8888
            flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            //位置大小设置
            width = WRAP_CONTENT
            height = WRAP_CONTENT
            gravity = Gravity.LEFT or Gravity.TOP
            //设置剧中屏幕显示
            x = outMetrics.widthPixels / 2 - width / 2
            y = outMetrics.heightPixels / 2 - height / 2
        }
        // 新建悬浮窗控件
        floatRootView = LayoutInflater.from(this).inflate(R.layout.fragment_map, null)
        // 将悬浮窗控件添加到WindowManager
        windowManager.addView(floatRootView, layoutParam)
    }
}
