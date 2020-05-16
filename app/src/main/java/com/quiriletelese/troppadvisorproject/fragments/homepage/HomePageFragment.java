package com.quiriletelese.troppadvisorproject.fragments.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.views.HomePageSearchActivity;

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
            case R.id.search_button_menu:
                startActivity(new Intent(getContext(), HomePageSearchActivity.class));
                break;
            case R.id.map_button_menu:
                Toast.makeText(getContext(), "Premuto Mappa", Toast.LENGTH_SHORT).show();
                break;
            case R.id.filter_button_menu:
                Toast.makeText(getContext(), "Premuto Filtri", Toast.LENGTH_SHORT).show();
                break;
            case R.id.radio_button_subitem_all:
                Toast.makeText(getContext(), "Premuto Tutto", Toast.LENGTH_SHORT).show();
                break;
            case R.id.radio_button_subitem_hotel:
                Toast.makeText(getContext(), "Premuto Hotel", Toast.LENGTH_SHORT).show();
                break;
            case R.id.radio_button_subitem_restaurant:
                Toast.makeText(getContext(), "Premuto Ristoranti", Toast.LENGTH_SHORT).show();
                break;
            case R.id.radio_button_subitem_attractions:
                Toast.makeText(getContext(), "Premuto Attrazioni", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}