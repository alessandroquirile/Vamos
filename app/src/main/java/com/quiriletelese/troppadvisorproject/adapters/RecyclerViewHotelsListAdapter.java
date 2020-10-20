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
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.util_interfaces.Constants;
import com.quiriletelese.troppadvisorproject.views.AccomodationDetailMapsActivity;
import com.quiriletelese.troppadvisorproject.views.HotelDetailActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class RecyclerViewHotelsListAdapter extends RecyclerView.Adapter<RecyclerViewHotelsListAdapter.ViewHolder>
        implements Constants {

    private final Context context;
    private final List<Hotel> hotels;

    public RecyclerViewHotelsListAdapter(Context context, List<Hotel> hotels) {
        this.context = context;
        this.hotels = hotels;
    }

    @NonNull
    @Override
    public RecyclerViewHotelsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_all_accomodations_list_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHotelsListAdapter.ViewHolder holder, int position) {
        setFieldsOnBIndViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    public void addListItems(List<Hotel> hotels) {
        this.hotels.addAll(hotels);
    }

    private void setFieldsOnBIndViewHolder(ViewHolder viewHolder, int position) {
        setImage(viewHolder, position);
        viewHolder.textViewAccomodationName.setText(hotels.get(position).getName());
        viewHolder.textViewAccomodationReview.setText(createAvarageRatingString(hotels.get(position)));
        viewHolder.textViewAccomodationAddress.setText(createAddressString(hotels.get(position)));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImage(RecyclerViewHotelsListAdapter.ViewHolder viewHolder, int position) {
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

    private String createAddressString(Hotel hotel) {
        String hotelAddress = "";
        hotelAddress = hotelAddress.concat(hotel.getTypeOfAddress() + " ");
        hotelAddress = hotelAddress.concat(hotel.getStreet() + ", ");
        hotelAddress = hotelAddress.concat(hotel.getHouseNumber() + ", ");
        hotelAddress = hotelAddress.concat(hotel.getCity() + ", ");
        if (!hotel.getProvince().equals(hotel.getCity()))
            hotelAddress = hotelAddress.concat(hotel.getProvince() + ", ");
        hotelAddress = hotelAddress.concat(hotel.getPostalCode());
        return hotelAddress;
    }

    private String createAvarageRatingString(Hotel hotel) {
        return !hasReviews(hotel) ? getString(R.string.no_reviews) : createAvarageRatingStringHelper(hotel);
    }

    @NotNull
    private String createAvarageRatingStringHelper(@NotNull Hotel hotel) {
        if (hotel.getTotalReviews().intValue() == 1)
            return hotel.getAvarageRating().intValue() + "/5 (" + hotel.getTotalReviews() + " " + getString(R.string.review) + ")";
        else
            return hotel.getAvarageRating().intValue() + "/5 (" + hotel.getTotalReviews() + " " + getString(R.string.reviews) + ")";
    }

    private boolean hasImage(int position) {
        return hotels.get(position).hasImage();
    }

    private String getFirtsImage(int position) {
        return hotels.get(position).getFirstImage();
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
            writeReviewActivityIntent.putExtra(ID, getId());
            writeReviewActivityIntent.putExtra(ACCOMODATION_TYPE, HOTEL);
            writeReviewActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            return writeReviewActivityIntent;
        }

        private Intent createAccomodationDetailMapsIntent() {
            Intent accomodationDetailMapsIntent = new Intent(context, AccomodationDetailMapsActivity.class);
            accomodationDetailMapsIntent.putExtra(ACCOMODATION, getHotel());
            accomodationDetailMapsIntent.putExtra(ACCOMODATION_TYPE, HOTEL);
            accomodationDetailMapsIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            return accomodationDetailMapsIntent;
        }

        private Intent createStartDetailActivityIntent() {
            Intent startDetailActivityIntent = new Intent(context, HotelDetailActivity.class);
            startDetailActivityIntent.putExtra(ID, getId());
            startDetailActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            return startDetailActivityIntent;
        }

        private String getId() {
            return hotels.get(this.getAdapterPosition()).getId();
        }

        private Hotel getHotel() {
            return hotels.get(this.getAdapterPosition());
        }

    }

}
