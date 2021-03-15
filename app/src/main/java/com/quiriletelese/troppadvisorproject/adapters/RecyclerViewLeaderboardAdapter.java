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
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RecyclerViewLeaderboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<User> users;
    private Context context;

    public RecyclerViewLeaderboardAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_leaderboard_activity_items_layout, parent, false);
            return new ViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_leaderboard_activity_header_layout, parent, false);
            return new HeaderViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        handleRecyclerViewItemsFields(holder, position);
    }

    @Override
    public int getItemCount() {
        return users.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private void handleRecyclerViewItemsFields(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder)
            handleHeaderItemsFields(holder);
        else
            handleBodyItemsFields(holder, position);
    }

    private void handleHeaderItemsFields(@NonNull RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        setHeaderUsersProfileImage(headerViewHolder.circleImageViewLeaderboardHeaderFirstUserPhoto, 0);
        setHeaderUsersProfileImage(headerViewHolder.circleImageViewLeaderboardHeaderSecondUserPhoto, 1);
        setHeaderUsersProfileImage(headerViewHolder.circleImageViewLeaderboardHeaderThirdUserPhoto, 2);
        headerViewHolder.textViewLeaderboardHeaderUserNameFirst.setText(users.get(0).getName());
        headerViewHolder.textViewLeaderboardHeaderUserNameSecond.setText(users.get(1).getName());
        headerViewHolder.textViewLeaderboardHeaderUserNameThird.setText(users.get(2).getName());
        headerViewHolder.textViewLeaderboardHeaderUserLevelFirst.setText(String.valueOf(users.get(0).getLevel()));
        headerViewHolder.textViewLeaderboardHeaderUserLevelSecond.setText(String.valueOf(users.get(1).getLevel()));
        headerViewHolder.textViewLeaderboardHeaderUserLevelThird.setText(String.valueOf(users.get(2).getLevel()));
    }

    private void handleBodyItemsFields(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        setBodyUsersProfileImage(viewHolder, position);
        viewHolder.textViewLeaderboardUserName.setText(users.get(position).getName());
        viewHolder.textViewLeaderboardUserLevel.setText(String.valueOf(users.get(position).getLevel()));
        viewHolder.textViewLeaderboardUserPosition.setText(position + 1);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setHeaderUsersProfileImage(CircleImageView circleImageView, int position) {
        if (hasImage(position)) {
            Picasso.with(context).load(getImage(position))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.troppadvisor_logo)
                    .error(R.drawable.picasso_error)
                    .into(circleImageView);
        } else
            circleImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.user_profile_no_photo));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setBodyUsersProfileImage(ViewHolder viewHolder, int position) {
        if (hasImage(position)) {
            Picasso.with(context).load(getImage(position))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.troppadvisor_logo)
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
            Intent intentSearchedUserProfile = new Intent();
            intentSearchedUserProfile.putExtra(Constants.getEmail(), getUserEmail());
            intentSearchedUserProfile.addFlags(FLAG_ACTIVITY_NEW_TASK);
            return intentSearchedUserProfile;
        }

        private String getUserEmail() {
            return users.get(this.getAdapterPosition()).getEmail();
        }

    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardViewFirst, cardViewSecond, cardViewThird;
        private TextView textViewLeaderboardHeaderUserNameFirst, textViewLeaderboardHeaderUserNameSecond,
                textViewLeaderboardHeaderUserNameThird;
        private CircleImageView circleImageViewLeaderboardHeaderFirstUserPhoto,
                circleImageViewLeaderboardHeaderSecondUserPhoto, circleImageViewLeaderboardHeaderThirdUserPhoto;
        private TextView textViewLeaderboardHeaderUserLevelFirst, textViewLeaderboardHeaderUserLevelSecond,
                textViewLeaderboardHeaderUserLevelThird;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
            setListenerOnComponents();
        }

        @Override
        public void onClick(View view) {
            startSearchedUserProfileActivity();
        }

        private void initializeComponents() {
            cardViewFirst = itemView.findViewById(R.id.card_view_first);
            cardViewSecond = itemView.findViewById(R.id.card_view_second);
            cardViewThird = itemView.findViewById(R.id.card_view_third);
            textViewLeaderboardHeaderUserNameFirst = itemView.findViewById(R.id.text_view_leaderboard_header_user_name_first);
            textViewLeaderboardHeaderUserNameSecond = itemView.findViewById(R.id.text_view_leaderboard_header_user_name_second);
            textViewLeaderboardHeaderUserNameThird = itemView.findViewById(R.id.text_view_leaderboard_header_user_name_third);
            circleImageViewLeaderboardHeaderFirstUserPhoto = itemView.findViewById(R.id.circle_image_view_leaderboard_header_first_user_photo);
            circleImageViewLeaderboardHeaderSecondUserPhoto = itemView.findViewById(R.id.circle_image_view_leaderboard_header_second_user_photo);
            circleImageViewLeaderboardHeaderThirdUserPhoto = itemView.findViewById(R.id.circle_image_view_leaderboard_header_third_user_photo);
            textViewLeaderboardHeaderUserLevelFirst = itemView.findViewById(R.id.text_view_leaderboard_header_user_level_first);
            textViewLeaderboardHeaderUserLevelSecond = itemView.findViewById(R.id.text_view_leaderboard_header_user_level_second);
            textViewLeaderboardHeaderUserLevelThird = itemView.findViewById(R.id.text_view_leaderboard_header_user_level_third);
        }

        private void setListenerOnComponents() {
            cardViewFirst.setOnClickListener(this);
            cardViewSecond.setOnClickListener(this);
            cardViewThird.setOnClickListener(this);
        }

        private void startSearchedUserProfileActivity() {
            context.startActivity(createStartSearchedUserProfileActivityIntent());
        }

        private Intent createStartSearchedUserProfileActivityIntent() {
            Intent intentSearchedUserProfile = new Intent();
            intentSearchedUserProfile.putExtra(Constants.getEmail(), getUserEmail());
            intentSearchedUserProfile.addFlags(FLAG_ACTIVITY_NEW_TASK);
            return intentSearchedUserProfile;
        }

        private String getUserEmail() {
            return users.get(this.getAdapterPosition()).getEmail();
        }

    }

}
