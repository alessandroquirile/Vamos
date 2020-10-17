package com.quiriletelese.troppadvisorproject.adapters;

import android.annotation.SuppressLint;
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
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.views.HotelDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class RecyclerViewHotelAdapter extends RecyclerView.Adapter<RecyclerViewHotelAdapter.ViewHolder>
        implements Constants {

    private final Context context;
    private final List<Hotel> hotels;

    public RecyclerViewHotelAdapter(Context context, List<Hotel> hotels) {
        this.context = context;
        this.hotels = hotels;
    }

    @NonNull
    @Override
    public RecyclerViewHotelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_recycler_view_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHotelAdapter.ViewHolder holder, int position) {
        setFieldsOnBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    private void setFieldsOnBindViewHolder(ViewHolder viewHolder, int position) {
        setImage(viewHolder, position);
        viewHolder.textViewHotelName.setText(hotels.get(position).getName());
        viewHolder.textViewHotelRating.setText(createAvarageRatingString(hotels.get(position)));
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

    private void startDetailActivity(String id) {
        Intent intentDetailActivity = new Intent(context, HotelDetailActivity.class);
        intentDetailActivity.putExtra(ID, id);
        intentDetailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentDetailActivity);
    }

    private boolean hasImage(int position) {
        return hotels.get(position).hasImage();
    }

    private String getFirtsImage(int position) {
        return hotels.get(position).getFirstImage();
    }

    private String createAvarageRatingString(Hotel hotel) {
        return !hasReviews(hotel) ? getString(R.string.no_reviews) : createAvarageRatingStringHelper(hotel);
    }

    private String createAvarageRatingStringHelper(Hotel hotel) {
        return hotel.getAvarageRating().intValue() + "/5 (" + hotel.getTotalReviews() + " " + getString(R.string.reviews) + ")";
    }

    private boolean hasReviews(Hotel hotel) {
        return hotel.hasReviews();
    }

    private Resources getResources() {
        return context.getResources();
    }

    private String getString(int string) {
        return getResources().getString(string);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout linearLayoutHomePageRecyclerView;
        private ImageView imageViewAccomodation;
        private TextView textViewHotelName, textViewHotelRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
            setListenerOnComponents();
        }

        @Override
        public void onClick(View view) {
            startDetailActivity(getHotelId());
        }

        private void initializeComponents() {
            linearLayoutHomePageRecyclerView = itemView.findViewById(R.id.linear_layout_home_page_recycler_view);
            imageViewAccomodation = itemView.findViewById(R.id.image_view_accomodation_home);
            textViewHotelName = itemView.findViewById(R.id.text_view_accomodation_name);
            textViewHotelRating = itemView.findViewById(R.id.text_view_accomodation_rating);
        }

        private void setListenerOnComponents() {
            linearLayoutHomePageRecyclerView.setOnClickListener(this);
        }

        private String getHotelId() {
            return hotels.get(this.getAdapterPosition()).getId();
        }

    }

}
