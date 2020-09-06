package com.quiriletelese.troppadvisorproject.controllers;

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
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.HotelsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HotelsListActivityController {

    private HotelsListActivity hotelsListActivity;
    private DAOFactory daoFactory;
    private PointSearch pointSearch;
    private RecyclerViewHotelsListAdapter recyclerViewHotelsListAdapter;
    private int page = 0, size = 3;
    private boolean loadData = false;

    public HotelsListActivityController(HotelsListActivity hotelsListActivity) {
        this.hotelsListActivity = hotelsListActivity;
    }

    public void findHotelsByPointNear(VolleyCallBack volleyCallBack, List<Double> pointSearchInformation, int page, int size) {
        PointSearch pointSearch = setPointSearchInformation(pointSearchInformation);
        daoFactory = DAOFactory.getInstance();
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty("hotel_storage_technology", hotelsListActivity.getApplicationContext()));
        hotelDAO.findByPointNear(volleyCallBack, pointSearch, hotelsListActivity.getApplicationContext(), page, size);
    }

    private PointSearch setPointSearchInformation(@NotNull List<Double> pointSearchInformation) {
        pointSearch = new PointSearch();
        pointSearch.setLatitude(pointSearchInformation.get(0));
        pointSearch.setLongitude(pointSearchInformation.get(1));
        pointSearch.setDistance(pointSearchInformation.get(2));
        return pointSearch;
    }

    public void initializeRecyclerViewHotel(List<Double> pointSearchArguments) {
        findHotelsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(List<?> accomodation) {
                initializeRecyclerViewHotelOnSuccess(accomodation);
            }

            @Override
            public void onError(List<?> accomodation, String error) {

            }
        }, pointSearchInformation(pointSearchArguments), this.page, this.size);
    }

    public void addRecyclerViewOnScrollListener(final List<Double> pointSearchArguments) {
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
                        loadMoreHotels(pointSearchArguments);
            }
        });
    }

    private boolean isScrollingDown(int dy) {
        return dy > 0;
    }

    private boolean isScrolledToLastItem(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    private void loadMoreHotels(List<Double> pointSearchArguments) {
        loadData = true;
        if (loadData) {
            loadData = false;
            setProgressBarLoadMoreVisible();
            findHotelsByPointNear(new VolleyCallBack() {
                @Override
                public void onSuccess(List<?> accomodation) {
                    addNewHotelsToList(accomodation);

                }

                @Override
                public void onError(List<?> accomodation, String error) {
                    hotelsListActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToastNoMoreHotels();
                        }
                    });
                }
            }, pointSearchInformation(pointSearchArguments), page += 1, size);
        }
    }

    private void initializeRecyclerViewHotelOnSuccess(List<?> accomodation) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(hotelsListActivity.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewHotelsListAdapter = new RecyclerViewHotelsListAdapter(hotelsListActivity.getApplicationContext(), (List<Hotel>) accomodation);
        hotelsListActivity.getRecyclerViewHotelsList().setLayoutManager(linearLayoutManager);
        hotelsListActivity.getRecyclerViewHotelsList().setAdapter(recyclerViewHotelsListAdapter);
    }

    private List<Double> pointSearchInformation(List<Double> pointSearchArguments) {
        List<Double> pointSearchInformation = new ArrayList<>();
        pointSearchInformation.add(40.829904);
        pointSearchInformation.add(14.248052);
        pointSearchInformation.add(1.0);
        return pointSearchInformation;
    }

    private void addNewHotelsToList(List<?> accomodation) {
        recyclerViewHotelsListAdapter.addListItems((List<Hotel>) accomodation);
        recyclerViewHotelsListAdapter.notifyDataSetChanged();
        setProgressBarLoadMoreInvisible();
    }

    private void showToastNoMoreHotels() {
        setProgressBarLoadMoreInvisible();
        Toast.makeText(hotelsListActivity, hotelsListActivity.getResources().getString(R.string.end_of_results), Toast.LENGTH_SHORT).show();
    }

    private void setProgressBarLoadMoreVisible() {
        ProgressBar progressBar = hotelsListActivity.getProgressBarLoadMore();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setProgressBarLoadMoreInvisible() {
        ProgressBar progressBar = hotelsListActivity.getProgressBarLoadMore();
        progressBar.setVisibility(View.GONE);
    }

}
