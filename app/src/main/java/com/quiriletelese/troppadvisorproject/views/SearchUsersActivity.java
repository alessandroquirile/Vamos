package com.quiriletelese.troppadvisorproject.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.SearchUsersActivityController;

public class SearchUsersActivity extends AppCompatActivity {

    private SearchUsersActivityController searchUsersActivityController;
    private RecyclerView recyclerViewSearchUSers;
    private View searchUsersEmptyTextLayout, searchUsersNoContentErrorLayout;
    private SearchView searchViewSearchUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        Toolbar toolbar = findViewById(R.id.tool_bar_search_users);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setContentInsetStartWithNavigation(0);

        initializeViewComponents();
        initializeController();
        setListenersOnViewComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_users_activity, menu);
        searchViewSearchUsers = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_users));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        getSearchViewSearchUsers().setIconifiedByDefault(false);
        getSearchViewSearchUsers().setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        getSearchViewSearchUsers().setQueryHint(getString(R.string.search_by_name_or_username_hint));
        getSearchViewSearchUsers().setFocusable(true);
        getSearchViewSearchUsers().requestFocus();
        getSearchViewSearchUsers().requestFocusFromTouch();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onOptionsItemSelectedHelper(item);
        return true;
    }

    private void onOptionsItemSelectedHelper(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
    }

    private void initializeViewComponents() {
        recyclerViewSearchUSers = findViewById(R.id.recycler_view_search_users);
        searchUsersEmptyTextLayout = findViewById(R.id.search_users_empty_text_layout);
        searchUsersNoContentErrorLayout = findViewById(R.id.search_users_no_content_layout);
    }

    private void initializeController() {
        searchUsersActivityController = new SearchUsersActivityController(this);
    }

    public void setListenersOnViewComponents() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> searchUsersActivityController.setListenersOnViewComponents(), 100);
    }

    public RecyclerView getRecyclerViewSearchUSers() {
        return recyclerViewSearchUSers;
    }

    public View getSearchUsersEmptyTextLayout() {
        return searchUsersEmptyTextLayout;
    }

    public View getSearchUsersNoContentErrorLayout() {
        return searchUsersNoContentErrorLayout;
    }

    public SearchView getSearchViewSearchUsers() {
        return searchViewSearchUsers;
    }
}