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
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAttractionsListAdapter extends RecyclerView.Adapter<RecyclerViewAttractionsListAdapter.ViewHolder> {

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

    public void addListItems(List<Attraction> attractions){
        this.attractions.addAll(attractions);
    }

    private void setFieldsOnBIndViewHolder(ViewHolder viewHolder, int position){
        setImage(viewHolder, position);
        viewHolder.textViewAccomodationName.setText(attractions.get(position).getName());
        viewHolder.textViewAccomodationReview.setText(createReviewString(attractions.get(position).getAvarageRating()));
        viewHolder.textViewAccomodationAddress.setText(createAddressString(attractions.get(position).getAddress()));
    }

    private void setImage(ViewHolder viewHolder, int position) {
        if (hasImage(position)) {
            Picasso.with(context).load(attractions.get(position).getImages().get(0))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.troppadvisor_logo)
                    .error(R.drawable.pizza)
                    .into(viewHolder.imageViewAccomodation);
        }
    }

    private boolean hasImage(int position) {
        return attractions.get(position).getImages().size() > 0;
    }

    private String createAddressString(Address address) {
        String attractionAddress = "";
        attractionAddress = attractionAddress.concat(address.getType() + " ");
        attractionAddress = attractionAddress.concat(address.getStreet() + ", ");
        attractionAddress = attractionAddress.concat(address.getHouseNumber() + ", ");
        attractionAddress = attractionAddress.concat(address.getCity() + ", ");
        attractionAddress = attractionAddress.concat(address.getProvince() + ", ");
        attractionAddress = attractionAddress.concat(address.getPostalCode());
        return attractionAddress;
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
