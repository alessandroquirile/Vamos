package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.views.EditProfileActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class EditProfileController implements View.OnClickListener {

    private EditProfileActivity editProfileActivity;

    public EditProfileController(EditProfileActivity editProfileActivity) {
        this.editProfileActivity = editProfileActivity;
    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.text_view_change_profile_image:
                selectImageFromGallery();
                break;
        }
    }

    public void setEditProfileActivityFields() {
        User user = getUserFromIntent();
        setCircleImageViewUserEditImage(user);
        setEditTextNameText(user);
        setEditTextLastNameText(user);
        setEditTextUsernameText(user);
        setSwitchCompatPrivateAccountValue(user);
    }

    public void setEditProfileActivityFieldsOnClickListener() {
        getTextViewChangeProfileImage().setOnClickListener(this);
    }

    private void selectImageFromGallery() {
        Intent intentSelectImageFromGallery = new Intent();
        intentSelectImageFromGallery.setType("image/*");
        intentSelectImageFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        editProfileActivity.startActivityForResult(Intent.createChooser(intentSelectImageFromGallery,
                getString(R.string.select_image)), Constants.getSelectPicture());
    }

    public void handleOnActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK)
            // compare the resultCode with the getSelectPicture constant
            if (requestCode == Constants.getSelectPicture()) {
                // Get the url of the image from data
                Uri selectedImageUri = Objects.requireNonNull(data).getData();
                if (null != selectedImageUri)
                    // update the preview image in the layout
                    getCircleImageViewUserEdit().setImageURI(selectedImageUri);
            }
    }

    public CircleImageView getCircleImageViewUserEdit() {
        return editProfileActivity.getCircleImageViewUserEdit();
    }

    public void setCircleImageViewUserEditImage(User user) {
        if (userHasImage(user))
            Picasso.with(getContext()).load(getUserImage(user))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.troppadvisor_logo)
                    .error(R.drawable.picasso_error)
                    .into(getCircleImageViewUserEdit());
    }

    public TextView getTextViewChangeProfileImage() {
        return editProfileActivity.getTextViewChangeProfileImage();
    }

    private EditText getEditTextName() {
        return editProfileActivity.getEditTextName();
    }

    public void setEditTextNameText(User user) {
        getEditTextName().setText(getUserName(user));
    }

    private EditText getEditTextLastName() {
        return editProfileActivity.getEditTextLastame();
    }

    public void setEditTextLastNameText(User user) {
        getEditTextLastName().setText(getUserLastName(user));
    }

    private EditText getEditTextUsername() {
        return editProfileActivity.getEditTextUsername();
    }

    public void setEditTextUsernameText(User user) {
        getEditTextUsername().setText(getUsername(user));
    }

    private SwitchCompat getSwitchCompatPrivateAccount() {
        return editProfileActivity.getSwitchCompatPrivateAccount();
    }

    public void setSwitchCompatPrivateAccountValue(User user) {
        getSwitchCompatPrivateAccount().setChecked(getIsPrivateAccount(user));
    }

    private User getUserFromIntent() {
        return (User) getIntent().getSerializableExtra("user");
    }

    private Context getContext() {
        return editProfileActivity.getApplicationContext();
    }

    private Intent getIntent() {
        return editProfileActivity.getIntent();
    }

    private Resources getResources() {
        return getContext().getResources();
    }

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }

    private boolean userHasImage(User user) {
        return !getUserImage(user).isEmpty();
    }

    private String getUserImage(User user) {
        return user.getImage();
    }

    private String getUserName(User user) {
        return user.getName();
    }

    private String getUserLastName(User user) {
        return user.getLastName();
    }

    private String getUsername(User user) {
        return user.getUsername();
    }

    private boolean getIsPrivateAccount(User user) {
        return user.isPrivateAccount();
    }

}
