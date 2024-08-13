package com.android.bluetooths.service;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.compose.ui.tooling.animation.clock.UtilsKt;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.android.bluetooths.R;
import com.android.bluetooths.utils.PermissionUtils;
import com.android.bluetooths.viewmodel.FloatViewModel;

public class LocService extends Service {

    private WindowManager windowManager;

    private View floatView;

    private LocationManager mLocManager;

    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.AppTheme_NoActionBar);

        mLocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!PermissionUtils.isAllowMockLocation(this)) {
            //PermissionUtils.showEnableMockLocationDialog(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class ServiceBinder extends Binder {

    }

}
