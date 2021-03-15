package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.EnterNewPasswordActivity;
import com.quiriletelese.troppadvisorproject.views.ResetPasswordActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ResetPasswordActivityController implements TextWatcher, View.OnClickListener {

    private final ResetPasswordActivity resetPasswordActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private AlertDialog alertDialog;

    public ResetPasswordActivityController(ResetPasswordActivity resetPasswordActivity) {
        this.resetPasswordActivity = resetPasswordActivity;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        onTextChandedHelper(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    private void sendConfirmationCodeHelper(VolleyCallBack volleyCallBack) {
        getAccountDAO().sendConfirmationCode(volleyCallBack, getEmail(), getContext());
    }

    private void sendConfirmationCode() {
        sendConfirmationCodeHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackOnSucces();
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError();
            }
        });
    }

    private void onTextChandedHelper(CharSequence charSequence) {
        detectEditText(charSequence);
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.button_send_code:
                handleButtonSendCodeClicked();
                break;
        }
    }

    private void handleButtonSendCodeClicked() {
        showSendingConfirmationCodeDialog();
        sendConfirmationCode();
    }

    private void volleyCallbackOnSucces() {
        dismissDialog();
        startEnterNewPasswordActvity();
    }

    private void volleyCallbackOnError() {
        dismissDialog();
        showToastOnUiThread(R.string.sending_confirmation_code_error_try_again);
    }

    private void detectEditText(CharSequence charSequence) {
        if (isEditTextEmailChanged(charSequence))
            editTextEmailChanged(charSequence);
    }

    private void editTextEmailChanged(CharSequence charSequence) {
        setViewEnabled(getButtonSendCode(), checkEmailFormat(charSequence));
    }

    private boolean checkEmailFormat(CharSequence email) {
        if (!isEmailValid(email)) {
            setTextInputLayoutError(R.string.email_pattern_error);
            return false;
        } else {
            setTextInputLayoutErrorNull();
            return true;
        }
    }

    private boolean isEditTextEmailChanged(CharSequence charSequence) {
        return charSequence.hashCode() == getEditTextForgotPasswordEmail().getText().hashCode();
    }

    public void setListenerOnViewComponents() {
        getButtonSendCode().setOnClickListener(this);
        getEditTextForgotPasswordEmail().addTextChangedListener(this);
    }

    private void showToastOnUiThread(int stringId) {
        resetPasswordActivity.runOnUiThread(() ->
                Toast.makeText(resetPasswordActivity, getString(stringId), Toast.LENGTH_LONG).show());
    }

    private void startEnterNewPasswordActvity() {
        Intent intentEnterNewPassworActivity = new Intent(getContext(), EnterNewPasswordActivity.class);
        intentEnterNewPassworActivity.putExtra(Constants.getEmail(), getEmail());
        intentEnterNewPassworActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intentEnterNewPassworActivity);
        resetPasswordActivity.finish();
    }

    private void showSendingConfirmationCodeDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        alertDialogBuilder.setView(getLayoutInflater().inflate(getAlertDialogLayout(), null));
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @NotNull
    @Contract(" -> new")
    private AlertDialog.Builder createAlertDialogBuilder() {
        return new AlertDialog.Builder(resetPasswordActivity);
    }

    @NotNull
    private LayoutInflater getLayoutInflater() {
        return resetPasswordActivity.getLayoutInflater();
    }

    private int getAlertDialogLayout() {
        return R.layout.dialog_wait_send_confirmation_code;
    }

    private void dismissDialog() {
        alertDialog.dismiss();
    }

    private void setViewEnabled(View view, boolean enable) {
        view.setEnabled(enable);
    }

    private void setTextInputLayoutError(int errorString) {
        getTextInputLayoutForgotPasswordEmail().setError(getString(errorString));
    }

    private void setTextInputLayoutErrorNull() {
        getTextInputLayoutForgotPasswordEmail().setError(null);
        getTextInputLayoutForgotPasswordEmail().setErrorEnabled(false);
    }

    public TextInputLayout getTextInputLayoutForgotPasswordEmail() {
        return resetPasswordActivity.getTextInputLayoutForgotPasswordEmail();
    }

    private EditText getEditTextForgotPasswordEmail() {
        return resetPasswordActivity.getEditTextForgotPasswordEmail();
    }

    public Button getButtonSendCode() {
        return resetPasswordActivity.getButtonSendCode();
    }

    private String getEmail() {
        return getEditTextForgotPasswordEmail().getText().toString();
    }

    private Context getContext() {
        return resetPasswordActivity.getApplicationContext();
    }

    private Resources getResources() {
        return resetPasswordActivity.getResources();
    }

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }

    private AccountDAO getAccountDAO() {
        return daoFactory.getAccountDAO(getStorageTechnology(Constants.getAccountStorageTechnology()));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
