package com.quiriletelese.troppadvisorproject.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Toolbar toolbar = findViewById(R.id.tool_bar_leaderboard);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeViewComponents();
        initializeController();
        findLeaderboard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_leaderboard, menu);
        menuItem = menu.findItem(R.id.user_position);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.user_position:
                scrollToUserPosition();
                break;
        }
        return true;
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

    public void scrollToUserPosition() {
        leaderboardActivityController.scrollToUserPosition();
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

    public MenuItem getMenuItem() {
        return menuItem;
    }
}