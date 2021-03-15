package com.quiriletelese.troppadvisorproject.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.EnterNewPasswordActivityController;

public class EnterNewPasswordActivity extends AppCompatActivity {

    private EnterNewPasswordActivityController enterNewPasswordActivityController;
    private TextInputLayout textInputLayoutConfirmationCode, textInputLayoutNewPassword,
            textInputLayoutConfirmNewPassword;
    private Button buttonResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_new_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeViewComponents();
        initializeController();
        setListenerOnViewComponents();
    }

    private void initializeViewComponents() {
        textInputLayoutConfirmationCode = findViewById(R.id.text_input_layout_confirmation_code);
        textInputLayoutNewPassword = findViewById(R.id.text_input_layout_new_password);
        textInputLayoutConfirmNewPassword = findViewById(R.id.text_input_layout_confirm_new_password);
        buttonResetPassword = findViewById(R.id.button_reset_password);
    }

    private void initializeController() {
        enterNewPasswordActivityController = new EnterNewPasswordActivityController(this);
    }

    private void setListenerOnViewComponents() {
        enterNewPasswordActivityController.setListenerOnViewComponents();
    }

    public TextInputLayout getTextInputLayoutConfirmationCode() {
        return textInputLayoutConfirmationCode;
    }

    public EditText getEditTextConfirmationCode() {
        return textInputLayoutConfirmationCode.getEditText();
    }

    public TextInputLayout getTextInputLayoutNewPassword() {
        return textInputLayoutNewPassword;
    }

    public EditText getEditTextNewPassword() {
        return textInputLayoutNewPassword.getEditText();
    }

    public TextInputLayout getTextInputLayoutConfirmNewPassword() {
        return textInputLayoutConfirmNewPassword;
    }

    public EditText getEditTextConfirmNewPassword() {
        return textInputLayoutConfirmNewPassword.getEditText();
    }

    public Button getButtonResetPassword() {
        return buttonResetPassword;
    }
}