package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.ResetPasswordActivityController;

public class ResetPasswordActivity extends AppCompatActivity {

    private ResetPasswordActivityController resetPasswordActivityController;
    private TextInputLayout textInputLayoutForgotPasswordEmail;
    private Button buttonSendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeViewComponents();
        initializeController();
        setListenerOnViewComponents();
    }

    private void initializeViewComponents() {
        textInputLayoutForgotPasswordEmail = findViewById(R.id.text_input_layout_forgot_password_email);
        buttonSendCode = findViewById(R.id.button_send_code);
    }

    private void initializeController() {
        resetPasswordActivityController = new ResetPasswordActivityController(this);
    }

    public void setListenerOnViewComponents() {
        resetPasswordActivityController.setListenerOnViewComponents();
    }

    public TextInputLayout getTextInputLayoutForgotPasswordEmail() {
        return textInputLayoutForgotPasswordEmail;
    }

    public EditText getEditTextForgotPasswordEmail() {
        return textInputLayoutForgotPasswordEmail.getEditText();
    }

    public Button getButtonSendCode() {
        return buttonSendCode;
    }
}