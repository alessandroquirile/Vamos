package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewUserReviewsAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.UserReviewsActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.todkars.shimmer.ShimmerRecyclerView;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserReviewsActivityController {

    private final UserReviewsActivity userReviewsActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private RecyclerViewUserReviewsAdapter recyclerViewUserReviewsAdapter;
    private int page = 0;

    public UserReviewsActivityController(UserReviewsActivity userReviewsActivity) {
        this.userReviewsActivity = userReviewsActivity;
    }

    private void findUserReviewsHelper(VolleyCallBack volleyCallBack) {
        int size = 30;
        getReviewDAO().findUserReviews(volleyCallBack, getUserId(), getContext(), page, size);
    }

    private void findUserReviews() {
        findUserReviewsHelper(new VolleyCallBack() {
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
        findUserReviewsHelper(new VolleyCallBack() {
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
        findUserReviews();
    }

    private void initializeRecyclerViewOnSuccess(List<Review> reviews) {
        LinearLayoutManager linearLayoutManager = createLinearLayoutManager();
        recyclerViewUserReviewsAdapter = createRecyclerViewAdapter(reviews);
        getShimmerRecyclerViewSeeReviews().setLayoutManager(linearLayoutManager);
        getShimmerRecyclerViewSeeReviews().setAdapter(recyclerViewUserReviewsAdapter);
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
    private RecyclerViewUserReviewsAdapter createRecyclerViewAdapter(List<Review> reviews) {
        return new RecyclerViewUserReviewsAdapter(getContext(), userReviewsActivity, reviews);
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

    private void showToastOnUiThread(int stringId) {
        userReviewsActivity.runOnUiThread(() ->
                Toast.makeText(userReviewsActivity, getString(stringId), Toast.LENGTH_SHORT).show());
    }

    private void loadMoreReviews() {
        setProgressBarLoadMoreVisibility(View.VISIBLE);
        loadMoreAccomodationReviews();
    }

    private void addNewReviewsToList(List<Review> reviews) {
        recyclerViewUserReviewsAdapter.addListItems(reviews);
        recyclerViewUserReviewsAdapter.notifyDataSetChanged();
        setProgressBarLoadMoreVisibility(View.INVISIBLE);
    }

    private void showToastNoMoreReviews() {
        setProgressBarLoadMoreVisibility(View.INVISIBLE);
        //showToastOnUiThread(R.string.end_of_results);
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
                    if (hasScrolledToLastItem(recyclerView))
                        loadMoreReviews();
            }
        });
    }

    private boolean isScrollingDown(int dy) {
        return dy > 0;
    }

    private boolean hasScrolledToLastItem(@NotNull RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    private void setProgressBarLoadMoreVisibility(int visibility){
        getProgressBarLoadMore().setVisibility(visibility);
    }


    private String getUserId() {
        return userReviewsActivity.getIntent().getStringExtra(Constants.getId());
    }

    private ReviewDAO getReviewDAO() {
        return daoFactory.getReviewDAO(getStorageTechnology(Constants.getReviewStorageTechnology()));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private ProgressBar getProgressBarLoadMore(){
        return userReviewsActivity.getProgressBarLoadMore();
    }

    private ShimmerRecyclerView getShimmerRecyclerViewSeeReviews() {
        return userReviewsActivity.getShimmerRecyclerViewSeeReviews();
    }

    private Context getContext() {
        return userReviewsActivity.getApplicationContext();
    }

    private Resources getResources() {
        return userReviewsActivity.getResources();
    }

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }

}
