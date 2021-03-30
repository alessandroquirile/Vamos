package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewLeaderboardAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.UserDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.LeaderboardActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeaderboardActivityController {

    private final LeaderboardActivity leaderboardActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private int userPosition;

    public LeaderboardActivityController(LeaderboardActivity leaderboardActivity) {
        this.leaderboardActivity = leaderboardActivity;
    }

    private void findLeaderboardHelper(VolleyCallBack volleyCallBack) {
        getUserDAO().findLeaderboard(volleyCallBack, getContext());
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
        handleUserPositionMenuItem(object);
    }

    private void volleyCallBackOnError(String errorCode) {
        setViewVisibility(getNoContentLeaderboardLayout(), View.VISIBLE);
        setViewVisibility(getProgressBarLeaderboard(), View.GONE);
    }

    private void handleUserPositionMenuItem(Object object) {
        userPosition = getUserPosition(object);
        if (userPosition != -1)
            getMenuItem().setTitle("Sei " + (userPosition + 1) + "° in classifica");
    }

    private int getUserPosition(Object object) {
        int position = -1;
        String email = getEmail();
        List<User> users = (List<User>) object;
        for (int i = 0; i < users.size(); i++)
            if (users.get(i).getEmail().equals(email)) {
                position = i;
                break;
            }
        return position;
    }

    public void scrollToUserPosition(){
        getRecyclerViewLeaderboard().scrollToPosition(userPosition);
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

    private void setViewVisibility(@NotNull View view, int visibility) {
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

    public MenuItem getMenuItem() {
        return leaderboardActivity.getMenuItem();
    }

    private String getEmail() {
        return new UserSharedPreferences(getContext()).getStringSharedPreferences(Constants.getEmail());
    }

    private UserDAO getUserDAO() {
        return daoFactory.getUserDAO(getStorageTechnology(Constants.getUserStorageTechnology()));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private Context getContext() {
        return leaderboardActivity.getApplicationContext();
    }

}
