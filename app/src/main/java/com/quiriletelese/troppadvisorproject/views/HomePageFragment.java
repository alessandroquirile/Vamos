package com.quiriletelese.troppadvisorproject.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.quiriletelese.troppadvisorproject.R;

public class HomePageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_button_menu_home_page_activity:
                startActivity(new Intent(getContext(),LoginActivity.class
                        /*SearchActivity.class*/ /*WriteReviewActivity.class*//*OverviewActivity.class*/));
                break;
            case R.id.map_button_menu_home_page_activity:
                startActivity(new Intent(getContext(), MapsActivity.class));
                break;
            case R.id.filter_button_menu_home_page_activity:
                break;
            case R.id.radio_button_subitem_all:
                break;
            case R.id.radio_button_subitem_hotel:
                break;
            case R.id.radio_button_subitem_restaurant:
                break;
            case R.id.radio_button_subitem_attractions:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}