package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.LoginController;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViewComponents();
        initializeController();
    }

    private void initializeViewComponents() {
        editTextEmail = findViewById(R.id.edit_text_email_login_activity);
        editTextPassword = findViewById(R.id.edit_text_password_login_activity);
        buttonLogin = findViewById(R.id.button_login_login_activity);
        textViewSignIn = findViewById(R.id.text_view_sign_in_login_activity);
    }

    public void initializeController() {
        LoginController loginController = new LoginController(this);
        loginController.setListenerOnLoginActivity();
    }

    public EditText getEditTextEmail() {
        return editTextEmail;
    }

    public void setEditTextEmail(EditText editTextEmail) {
        this.editTextEmail = editTextEmail;
    }

    public EditText getEditTextPassword() {
        return editTextPassword;
    }

    public void setEditTextPassword(EditText editTextPassword) {
        this.editTextPassword = editTextPassword;
    }

    public Button getButtonLogin() {
        return buttonLogin;
    }

    public void setButtonLogin(Button buttonLogin) {
        this.buttonLogin = buttonLogin;
    }

    public TextView getTextViewSignIn() {
        return textViewSignIn;
    }

    public void setTextViewSignIn(TextView textViewSignIn) {
        this.textViewSignIn = textViewSignIn;
    }
}
