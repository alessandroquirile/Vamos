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
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.views.SearchedUserProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewSearchUsersAdapter extends RecyclerView.Adapter<RecyclerViewSearchUsersAdapter.ViewHolder> {

    private List<User> users;
    private Context context;

    public RecyclerViewSearchUsersAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_search_users_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        handleOnBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private void handleOnBindViewHolder(ViewHolder viewHolder, int position) {
        setImage(viewHolder, position);
        setTextiewFieldsText(viewHolder, position);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImage(RecyclerViewSearchUsersAdapter.ViewHolder viewHolder, int position) {
        if (hasImage(position)) {
            Picasso.with(context).load(getImage(position))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.user_profile_no_photo)
                    .error(R.drawable.picasso_error)
                    .into(viewHolder.circleImageViewUserImage);
        } else
            viewHolder.circleImageViewUserImage.setImageDrawable(context.getResources().getDrawable(R.drawable.user_profile_no_photo));
    }

    private void setTextiewFieldsText(ViewHolder viewHolder, int position) {
        viewHolder.textViewUserName.setText(createUserNameAndLastname(viewHolder, position));
        viewHolder.textViewUsername.setText(users.get(position).getUsername());
        viewHolder.textViewUserLevel.setText(String.valueOf(users.get(position).getLevel()));
    }

    private String createUserNameAndLastname(ViewHolder viewHolder, int position) {
        return users.get(position).getName().concat(" ").concat(users.get(position).getLastName());
    }

    private boolean hasImage(int position) {
        return users.get(position).hasImage();
    }

    private String getImage(int position) {
        return users.get(position).getImage();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayoutCompat linearLayoutCompatSearchUserItem;
        private CircleImageView circleImageViewUserImage;
        private TextView textViewUserName, textViewUsername, textViewUserLevel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
            setListenerOnCompoents();
        }

        @Override
        public void onClick(View view) {
            onClickHelper(view);
        }

        private void initializeComponents() {
            linearLayoutCompatSearchUserItem = itemView.findViewById(R.id.linear_layout_compat_search_users_item);
            circleImageViewUserImage = itemView.findViewById(R.id.circle_image_view_search_users);
            textViewUserName = itemView.findViewById(R.id.text_view_search_users_name);
            textViewUsername = itemView.findViewById(R.id.text_view_search_users_username);
            textViewUserLevel = itemView.findViewById(R.id.text_view_search_users_level);
        }

        private void onClickHelper(View view) {
            int id = view.getId();
            if (id == R.id.linear_layout_compat_search_users_item)
                startSearchedUserProfileActivity(getEmail());
        }

        private void setListenerOnCompoents() {
            linearLayoutCompatSearchUserItem.setOnClickListener(this);
        }

        private void startSearchedUserProfileActivity(String email) {
            Intent intentSearchedUserProfileActivity = new Intent(context, SearchedUserProfileActivity.class);
            intentSearchedUserProfileActivity.putExtra(Constants.getEmail(), email);
            intentSearchedUserProfileActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentSearchedUserProfileActivity);
        }

        private String getEmail() {
            return users.get(this.getAdapterPosition()).getEmail();
        }
    }
}
