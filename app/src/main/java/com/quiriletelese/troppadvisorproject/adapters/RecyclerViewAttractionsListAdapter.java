package com.quiriletelese.troppadvisorproject.adapters;

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
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.views.AccomodationDetailMapsActivity;
import com.quiriletelese.troppadvisorproject.views.AttractionDetailActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class RecyclerViewAttractionsListAdapter extends RecyclerView.Adapter<RecyclerViewAttractionsListAdapter.ViewHolder>
        implements Constants {

    private Context context;
    private List<Attraction> attractions;

    public RecyclerViewAttractionsListAdapter(Context context, List<Attraction> attractions) {
        this.context = context;
        this.attractions = attractions;
    }

    @NonNull
    @Override
    public RecyclerViewAttractionsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_all_accomodations_list_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAttractionsListAdapter.ViewHolder holder, int position) {
        setFieldsOnBIndViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return attractions.size();
    }

    public void addListItems(List<Attraction> attractions) {
        this.attractions.addAll(attractions);
    }

    private void setFieldsOnBIndViewHolder(ViewHolder viewHolder, int position) {
        setImage(viewHolder, position);
        viewHolder.textViewAccomodationName.setText(attractions.get(position).getName());
        viewHolder.textViewAccomodationReview.setText(createAvarageRatingString(attractions.get(position)));
        viewHolder.textViewAccomodationAddress.setText(createAddressString(attractions.get(position)));
    }

    private void setImage(ViewHolder viewHolder, int position) {
        if (hasImage(position)) {
            Picasso.with(context).load(getFirtsImage(position))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.troppadvisor_logo)
                    .error(R.drawable.picasso_error)
                    .into(viewHolder.imageViewAccomodation);
        }
    }

    private String createAddressString(Attraction attraction) {
        String attractionAddress = "";
        attractionAddress = attractionAddress.concat(attraction.getTypeOfAddress() + " ");
        attractionAddress = attractionAddress.concat(attraction.getStreet() + ", ");
        attractionAddress = attractionAddress.concat(attraction.getHouseNumber() + ", ");
        attractionAddress = attractionAddress.concat(attraction.getCity() + ", ");
        attractionAddress = attractionAddress.concat(attraction.getProvince() + ", ");
        attractionAddress = attractionAddress.concat(attraction.getPostalCode());
        return attractionAddress;
    }

    private boolean hasImage(int position) {
        return attractions.get(position).hasImage();
    }

    private String getFirtsImage(int position) {
        return attractions.get(position).getFirstImage();
    }

    private String createAvarageRatingString(Attraction attraction) {
        return !hasReviews(attraction) ? getString(R.string.no_reviews) : createAvarageRatingStringHelper(attraction);
    }

    private String createAvarageRatingStringHelper(Attraction attraction) {
        return attraction.getAvarageRating().intValue() + "/5 (" + attraction.getTotalReviews() + " " + getString(R.string.reviews) + ")";
    }

    private Resources getResources() {
        return context.getResources();
    }

    private String getString(int string) {
        return getResources().getString(string);
    }

    private boolean hasReviews(Attraction attraction) {
        return attraction.hasReviews();
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
            imageViewAccomodation = itemView.findViewById(R.id.image_view_accomodation);
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

        private Intent createWriteReviewActivityIntent() {
            Intent writeReviewActivityIntent = new Intent(context, WriteReviewActivity.class);
            writeReviewActivityIntent.putExtra(ID, getId());
            writeReviewActivityIntent.putExtra(ACCOMODATION_TYPE, HOTEL);
            writeReviewActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            return writeReviewActivityIntent;
        }

        private void startAccomodationDetailMapsActivity() {
            context.startActivity(createAccomodationDetailMapsIntent());
        }

        private Intent createAccomodationDetailMapsIntent() {
            Intent accomodationDetailMapsIntent = new Intent(context, AccomodationDetailMapsActivity.class);
            accomodationDetailMapsIntent.putExtra(ACCOMODATION, getAttraction());
            accomodationDetailMapsIntent.putExtra(ACCOMODATION_TYPE, ATTRACTION);
            accomodationDetailMapsIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            return accomodationDetailMapsIntent;
        }

        private void startDetailActivity() {
            context.startActivity(createStartDetailActivityIntent());
        }

        private Intent createStartDetailActivityIntent() {
            Intent startDetailActivityIntent = new Intent(context, AttractionDetailActivity.class);
            startDetailActivityIntent.putExtra(ID, getId());
            startDetailActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            return startDetailActivityIntent;
        }

        private String getId() {
            return attractions.get(this.getAdapterPosition()).getId();
        }

        private Attraction getAttraction() {
            return attractions.get(this.getAdapterPosition());
        }

    }

}
