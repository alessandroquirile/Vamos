package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewSearchUsersAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.UserDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.SearchUsersActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SearchUsersActivityController implements SearchView.OnQueryTextListener {

    private SearchUsersActivity searchUsersActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();

    public SearchUsersActivityController(SearchUsersActivity searchUsersActivity) {
        this.searchUsersActivity = searchUsersActivity;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        onQueryTextChangeHelper(newText);
        return true;
    }

    private void findByNameOrUsernameHelper(VolleyCallBack volleyCallBack, String nameOrUsername) {
        getUserDAO().findByNameOrUsername(volleyCallBack, nameOrUsername, getContext());
    }

    private void onQueryTextChangeHelper(String newText) {
        if (isTextEmpty(newText)) {
            setViewVisibility(getSearchUsersEmptyTextLayout(), View.VISIBLE);
            setViewVisibility(getSearchUsersNoContentErrorLayout(), View.GONE);
        } else {
            setViewVisibility(getSearchUsersEmptyTextLayout(), View.GONE);
            setViewVisibility(getSearchUsersNoContentErrorLayout(), View.GONE);
            findByNameOrUsername(newText);
        }
    }

    private void findByNameOrUsername(String nameOrUsername) {
        findByNameOrUsernameHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallBackOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        }, nameOrUsername);
    }

    private void volleyCallBackOnSuccess(Object object) {
        initializeRecylerView(object);
    }

    private void volleyCallbackOnError(String errocode) {
        switch (errocode) {
            case "204":
                setViewVisibility(getSearchUsersEmptyTextLayout(), View.GONE);
                setViewVisibility(getSearchUsersNoContentErrorLayout(), View.VISIBLE);
                break;
            default:
                showToastOnUiThred(R.string.unexpected_error_while_fetch_data);
                setViewVisibility(getSearchUsersEmptyTextLayout(), View.GONE);
                setViewVisibility(getSearchUsersNoContentErrorLayout(), View.GONE);
        }
    }

    private void initializeRecylerView(Object object) {
        List<User> users = (List<User>) object;
        getRecyclerViewSearchUSers().setLayoutManager(createLinearLayoutManager());
        getRecyclerViewSearchUSers().setAdapter(createRecyclerViewAdapter(users));
    }

    private RecyclerViewSearchUsersAdapter createRecyclerViewAdapter(List<User> users) {
        return new RecyclerViewSearchUsersAdapter(users, getContext());
    }

    @NotNull
    @Contract(" -> new")
    private LinearLayoutManager createLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
    }

    public void setListenersOnViewComponents() {
        getSearchViewSearchUsers().setOnQueryTextListener(this);
    }

    private void showToastOnUiThred(int stringId) {
        searchUsersActivity.runOnUiThread(() ->
                Toast.makeText(getContext(), getString(stringId), Toast.LENGTH_SHORT).show());
    }

    private RecyclerView getRecyclerViewSearchUSers() {
        return searchUsersActivity.getRecyclerViewSearchUSers();
    }

    private View getSearchUsersEmptyTextLayout() {
        return searchUsersActivity.getSearchUsersEmptyTextLayout();
    }

    public View getSearchUsersNoContentErrorLayout() {
        return searchUsersActivity.getSearchUsersNoContentErrorLayout();
    }

    private SearchView getSearchViewSearchUsers() {
        return searchUsersActivity.getSearchViewSearchUsers();
    }

    private Resources getResources() {
        return searchUsersActivity.getResources();
    }

    private String getString(int string) {
        return getResources().getString(string);
    }

    private Context getContext() {
        return searchUsersActivity.getApplicationContext();
    }

    private UserDAO getUserDAO() {
        return daoFactory.getUserDAO(getStorageTechnology(Constants.getUserStorageTechnology()));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private void setViewVisibility(View view, int visibility) {
        view.setVisibility(visibility);
    }

    public boolean isTextEmpty(String text) {
        return text.isEmpty();
    }

}
