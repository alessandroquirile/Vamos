package com.quiriletelese.troppadvisorproject.controllers;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
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
    private int page = 0, size = 4;
    private boolean loadData = true;

    public SeeReviewsActivityController(SeeReviewsActivity seeReviewsActivity) {
        this.seeReviewsActivity = seeReviewsActivity;
    }

    private void findAccomodationReviewsHelper(VolleyCallBack volleyCallBack, String id, int page) {
        daoFactory = DAOFactory.getInstance();
        ReviewDAO reviewDAO = daoFactory.getReviewDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY, seeReviewsActivity.getApplicationContext()));
        reviewDAO.findAccomodationReviews(volleyCallBack, id, seeReviewsActivity.getApplicationContext(), page, size);
    }

    private void findAccomodationReviews() {
        findAccomodationReviewsHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewOnSuccess((List<Review>) object);
            }

            @Override
            public void onError(String errorCode) {
                seeReviewsActivity.runOnUiThread(() -> {
                    Toast.makeText(seeReviewsActivity, errorCode, Toast.LENGTH_SHORT).show();
                });
            }
        }, getAccomodationId(), page);
    }

    public void intializeRecyclerView() {
        findAccomodationReviews();
    }

    private void initializeRecyclerViewOnSuccess(List<Review> reviews) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(seeReviewsActivity.getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerViewSeeReviewsAdapter = new RecyclerViewSeeReviewsAdapter(seeReviewsActivity.getApplicationContext(), reviews);
        seeReviewsActivity.getShimmerRecyclerViewSeeReviews().setLayoutManager(linearLayoutManager);
        seeReviewsActivity.getShimmerRecyclerViewSeeReviews().setAdapter(recyclerViewSeeReviewsAdapter);
    }

    public void initializeRecyclerViewsFakeContent() {
        seeReviewsActivity.getShimmerRecyclerViewSeeReviews().setLayoutManager(new LinearLayoutManager(seeReviewsActivity.getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
        seeReviewsActivity.getShimmerRecyclerViewSeeReviews().showShimmer();
    }

    private void loadMoreReviews() {
        if (loadData) {
            setProgressBarLoadMoreVisible();
            loadMoreAccomodationReviews();
        }
    }

    private void loadMoreAccomodationReviews() {
        findAccomodationReviewsHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                addNewReviewsToList((List<Review>) object);
            }

            @Override
            public void onError(String errorCode) {
                seeReviewsActivity.runOnUiThread(() -> {
                    loadData = false;
                    showToastNoMoreReviews();
                });
            }
        }, getAccomodationId(), page += 1);
        System.out.println("PAGE = " + page);
    }

    private void addNewReviewsToList(List<Review> reviews) {
        recyclerViewSeeReviewsAdapter.addListItems(reviews);
        recyclerViewSeeReviewsAdapter.notifyDataSetChanged();
        setProgressBarLoadMoreInvisible();
    }

    private void showToastNoMoreReviews() {
        setProgressBarLoadMoreInvisible();
        Toast.makeText(seeReviewsActivity, seeReviewsActivity.getResources().getString(R.string.end_of_results), Toast.LENGTH_SHORT).show();
    }

    public void addRecyclerViewOnScrollListener() {
        seeReviewsActivity.getShimmerRecyclerViewSeeReviews().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isScrollingDown(dy))
                    if (isScrolledToLastItem(recyclerView))
                        loadMoreReviews();
            }
        });
    }

    private boolean isScrollingDown(int dy) {
        return dy > 0;
    }

    private boolean isScrolledToLastItem(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    private void setProgressBarLoadMoreVisible() {
        ProgressBar progressBar = seeReviewsActivity.getProgressBarReviewsLoadMore();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setProgressBarLoadMoreInvisible() {
        ProgressBar progressBar = seeReviewsActivity.getProgressBarReviewsLoadMore();
        progressBar.setVisibility(View.GONE);
    }

    public void setToolbarSubtitle() {
        seeReviewsActivity.getSupportActionBar().setSubtitle(getAccomodationName());
    }

    private String getAccomodationId() {
        return seeReviewsActivity.getIntent().getStringExtra(ID);
    }

    private String getAccomodationName() {
        return seeReviewsActivity.getIntent().getStringExtra(ACCOMODATION_NAME);
    }

}
