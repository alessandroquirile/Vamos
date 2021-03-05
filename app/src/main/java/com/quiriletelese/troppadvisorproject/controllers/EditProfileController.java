package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.EditProfileActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class EditProfileController implements View.OnClickListener {

    private final EditProfileActivity editProfileActivity;
    private File newProfileImage = null;

    public EditProfileController(EditProfileActivity editProfileActivity) {
        this.editProfileActivity = editProfileActivity;
    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.floating_action_button_change_profile_image:
                selectImageFromGallery();
                break;
        }
    }

    public void setEditProfileActivityFields() {
        /*User user = getUserFromIntent();
        setCircleImageViewUserEditImage(user);
        setEditTextNameText(user);
        setEditTextLastNameText(user);
        setEditTextUsernameText(user);
        setSwitchCompatPrivateAccountValue(user);*/
    }

    public void setEditProfileActivityFieldsOnClickListener() {
        getFloatingActionButtonChangeProfileImage().setOnClickListener(this);
    }

    private void selectImageFromGallery() {
        Intent intentSelectImageFromGallery = new Intent();
        intentSelectImageFromGallery.setType("image/*");
        intentSelectImageFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        editProfileActivity.startActivityForResult(Intent.createChooser(intentSelectImageFromGallery,
                getString(R.string.select_image)), Constants.getSelectPictureCode());
    }

    public void handleOnActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK)
            // compare the resultCode with the getSelectPicture constant
            if (requestCode == Constants.getSelectPictureCode()) {
                // Get the url of the image from data
                Uri selectedImageUri = Objects.requireNonNull(data).getData();
                newProfileImage = createSelectedProfilePictureFile(selectedImageUri);
                if (null != selectedImageUri)
                    // update the preview image in the layout
                    getCircleImageViewUserEdit().setImageURI(selectedImageUri);
            }
    }

    private File createSelectedProfilePictureFile(Uri selectedImageUri) {
        deletePreviuosFile();
        File profilePicture = null;
        try {
            try {
                profilePicture = createPictureFile();
            } catch (IOException ex) {
                Log.d(TAG, "Error occurred while creating the file");
            }
            InputStream inputStream = editProfileActivity.getContentResolver().openInputStream(selectedImageUri);
            FileOutputStream fileOutputStream = new FileOutputStream(profilePicture);
            // Copying
            copyStream(inputStream, fileOutputStream);
            fileOutputStream.close();
            inputStream.close();
        } catch (Exception e) {
            Log.d(TAG, "onActivityResult: " + e.toString());
        }
        return profilePicture;
    }

    private File createPictureFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = editProfileActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName /* prefix */, ".jpg", storageDir /* directory */);
        saveUserProfilePicturePath(image.getAbsolutePath());
        return image;
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    private void deletePreviuosFile() {
        if (!isPreviousSavedPictureEmpty()) {
            Log.d("Previoud image path", getPreviousSavedPicturePath());
            boolean deleted = new File(getPreviousSavedPicturePath()).delete();
            System.out.println("DELETEDDDDDDDDDDDDDDDDDDDD = " + deleted);
        }
    }

    private void saveUserProfilePicturePath(String path) {
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(getContext());
        userSharedPreferences.putStringSharedPreferences(Constants.getSavedProfileImagePath(), path);
        Log.d("Saved path", userSharedPreferences.getStringSharedPreferences(Constants.getSavedProfileImagePath()));
    }

    private boolean isPreviousSavedPictureEmpty() {
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(getContext());
        String previousSavedPicture = userSharedPreferences.getStringSharedPreferences(Constants.getSavedProfileImagePath());
        return previousSavedPicture.isEmpty();
    }

    private String getPreviousSavedPicturePath(){
        return new UserSharedPreferences(getContext()).getStringSharedPreferences(Constants.getSavedProfileImagePath());
    }

    public CircleImageView getCircleImageViewUserEdit() {
        return editProfileActivity.getCircleImageViewUserEdit();
    }

    public void setCircleImageViewUserEditImage(User user) {
        if (userHasImage(user))
            Picasso.with(getContext()).load(getUserImage(user))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.user_profile_no_photo)
                    .error(R.drawable.picasso_error)
                    .into(getCircleImageViewUserEdit());
    }

    public FloatingActionButton getFloatingActionButtonChangeProfileImage() {
        return editProfileActivity.getFloatingActionButtonChangeProfileImage();
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
