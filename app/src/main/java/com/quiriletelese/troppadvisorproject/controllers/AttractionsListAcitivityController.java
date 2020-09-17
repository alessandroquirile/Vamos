package com.quiriletelese.troppadvisorproject.controllers;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewAttractionsListAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.AttractionsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.w3c.dom.Attr;

import java.util.List;

public class AttractionsListAcitivityController implements Constants {

    private AttractionsListActivity attractionsListActivity;
    private DAOFactory daoFactory;
    private RecyclerViewAttractionsListAdapter recyclerViewAttractionsListAdapter;
    private int page = 0, size = 3;
    private boolean loadData = false;

    public AttractionsListAcitivityController(AttractionsListActivity attractionsListActivity) {
        this.attractionsListActivity = attractionsListActivity;
    }

    public void findAttractionsByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch, int page, int size) {
        daoFactory = DAOFactory.getInstance();
        AttractionDAO attractionDAO = daoFactory.getAttractionDAO(ConfigFileReader.getProperty(ATTRACTION_STORAGE_TECHNOLOGY, attractionsListActivity.getApplicationContext()));
        attractionDAO.findByPointNear(volleyCallBack, pointSearch, attractionsListActivity.getApplicationContext(), page, size);
    }

    public void initializeRecyclerView(PointSearch pointSearch) {
        findAttractionsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewOnSuccess((List<Attraction>) object);
            }

            @Override
            public void onError(String errorCode) {

            }
        }, pointSearch, this.page, this.size);
    }

    public void addRecyclerViewOnScrollListener(final PointSearch pointSearch) {
        attractionsListActivity.getRecyclerViewAttractionsList().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isScrollingDown(dy))
                    if (isScrolledToLastItem(recyclerView))
                        loadMoreAttractions(pointSearch);
            }
        });
    }

    private boolean isScrollingDown(int dy) {
        return dy > 0;
    }

    private boolean isScrolledToLastItem(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    private void loadMoreAttractions(PointSearch pointSearch) {
        loadData = true;
        if (loadData) {
            loadData = false;
            setProgressBarLoadMoreVisible();
            findAttractionsByPointNear(new VolleyCallBack() {
                @Override
                public void onSuccess(Object object) {
                    addNewAttractionsToList((List<Attraction>) object);

                }

                @Override
                public void onError(String errorCode) {
                    attractionsListActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToastNoMoreAttractions();
                        }
                    });
                }
            }, pointSearch, page += 1, size);
        }
    }

    private void initializeRecyclerViewOnSuccess(List<Attraction> attractions) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(attractionsListActivity.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewAttractionsListAdapter = new RecyclerViewAttractionsListAdapter(attractionsListActivity.getApplicationContext(), attractions);
        attractionsListActivity.getRecyclerViewAttractionsList().setLayoutManager(linearLayoutManager);
        attractionsListActivity.getRecyclerViewAttractionsList().setAdapter(recyclerViewAttractionsListAdapter);
    }

    private void addNewAttractionsToList(List<Attraction> attractions) {
        recyclerViewAttractionsListAdapter.addListItems(attractions);
        recyclerViewAttractionsListAdapter.notifyDataSetChanged();
        setProgressBarLoadMoreInvisible();
    }

    private void showToastNoMoreAttractions() {
        setProgressBarLoadMoreInvisible();
        Toast.makeText(attractionsListActivity, attractionsListActivity.getResources().getString(R.string.end_of_results), Toast.LENGTH_SHORT).show();
    }

    private void setProgressBarLoadMoreVisible() {
        ProgressBar progressBar = attractionsListActivity.getProgressBarAttractionLoadMore();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setProgressBarLoadMoreInvisible() {
        ProgressBar progressBar = attractionsListActivity.getProgressBarAttractionLoadMore();
        progressBar.setVisibility(View.GONE);
    }

}
