package com.quiriletelese.troppadvisorproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.SearchedUserProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RecyclerViewLeaderboardAdapter extends RecyclerView.Adapter<RecyclerViewLeaderboardAdapter.ViewHolder> {

    private List<User> users;
    private Context context;

    public RecyclerViewLeaderboardAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewLeaderboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_leaderboard_activity_items_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewLeaderboardAdapter.ViewHolder holder, int position) {
        handleRecyclerViewItemsFields(holder, position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private void handleRecyclerViewItemsFields(@NonNull RecyclerViewLeaderboardAdapter.ViewHolder holder, int position) {
        handleBodyItemsFields(holder, position);
    }

    private void handleBodyItemsFields(@NonNull RecyclerViewLeaderboardAdapter.ViewHolder viewHolder, int position) {
        setBodyUsersProfileImage(viewHolder, position);
        viewHolder.textViewLeaderboardUserName.setText(users.get(position).getName().concat(" - ").concat(users.get(position).getUsername()));
        viewHolder.textViewLeaderboardUserLevel.setText(String.valueOf(users.get(position).getLevel()));
        setTextViewLeaderboardUserPositionBackground(viewHolder.textViewLeaderboardUserPosition, position);
        viewHolder.textViewLeaderboardUserPosition.setText(String.valueOf(position + 1));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setBodyUsersProfileImage(ViewHolder viewHolder, int position) {
        if (hasImage(position)) {
            Picasso.with(context).load(getImage(position))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.user_profile_no_photo)
                    .error(R.drawable.picasso_error)
                    .into(viewHolder.circleImageViewLeaderboardUserPhoto);
        } else
            viewHolder.circleImageViewLeaderboardUserPhoto.setImageDrawable(
                    context.getResources().getDrawable(R.drawable.user_profile_no_photo));
    }

    private String getImage(int position) {
        return users.get(position).getImage();
    }

    private boolean hasImage(int position) {
        return users.get(position).hasImage();
    }

    private void setTextViewLeaderboardUserPositionBackground(TextView textView, int position) {
        switch (position) {
            case 0:
                textView.setBackground(context.getResources().getDrawable(R.drawable.button_gold_round_corner));
                break;
            case 1:
                textView.setBackground(context.getResources().getDrawable(R.drawable.button_silver_round_corner));
                break;
            case 2:
                textView.setBackground(context.getResources().getDrawable(R.drawable.button_bronze_round_corner));
                break;
            default:
                textView.setBackground(context.getResources().getDrawable(R.drawable.button_green_round_corner));
                break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayoutCompat linearLayoutCompatLeaderboardItem;
        private CircleImageView circleImageViewLeaderboardUserPhoto;
        private TextView textViewLeaderboardUserName;
        private TextView textViewLeaderboardUserLevel;
        private TextView textViewLeaderboardUserPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
            setListenerOnComponents();
        }

        @Override
        public void onClick(View view) {
            startSearchedUserProfileActivity();
        }

        private void initializeComponents() {
            linearLayoutCompatLeaderboardItem = itemView.findViewById(R.id.linear_layout_compat_leaderboard_item);
            circleImageViewLeaderboardUserPhoto = itemView.findViewById(R.id.circle_image_view_leaderboard_user_photo);
            textViewLeaderboardUserName = itemView.findViewById(R.id.text_view_leaderboard_user_name);
            textViewLeaderboardUserLevel = itemView.findViewById(R.id.text_view_leaderboard_user_level);
            textViewLeaderboardUserPosition = itemView.findViewById(R.id.text_view_leaderboard_user_position);
        }

        private void setListenerOnComponents() {
            linearLayoutCompatLeaderboardItem.setOnClickListener(this);
        }

        private void startSearchedUserProfileActivity() {
            context.startActivity(createStartSearchedUserProfileActivityIntent());
        }

        private Intent createStartSearchedUserProfileActivityIntent() {
            Intent intentSearchedUserProfile = new Intent(context, SearchedUserProfileActivity.class);
            intentSearchedUserProfile.putExtra(Constants.getEmail(), getUserEmail());
            intentSearchedUserProfile.addFlags(FLAG_ACTIVITY_NEW_TASK);
            return intentSearchedUserProfile;
        }

        private String getUserEmail() {
            return users.get(this.getAdapterPosition()).getEmail();
        }

    }

}
