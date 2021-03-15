package com.quiriletelese.troppadvisorproject.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.EditProfileActivityController;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private EditProfileActivityController editProfileActivityController;
    private CircleImageView circleImageViewUserEdit;
    private TextView textViewReturnToTheOriginalProfileImage;
    private FloatingActionButton floatingActionButtonChangeProfileImage;
    private TextInputLayout textInputLayoutName, textInputLayoutLastName, textInputLayoutUsername,
            textInputLayoutSelectTitle;
    private AutoCompleteTextView autoCompleteTextViewChosenTitle;
    private SwitchCompat switchCompatPrivateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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

    private void onOptionsItemSelectedHelper(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_changes:
                saveChanges();
                break;
        }
    }

    private void initializeViewComponents() {
        circleImageViewUserEdit = findViewById(R.id.circle_image_view_user_edit);
        textViewReturnToTheOriginalProfileImage = findViewById(R.id.text_view_return_to_the_original_profile_image);
        floatingActionButtonChangeProfileImage = findViewById(R.id.floating_action_button_change_profile_image);
        textInputLayoutName = findViewById(R.id.text_input_layout_name);
        textInputLayoutLastName = findViewById(R.id.text_input_layout_lastname);
        textInputLayoutUsername = findViewById(R.id.text_input_layout_username);
        textInputLayoutSelectTitle = findViewById(R.id.text_input_layout_select_user_title);
        autoCompleteTextViewChosenTitle = findViewById(R.id.auto_complete_text_view_chosen_title);
        switchCompatPrivateAccount = findViewById(R.id.switch_compat_private_account);
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

    private void saveChanges() {
        editProfileActivityController.saveChanges();
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

    public EditText getEditTextLastame() {
        return textInputLayoutLastName.getEditText();
    }

    public EditText getEditTextUsername() {
        return textInputLayoutUsername.getEditText();
    }

    public AutoCompleteTextView getAutoCompleteTextViewChosenTitle() {
        return autoCompleteTextViewChosenTitle;
    }

    public SwitchCompat getSwitchCompatPrivateAccount() {
        return switchCompatPrivateAccount;
    }
}