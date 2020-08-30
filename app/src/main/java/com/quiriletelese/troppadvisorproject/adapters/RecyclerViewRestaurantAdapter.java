package com.quiriletelese.troppadvisorproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewRestaurantAdapter extends RecyclerView.Adapter<RecyclerViewRestaurantAdapter.ViewHolder> {
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
        //setHotelImage(viewHolder, position);
        viewHolder.textViewRestaurantName.setText(restaurants.get(position).getName());
        if (restaurants.get(position).getAvarageRating() == 0)
            viewHolder.textViewRestaurantRating.setText(R.string.no_review);
        else
            viewHolder.textViewRestaurantRating.setText(restaurants.get(position).getAvarageRating() + "/5");

    }

    private void setHotelImage(ViewHolder viewHolder, int position) {
        Picasso.with(context).load(restaurants.get(position).getImages().get(0))
                .fit()
                .centerCrop()
                .placeholder(R.drawable.pizza)
                .into(viewHolder.imageViewRestaurant);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private ImageView imageViewRestaurant;
        private TextView textViewRestaurantName, textViewRestaurantRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
        }

        private void initializeComponents() {
            context = itemView.getContext();
            imageViewRestaurant = itemView.findViewById(R.id.image_view_accomodation);
            textViewRestaurantName = itemView.findViewById(R.id.text_view_accomodation_name);
            textViewRestaurantRating = itemView.findViewById(R.id.text_view_accomodation_rating);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "Cliccato " + restaurants.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }

    }

}
