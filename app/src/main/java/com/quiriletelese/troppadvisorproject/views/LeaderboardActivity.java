package com.quiriletelese.troppadvisorproject.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewLeaderboardAdapter;

import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLeaderboard;
    private RecyclerViewLeaderboardAdapter recyclerViewLeaderboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeViewComponents();
    }

    private void initializeViewComponents() {
        recyclerViewLeaderboard = findViewById(R.id.recycler_view_leaderboard);
        recyclerViewLeaderboardAdapter = new RecyclerViewLeaderboardAdapter(getApplicationContext(), null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewLeaderboard.setAdapter(recyclerViewLeaderboardAdapter);
        recyclerViewLeaderboard.setLayoutManager(linearLayoutManager);
    }

}