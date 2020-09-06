package com.quiriletelese.troppadvisorproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.models.Hotel;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_hotels_list_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHotelsListAdapter.ViewHolder holder, int position) {
        setFieldsOnBIndViewHolder(holder, position);
    }

    public void addListItems(List<Hotel> hotels){
        this.hotels.addAll(hotels);
    }

    private void setFieldsOnBIndViewHolder(ViewHolder viewHolder, int position){
        viewHolder.textViewHotelName.setText(hotels.get(position).getName());
        viewHolder.textViewHotelReview.setText(createHotelReviewString(hotels.get(position).getAvarageRating()));
        viewHolder.textViewHotelAddress.setText(createAddressString(hotels.get(position).getAddress()));
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

    private String createHotelReviewString(Integer review){
        String rating = "";
        rating = rating.concat(review + "/5");
        return rating;
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayoutHotel;
        private TextView textViewHotelName, textViewHotelReview, textViewHotelAddress;
        private Button buttonWriteHotelReview, buttonSeeHotelOnMap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
        }

        private void initializeComponents() {
            relativeLayoutHotel = itemView.findViewById(R.id.relative_layout_hotel);
            textViewHotelName = itemView.findViewById(R.id.text_view_hotel_name);
            textViewHotelReview = itemView.findViewById(R.id.text_view_hotel_review);
            textViewHotelAddress = itemView.findViewById(R.id.text_view_hotel_address);
            buttonWriteHotelReview= itemView.findViewById(R.id.button_write_hotel_review);
            buttonSeeHotelOnMap = itemView.findViewById(R.id.button_see_hotel_on_map);
        }

    }

}
