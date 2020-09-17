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
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewHotelsListAdapter extends RecyclerView.Adapter<RecyclerViewHotelsListAdapter.ViewHolder> {

    private Context context;
    private List<Hotel> hotels;

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

    public void addListItems(List<Hotel> hotels){
        this.hotels.addAll(hotels);
    }

    private void setFieldsOnBIndViewHolder(ViewHolder viewHolder, int position){
        //setImage(viewHolder, position);
        viewHolder.textViewAccomodationName.setText(hotels.get(position).getName());
        viewHolder.textViewAccomodationReview.setText(createReviewString(hotels.get(position).getAvarageRating()));
        viewHolder.textViewAccomodationAddress.setText(createAddressString(hotels.get(position).getAddress()));
    }

    private void setImage(RecyclerViewHotelsListAdapter.ViewHolder viewHolder, int position) {
        if (hasImage(position)) {
            Picasso.with(context).load(hotels.get(position).getImages().get(0))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.troppadvisor_logo)
                    .error(R.drawable.pizza)
                    .into(viewHolder.imageViewAccomodation);
        }
    }

    private boolean hasImage(int position) {
        return hotels.get(position).getImages().size() > 0;
    }

    private String createAddressString(Address address) {
        String hotelAddress = "";
        hotelAddress = hotelAddress.concat(address.getType() + " ");
        hotelAddress = hotelAddress.concat(address.getStreet() + ", ");
        hotelAddress = hotelAddress.concat(address.getHouseNumber() + ", ");
        hotelAddress = hotelAddress.concat(address.getCity() + ", ");
        hotelAddress = hotelAddress.concat(address.getProvince() + ", ");
        hotelAddress = hotelAddress.concat(address.getPostalCode());
        return hotelAddress;
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
