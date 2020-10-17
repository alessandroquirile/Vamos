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
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.views.AttractionDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class RecyclerViewAttractionAdapter extends RecyclerView.Adapter<RecyclerViewAttractionAdapter.ViewHolder> implements Constants {

    private final Context context;
    private final List<Attraction> attractions;

    public RecyclerViewAttractionAdapter(Context context, List<Attraction> attractions) {
        this.context = context;
        this.attractions = attractions;
    }

    @NonNull
    @Override
    public RecyclerViewAttractionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_recycler_view_element, parent, false);
        return new ViewHolder(view);
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
        setImage(viewHolder, position);
        viewHolder.textViewAttractionName.setText(attractions.get(position).getName());
        viewHolder.textViewAttractionRating.setText(createAvarageRatingString(attractions.get(position)));
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
        Intent intentDetailActivity = new Intent(context, AttractionDetailActivity.class);
        intentDetailActivity.putExtra(ID, id);
        intentDetailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentDetailActivity);
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

        private ImageView imageViewAccomodation;
        private TextView textViewAttractionName, textViewAttractionRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
        }

        @Override
        public void onClick(View view) {
            startDetailActivity(attractions.get(this.getAdapterPosition()).getId());
        }

        private void initializeComponents() {
            LinearLayout linearLayoutHomePageRecyclerView = itemView.findViewById(R.id.linear_layout_home_page_recycler_view);
            imageViewAccomodation = itemView.findViewById(R.id.image_view_accomodation_home);
            textViewAttractionName = itemView.findViewById(R.id.text_view_accomodation_name);
            textViewAttractionRating = itemView.findViewById(R.id.text_view_accomodation_rating);
            linearLayoutHomePageRecyclerView.setOnClickListener(this);
        }

    }

}
