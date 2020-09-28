package com.quiriletelese.troppadvisorproject.adapters;

import android.content.Context;
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
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewRestaurantsListAdapter extends RecyclerView.Adapter<RecyclerViewRestaurantsListAdapter.ViewHolder> {

    private Context context;
    private List<Restaurant> restaurants;

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

    public void addListItems(List<Restaurant> restaurants){
        this.restaurants.addAll(restaurants);
    }

    public void setListItems(List<Restaurant> restaurants){
        this.restaurants.clear();
        this.restaurants.addAll(restaurants);
    }

    private void setFieldsOnBIndViewHolder(ViewHolder viewHolder, int position){
        setImage(viewHolder, position);
        viewHolder.textViewAccomodationName.setText(restaurants.get(position).getName());
        viewHolder.textViewAccomodationReview.setText(createReviewString(restaurants.get(position).getAvarageRating()));
        viewHolder.textViewAccomodationAddress.setText(createAddressString(restaurants.get(position).getAddress()));
    }

    private void setImage(ViewHolder viewHolder, int position) {
        if (hasImage(position)) {
            Picasso.with(context).load(restaurants.get(position).getImages().get(0))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.troppadvisor_logo)
                    .error(R.drawable.pizza)
                    .into(viewHolder.imageViewAccomodation);
        }
    }

    private boolean hasImage(int position) {
        return restaurants.get(position).getImages().size() > 0;
    }

    private String createAddressString(Address address) {
        String restaurantAddress = "";
        restaurantAddress = restaurantAddress.concat(address.getType() + " ");
        restaurantAddress = restaurantAddress.concat(address.getStreet() + ", ");
        restaurantAddress = restaurantAddress.concat(address.getHouseNumber() + ", ");
        restaurantAddress = restaurantAddress.concat(address.getCity() + ", ");
        restaurantAddress = restaurantAddress.concat(address.getProvince() + ", ");
        restaurantAddress = restaurantAddress.concat(address.getPostalCode());
        return restaurantAddress;
    }

    private String createReviewString(Integer review){
        if (review.equals(0))
            return context.getResources().getString(R.string.no_review);
        else {
            String rating = "";
            rating = rating.concat(review + "/5");
            return rating;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayoutAccomodation;
        private ImageView imageViewAccomodation;
        private TextView textViewAccomodationName, textViewAccomodationReview, textViewAccomodationAddress;
        private Button buttonWriteAccomodationReview, buttonSeeAccomodationOnMap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
        }

        private void initializeComponents() {
            relativeLayoutAccomodation = itemView.findViewById(R.id.relative_layout_main);
            imageViewAccomodation = itemView.findViewById(R.id.image_view_accomodation);
            textViewAccomodationName = itemView.findViewById(R.id.text_view_accomodation_name);
            textViewAccomodationReview = itemView.findViewById(R.id.text_view_accomodation_review);
            textViewAccomodationAddress = itemView.findViewById(R.id.text_view_accomodation_address);
            buttonWriteAccomodationReview = itemView.findViewById(R.id.button_write_accomodation_review);
            buttonSeeAccomodationOnMap = itemView.findViewById(R.id.button_see_accomodation_on_map);
        }

    }

}
