package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.SignUpActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class SignUpActivityController implements View.OnClickListener, DialogInterface.OnDismissListener,
        Constants {

    private SignUpActivity signUpActivity;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private String email, name, lastName, username;
    private char[] password, repeatPassword;
    private AlertDialog alertDialogWaitForSignUpResult;

    public SignUpActivityController(SignUpActivity signUpActivity) {
        this.signUpActivity = signUpActivity;
    }

    @Override
    public void onClick(@NotNull View view) {
        onClickHelper(view);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }

    private void createAccount(VolleyCallBack volleyCallBack) {
        getAccountDAO().createAccount(volleyCallBack, createAccountForSignUp(), getContext());
    }

    public void createAccountHelper() {
        showWaitForSignUpResultDialog();
        createAccount(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackOnSuccess();
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        });
    }

    private void onClickHelper(View view){
        switch (view.getId()) {
            case R.id.button_sign_up:
                createAccount();
                break;
            case R.id.floating_action_button_go_back_sign_up_activity:
                onBackPressed();
                break;
        }
    }

    public void setListenerOnViewComponents() {
        getButtonSignUp().setOnClickListener(this);
        getFloatingActionButtonGoBack().setOnClickListener(this);
    }

    private void createAccount() {
        getAccountInformation();
        if (isAllFieldsCorret())
            createAccountHelper();
    }

    private void getAccountInformation() {
        email = getTextInputLayoutEmailValue();
        name = getTextInputLayoutNameValue();
        lastName = getTextInputLayoutLastNameValue();
        username = getTextInputLayoutUsernameValue();
        password = getTextInputLayoutPasswordValue();
        repeatPassword = getTextInputLayoutRepeatPasswordValue();
    }

    @NotNull
    private Account createAccountForSignUp() {
        Account account = new Account();
        account.setEmail(email);
        account.setName(name);
        account.setFamilyName(lastName);
        account.setUsername(username);
        account.setPassword(password);
        return account;
    }

    private void volleyCallbackOnSuccess() {
        dismissWaitForSignUpResultDialog();
        showConfirmAccountDialog();
    }

    private void volleyCallbackOnError(@NotNull String errorCode) {
        dismissWaitForSignUpResultDialog();
        switch (errorCode) {
            case USERNAME_ERROR:
                showUsernameAlreadyExistError();
                break;
            case EMAIL_ERROR:
                showEmailAlreadyExistError();
                break;
            case INTERNAL_ERROR_SERVER:
                showToastOnUiThread(R.string.unexpected_error_during_sign_up);
                break;
        }
    }

    private void showConfirmAccountDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        LayoutInflater layoutInflater = getLayoutInflater();
        View dialogView = layoutInflater.inflate(getAlertDialogLayout(R.layout.dialog_confirm_account), null);
        alertDialogBuilder.setView(dialogView).
                setPositiveButton("ok", null);
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
        dialog.setOnDismissListener(this);
    }

    private void showWaitForSignUpResultDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        LayoutInflater layoutInflater = getLayoutInflater();
        View dialogView = layoutInflater.inflate(getAlertDialogLayout(R.layout.dialog_wait_for_account_creation_layout), null);
        alertDialogBuilder.setView(dialogView);
        alertDialogWaitForSignUpResult = alertDialogBuilder.create();
        alertDialogWaitForSignUpResult.show();
    }

    @NotNull
    @Contract(" -> new")
    private AlertDialog.Builder createAlertDialogBuilder() {
        return new AlertDialog.Builder(signUpActivity);
    }

    private void dismissWaitForSignUpResultDialog() {
        alertDialogWaitForSignUpResult.dismiss();
    }

    private boolean isAllFieldsCorret() {
        return isFieldsCorrectlyInserted() && isEmailCorrectlyFilled() && isPasswordsEquals();
    }

    private boolean isFieldsCorrectlyInserted() {
        return isEmailCorrectlyInserted() && isNameCorrectlyInserted() && isLastNameCorrectlyInserted()
                && isUsernameCorrectlyInserted() && isPasswordCorrectlyInserted() && isRepeatPasswordCorrectlyInserted();
    }

    private boolean isEmailCorrectlyInserted() {
        if (isEmailEmpty()) {
            showFieldErrorMessage(getTextInputLayoutEmail(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(getTextInputLayoutEmail(), null);
            return true;
        }
    }

    private boolean isNameCorrectlyInserted() {
        if (isNameEmpty()) {
            showFieldErrorMessage(getTextInputLayoutName(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(getTextInputLayoutName(), null);
            return true;
        }
    }

    private boolean isLastNameCorrectlyInserted() {
        if (isLastNameEmpty()) {
            showFieldErrorMessage(getTextInputLayoutLastName(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(getTextInputLayoutLastName(), null);
            return true;
        }
    }

    private boolean isUsernameCorrectlyInserted() {
        if (isUsernameEmpty()) {
            showFieldErrorMessage(getTextInputLayoutUsername(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(getTextInputLayoutUsername(), null);
            return true;
        }
    }

    private boolean isPasswordCorrectlyInserted() {
        if (isPasswordEmpty()) {
            showFieldErrorMessage(getTextInputLayoutPassword(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(getTextInputLayoutPassword(), null);
            return true;
        }
    }

    private boolean isRepeatPasswordCorrectlyInserted() {
        if (isRepeatPasswordEmpty()) {
            showFieldErrorMessage(getTextInputLayoutRepeatPassword(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(getTextInputLayoutRepeatPassword(), null);
            return true;
        }
    }

    private void showFieldErrorMessage(@NotNull TextInputLayout textInputLayout, String error) {
        textInputLayout.setError(error);
    }

    private boolean isPasswordsEquals() {
        if (!isPasswordsEqualsHelper()) {
            getTextInputLayoutPassword().setError(getPasswordsNotMatchErrorMessage());
            getTextInputLayoutRepeatPassword().setError(getPasswordsNotMatchErrorMessage());
            return false;
        } else
            return true;
    }

    private boolean isPasswordsEqualsHelper() {
        return Arrays.equals(password, repeatPassword);
    }

    private boolean isEmailCorrectlyFilled() {
        if (!isEmailValid()) {
            getTextInputLayoutEmail().setError(getEmailPatternErrorMessage());
            return false;
        } else
            return true;
    }

    private void showUsernameAlreadyExistError() {
        getTextInputLayoutUsername().setError(getUsernameAlreadyExistErrorMessage());
    }

    private void showEmailAlreadyExistError() {
        getTextInputLayoutEmail().setError(getEmailAlreadyExistErrorMessage());
    }

    private void finish(){
        signUpActivity.finish();
    }

    private void showToastOnUiThread(int string) {
        signUpActivity.runOnUiThread(() -> {
            Toast.makeText(signUpActivity, getString(string), Toast.LENGTH_SHORT).show();
        });
    }

    public void onBackPressed() {
        signUpActivity.onBackPressed();
    }

    private Context getContext() {
        return signUpActivity.getApplicationContext();
    }

    private AccountDAO getAccountDAO() {
        return daoFactory.getAccountDAO(getStorageTechnology(ACCOUNT_STORAGE_TECHNOLOGY));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    @NotNull
    private LayoutInflater getLayoutInflater() {
        return signUpActivity.getLayoutInflater();
    }

    private int getAlertDialogLayout(int layout) {
        return layout;
    }

    private Button getButtonSignUp() {
        return signUpActivity.getButtonSignUp();
    }

    private FloatingActionButton getFloatingActionButtonGoBack() {
        return signUpActivity.getFloatingActionButtonGoBack();
    }

    private TextInputLayout getTextInputLayoutEmail() {
        return signUpActivity.getTextInputLayoutEmail();
    }

    private TextInputLayout getTextInputLayoutName() {
        return signUpActivity.getTextInputLayoutName();
    }

    private TextInputLayout getTextInputLayoutLastName() {
        return signUpActivity.getTextInputLayoutLastName();
    }

    private TextInputLayout getTextInputLayoutUsername() {
        return signUpActivity.getTextInputLayoutUsername();
    }

    private TextInputLayout getTextInputLayoutPassword() {
        return signUpActivity.getTextInputLayoutPassword();
    }

    private TextInputLayout getTextInputLayoutRepeatPassword() {
        return signUpActivity.getTextInputLayoutRepeatPassword();
    }

    private String getTextInputLayoutEmailValue() {
        return signUpActivity.getTextInputLayoutEmailValue();
    }

    private String getTextInputLayoutNameValue() {
        return signUpActivity.getTextInputLayoutNameValue();
    }

    private String getTextInputLayoutLastNameValue() {
        return signUpActivity.getTextInputLayoutLastNameValue();
    }

    private String getTextInputLayoutUsernameValue() {
        return signUpActivity.getTextInputLayoutUsernameValue();
    }

    private char[] getTextInputLayoutPasswordValue() {
        return signUpActivity.getTextInputLayoutPasswordValue();
    }

    private char[] getTextInputLayoutRepeatPasswordValue() {
        return signUpActivity.getTextInputLayoutRepeatPasswordValue();
    }

    private boolean isEmailEmpty() {
        return email.isEmpty();
    }

    boolean isEmailValid() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isNameEmpty() {
        return name.isEmpty();
    }

    private boolean isLastNameEmpty() {
        return lastName.isEmpty();
    }

    private boolean isUsernameEmpty() {
        return username.isEmpty();
    }

    private boolean isPasswordEmpty() {
        return password.length == 0;
    }

    private boolean isRepeatPasswordEmpty() {
        return repeatPassword.length == 0;
    }

    @NotNull
    private String getFieldCannotBeEmptyErrorMessage() {
        return signUpActivity.getResources().getString(R.string.field_cannot_be_empty);
    }

    @NotNull
    private String getPasswordsNotMatchErrorMessage() {
        return signUpActivity.getResources().getString(R.string.passwords_not_match);
    }

    @NotNull
    private String getEmailPatternErrorMessage() {
        return signUpActivity.getResources().getString(R.string.email_pattern_error);
    }

    @NotNull
    private String getEmailAlreadyExistErrorMessage() {
        return signUpActivity.getResources().getString(R.string.email_already_exist);
    }

    @NotNull
    private String getUsernameAlreadyExistErrorMessage() {
        return signUpActivity.getResources().getString(R.string.username_already_exist);
    }

    private Resources getResources(){
        return signUpActivity.getResources();
    }

    @NotNull
    private String getString(int string){
        return getResources().getString(string);
    }

}