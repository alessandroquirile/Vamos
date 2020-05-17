package com.quiriletelese.troppadvisorproject.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.quiriletelese.troppadvisorproject.R;

import java.util.Objects;

public class SeeResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_results); //Se qui vedi che non trova il layout non so perchè lo fa, sarà un bug perchè comunque in fase d'avvio non da nessun problema
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vedi risultati"); //Temporaneo
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_see_results_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_button_menu_see_results_activity:
                startActivity(new Intent(SeeResultsActivity.this, MapsActivity.class));
                break;
            case R.id.filter_button_menu_see_results_activity:
                startActivity(new Intent(SeeResultsActivity.this, SeeResultsFilterActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
