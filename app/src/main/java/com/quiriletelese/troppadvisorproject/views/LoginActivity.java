package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.LoginActivityController;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class LoginActivity extends AppCompatActivity {

    private LoginActivityController loginActivityController;
    private TextInputLayout textInputLayoutKey, textInputLayoutPassword;
    private Button buttonLogin;
    private TextView textViewSignIn, textViewCancelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViewComponents();
        initializeController();
        setListenerOnViewComponents();

    }

    private void initializeViewComponents() {
        textInputLayoutKey = findViewById(R.id.text_input_layout_key_login);
        textInputLayoutPassword = findViewById(R.id.text_input_layout_password_login);
        buttonLogin = findViewById(R.id.button_login);
        textViewSignIn = findViewById(R.id.text_view_sign_in);
        textViewCancelLogin = findViewById(R.id.text_view_cancel_login);
    }

    private void initializeController() {
         loginActivityController = new LoginActivityController(this);
    }

    private void setListenerOnViewComponents() {
        loginActivityController.setListenerOnViewComponents();
    }

    public TextInputLayout getTextInputLayoutKey() {
        return textInputLayoutKey;
    }

    public TextInputLayout getTextInputLayoutPassword() {
        return textInputLayoutPassword;
    }

    public Button getButtonLogin() {
        return buttonLogin;
    }

    public TextView getTextViewSignIn() {
        return textViewSignIn;
    }

    public TextView getTextViewCancelLogin() {
        return textViewCancelLogin;
    }

    public String getTextInputLayoutKeyValue(){
        return textInputLayoutKey.getEditText().getText().toString().trim();
    }
    public char[] getTextInputLayoutPasswordValue(){
        return textInputLayoutPassword.getEditText().getText().toString().trim().toCharArray();
    }

}
