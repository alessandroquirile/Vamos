package com.quiriletelese.troppadvisorproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.views.AccomodationDetailMapsActivity;
import com.quiriletelese.troppadvisorproject.views.RestaurantDetailActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class RecyclerViewRestaurantsListAdapter extends RecyclerView.Adapter<RecyclerViewRestaurantsListAdapter.ViewHolder> {

    private final Context context;
    private final List<Restaurant> restaurants;

    public RecyclerViewRestaurantsListAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public RecyclerViewRestaurantsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_all_accomodations_list_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewRestaurantsListAdapter.ViewHolder holder, int position) {
        setFieldsOnBIndViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void addListItems(List<Restaurant> restaurants) {
        this.restaurants.addAll(restaurants);
    }

    private void setFieldsOnBIndViewHolder(ViewHolder viewHolder, int position) {
        setImage(viewHolder, position);
        viewHolder.textViewAccomodationName.setText(restaurants.get(position).getName());
        viewHolder.textViewAccomodationReview.setText(createAvarageRatingString(restaurants.get(position)));
        viewHolder.textViewAccomodationAddress.setText(createAddressString(restaurants.get(position)));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImage(ViewHolder viewHolder, int position) {
        if (hasImage(position)) {
            Picasso.with(context).load(getFirtsImage(position))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.troppadvisor_logo)
                    .error(R.drawable.picasso_error)
                    .into(viewHolder.imageViewAccomodation);
        } else
            viewHolder.imageViewAccomodation.setImageDrawable(context.getResources().getDrawable(R.drawable.picasso_error));
    }

    private boolean hasImage(int position) {
        return restaurants.get(position).hasImage();
    }

    private String getFirtsImage(int position) {
        return restaurants.get(position).getFirstImage();
    }

    @NotNull
    private String createAddressString(@NotNull Restaurant restaurant) {
        String restaurantAddress = "";
        restaurantAddress = restaurantAddress.concat(restaurant.getTypeOfAddress() + " ");
        restaurantAddress = restaurantAddress.concat(restaurant.getStreet() + ", ");
        restaurantAddress = restaurantAddress.concat(restaurant.getHouseNumber() + ", ");
        restaurantAddress = restaurantAddress.concat(restaurant.getCity() + ", ");
        if (!restaurant.getProvince().equals(restaurant.getCity()))
            restaurantAddress = restaurantAddress.concat(restaurant.getProvince() + ", ");
        restaurantAddress = restaurantAddress.concat(restaurant.getPostalCode());
        return restaurantAddress;
    }

    private String createAvarageRatingString(Restaurant restaurant) {
        return !hasReviews(restaurant) ? getString(R.string.no_reviews) : createAvarageRatingStringHelper(restaurant);
    }

    @NotNull
    private String createAvarageRatingStringHelper(@NotNull Restaurant restaurant) {
        if (restaurant.getTotalReviews().intValue() == 1)
            return restaurant.getAvarageRating().intValue() + "/5 (" + restaurant.getTotalReviews() + " " + getString(R.string.review) + ")";
        else
            return restaurant.getAvarageRating().intValue() + "/5 (" + restaurant.getTotalReviews() + " " + getString(R.string.reviews) + ")";
    }

    private boolean hasReviews(Restaurant restaurant) {
        return restaurant.hasReviews();
    }

    private Resources getResources() {
        return context.getResources();
    }

    private String getString(int string) {
        return getResources().getString(string);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RelativeLayout relativeLayoutAccomodation;
        private ImageView imageViewAccomodation;
        private TextView textViewAccomodationName, textViewAccomodationReview, textViewAccomodationAddress;
        private Button buttonWriteReview, buttonSeeAccomodationOnMap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
            setListenerOnComponents();
        }

        @Override
        public void onClick(View view) {
            onClickHelper(view);
        }

        private void initializeComponents() {
            relativeLayoutAccomodation = itemView.findViewById(R.id.relative_layout_main);
            imageViewAccomodation = itemView.findViewById(R.id.image_view_accomodation_list);
            textViewAccomodationName = itemView.findViewById(R.id.text_view_accomodation_name);
            textViewAccomodationReview = itemView.findViewById(R.id.text_view_accomodation_review);
            textViewAccomodationAddress = itemView.findViewById(R.id.text_view_hotel_address);
            buttonWriteReview = itemView.findViewById(R.id.button_write_review);
            buttonSeeAccomodationOnMap = itemView.findViewById(R.id.button_see_accomodation_on_map);
        }


        private void setListenerOnComponents() {
            buttonWriteReview.setOnClickListener(this);
            buttonSeeAccomodationOnMap.setOnClickListener(this);
            relativeLayoutAccomodation.setOnClickListener(this);
        }

        private void onClickHelper(View view) {
            switch (view.getId()) {
                case R.id.button_write_review:
                    startWriteReviewActivity();
                    break;
                case R.id.button_see_accomodation_on_map:
                    startAccomodationDetailMapsActivity();
                    break;
                case R.id.relative_layout_main:
                    startDetailActivity();
                    break;
            }
        }

        private void startWriteReviewActivity() {
            context.startActivity(createWriteReviewActivityIntent());
        }

        private void startAccomodationDetailMapsActivity() {
            context.startActivity(createAccomodationDetailMapsIntent());
        }

        private void startDetailActivity() {
            context.startActivity(createStartDetailActivityIntent());
        }

        private Intent createWriteReviewActivityIntent() {
            Intent writeReviewActivityIntent = new Intent(context, WriteReviewActivity.class);
            writeReviewActivityIntent.putExtra(Constants.getId(), getId());
            writeReviewActivityIntent.putExtra(Constants.getAccomodationType(), Constants.getRestaurant());
            writeReviewActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            return writeReviewActivityIntent;
        }

        private Intent createAccomodationDetailMapsIntent() {
            Intent accomodationDetailMapsIntent = new Intent(context, AccomodationDetailMapsActivity.class);
            accomodationDetailMapsIntent.putExtra(Constants.getAccomodation(), getRestaurant());
            accomodationDetailMapsIntent.putExtra(Constants.getAccomodationType(), Constants.getRestaurant());
            accomodationDetailMapsIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            return accomodationDetailMapsIntent;
        }

        private Intent createStartDetailActivityIntent() {
            Intent startDetailActivityIntent = new Intent(context, RestaurantDetailActivity.class);
            startDetailActivityIntent.putExtra(Constants.getId(), getId());
            startDetailActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            return startDetailActivityIntent;
        }

        private String getId() {
            return restaurants.get(this.getAdapterPosition()).getId();
        }

        private Restaurant getRestaurant() {
            return restaurants.get(this.getAdapterPosition());
        }

    }

}
