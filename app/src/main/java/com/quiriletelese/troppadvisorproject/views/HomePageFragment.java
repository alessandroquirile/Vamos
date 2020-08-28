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
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.HomePageController;

public class HomePageFragment extends Fragment {

    private HomePageController homePageController;
    private RecyclerView recyclerViewHotel, recyclerViewRestaurant, recyclerViewAttraction;
    private View frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViewComponents(view);
        initializeRecyclerViewHotel();
        initializeRecyclerViewRestaurant();
        initializeRecyclerViewAttraction();
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
                startSearchActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeViewComponents(View view) {
        homePageController = new HomePageController(HomePageFragment.this);
        recyclerViewHotel = view.findViewById(R.id.recycler_view_hotel);
        recyclerViewRestaurant = view.findViewById(R.id.recycler_view_restaurant);
        recyclerViewAttraction = view.findViewById(R.id.recycler_view_attraction);
        frameLayout = view.findViewById(R.id.home_page_loading_screen);
        frameLayout.setVisibility(View.VISIBLE);
    }

    private void startSearchActivity(){
        startActivity(new Intent(getActivity(), SearchActivity.class));
    }

    private void initializeRecyclerViewHotel() {
        homePageController.initializeRecyclerViewHotel();
    }

    private void initializeRecyclerViewRestaurant() {
        homePageController.initializeRecyclerViewRestaurant();
    }

    private void initializeRecyclerViewAttraction() {
        homePageController.initializeRecyclerViewAttraction();
    }

    public RecyclerView getRecyclerViewHotel() {
        return recyclerViewHotel;
    }

    public RecyclerView getRecyclerViewRestaurant() {
        return recyclerViewRestaurant;
    }

    public RecyclerView getRecyclerViewAttraction() {
        return recyclerViewAttraction;
    }

    public View getFrameLayout() {
        return frameLayout;
    }
}