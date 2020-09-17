package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewHotelsListAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.HotelMapActivity;
import com.quiriletelese.troppadvisorproject.views.HotelsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import java.util.List;

public class HotelsListActivityController implements Constants {

    private HotelsListActivity hotelsListActivity;
    private DAOFactory daoFactory;
    private RecyclerViewHotelsListAdapter recyclerViewHotelsListAdapter;
    private int page = 0, size = 3;
    private boolean loadData = false;

    public HotelsListActivityController(HotelsListActivity hotelsListActivity) {
        this.hotelsListActivity = hotelsListActivity;
    }

    public void findHotelsByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch, int page, int size) {
        daoFactory = DAOFactory.getInstance();
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY, hotelsListActivity.getApplicationContext()));
        hotelDAO.findByPointNear(volleyCallBack, pointSearch, hotelsListActivity.getApplicationContext(), page, size);
    }

    public void initializeRecyclerView(PointSearch pointSearch) {
        findHotelsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewOnSuccess((List<Hotel>) object);
            }

            @Override
            public void onError(String errorCode) {

            }
        }, pointSearch, this.page, this.size);
    }

    public void addRecyclerViewOnScrollListener(final PointSearch pointSearch) {
        hotelsListActivity.getRecyclerViewHotelsList().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isScrollingDown(dy))
                    if (isScrolledToLastItem(recyclerView))
                        loadMoreHotels(pointSearch);
            }
        });
    }

    private boolean isScrollingDown(int dy) {
        return dy > 0;
    }

    private boolean isScrolledToLastItem(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    private void loadMoreHotels(PointSearch pointSearch) {
        loadData = true;
        if (loadData) {
            loadData = false;
            setProgressBarLoadMoreVisible();
            findHotelsByPointNear(new VolleyCallBack() {
                @Override
                public void onSuccess(Object object) {
                    addNewHotelsToList((List<Hotel>) object);

                }

                @Override
                public void onError(String errorCode) {
                    hotelsListActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToastNoMoreHotels();
                        }
                    });
                }
            }, pointSearch, page += 1, size);
        }
    }

    private void initializeRecyclerViewOnSuccess(List<Hotel> hotels) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(hotelsListActivity.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewHotelsListAdapter = new RecyclerViewHotelsListAdapter(hotelsListActivity.getApplicationContext(), hotels);
        hotelsListActivity.getRecyclerViewHotelsList().setLayoutManager(linearLayoutManager);
        hotelsListActivity.getRecyclerViewHotelsList().setAdapter(recyclerViewHotelsListAdapter);
    }

    private void addNewHotelsToList(List<Hotel> hotels) {
        recyclerViewHotelsListAdapter.addListItems(hotels);
        recyclerViewHotelsListAdapter.notifyDataSetChanged();
        setProgressBarLoadMoreInvisible();
    }

    private void showToastNoMoreHotels() {
        setProgressBarLoadMoreInvisible();
        Toast.makeText(hotelsListActivity, hotelsListActivity.getResources().getString(R.string.end_of_results), Toast.LENGTH_SHORT).show();
    }

    private void setProgressBarLoadMoreVisible() {
        ProgressBar progressBar = hotelsListActivity.getProgressBarHotelLoadMore();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setProgressBarLoadMoreInvisible() {
        ProgressBar progressBar = hotelsListActivity.getProgressBarHotelLoadMore();
        progressBar.setVisibility(View.GONE);
    }

    public void startHotelMapsActivity() {
        Intent hotelMapsActivity = new Intent(hotelsListActivity.getApplicationContext(), HotelMapActivity.class);
        hotelMapsActivity.putExtra(POINT_SEARCH, hotelsListActivity.getIntent().getSerializableExtra(POINT_SEARCH));
        hotelsListActivity.startActivity(hotelMapsActivity);
    }

}
