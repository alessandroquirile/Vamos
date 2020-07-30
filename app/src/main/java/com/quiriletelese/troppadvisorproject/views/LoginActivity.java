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
        editTextEmail = findViewById(R.id.edit_text_email_or_username_login_activity);
        editTextPassword = findViewById(R.id.edit_text_password_login_activity);
        buttonLogin = findViewById(R.id.button_login_login_activity);
        textViewSignIn = findViewById(R.id.text_view_sign_in_login_activity);
    }

    public void initializeController() {
        LoginController loginController = new LoginController(this);
        loginController.setListenersOnLoginActivity();
    }

    public EditText getEditTextEmail() {
        return editTextEmail;
    }

    public EditText getEditTextPassword() {
        return editTextPassword;
    }

    public Button getButtonLogin() {
        return buttonLogin;
    }

    public TextView getTextViewSignIn() {
        return textViewSignIn;
    }
}
