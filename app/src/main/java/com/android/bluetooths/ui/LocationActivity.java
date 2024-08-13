package com.android.bluetooths.ui;

import android.content.Context;
import android.content.Intent;
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
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

import com.android.bluetooths.R;

import com.android.bluetooths.databinding.ActivityLocationBinding;
import com.android.bluetooths.databinding.ActivityMainBinding;
import com.android.bluetooths.viewmodel.SearchViewModel;
import com.android.bluetooths.viewmodel.Searcher;


public class LocationActivity extends BaseActivity {


    private EditText mEditCity;
    private AutoCompleteTextView mKeyWordsView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLocationBinding dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_location);

        mEditCity = dataBinding.city;
        mKeyWordsView = dataBinding.searchkey;

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Loc");

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
        NavController navController = navHostFragment.getNavController();
        if (bundle != null) {
            navController.navigate(R.id.map_Fragment, bundle);
        }


        final SearchViewModel searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);


        searchViewModel.searcherMutableLiveData.observe(this, new Observer<Searcher>() {
            @Override
            public void onChanged(Searcher searcher) {
                mEditCity.setText(searcher.getCity());
            }
        });


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


                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(navController.getGraph().getStartDestinationId(), false)
                            .setRestoreState(true)
                            .build();
                    navController.navigate(R.id.search_Fragment, null, navOptions);
                }
                searchViewModel.input(mEditCity.getText().toString(), cs.toString());

            }

        });


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
