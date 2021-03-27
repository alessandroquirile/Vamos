package com.quiriletelese.troppadvisorproject.controllers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.UserDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Badge;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.EditProfileActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class EditProfileActivityController implements View.OnClickListener, TextWatcher, AdapterView.OnItemClickListener {

    private final EditProfileActivity editProfileActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private Bitmap bitmapNewProfileImage;
    private String filePath = "";
    private int close = 0;
    private int total = 0;
    private boolean isUserInformationsChanged = false, isRequiredFieldsCorrectlyFilled = true, canClose = true;
    private AlertDialog alertDialogWaitSaveChanges;

    public EditProfileActivityController(EditProfileActivity editProfileActivity) {
        this.editProfileActivity = editProfileActivity;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        isUserInformationsChanged = true;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        isUserInformationsChanged = true;
        detectEditText(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void updateUserImageHelper(VolleyCallBack volleyCallBack) {
        getUserDAO().updateUserImage(volleyCallBack, getUserEmail(), bitmapNewProfileImage, getContext());
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
                handleUpdateUserInformationsVolleySuccess();
            }

            @Override
            public void onError(String errorCode) {
                handleUpdateUserInformationsVolleyError(errorCode);
            }
        });
    }

    private void handleUpdateUserInformationsVolleyError(String errorCode) {
        if (alertDialogWaitSaveChanges.isShowing())
            alertDialogWaitSaveChanges.dismiss();
        canClose = false;
        switch (errorCode) {
            case "Username error":
                showUsernameAlreadyExistError();
                break;
            default:
                showToastOnUiThred(R.string.unexpected_error_while_updating_user_informations);
                break;
        }
    }

    private String getEmail() {
        return new UserSharedPreferences(getContext()).getStringSharedPreferences(Constants.getEmail());
    }

    private void handleUpdateUserImageVolleySuccess() {
        canClose = true;
        filePath = "";
        total += 1;
        if (total == close && canClose) {
            if (alertDialogWaitSaveChanges.isShowing())
                alertDialogWaitSaveChanges.dismiss();
            finish(RESULT_OK);
        }
    }

    private void handleUpdateUserImageVolleyError(String errorCode) {
        if (alertDialogWaitSaveChanges.isShowing())
            alertDialogWaitSaveChanges.dismiss();
        canClose = false;
        showToastOnUiThred(R.string.unexpected_error_while_updating_image);
    }

    private void handleUpdateUserInformationsVolleySuccess() {
        canClose = true;
        total += 1;
        if (total == close && canClose) {
            if (alertDialogWaitSaveChanges.isShowing())
                alertDialogWaitSaveChanges.dismiss();
            finish(RESULT_OK);
        }
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.floating_action_button_change_profile_image:
                selectImageFromGallery();
                break;
            case R.id.text_view_return_to_the_original_profile_image:
                filePath = "";
                setCircleImageViewUserEditImage(getUserFromIntent());
                setTextViewReturnToTheOriginalImageVisibility(View.GONE);
                break;
        }
    }

    public void finish(int result) {
        editProfileActivity.setResult(result, new Intent());
        editProfileActivity.finish();
    }

    private void selectImageFromGallery() {
        if (!isExternalStoragePermissionsGranted())
            requestPermissions(createExternalStorageArrayStringPermissions(), Constants.getExternalStoragePermissionsCode());
        else
            startSelectImageFromGalleryActivity();
    }

    private void requestPermissions(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(editProfileActivity, permissions, requestCode);
    }

    private String[] createExternalStorageArrayStringPermissions() {
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    private boolean isExternalStoragePermissionsGranted() {
        return (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                checkPermissionResult(grantResults);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionResult(@NonNull int[] grantResults) {
        if (isPermissionGranted(grantResults)) {
            startSelectImageFromGalleryActivity();
        } else
            showShouldShowRequestPermissionRationaleDialog((dialogInterface, i) ->
                    requestPermissions(createExternalStorageArrayStringPermissions(), Constants.getAccessFineLocationCode()));
    }

    public void showWarningDialog() {
        if (isUserInformationsChanged)
            new AlertDialog.Builder(editProfileActivity)
                    .setTitle(getString(R.string.pay_attention))
                    .setMessage(getString(R.string.pay_attention_body_profile))
                    .setPositiveButton("Sì", ((dialogInterface, i) -> {
                        finish(Activity.RESULT_CANCELED);
                    }))
                    .setNegativeButton("No", null)
                    .setCancelable(false)
                    .create()
                    .show();
        else
            finish(Activity.RESULT_CANCELED);
    }

    private void showShouldShowRequestPermissionRationaleDialog(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(editProfileActivity)
                .setView(getLayoutInflater().inflate(R.layout.dialog_missing_external_storage_permission_layout, null))
                .setPositiveButton("OK", okListener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private void startSelectImageFromGalleryActivity() {
        editProfileActivity.startActivityForResult(Intent.createChooser(createSelectImageFromGalleryIntent(),
                getString(R.string.select_image)), Constants.getSelectPictureCode());
    }

    private Intent createSelectImageFromGalleryIntent() {
        Intent intentSelectImageFromGallery = new Intent();
        intentSelectImageFromGallery.setType("image/*");
        intentSelectImageFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        return intentSelectImageFromGallery;
    }

    @NotNull
    private LayoutInflater getLayoutInflater() {
        return editProfileActivity.getLayoutInflater();
    }

    private boolean isPermissionGranted(@NonNull int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
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
            if (!checkTapTargetBooleanPreferences()) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> setTapTargetSequence(), 200);
            }
        } /*else
            editProfileActivity.finish();*/
    }

    public void setListenersOnViewComponents() {
        getFloatingActionButtonChangeProfileImage().setOnClickListener(this);
        getTextViewReturnToTheOriginalProfileImage().setOnClickListener(this);
        getEditTextName().addTextChangedListener(this);
        getEditTextLastName().addTextChangedListener(this);
        getEditTextUsername().addTextChangedListener(this);
        getAutoCompleteTextViewChosenTitle().setOnItemClickListener(this);
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
                    filePath = getRealPathFromURI(selectedImageUri);/*createSelectedProfilePictureFile(selectedImageUri);*/
                    try {
                        bitmapNewProfileImage = resizeBitmap(MediaStore.Images.Media.getBitmap(editProfileActivity.getContentResolver(), selectedImageUri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    getCircleImageViewUserEdit().setImageURI(selectedImageUri);
                }
            }
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        final int maxSize = 800;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }
        return Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
    }

//    public String getPath(Uri uri) {
//        Cursor cursor = editProfileActivity.getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        String document_id = cursor.getString(0);
//        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
//        cursor.close();
//        cursor = editProfileActivity.getContentResolver().query(
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//        cursor.moveToFirst();
//        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//        cursor.close();
//        return path;
//    }

    private String getRealPathFromURI(Uri contentURI) {
        String thePath = "no-path-found";
        String[] filePathColumn = {MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = editProfileActivity.getContentResolver().query(contentURI, filePathColumn, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            thePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return thePath.isEmpty() ? null : thePath;
    }

    private boolean isNewProfileImageNull() {
        return "".equals(filePath);
    }


    public void saveChanges() {
        showWaitSaveChangesDialog();
        if (isRequiredFieldsCorrectlyFilled) {
            if (!isNewProfileImageNull()) {
                updateUserImage();
                close += 1;
            }
            if (isUserInformationsChanged) {
                updateUserInformations();
                close += 1;
            }
        } else
            Toast.makeText(editProfileActivity, getString(R.string.fill_required_fields_error), Toast.LENGTH_SHORT).show();
    }

    private User createUserForUpdate() {
        return getUserInformationsFromFields();
    }

    private User getUserInformationsFromFields() {
        User user = new User();
        user.setEmail(getEmail());
        user.setName(getNewName());
        user.setLastName(getNewLastName());
        user.setUsername(getNewUsername());
        user.setChosenTitle(getNewChosenTitle());
        return user;
    }

    private void showToastOnUiThred(int stringId) {
        editProfileActivity.runOnUiThread(() ->
                Toast.makeText(editProfileActivity, getString(stringId), Toast.LENGTH_SHORT).show());
    }

    private void showWaitSaveChangesDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        alertDialogBuilder.setView(getLayoutInflater().inflate(getAlertDialogLayout(), null));
        alertDialogBuilder.setCancelable(false);
        alertDialogWaitSaveChanges = alertDialogBuilder.create();
        alertDialogWaitSaveChanges.show();
    }

    @NotNull
    @Contract(" -> new")
    private AlertDialog.Builder createAlertDialogBuilder() {
        return new AlertDialog.Builder(editProfileActivity);
    }

    private int getAlertDialogLayout() {
        return R.layout.dialog_wait_save_changes_layout;
    }

    public Toolbar getToolbar() {
        return editProfileActivity.getToolbar();
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
        getAutoCompleteTextViewChosenTitle().setText(getChosenTitle(user));
    }

    private void setAutoCompleteTextViewChosenTitleTextAdapter(User user) {
        getAutoCompleteTextViewChosenTitle().setAdapter(createAutoCompleteTextViewChosenTitleTextAdapter(user));
    }

    private List<String> getObtainedTitlesFromUser(User user) {
        List<String> titles = new ArrayList<>();
        for (Badge title : user.getObtainedBadges())
            if (!title.getName().equals("Clemente") && !title.getName().equals("Severo"))
                titles.add(title.getName());
        return titles;
    }

    private ArrayAdapter<String> createAutoCompleteTextViewChosenTitleTextAdapter(User user) {
        return new ArrayAdapter<>(getContext(), R.layout.chosen_title_drop_down_menu_item_layout, getObtainedTitlesFromUser(user));
    }

    private void setTextViewReturnToTheOriginalImageVisibility(int visibility) {
        getTextViewReturnToTheOriginalProfileImage().setVisibility(visibility);
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
        return getEditTextLastName().getText().toString();
    }

    private boolean isUsernameEmpty() {
        return getEditTextUsername().getText().toString().isEmpty();
    }

    private String getNewUsername() {
        return getEditTextUsername().getText().toString();
    }

    private boolean isChosenTitleEmpty() {
        return getAutoCompleteTextViewChosenTitle().getText().toString().isEmpty();
    }

    private String getNewChosenTitle() {
        return getAutoCompleteTextViewChosenTitle().getText().toString();
    }

    private boolean checkTapTargetBooleanPreferences() {
        return new UserSharedPreferences(getContext()).constains(Constants.getTapTargetEditProfile());
    }

    private void writeTapTargetBooleanPreferences() {
        new UserSharedPreferences(getContext()).putBooleanSharedPreferences(Constants.getTapTargetEditProfile(), true);
    }

    public void setTapTargetSequence() {
        new TapTargetSequence(editProfileActivity).targets(
                createTapTargetForToolbarNavigationIcon(getString(R.string.cancel_edit_profile_tap_title),
                        getString(R.string.cancel_edit_profile_tap_description), 50),
                createTapTargetForToolbar(R.id.menu_save_changes, getString(R.string.save_changes),
                        getString(R.string.save_changes_tap_description), 50),
                create(getFloatingActionButtonChangeProfileImage(), getString(R.string.pick_image_tap_title),
                        getString(R.string.pick_image_tap_description), false, 70))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        writeTapTargetBooleanPreferences();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        // Perform action for the current target
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                }).start();

    }

    private TapTarget createTapTargetForToolbarNavigationIcon(String title, String body, int radius) {
        return TapTarget.forToolbarNavigationIcon(getToolbar(), title, body)
                .dimColor(android.R.color.black)
                .outerCircleColor(R.color.colorPrimary)
                .textColor(android.R.color.white)
                .targetCircleColor(R.color.white)
                .drawShadow(true)
                .cancelable(false)
                .tintTarget(true)
                .targetRadius(radius);
    }

    private TapTarget createTapTargetForToolbar(int menuItemId, String title, String body, int radius) {
        return TapTarget.forToolbarMenuItem(getToolbar(), menuItemId, title, body)
                .dimColor(android.R.color.black)
                .outerCircleColor(R.color.colorPrimary)
                .textColor(android.R.color.white)
                .targetCircleColor(R.color.white)
                .drawShadow(true)
                .cancelable(false)
                .tintTarget(true)
                .targetRadius(radius);
    }

    private TapTarget create(View view, String title, String body, boolean tintTarget, int radius) {
        return TapTarget.forView(view, title, body)
                .dimColor(android.R.color.black)
                .outerCircleColor(R.color.colorPrimary)
                .textColor(android.R.color.white)
                .targetCircleColor(R.color.white)
                .drawShadow(true)
                .cancelable(false)
                .tintTarget(tintTarget)
                .targetRadius(radius);
    }
}
