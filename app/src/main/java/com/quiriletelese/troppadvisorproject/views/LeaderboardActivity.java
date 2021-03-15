package com.quiriletelese.troppadvisorproject.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewLeaderboardAdapter;
import com.quiriletelese.troppadvisorproject.controllers.LeaderboardActivityController;

import java.util.List;
import java.util.Objects;

public class LeaderboardActivity extends AppCompatActivity {

    private LeaderboardActivityController leaderboardActivityController;
    private ProgressBar progressBarLeaderboard;
    private RecyclerView recyclerViewLeaderboard;
    private View noContentLeaderboardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeViewComponents();
        initializeController();
        findLeaderboard();
    }

    private void initializeViewComponents() {
        progressBarLeaderboard = findViewById(R.id.progress_bar_leaderboard);
        recyclerViewLeaderboard = findViewById(R.id.recycler_view_leaderboard);
        noContentLeaderboardLayout = findViewById(R.id.no_content_leaderboard_layout);
    }

    private void initializeController() {
        leaderboardActivityController = new LeaderboardActivityController(this);
    }

    private void findLeaderboard() {
        leaderboardActivityController.findLeaderboard();
    }

    public ProgressBar getProgressBarLeaderboard() {
        return progressBarLeaderboard;
    }

    public RecyclerView getRecyclerViewLeaderboard() {
        return recyclerViewLeaderboard;
    }

    public View getNoContentLeaderboardLayout() {
        return noContentLeaderboardLayout;
    }

}