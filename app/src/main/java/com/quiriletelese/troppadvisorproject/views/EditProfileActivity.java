package com.quiriletelese.troppadvisorproject.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.EditProfileController;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private EditProfileController editProfileController;
    private CircleImageView circleImageViewUserEdit;
    private FloatingActionButton floatingActionButtonChangeProfileImage;
    private TextInputLayout textInputLayoutName, textInputLayoutLastName, textInputLayoutUsername,
            textInputLayoutSelectTitle;
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
        setFieldsOnClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        editProfileController.handleOnActivityResult(requestCode, resultCode, data);
    }

    private void initializeViewComponents() {
        circleImageViewUserEdit = findViewById(R.id.circle_image_view_user_edit);
        floatingActionButtonChangeProfileImage = findViewById(R.id.floating_action_button_change_profile_image);
        textInputLayoutName = findViewById(R.id.text_input_layout_name);
        textInputLayoutLastName = findViewById(R.id.text_input_layout_lastname);
        textInputLayoutUsername = findViewById(R.id.text_input_layout_username);
        textInputLayoutSelectTitle = findViewById(R.id.text_input_layout_select_user_title);
        switchCompatPrivateAccount = findViewById(R.id.switch_compat_private_account);
    }

    private void initializeController() {
        editProfileController = new EditProfileController(this);
    }

    private void setFields() {
        editProfileController.setEditProfileActivityFields();
    }

    private void setFieldsOnClickListener() {
        editProfileController.setEditProfileActivityFieldsOnClickListener();
    }

    public CircleImageView getCircleImageViewUserEdit() {
        return circleImageViewUserEdit;
    }

    public FloatingActionButton getFloatingActionButtonChangeProfileImage() {
        return floatingActionButtonChangeProfileImage;
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

    public SwitchCompat getSwitchCompatPrivateAccount() {
        return switchCompatPrivateAccount;
    }
}