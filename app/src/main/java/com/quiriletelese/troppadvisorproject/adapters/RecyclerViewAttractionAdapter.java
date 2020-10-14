package com.quiriletelese.troppadvisorproject.adapters;

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
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.views.AttractionDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class RecyclerViewAttractionAdapter extends RecyclerView.Adapter<RecyclerViewAttractionAdapter.ViewHolder> implements Constants {

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
        return attractions.size();
    }

    private void setFieldsOnBindViewHolder(ViewHolder viewHolder, int position) {
        //setImage(viewHolder, position);
        viewHolder.textViewAttractionName.setText(attractions.get(position).getName());
        viewHolder.textViewAttractionRating.setText(createAvarageRatingString(attractions.get(position)));

    }

    private void setImage(ViewHolder viewHolder, int position) {
        if (hasImage(position)) {
            Picasso.with(context).load(getFirtsImage(position))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.pizza)
                    .into(viewHolder.imageViewAttraction);
        }
    }

    private void startAttractionDetailActivity(String id) {
        Intent intent = new Intent(context, AttractionDetailActivity.class);
        intent.putExtra(ID, id);
        context.startActivity(intent);
    }

    private boolean hasImage(int position) {
        return attractions.get(position).isImagesGraterThanZero();
    }

    private String getFirtsImage(int position) {
        return attractions.get(position).getImages().get(0);
    }

    private String createAvarageRatingString(Attraction attraction) {
        return !hasReviews(attraction) ? getString(R.string.no_reviews) : createAvarageRatingStringHelper(attraction);
    }

    private String createAvarageRatingStringHelper(Attraction attraction) {
        return attraction.getAvarageRating() + "/5 (" + attraction.getTotalReviews() + " " + getString(R.string.reviews) + ")";
    }

    private Resources getResources() {
        return context.getResources();
    }

    private String getString(int string) {
        return getResources().getString(string);
    }

    private boolean hasReviews(Attraction attraction) {
        return !attraction.getAvarageRating().equals(0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context context;
        private LinearLayout linearLayoutHomePageRecyclerView;
        private ImageView imageViewAttraction;
        private TextView textViewAttractionName, textViewAttractionRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
        }

        private void initializeComponents() {
            context = itemView.getContext();
            linearLayoutHomePageRecyclerView = itemView.findViewById(R.id.linear_layout_home_page_recycler_view);
            imageViewAttraction = itemView.findViewById(R.id.image_view_accomodation);
            textViewAttractionName = itemView.findViewById(R.id.text_view_accomodation_name);
            textViewAttractionRating = itemView.findViewById(R.id.text_view_accomodation_rating);
            linearLayoutHomePageRecyclerView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            startAttractionDetailActivity(attractions.get(this.getAdapterPosition()).getId());
        }

    }

}
