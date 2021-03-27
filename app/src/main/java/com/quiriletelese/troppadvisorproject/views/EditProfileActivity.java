package com.quiriletelese.troppadvisorproject.views;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.EditProfileActivityController;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private EditProfileActivityController editProfileActivityController;
    private Toolbar toolbar;
    private CircleImageView circleImageViewUserEdit;
    private TextView textViewReturnToTheOriginalProfileImage;
    private FloatingActionButton floatingActionButtonChangeProfileImage;
    private TextInputLayout textInputLayoutName, textInputLayoutLastName, textInputLayoutUsername,
            textInputLayoutSelectTitle, textInputLayoutEmail;
    private AutoCompleteTextView autoCompleteTextViewChosenTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        toolbar = findViewById(R.id.tool_bar_edit_profile);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close_white);

        initializeViewComponents();
        initializeController();
        setFields();
        setListenersOnViewComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onOptionsItemSelectedHelper(item);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        editProfileActivityController.handleOnActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        editProfileActivityController.onRequestPermissionsResult(requestCode, grantResults);
    }

    private void onOptionsItemSelectedHelper(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showWarningDialog();
                break;
            case R.id.menu_save_changes:
                saveChanges();
                break;
        }
    }

    private void initializeViewComponents() {
        textInputLayoutEmail = findViewById(R.id.text_input_layout_email);
        circleImageViewUserEdit = findViewById(R.id.circle_image_view_user_edit);
        textViewReturnToTheOriginalProfileImage = findViewById(R.id.text_view_return_to_the_original_profile_image);
        floatingActionButtonChangeProfileImage = findViewById(R.id.floating_action_button_change_profile_image);
        textInputLayoutName = findViewById(R.id.text_input_layout_name);
        textInputLayoutLastName = findViewById(R.id.text_input_layout_lastname);
        textInputLayoutUsername = findViewById(R.id.text_input_layout_username);
        textInputLayoutSelectTitle = findViewById(R.id.text_input_layout_select_user_title);
        autoCompleteTextViewChosenTitle = findViewById(R.id.auto_complete_text_view_chosen_title);
    }

    private void initializeController() {
        editProfileActivityController = new EditProfileActivityController(this);
    }

    private void setFields() {
        editProfileActivityController.setEditProfileActivityFields();
    }

    public void setListenersOnViewComponents() {
        editProfileActivityController.setListenersOnViewComponents();
    }

    public void showWarningDialog() {
        editProfileActivityController.showWarningDialog();
    }

    private void saveChanges() {
        editProfileActivityController.saveChanges();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public CircleImageView getCircleImageViewUserEdit() {
        return circleImageViewUserEdit;
    }

    public TextView getTextViewReturnToTheOriginalProfileImage() {
        return textViewReturnToTheOriginalProfileImage;
    }

    public FloatingActionButton getFloatingActionButtonChangeProfileImage() {
        return floatingActionButtonChangeProfileImage;
    }

    public TextInputLayout getTextInputLayoutName() {
        return textInputLayoutName;
    }

    public TextInputLayout getTextInputLayoutLastName() {
        return textInputLayoutLastName;
    }

    public TextInputLayout getTextInputLayoutUsername() {
        return textInputLayoutUsername;
    }

    public EditText getEditTextName() {
        return textInputLayoutName.getEditText();
    }

    public EditText getEditTextEmail() {
        return textInputLayoutEmail.getEditText();
    }

    public EditText getEditTextLastame() {
        return textInputLayoutLastName.getEditText();
    }

    public EditText getEditTextUsername() {
        return textInputLayoutUsername.getEditText();
    }

    public AutoCompleteTextView getAutoCompleteTextViewChosenTitle() {
        return autoCompleteTextViewChosenTitle;
    }
}