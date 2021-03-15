package com.quiriletelese.troppadvisorproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.model_helpers.Badge;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RecyclerViewProfileBadgeAdapter extends RecyclerView.Adapter<RecyclerViewProfileBadgeAdapter.ViewHolder> {

    private List<Badge> badges;
    private List<Badge> missingBadges;
    private Context context;

    public RecyclerViewProfileBadgeAdapter(Set<Badge> badges, Set<Badge> missingBadges, Context context) {
        this.badges = new ArrayList<>(badges);
        missingBadges.removeAll(badges);
        this.missingBadges = new ArrayList<>(missingBadges);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_profile_badge_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        handleOnBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return badges.size() + missingBadges.size();
    }

    private void handleOnBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position > badges.size())
            holder.imageViewBadge.setImageResource(R.drawable.missing_badge_icon);
        else {
            holder.textViewBadgeName.setTextColor(context.getResources().getColor(R.color.black));
            holder.textViewBadgeDescription.setTextColor(context.getResources().getColor(R.color.black));
        }
        holder.textViewBadgeName.setText(badges.get(position).getName());
        holder.textViewBadgeDescription.setText(badges.get(position).getDescription());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewBadge;
        private TextView textViewBadgeName, textViewBadgeDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
        }

        private void initializeComponents() {
            imageViewBadge = imageViewBadge.findViewById(R.id.image_view_badge);
            textViewBadgeName = itemView.findViewById(R.id.text_view_badge_name);
            textViewBadgeDescription = itemView.findViewById(R.id.text_view_badge_description);
        }
    }
}
