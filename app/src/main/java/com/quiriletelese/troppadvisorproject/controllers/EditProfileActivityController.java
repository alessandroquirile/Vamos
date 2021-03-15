package com.quiriletelese.troppadvisorproject.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.UserDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Badge;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.EditProfileActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class EditProfileActivityController implements View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {

    private final EditProfileActivity editProfileActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private File newProfilePicture = null;
    private boolean isUserInformationsChanged = false, isRequiredFieldsCorrectlyFilled = true;

    public EditProfileActivityController(EditProfileActivity editProfileActivity) {
        this.editProfileActivity = editProfileActivity;
    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        isUserInformationsChanged = true;
        detectEditText(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        isUserInformationsChanged = true;
    }

    private void updateUserImageHelper(VolleyCallBack volleyCallBack) {
        getUserDAO().updateUserImage(volleyCallBack, getUserEmail(), getByteArrayFromFile(newProfilePicture), getContext());
    }

    private void updateUserInformationsHelper(VolleyCallBack volleyCallBack) {
        getUserDAO().updateUserInformations(volleyCallBack, createUserForUpdate(), getContext());
    }

    private void updateUserImage() {
        updateUserImageHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                handleUpdateUserImageVolleySuccess();
            }

            @Override
            public void onError(String errorCode) {
                handleUpdateUserImageVolleyError(errorCode);
            }
        });
    }

    private void updateUserInformations() {
        updateUserInformationsHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(String errorCode) {
                handleUpdateUserInformationsVolleyError(errorCode);
            }
        });
    }

    private void handleUpdateUserInformationsVolleyError(String errorCode) {
        switch (errorCode) {
            case "Username error":
                showUsernameAlreadyExistError();
                break;
            default:
                showToastOnUiThred(R.string.unexpected_error_while_updating_user_informations);

        }
    }

    private void handleUpdateUserImageVolleySuccess() {
        newProfilePicture = null;
    }

    private void handleUpdateUserImageVolleyError(String errorCode) {
        showToastOnUiThred(R.string.unexpected_error_while_updating_image);
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.floating_action_button_change_profile_image:
                selectImageFromGallery();
                break;
            case R.id.text_view_return_to_the_original_profile_image:
                setCircleImageViewUserEditImage(getUserFromIntent());
                setTextViewReturnToTheOriginalImageVisibility(View.GONE);
        }
    }

    private void selectImageFromGallery() {
        Intent intentSelectImageFromGallery = new Intent();
        intentSelectImageFromGallery.setType("image/*");
        intentSelectImageFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        editProfileActivity.startActivityForResult(Intent.createChooser(intentSelectImageFromGallery,
                getString(R.string.select_image)), Constants.getSelectPictureCode());
    }

    private void detectEditText(CharSequence charSequence) {
        if (isEditTextNameChanged(charSequence))
            editTextNameOnTextChanged(charSequence);
        else if (isEditTextLastNameChanged(charSequence))
            editTextLastNameChangedOnTextChanged(charSequence);
        else editTextUsernameOnTextChanged(charSequence);
    }


    private boolean isEditTextNameChanged(CharSequence charSequence) {
        return charSequence.hashCode() == getEditTextName().getText().hashCode();
    }

    private void editTextNameOnTextChanged(CharSequence charSequence) {
        if (charSequence.toString().isEmpty()) {
            setIsRequiredFieldsCorrect(false);
            getTextInputLayoutName().setErrorEnabled(true);
            getTextInputLayoutName().setError(getString(R.string.field_cannot_be_empty));
        } else {
            setIsRequiredFieldsCorrect(true);
            getTextInputLayoutName().setError(null);
            getTextInputLayoutName().setErrorEnabled(false);
        }
    }

    private boolean isEditTextLastNameChanged(CharSequence charSequence) {
        return charSequence.hashCode() == getEditTextLastName().getText().hashCode();
    }

    private void editTextLastNameChangedOnTextChanged(CharSequence charSequence) {
        if (charSequence.toString().isEmpty()) {
            setIsRequiredFieldsCorrect(false);
            getTextInputLayoutLastName().setErrorEnabled(true);
            getTextInputLayoutLastName().setError(getString(R.string.field_cannot_be_empty));
        } else {
            setIsRequiredFieldsCorrect(true);
            getTextInputLayoutLastName().setError(null);
            getTextInputLayoutLastName().setErrorEnabled(false);
        }
    }

    private void editTextUsernameOnTextChanged(CharSequence charSequence) {
        if (charSequence.toString().isEmpty()) {
            setIsRequiredFieldsCorrect(false);
            getTextInputLayoutUsername().setErrorEnabled(true);
            getTextInputLayoutUsername().setError(getString(R.string.field_cannot_be_empty));
        } else {
            setIsRequiredFieldsCorrect(true);
            getTextInputLayoutUsername().setError(null);
            getTextInputLayoutUsername().setErrorEnabled(false);
        }
    }

    public void setEditProfileActivityFields() {
        User user = getUserFromIntent();
        if (user != null) {
            setCircleImageViewUserEditImage(user);
            setEditTextNameText(user);
            setEditTextLastNameText(user);
            setEditTextUsernameText(user);
            setAutoCompleteTextViewChosenTitleText(user);
            setAutoCompleteTextViewChosenTitleTextAdapter(user);
            setSwitchCompatPrivateAccountValue(user);
        } /*else
            editProfileActivity.finish();*/
    }

    public void setListenersOnViewComponents() {
        getFloatingActionButtonChangeProfileImage().setOnClickListener(this);
        getTextViewReturnToTheOriginalProfileImage().setOnClickListener(this);
        getEditTextName().addTextChangedListener(this);
        getEditTextLastName().addTextChangedListener(this);
        getEditTextUsername().addTextChangedListener(this);
        getSwitchCompatPrivateAccount().setOnCheckedChangeListener(this);
    }

    public void handleOnActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK)
            // compare the resultCode with the getSelectPicture constant
            if (requestCode == Constants.getSelectPictureCode()) {
                setTextViewReturnToTheOriginalImageVisibility(View.VISIBLE);
                // Get the url of the image from data
                Uri selectedImageUri = Objects.requireNonNull(data).getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    newProfilePicture = createSelectedProfilePictureFile(selectedImageUri);
                    getCircleImageViewUserEdit().setImageURI(selectedImageUri);
                }
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
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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

    public static byte[] getByteArrayFromFile(File file) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            for (int readNum; (readNum = fis.read(b)) != -1; ) {
                bos.write(b, 0, readNum);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            Log.d("mylog", e.toString());
        }
        return null;
    }

    private void deletePreviuosFile() {
        if (!isPreviousSavedPictureEmpty()) {
            Log.d("Previoud image path", getPreviousSavedPicturePath());
            Log.d("DELETED PREVIOUS PIC", String.valueOf(new File(getPreviousSavedPicturePath()).delete()));
        }
    }

    private void saveUserProfilePicturePath(String path) {
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(getContext());
        userSharedPreferences.putStringSharedPreferences(Constants.getSavedProfileImagePath(), path);
        Log.d("SAVED FILE PATH", userSharedPreferences.getStringSharedPreferences(Constants.getSavedProfileImagePath()));
    }

    private boolean isPreviousSavedPictureEmpty() {
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(getContext());
        String previousSavedPicture = userSharedPreferences.getStringSharedPreferences(Constants.getSavedProfileImagePath());
        return previousSavedPicture.isEmpty();
    }

    private boolean isNewProfileImageNull() {
        return newProfilePicture == null;
    }

    private String getPreviousSavedPicturePath() {
        return new UserSharedPreferences(getContext()).getStringSharedPreferences(Constants.getSavedProfileImagePath());
    }

    public void saveChanges() {
        if (!isNewProfileImageNull())
            updateUserImage();
        if (isUserInformationsChanged) {
            if (isRequiredFieldsCorrectlyFilled)
                updateUserInformations();
            else
                Toast.makeText(editProfileActivity, getString(R.string.fill_required_fields_error), Toast.LENGTH_SHORT).show();
        }
        editProfileActivity.onBackPressed();
    }

    private User createUserForUpdate() {
        return getUserInformationsFromFields(getUserFromIntent());
    }

    private User getUserInformationsFromFields(User user) {
        if (!isUserNameEmpty())
            user.setName(getNewName());
        if (!isUserLastNameEmpty())
            user.setLastName(getNewLastName());
        if (!isUsernameEmpty())
            user.setUsername(getNewUsername());
        return user;
    }

    private void showToastOnUiThred(int stringId) {
        editProfileActivity.runOnUiThread(() ->
                Toast.makeText(editProfileActivity, getString(stringId), Toast.LENGTH_SHORT).show());
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
        else
            getCircleImageViewUserEdit().setImageResource(R.drawable.user_profile_no_photo);
    }

    public TextView getTextViewReturnToTheOriginalProfileImage() {
        return editProfileActivity.getTextViewReturnToTheOriginalProfileImage();
    }

    public FloatingActionButton getFloatingActionButtonChangeProfileImage() {
        return editProfileActivity.getFloatingActionButtonChangeProfileImage();
    }

    public TextInputLayout getTextInputLayoutName() {
        return editProfileActivity.getTextInputLayoutName();
    }

    public TextInputLayout getTextInputLayoutLastName() {
        return editProfileActivity.getTextInputLayoutLastName();
    }

    public TextInputLayout getTextInputLayoutUsername() {
        return editProfileActivity.getTextInputLayoutUsername();
    }

    private void showUsernameAlreadyExistError() {
        getTextInputLayoutUsername().setError(getUsernameAlreadyExistErrorMessage());
    }

    @NotNull
    private String getUsernameAlreadyExistErrorMessage() {
        return getString(R.string.username_already_exist);
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

    private AutoCompleteTextView getAutoCompleteTextViewChosenTitle() {
        return editProfileActivity.getAutoCompleteTextViewChosenTitle();
    }

    public void setAutoCompleteTextViewChosenTitleText(User user) {
        getEditTextUsername().setText(getChosenTitle(user));
    }

    private void setAutoCompleteTextViewChosenTitleTextAdapter(User user) {
        getAutoCompleteTextViewChosenTitle().setAdapter(createAutoCompleteTextViewChosenTitleTextAdapter(user));
    }

    private List<String> getObtainedTitlesFromUser(User user) {
        List<String> titles = new ArrayList<>();
        for (Badge title : user.getObtainedBadges())
            titles.add(title.getName());
        return titles;
    }

    private ArrayAdapter<String> createAutoCompleteTextViewChosenTitleTextAdapter(User user) {
        return new ArrayAdapter<>(getContext(), R.layout.chosen_title_drop_down_menu_item_layout, getObtainedTitlesFromUser(user));
    }

    private void setTextViewReturnToTheOriginalImageVisibility(int visibility) {
        getTextViewReturnToTheOriginalProfileImage().setVisibility(visibility);
    }

    private SwitchCompat getSwitchCompatPrivateAccount() {
        return editProfileActivity.getSwitchCompatPrivateAccount();
    }

    private void setSwitchCompatPrivateAccountValue(User user) {
        getSwitchCompatPrivateAccount().setChecked(getIsPrivateAccount(user));
    }

    private void setIsRequiredFieldsCorrect(boolean value) {
        isRequiredFieldsCorrectlyFilled = value;
    }

    private User getUserFromIntent() {
        return (User) getIntent().getSerializableExtra("user");
    }

    private String getUserEmail() {
        return new UserSharedPreferences(getContext()).getStringSharedPreferences(Constants.getEmail());
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

    private String getChosenTitle(User user) {
        return user.getChosenTitle();
    }

    private boolean getIsPrivateAccount(User user) {
        return user.isPrivateAccount();
    }

    private UserDAO getUserDAO() {
        return daoFactory.getUserDAO(getStorageTechnology(Constants.getUserStorageTechnology()));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private boolean isUserUpdateImageSuccess(Object object) {
        return (Boolean) object;
    }

    private boolean isUserNameEmpty() {
        return getEditTextName().getText().toString().isEmpty();
    }

    private String getNewName() {
        return getEditTextName().getText().toString();
    }

    private boolean isUserLastNameEmpty() {
        return getEditTextLastName().getText().toString().isEmpty();
    }

    private String getNewLastName() {
        return getEditTextName().getText().toString();
    }

    private boolean isUsernameEmpty() {
        return getEditTextUsername().getText().toString().isEmpty();
    }

    private String getNewUsername() {
        return getEditTextName().getText().toString();
    }

    private boolean isChosenTitleEmpty() {
        return getAutoCompleteTextViewChosenTitle().getText().toString().isEmpty();
    }

    private String getNewChosenTitle() {
        return getEditTextName().getText().toString();
    }

}
