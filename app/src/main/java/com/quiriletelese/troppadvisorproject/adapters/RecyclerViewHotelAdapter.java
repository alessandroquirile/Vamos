package com.quiriletelese.troppadvisorproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.views.HotelDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewHotelAdapter extends RecyclerView.Adapter<RecyclerViewHotelAdapter.ViewHolder> implements Constants {
    private Context context;
    private List<Hotel> hotels;
    private int position;

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
        this.position = position;
        setFieldsOnBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return hotels == null ? 0 : hotels.size();
    }

    private void setFieldsOnBindViewHolder(ViewHolder viewHolder, int position) {
        setHotelImage(viewHolder, position);
        viewHolder.textViewHotelName.setText(hotels.get(position).getName());
        if (hotels.get(position).getAvarageRating() == 0)
            viewHolder.textViewHotelRating.setText(R.string.no_review);
        else
            viewHolder.textViewHotelRating.setText(hotels.get(position).getAvarageRating() + "/5");

    }

    private void setHotelImage(ViewHolder viewHolder, int position) {
        if (hotelHasImage(position)) {
            Picasso.with(context).load(hotels.get(position).getImages().get(0))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.troppadvisor_logo)
                    .error(R.drawable.pizza)
                    .into(viewHolder.imageViewHotel);
        }
    }

    private boolean hotelHasImage(int position) {
        return hotels.get(position).getImages().size() > 0;
    }

    private void startHotelDetailActivity(Hotel hotel){
        Intent intent = new Intent(context, HotelDetailActivity.class);
        intent.putExtra(HOTEL, hotel);
        context.startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private LinearLayout linearLayoutHomePageRecyclerView;
        private ImageView imageViewHotel;
        private TextView textViewHotelName, textViewHotelRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
        }

        private void initializeComponents() {
            context = itemView.getContext();
            linearLayoutHomePageRecyclerView = itemView.findViewById(R.id.linear_layout_home_page_recycler_view);
            imageViewHotel = itemView.findViewById(R.id.image_view_accomodation);
            textViewHotelName = itemView.findViewById(R.id.text_view_accomodation_name);
            textViewHotelRating = itemView.findViewById(R.id.text_view_accomodation_rating);
            linearLayoutHomePageRecyclerView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            startHotelDetailActivity(hotels.get(this.getAdapterPosition()));
        }

    }

}
