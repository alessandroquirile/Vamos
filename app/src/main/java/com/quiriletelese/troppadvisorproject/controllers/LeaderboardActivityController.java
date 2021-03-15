package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewLeaderboardAdapter;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.views.LeaderboardActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeaderboardActivityController {

    private LeaderboardActivity leaderboardActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();

    public LeaderboardActivityController(LeaderboardActivity leaderboardActivity) {
        this.leaderboardActivity = leaderboardActivity;
    }

    private void findLeaderboardHelper(VolleyCallBack volleyCallBack) {
        daoFactory.getUserDAO(Constants.getUserStorageTechnology()).findLeaderboard(volleyCallBack, getContext());
    }

    public void findLeaderboard() {
        findLeaderboardHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallBackOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallBackOnError(errorCode);
            }
        });
    }

    private void volleyCallBackOnSuccess(Object object) {
        setViewVisibility(getNoContentLeaderboardLayout(), View.GONE);
        setViewVisibility(getProgressBarLeaderboard(), View.GONE);
        initializeRecylerView(object);
    }

    private void volleyCallBackOnError(String errorCode) {
        setViewVisibility(getNoContentLeaderboardLayout(), View.VISIBLE);
        setViewVisibility(getProgressBarLeaderboard(), View.GONE);
    }

    private void initializeRecylerView(Object object) {
        List<User> users = (List<User>) object;
        getRecyclerViewLeaderboard().setLayoutManager(createLinearLayoutManager());
        getRecyclerViewLeaderboard().setAdapter(createRecyclerViewAdapter(users));
    }

    private RecyclerViewLeaderboardAdapter createRecyclerViewAdapter(List<User> users) {
        return new RecyclerViewLeaderboardAdapter(users, getContext());
    }

    @NotNull
    @Contract(" -> new")
    private LinearLayoutManager createLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
    }

    private void setViewVisibility(View view, int visibility) {
        view.setVisibility(visibility);
    }

    public ProgressBar getProgressBarLeaderboard() {
        return leaderboardActivity.getProgressBarLeaderboard();
    }

    public RecyclerView getRecyclerViewLeaderboard() {
        return leaderboardActivity.getRecyclerViewLeaderboard();
    }

    public View getNoContentLeaderboardLayout() {
        return leaderboardActivity.getNoContentLeaderboardLayout();
    }

    private Context getContext() {
        return leaderboardActivity.getApplicationContext();
    }

}
