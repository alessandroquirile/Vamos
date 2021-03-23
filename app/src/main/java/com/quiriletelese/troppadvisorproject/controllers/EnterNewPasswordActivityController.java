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
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class EnterNewPasswordActivityController implements TextWatcher, View.OnClickListener {

    private final EnterNewPasswordActivity enterNewPasswordActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private AlertDialog alertDialog;

    public EnterNewPasswordActivityController(EnterNewPasswordActivity enterNewPasswordActivity) {
        this.enterNewPasswordActivity = enterNewPasswordActivity;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        onTextChangedHelper(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    private void changePasswordHelper(VolleyCallBack volleyCallBack) {
        getAccountDAO().changePassword(volleyCallBack, getEmail(), getConfirmationCode(), getNewPassword(), getContext());
    }

    private void changePassword() {
        changePasswordHelper(new VolleyCallBack() {
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

    private void onTextChangedHelper(CharSequence charSequence) {
        detectEditText(charSequence);
        setViewEnabled(getButtonResetPassword(), areFieldsCorrectlyFilled());
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.button_reset_password:
                handleResetPasswordButtonClicked();
                break;
        }
    }

    private void volleyCallbackOnSucces() {
        dismissDialog();
        showToastOnUiThread(R.string.password_changed_successfully);
        enterNewPasswordActivity.finish();
    }

    private void volleyCallbackOnError() {
        dismissDialog();
        showToastOnUiThread(R.string.changing_password_error);
    }

    public void setListenerOnViewComponents() {
        getButtonResetPassword().setOnClickListener(this);
        getEditTextConfirmationCode().addTextChangedListener(this);
        getEditTextNewPassword().addTextChangedListener(this);
        getEditTextConfirmNewPassword().addTextChangedListener(this);
    }

    private void handleResetPasswordButtonClicked() {
        showResettingPasswordDialog();
        changePassword();
    }

    private void detectEditText(CharSequence charSequence) {
        if (isEditTextConfirmationCodeChanged(charSequence))
            editTextConfirmationCodeChanged(charSequence);
        else if (isEditTextNewPasswordChanged(charSequence))
            editTextNewPasswordChanged(charSequence);
        else
            editTextConfirmNewPasswordChanged(charSequence);
    }

    private void editTextConfirmationCodeChanged(CharSequence charSequence) {
        if (isStringEmpty(charSequence.toString()))
            setTextInputLayoutError(getTextInputLayoutConfirmationCode(), R.string.field_cannot_be_empty);
        else
            setTextInputLayoutErrorNull(getTextInputLayoutConfirmationCode());
    }

    private void editTextNewPasswordChanged(CharSequence charSequence) {
        if (!isPasswordLegit(charSequence.toString()))
            setTextInputLayoutError(getTextInputLayoutNewPassword(), R.string.password_not_allowed);
        else
            setTextInputLayoutErrorNull(getTextInputLayoutNewPassword());
    }

    private void editTextConfirmNewPasswordChanged(CharSequence charSequence) {
        if (!areStringEquals(getNewPassword(), charSequence.toString()))
            setTextInputLayoutError(getTextInputLayoutConfirmNewPassword(), R.string.passwords_not_match);
        else
            setTextInputLayoutErrorNull(getTextInputLayoutConfirmNewPassword());
    }

    private boolean isEditTextConfirmationCodeChanged(CharSequence charSequence) {
        return charSequence.hashCode() == getEditTextConfirmationCode().getText().hashCode();
    }

    private boolean isEditTextNewPasswordChanged(CharSequence charSequence) {
        return charSequence.hashCode() == getEditTextNewPassword().getText().hashCode();
    }

    private void setTextInputLayoutError(TextInputLayout textInputLayout, int errorString) {
        textInputLayout.setError(getString(errorString));
    }

    private void setTextInputLayoutErrorNull(TextInputLayout textInputLayout) {
        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
    }

    private boolean areFieldsCorrectlyFilled() {
        return !isStringEmpty(getConfirmationCode()) && isPasswordLegit(getNewPassword()) && areStringEquals(getNewPassword(), getConfirmNePassword());
    }

    private void setViewEnabled(View view, boolean enable) {
        view.setEnabled(enable);
    }

    private void showToastOnUiThread(int stringId) {
        enterNewPasswordActivity.runOnUiThread(() ->
                Toast.makeText(enterNewPasswordActivity, getString(stringId), Toast.LENGTH_LONG).show());
    }

    private void showResettingPasswordDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        alertDialogBuilder.setView(getLayoutInflater().inflate(getAlertDialogLayout(), null));
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @NotNull
    @Contract(" -> new")
    private AlertDialog.Builder createAlertDialogBuilder() {
        return new AlertDialog.Builder(enterNewPasswordActivity);
    }

    @NotNull
    private LayoutInflater getLayoutInflater() {
        return enterNewPasswordActivity.getLayoutInflater();
    }

    private int getAlertDialogLayout() {
        return R.layout.dialog_wait_reset_password;
    }

    private void dismissDialog() {
        alertDialog.dismiss();
    }

    private boolean areStringEquals(String first, String second) {
        return first.equals(second);
    }

    private boolean isStringEmpty(String string) {
        return string.isEmpty();
    }

    private boolean isPasswordLegit(String password) {
        return password.length() >= 8 && Pattern.compile("[0-9]").matcher(password).find();
    }

    public TextInputLayout getTextInputLayoutConfirmationCode() {
        return enterNewPasswordActivity.getTextInputLayoutConfirmationCode();
    }

    private EditText getEditTextConfirmationCode() {
        return enterNewPasswordActivity.getEditTextConfirmationCode();
    }


    public TextInputLayout getTextInputLayoutNewPassword() {
        return enterNewPasswordActivity.getTextInputLayoutNewPassword();
    }

    private EditText getEditTextNewPassword() {
        return enterNewPasswordActivity.getEditTextNewPassword();
    }

    public TextInputLayout getTextInputLayoutConfirmNewPassword() {
        return enterNewPasswordActivity.getTextInputLayoutConfirmNewPassword();
    }

    private EditText getEditTextConfirmNewPassword() {
        return enterNewPasswordActivity.getEditTextConfirmNewPassword();
    }

    public Button getButtonResetPassword() {
        return enterNewPasswordActivity.getButtonResetPassword();
    }

    private String getEmail() {
        return getIntent().getStringExtra(Constants.getEmail());
    }

    private String getConfirmationCode() {
        return getEditTextConfirmationCode().getText().toString();
    }

    private String getNewPassword() {
        return getEditTextNewPassword().getText().toString();
    }

    private String getConfirmNePassword() {
        return getEditTextConfirmNewPassword().getText().toString();
    }

    private Context getContext() {
        return enterNewPasswordActivity.getApplicationContext();
    }

    private Intent getIntent() {
        return enterNewPasswordActivity.getIntent();
    }

    private Resources getResources() {
        return enterNewPasswordActivity.getResources();
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

}
