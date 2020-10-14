package com.quiriletelese.troppadvisorproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.views.RestaurantDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewRestaurantAdapter extends RecyclerView.Adapter<RecyclerViewRestaurantAdapter.ViewHolder> implements Constants {
    private Context context;
    private List<Restaurant> restaurants;

    public RecyclerViewRestaurantAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public RecyclerViewRestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_recycler_view_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewRestaurantAdapter.ViewHolder holder, int position) {
        setFieldsOnBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return restaurants == null ? 0 : restaurants.size();
    }

    private void setFieldsOnBindViewHolder(ViewHolder viewHolder, int position) {
        //setRestaurantImage(viewHolder, position);
        viewHolder.textViewRestaurantName.setText(restaurants.get(position).getName());
            viewHolder.textViewRestaurantRating.setText(createAvarageRatingString(restaurants.get(position)));

    }

    private void setRestaurantImage(ViewHolder viewHolder, int position) {
        Picasso.with(context).load(restaurants.get(position).getImages().get(0))
                .fit()
                .centerCrop()
                .placeholder(R.drawable.pizza)
                .into(viewHolder.imageViewRestaurant);
    }

    private void startRestaurantDetailActivity(String id){
        Intent intent = new Intent(context, RestaurantDetailActivity.class);
        intent.putExtra(ID, id);
        context.startActivity(intent);
    }

    private String createAvarageRatingString(Restaurant restaurant) {
        return !hasReviews(restaurant) ? getString(R.string.no_reviews) : createAvarageRatingStringHelper(restaurant);
    }

    private String createAvarageRatingStringHelper(Restaurant restaurant){
        return restaurant.getAvarageRating() + "/5 (" + restaurant.getTotalReviews() + " " + getString(R.string.reviews) + ")";
    }

    private boolean hasReviews(Restaurant restaurant){
        return restaurant.hasReviews();
    }

    private Resources getResources() {
        return context.getResources();
    }

    private String getString(int string) {
        return getResources().getString(string);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private LinearLayout linearLayoutHomePageRecyclerView;
        private ImageView imageViewRestaurant;
        private TextView textViewRestaurantName, textViewRestaurantRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
        }

        private void initializeComponents() {
            context = itemView.getContext();
            linearLayoutHomePageRecyclerView = itemView.findViewById(R.id.linear_layout_home_page_recycler_view);
            imageViewRestaurant = itemView.findViewById(R.id.image_view_accomodation);
            textViewRestaurantName = itemView.findViewById(R.id.text_view_accomodation_name);
            textViewRestaurantRating = itemView.findViewById(R.id.text_view_accomodation_rating);
            linearLayoutHomePageRecyclerView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            startRestaurantDetailActivity(restaurants.get(this.getAdapterPosition()).getId());
        }

    }

}
