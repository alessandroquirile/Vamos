package com.quiriletelese.troppadvisorproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAttractionAdapter extends RecyclerView.Adapter<RecyclerViewAttractionAdapter.ViewHolder> {
    private Context context;
    private List<Attraction> attractions;

    public RecyclerViewAttractionAdapter(Context context, List<Attraction> attractions) {
        this.context = context;
        this.attractions = attractions;
    }

    @NonNull
    @Override
    public RecyclerViewAttractionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_recycler_view_element, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAttractionAdapter.ViewHolder holder, int position) {
        setFieldsOnBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return attractions == null ? 0 : attractions.size();
    }

    private void setFieldsOnBindViewHolder(ViewHolder viewHolder, int position) {
        //setHotelImage(viewHolder, position);
        viewHolder.textViewAttractionName.setText(attractions.get(position).getName());
        if (attractions.get(position).getAvarageRating() == 0)
            viewHolder.textViewAttractionRating.setText(R.string.no_review);
        else
            viewHolder.textViewAttractionRating.setText(attractions.get(position).getAvarageRating() + "/5");

    }

    private void setRestamage(ViewHolder viewHolder, int position) {
        Picasso.with(context).load(attractions.get(position).getImages().get(0))
                .fit()
                .centerCrop()
                .placeholder(R.drawable.pizza)
                .into(viewHolder.imageViewAttraction);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private ImageView imageViewAttraction;
        private TextView textViewAttractionName, textViewAttractionRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
        }

        private void initializeComponents() {
            context = itemView.getContext();
            imageViewAttraction = itemView.findViewById(R.id.image_view_accomodation);
            textViewAttractionName = itemView.findViewById(R.id.text_view_accomodation_name);
            textViewAttractionRating = itemView.findViewById(R.id.text_view_accomodation_rating);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "Cliccato " + attractions.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }

    }

}
