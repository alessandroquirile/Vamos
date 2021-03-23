package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.SignUpActivityController;

import java.util.Objects;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class SignUpActivity extends AppCompatActivity {

    private SignUpActivityController signUpActivityController;
    private FloatingActionButton floatingActionButtonGoBack, floatingActionButtonHelp;
    private TextInputLayout textInputLayoutEmail, textInputLayoutName, textInputLayoutLastName,
            textInputLayoutUsername, textInputLayoutPassword, textInputLayoutRepeatPassword;
    private Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeViewComponents();
        initializeController();
        setListenerOnViewComponents();

    }

    public void initializeViewComponents() {
        floatingActionButtonGoBack = findViewById(R.id.floating_action_button_go_back_sign_up_activity);
        floatingActionButtonHelp = findViewById(R.id.floating_action_button_help_sign_up_activity);
        textInputLayoutEmail = findViewById(R.id.tex_input_layout_email_sign_up);
        textInputLayoutName = findViewById(R.id.text_input_layout_name_sign_up);
        textInputLayoutLastName = findViewById(R.id.text_input_layout_last_name_sign_up);
        textInputLayoutUsername = findViewById(R.id.text_input_layout_username_sign_up);
        textInputLayoutPassword = findViewById(R.id.text_input_layout_password_sign_up);
        textInputLayoutRepeatPassword = findViewById(R.id.text_input_layout_repeat_password_sign_up);
        buttonSignUp = findViewById(R.id.button_sign_up);
    }

    public void initializeController() {
        signUpActivityController = new SignUpActivityController(this);
    }

    private void setListenerOnViewComponents() {
        signUpActivityController.setListenerOnViewComponents();
    }

    public FloatingActionButton getFloatingActionButtonGoBack() {
        return floatingActionButtonGoBack;
    }

    public FloatingActionButton getFloatingActionButtonHelp() {
        return floatingActionButtonHelp;
    }

    public TextInputLayout getTextInputLayoutEmail() {
        return textInputLayoutEmail;
    }

    public EditText getTextInputLayoutEmailEditText() {
        return textInputLayoutEmail.getEditText();
    }

    public TextInputLayout getTextInputLayoutName() {
        return textInputLayoutName;
    }

    public EditText getTextInputLayoutNameEditText() {
        return textInputLayoutName.getEditText();
    }

    public TextInputLayout getTextInputLayoutLastName() {
        return textInputLayoutLastName;
    }

    public EditText getTextInputLayoutLastNameEditText() {
        return textInputLayoutLastName.getEditText();
    }

    public TextInputLayout getTextInputLayoutUsername() {
        return textInputLayoutUsername;
    }

    public EditText getTextInputLayoutUsernameEditText() {
        return textInputLayoutUsername.getEditText();
    }

    public TextInputLayout getTextInputLayoutPassword() {
        return textInputLayoutPassword;
    }

    public EditText getTextInputLayoutPasswordEditText() {
        return textInputLayoutPassword.getEditText();
    }

    public TextInputLayout getTextInputLayoutRepeatPassword() {
        return textInputLayoutRepeatPassword;
    }

    public EditText getTextInputLayoutRepeatPasswordEditText() {
        return textInputLayoutRepeatPassword.getEditText();
    }

    public Button getButtonSignUp() {
        return buttonSignUp;
    }

    public String getTextInputLayoutEmailValue() {
        return Objects.requireNonNull(getTextInputLayoutEmailEditText()).getText().toString().trim();
    }

    public String getTextInputLayoutNameValue() {
        return Objects.requireNonNull(getTextInputLayoutNameEditText()).getText().toString().trim();
    }

    public String getTextInputLayoutLastNameValue() {
        return Objects.requireNonNull(getTextInputLayoutLastNameEditText()).getText().toString().trim();
    }

    public String getTextInputLayoutUsernameValue() {
        return Objects.requireNonNull(getTextInputLayoutUsernameEditText()).getText().toString().trim();
    }

    public String getTextInputLayoutPasswordValue() {
        return Objects.requireNonNull(getTextInputLayoutPasswordEditText()).getText().toString().trim();
    }

    public String getTextInputLayoutRepeatPasswordValue() {
        return Objects.requireNonNull(getTextInputLayoutRepeatPasswordEditText()).getText().toString().trim();
    }

}
