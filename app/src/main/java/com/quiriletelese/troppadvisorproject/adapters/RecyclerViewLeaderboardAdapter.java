package com.quiriletelese.troppadvisorproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewLeaderboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private List<User> users;
    private Long userPosition = 4L;

    public RecyclerViewLeaderboardAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_leaderboard_activity_items_layout, parent, false);
            return new ViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_leaderboard_activity_header_layout, parent, false);
            return new HeaderViewHolder(itemView);
        } else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        handleRecyclerViewItemsFields(holder, position);
    }

    @Override
    public int getItemCount() {
        return /*users.size() + 1*/20;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private void handleRecyclerViewItemsFields(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder)
            handleHeaderItemsFields(holder, position);
        else
            handleBodyItemsFields(holder, position);
    }

    private void handleHeaderItemsFields(@NonNull RecyclerView.ViewHolder holder, int position) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.textViewLeaderboardHeaderUserNameFirst.setText("Primo");
        headerViewHolder.textViewLeaderboardHeaderUserNameSecond.setText("Secondo");
        headerViewHolder.textViewLeaderboardHeaderUserNameThird.setText("Terzo");
        headerViewHolder.circleImageViewLeaderboardHeaderFirstUserPhoto.setImageResource(R.drawable.profile_user);
        headerViewHolder.circleImageViewLeaderboardHeaderSecondUserPhoto.setImageResource(R.drawable.profile_user);
        headerViewHolder.circleImageViewLeaderboardHeaderThirdUserPhoto.setImageResource(R.drawable.profile_user);
        headerViewHolder.textViewLeaderboardHeaderUserLevelFirst.setText("1");
        headerViewHolder.textViewLeaderboardHeaderUserLevelSecond.setText("2");
        headerViewHolder.textViewLeaderboardHeaderUserLevelThird.setText("3");
    }

    private void handleBodyItemsFields(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.circleImageViewLeaderboardUserPhoto.setImageResource(R.drawable.profile_user);
        viewHolder.textViewLeaderboardUserName.setText("Nome utente");
        viewHolder.textViewLeaderboardUserLevel.setText("1000");
        viewHolder.textViewLeaderboardUserPosition.setText("1");
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

    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewLeaderboardHeaderUserNameFirst, textViewLeaderboardHeaderUserNameSecond,
                textViewLeaderboardHeaderUserNameThird;
        private CircleImageView circleImageViewLeaderboardHeaderFirstUserPhoto,
                circleImageViewLeaderboardHeaderSecondUserPhoto, circleImageViewLeaderboardHeaderThirdUserPhoto;
        private TextView textViewLeaderboardHeaderUserLevelFirst, textViewLeaderboardHeaderUserLevelSecond,
                textViewLeaderboardHeaderUserLevelThird;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
        }

        @Override
        public void onClick(View view) {

        }

        private void initializeComponents() {
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

    }

}
