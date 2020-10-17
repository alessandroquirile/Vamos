package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewSeeReviewsAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.util_interfaces.Constants;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.SeeReviewsActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.todkars.shimmer.ShimmerRecyclerView;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class SeeReviewsActivityController implements Constants {

    private final SeeReviewsActivity seeReviewsActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private RecyclerViewSeeReviewsAdapter recyclerViewSeeReviewsAdapter;
    private final int size = 30;
    private int page = 0;

    public SeeReviewsActivityController(SeeReviewsActivity seeReviewsActivity) {
        this.seeReviewsActivity = seeReviewsActivity;
    }

    private void findAccomodationReviewsHelper(VolleyCallBack volleyCallBack) {
        getReviewDAO().findAccomodationReviews(volleyCallBack, getAccomodationId(), getContext(), page, size);
    }

    private void findAccomodationReviews() {
        findAccomodationReviewsHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewOnSuccess((List<Review>) object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        });
    }

    private void loadMoreAccomodationReviews() {
        page++;
        findAccomodationReviewsHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                addNewReviewsToList((List<Review>) object);
            }

            @Override
            public void onError(String errorCode) {
                showToastNoMoreReviews();
            }
        });
    }

    public void intializeRecyclerView() {
        findAccomodationReviews();
    }

    private void initializeRecyclerViewOnSuccess(List<Review> reviews) {
        LinearLayoutManager linearLayoutManager = createLinearLayoutManager();
        recyclerViewSeeReviewsAdapter = createRecyclerViewAdapter(reviews);
        getShimmerRecyclerViewSeeReviews().setLayoutManager(linearLayoutManager);
        getShimmerRecyclerViewSeeReviews().setAdapter(recyclerViewSeeReviewsAdapter);
    }

    public void initializeRecyclerViewsFakeContent() {
        getShimmerRecyclerViewSeeReviews().setLayoutManager(createLinearLayoutManager());
        getShimmerRecyclerViewSeeReviews().showShimmer();
    }

    @NotNull
    @Contract(" -> new")
    private LinearLayoutManager createLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), setRecyclerViewVerticalOrientation(), false);
    }

    private int setRecyclerViewVerticalOrientation() {
        return RecyclerView.VERTICAL;
    }

    @NotNull
    @Contract("_ -> new")
    private RecyclerViewSeeReviewsAdapter createRecyclerViewAdapter(List<Review> reviews) {
        return new RecyclerViewSeeReviewsAdapter(getContext(), reviews);
    }

    private void volleyCallbackOnError(@NotNull String errorCode) {
        switch (errorCode) {
            case "204":
                handle204VolleyError();
                break;
            default:
                handleOtherVolleyError();
                break;
        }
    }

    private void handle204VolleyError() {
        showToastOnUiThread(R.string.no_reviews);
    }

    private void handleOtherVolleyError() {
        showToastOnUiThread(R.string.unexpected_error_while_fetch_data);
    }

    private void showToastOnUiThread(int string) {
        seeReviewsActivity.runOnUiThread(() -> {
            Toast.makeText(seeReviewsActivity, getString(string), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadMoreReviews() {
        setProgressBarLoadMoreVisibility(View.VISIBLE);
        loadMoreAccomodationReviews();
    }

    private void addNewReviewsToList(List<Review> reviews) {
        recyclerViewSeeReviewsAdapter.addListItems(reviews);
        recyclerViewSeeReviewsAdapter.notifyDataSetChanged();
        setProgressBarLoadMoreVisibility(View.INVISIBLE);
    }

    private void showToastNoMoreReviews() {
        setProgressBarLoadMoreVisibility(View.INVISIBLE);
        showToastOnUiThread(R.string.end_of_results);
    }

    public void addRecyclerViewOnScrollListener() {
        getShimmerRecyclerViewSeeReviews().addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private boolean isScrolledToLastItem(@NotNull RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    private void setProgressBarLoadMoreVisibility(int visibility){
        getProgressBarLoadMore().setVisibility(visibility);
    }

    public void setToolbarSubtitle() {
        getSupportActionBar().setSubtitle(getAccomodationName());
    }

    private String getAccomodationId() {
        return seeReviewsActivity.getIntent().getStringExtra(ID);
    }

    private String getAccomodationName() {
        return seeReviewsActivity.getIntent().getStringExtra(ACCOMODATION_NAME);
    }

    private ReviewDAO getReviewDAO() {
        return daoFactory.getReviewDAO(getStorageTechnology(REVIEW_STORAGE_TECHNOLOGY));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private ActionBar getSupportActionBar(){
        return seeReviewsActivity.getSupportActionBar();
    }

    private ProgressBar getProgressBarLoadMore(){
        return seeReviewsActivity.getProgressBarLoadMore();
    }

    private ShimmerRecyclerView getShimmerRecyclerViewSeeReviews() {
        return seeReviewsActivity.getShimmerRecyclerViewSeeReviews();
    }

    private Context getContext() {
        return seeReviewsActivity.getApplicationContext();
    }

    private Resources getResources() {
        return seeReviewsActivity.getResources();
    }

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }

}
