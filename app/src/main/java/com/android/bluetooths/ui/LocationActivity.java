package com.android.bluetooths.ui;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.provider.ProviderProperties;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.android.bluetooths.R;

import com.android.bluetooths.viewmodel.SearchViewModel;


public class LocationActivity extends BaseActivity {


    private EditText mEditCity;
    private AutoCompleteTextView mKeyWordsView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mEditCity = findViewById(R.id.city);
        mKeyWordsView = findViewById(R.id.searchkey);


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
        NavController navController = navHostFragment.getNavController();


        final SearchViewModel searchViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(SearchViewModel.class);



        mKeyWordsView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int currentFragmentId = navController.getCurrentDestination().getId();

                if (hasFocus && currentFragmentId != R.id.search_Fragment) {

                    navController.navigate(R.id.search_Fragment);
                }
            }
        });

        mKeyWordsView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

                if (cs.length() <= 0) {
                    return;
                }

                int currentFragmentId = navController.getCurrentDestination().getId();
                if (currentFragmentId != R.id.search_Fragment) {
                    navController.navigate(R.id.search_Fragment);
                }
                searchViewModel.input(mEditCity.getText().toString(), cs.toString());

            }

        });



    }





    private void setMockLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, true, false,
                false, true, true, true, ProviderProperties.POWER_USAGE_HIGH, ProviderProperties.ACCURACY_FINE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("ttttttt", "no gps");
        }

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(37.4219999);
        location.setLongitude(-122.0840575);
        location.setAccuracy(1.0f);
        location.setTime(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            location.setElapsedRealtimeNanos(System.nanoTime());
        }
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
        locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        try {
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
            locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("Please make sure the app is set as the mock location app in developer options.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
