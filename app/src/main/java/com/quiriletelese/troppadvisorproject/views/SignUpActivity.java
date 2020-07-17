package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.CreateAccountController;

public class SignUpActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButtonGoBack;
    private EditText editTextEmail;
    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextNickname;
    private EditText editTextPassword;
    private EditText editTextRepeatPassword;
    private Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeViewComponents();
        initializeController();
    }

    public void initializeViewComponents() {
        floatingActionButtonGoBack = findViewById(R.id.floating_action_button_go_back_sign_up_activity);
        editTextEmail = findViewById(R.id.edit_text_email_sign_up_activity);
        editTextName = findViewById(R.id.edit_text_name_sign_up_activity);
        editTextLastName = findViewById(R.id.edit_text_lastname_sign_up_activity);
        editTextNickname = findViewById(R.id.edit_text_nickname_sign_up_activity);
        editTextPassword = findViewById(R.id.edit_text_password_sign_up_activity);
        editTextRepeatPassword = findViewById(R.id.edit_text_repeat_password_sign_up_activity);
        buttonSignUp = findViewById(R.id.button_sign_up_sign_up_activity);
    }

    public void initializeController() {
        CreateAccountController createAccountController = new CreateAccountController(this);
        createAccountController.setListenersOnSignUpActivity();
    }

    public FloatingActionButton getFloatingActionButtonGoBack() {
        return floatingActionButtonGoBack;
    }

    public void setFloatingActionButtonGoBack(FloatingActionButton floatingActionButtonGoBack) {
        this.floatingActionButtonGoBack = floatingActionButtonGoBack;
    }

    public EditText getEditTextEmail() {
        return editTextEmail;
    }

    public void setEditTextEmail(EditText editTextEmail) {
        this.editTextEmail = editTextEmail;
    }

    public EditText getEditTextName() {
        return editTextName;
    }

    public void setEditTextName(EditText editTextName) {
        this.editTextName = editTextName;
    }

    public EditText getEditTextLastName() {
        return editTextLastName;
    }

    public void setEditTextLastName(EditText editTextLastName) {
        this.editTextLastName = editTextLastName;
    }

    public EditText getEditTextNickname() {
        return editTextNickname;
    }

    public void setEditTextNickname(EditText editTextNickname) {
        this.editTextNickname = editTextNickname;
    }

    public EditText getEditTextPassword() {
        return editTextPassword;
    }

    public void setEditTextPassword(EditText editTextPassword) {
        this.editTextPassword = editTextPassword;
    }

    public EditText getEditTextRepeatPassword() {
        return editTextRepeatPassword;
    }

    public void setEditTextRepeatPassword(EditText editTextRepeatPassword) {
        this.editTextRepeatPassword = editTextRepeatPassword;
    }

    public Button getButtonSignUp() {
        return buttonSignUp;
    }

    public void setButtonSignUp(Button buttonSignUp) {
        this.buttonSignUp = buttonSignUp;
    }
}
