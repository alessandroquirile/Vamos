package com.quiriletelese.troppadvisorproject.controllers;

import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewSeeReviewsAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.SeeReviewsActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import java.util.List;

public class SeeReviewsActivityController implements Constants {

    private SeeReviewsActivity seeReviewsActivity;
    private DAOFactory daoFactory;
    private RecyclerViewSeeReviewsAdapter recyclerViewSeeReviewsAdapter;
    private int page = 0, size = 30;
    private boolean loadData = false;

    public SeeReviewsActivityController(SeeReviewsActivity seeReviewsActivity) {
        this.seeReviewsActivity = seeReviewsActivity;
    }

    private void findRestaurantReviews(VolleyCallBack volleyCallBack, String id) {
        daoFactory = DAOFactory.getInstance();
        ReviewDAO reviewDAO = daoFactory.getReviewDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY, seeReviewsActivity.getApplicationContext()));
        reviewDAO.findRestaurantReviews(volleyCallBack, id, seeReviewsActivity.getApplicationContext(), page, size);
    }

    public void intializeRecyclerView() {
        findRestaurantReviews(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(seeReviewsActivity, getAccomodationId(), Toast.LENGTH_SHORT).show();
                initializeRecyclerViewOnSuccess((List<Review>) object);
            }

            @Override
            public void onError(String errorCode) {
                seeReviewsActivity.runOnUiThread(() -> {
                    Toast.makeText(seeReviewsActivity, errorCode, Toast.LENGTH_SHORT).show();
                });
            }
        }, getAccomodationId());
    }

    /*public void addRecyclerViewOnScrollListener(final PointSearch pointSearch) {
        seeReviewsActivity.getRecyclerViewSeeReviews().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isScrollingDown(dy))
                    if (isScrolledToLastItem(recyclerView))
                        loadMoreReviews(pointSearch);
            }
        });
    }*/

    private void initializeRecyclerViewOnSuccess(List<Review> reviews) {
        Toast.makeText(seeReviewsActivity, reviews.toString(), Toast.LENGTH_SHORT).show();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(seeReviewsActivity.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewSeeReviewsAdapter = new RecyclerViewSeeReviewsAdapter(seeReviewsActivity.getApplicationContext(), reviews);
        seeReviewsActivity.getRecyclerViewSeeReviews().setLayoutManager(linearLayoutManager);
        seeReviewsActivity.getRecyclerViewSeeReviews().setAdapter(recyclerViewSeeReviewsAdapter);
    }

    /*private void addNewHotelsToList(List<Hotel> hotels) {
        recyclerViewHotelsListAdapter.addListItems(hotels);
        recyclerViewHotelsListAdapter.notifyDataSetChanged();
        setProgressBarLoadMoreInvisible();
    }

    private void showToastNoMoreHotels() {
        setProgressBarLoadMoreInvisible();
        Toast.makeText(hotelsListActivity, hotelsListActivity.getResources().getString(R.string.end_of_results), Toast.LENGTH_SHORT).show();
    }*/

    /*private void loadMoreReviews(PointSearch pointSearch) {
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
    }*/

    private boolean isScrollingDown(int dy) {
        return dy > 0;
    }

    private boolean isScrolledToLastItem(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    /*private void setProgressBarLoadMoreVisible() {
        ProgressBar progressBar = hotelsListActivity.getProgressBarHotelLoadMore();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setProgressBarLoadMoreInvisible() {
        ProgressBar progressBar = hotelsListActivity.getProgressBarHotelLoadMore();
        progressBar.setVisibility(View.GONE);
    }*/

    public void setToolbarSubtitle(){
        seeReviewsActivity.getSupportActionBar().setSubtitle(getAccomodationNAme());
    }

    private String getAccomodationId() {
        return seeReviewsActivity.getIntent().getStringExtra(ID);
    }

    private String getAccomodationNAme() {
        return seeReviewsActivity.getIntent().getStringExtra(ACCOMODATION_NAME);
    }

}
