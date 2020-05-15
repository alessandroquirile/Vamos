package com.quiriletelese.troppadvisorproject.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.quiriletelese.troppadvisorproject.R;

public class HomePageActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }


    // Costruisce il menù in alto a destra
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // Listener sul click di un tasto dal menù in alto a destra
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchButton:
                startActivity(new Intent(HomePageActivity.this, LoginActivity.class));
                break;
            case R.id.mapButton:
                Toast.makeText(getApplicationContext(), "Premuto Mappa", Toast.LENGTH_SHORT).show();
                break;
            case R.id.filterButton:
                Toast.makeText(getApplicationContext(), "Premuto Filtri", Toast.LENGTH_SHORT).show();
                break;

            case R.id.subitem_tutto:
                    Toast.makeText(getApplicationContext(), "Premuto Tutto", Toast.LENGTH_SHORT).show();
                break;
            case R.id.subitem_hotel:
                    Toast.makeText(getApplicationContext(), "Premuto Hotel", Toast.LENGTH_SHORT).show();
                break;
            case R.id.subitem_ristoranti:
                Toast.makeText(getApplicationContext(), "Premuto Ristoranti", Toast.LENGTH_SHORT).show();
                break;
            case R.id.subitem_attrazioni:
                    Toast.makeText(getApplicationContext(), "Premuto Attrazioni", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
