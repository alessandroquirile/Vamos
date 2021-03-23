package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.SignUpActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class SignUpActivityController implements View.OnClickListener, DialogInterface.OnDismissListener,
        TextWatcher {

    private final SignUpActivity signUpActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    //private String email, name, lastName, username, password, repeatPassword;
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

    private void onClickHelper(@NotNull View view) {
        switch (view.getId()) {
            case R.id.button_sign_up:
                createAccount();
                break;
            case R.id.floating_action_button_go_back_sign_up_activity:
                onBackPressed();
                break;
            case R.id.floating_action_button_help_sign_up_activity:
                showHelpDialog();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        detectEditText(charSequence);
        setButtonSignUpEnabled(areFieldsCorrectlyInserted());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void createAccountHelper(VolleyCallBack volleyCallBack) {
        getAccountDAO().createAccount(volleyCallBack, createAccountForSignUp(), getContext());
    }

    public void createAccount() {
        showWaitForSignUpResultDialog();
        createAccountHelper(new VolleyCallBack() {
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

    public void setListenerOnViewComponents() {
        getButtonSignUp().setOnClickListener(this);
        getFloatingActionButtonGoBack().setOnClickListener(this);
        getFloatingActionButtonHelp().setOnClickListener(this);
        getTextInputLayoutEmailEditText().addTextChangedListener(this);
        getTextInputLayoutNameEditText().addTextChangedListener(this);
        getTextInputLayoutLastNameEditText().addTextChangedListener(this);
        getTextInputLayoutUsernameEditText().addTextChangedListener(this);
        getTextInputLayoutPasswordEditText().addTextChangedListener(this);
        getTextInputLayoutRepeatPasswordEditText().addTextChangedListener(this);
    }

//    private void createAccount() {
//        setAccountInformation();
//        if (areAllFieldsCorrect())
//            createAccountHelper();
//    }

//    private void setAccountInformation() {
//        email = getTextInputLayoutEmailValue();
//        name = getTextInputLayoutNameValue();
//        lastName = getTextInputLayoutLastNameValue();
//        username = getTextInputLayoutUsernameValue();
//        password = getTextInputLayoutPasswordValue();
//        repeatPassword = getTextInputLayoutRepeatPasswordValue();
//    }

    @NotNull
    private Account createAccountForSignUp() {
        Account account = new Account();
        account.setEmail(getTextInputLayoutEmailValue());
        account.setName(getTextInputLayoutNameValue());
        account.setFamilyName(getTextInputLayoutLastNameValue());
        account.setUsername(getTextInputLayoutUsernameValue());
        account.setPassword(getTextInputLayoutPasswordValue());
        return account;
    }

    private void volleyCallbackOnSuccess() {
        dismissWaitForSignUpResultDialog();
        showConfirmAccountDialog();
    }

    private void volleyCallbackOnError(@NotNull String errorCode) {
        dismissWaitForSignUpResultDialog();
        switch (errorCode) {
            case "Username error":
                showUsernameAlreadyExistError();
                break;
            case "Email error":
                showEmailAlreadyExistError();
                break;
            case "500":
                showToastOnUiThread(R.string.unexpected_error_during_sign_up);
                break;
        }
    }

    private void detectEditText(CharSequence charSequence) {
        if (isEditTextEmailChanged(charSequence))
            handleEditTextEmailChanged(charSequence);
        else if (isEditTextNameChanged(charSequence))
            handleEditTextNameChanged(charSequence);
        else if (isEditTextLastNameChanged(charSequence))
            handleEditTextLastNameChanged(charSequence);
        else if (isEditTextUsernameChanged(charSequence))
            handleEditTextUsernameChanged(charSequence);
        else if (isEditTextPasswordChanged(charSequence))
            handleEditTextPasswordChanged(charSequence);
        else
            handleEditTextRepeatPasswordChanged();
    }

    private void handleEditTextEmailChanged(CharSequence charSequence) {
        if (!isEmailValid(charSequence.toString()))
            setTextInputLayoutError(getTextInputLayoutEmail(), R.string.email_pattern_error);
        else
            setTextInputLayoutErrorNull(getTextInputLayoutEmail());
    }

    private void handleEditTextNameChanged(CharSequence charSequence) {
        if (isNameEmpty(charSequence.toString()))
            setTextInputLayoutError(getTextInputLayoutName(), R.string.field_cannot_be_empty);
        else
            setTextInputLayoutErrorNull(getTextInputLayoutName());
    }

    private void handleEditTextLastNameChanged(CharSequence charSequence) {
        if (isLastNameEmpty(charSequence.toString()))
            setTextInputLayoutError(getTextInputLayoutLastName(), R.string.field_cannot_be_empty);
        else
            setTextInputLayoutErrorNull(getTextInputLayoutLastName());
    }

    private void handleEditTextUsernameChanged(CharSequence charSequence) {
        if (isUsernameEmpty(charSequence.toString()))
            setTextInputLayoutError(getTextInputLayoutUsername(), R.string.field_cannot_be_empty);
        else
            setTextInputLayoutErrorNull(getTextInputLayoutUsername());
    }

    private void handleEditTextPasswordChanged(CharSequence charSequence) {
        if (!isPasswordLegit(charSequence.toString()))
            setTextInputLayoutError(getTextInputLayoutPassword(), R.string.legit_password_error);
        else
            setTextInputLayoutErrorNull(getTextInputLayoutPassword());
    }

    private void handleEditTextRepeatPasswordChanged() {
        if (!arePasswordsEquals())
            setTextInputLayoutError(getTextInputLayoutRepeatPassword(), R.string.passwords_not_match);
        else
            setTextInputLayoutErrorNull(getTextInputLayoutRepeatPassword());
    }

    private void setTextInputLayoutError(TextInputLayout textInputLayout, int errorString) {
        textInputLayout.setError(getString(errorString));
    }

    private void setTextInputLayoutErrorNull(TextInputLayout textInputLayout) {
        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
    }

    private void setButtonSignUpEnabled(boolean enabled) {
        getButtonSignUp().setEnabled(enabled);
    }

    private void showConfirmAccountDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        View dialogView = getLayoutInflater().inflate(getAlertDialogLayout(R.layout.dialog_first_500_point_dialog_layout), null);
        alertDialogBuilder.setView(dialogView).
                setPositiveButton("ok", null);
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
        dialog.setOnDismissListener(this);
    }

    private void showHelpDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        View dialogView = getLayoutInflater().inflate(getAlertDialogLayout(R.layout.dialog_help_layout), null);
        alertDialogBuilder.setView(dialogView).
                setPositiveButton("ok", null);
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
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

//    private boolean areAllFieldsCorrect() {
//        return areFieldsCorrectlyInserted()
//    }

//    private boolean areFieldsCorrectlyInserted() {
//        return isEmailCorrectlyInserted() && isNameCorrectlyInserted() && isLastNameCorrectlyInserted()
//                && isUsernameCorrectlyInserted() && isPasswordCorrectlyInserted() && isRepeatPasswordCorrectlyInserted();
//    }

    private boolean areFieldsCorrectlyInserted() {
        return isEmailValid(getTextInputLayoutEmailValue()) && !isNameEmpty(getTextInputLayoutNameValue())
                && !isLastNameEmpty(getTextInputLayoutLastNameValue()) && !isUsernameEmpty(getTextInputLayoutUsernameValue())
                && isPasswordLegit(getTextInputLayoutPasswordValue()) && arePasswordsEquals();
    }

    /*private boolean isEmailCorrectlyInserted() {
        if (!isEmailValid()) {
            setTextInputLayoutError(getTextInputLayoutEmail(), R.string.email_pattern_error);
            return false;
        } else {
            setTextInputLayoutErrorNull(getTextInputLayoutEmail());
            return true;
        }
    }

    private boolean isNameCorrectlyInserted() {
        if (isNameEmpty()) {
            setTextInputLayoutError(getTextInputLayoutName(), R.string.field_cannot_be_empty);
            return false;
        } else {
            setTextInputLayoutErrorNull(getTextInputLayoutName());
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
    }*/

    /*private void showFieldErrorMessage(@NotNull TextInputLayout textInputLayout, String error) {
        textInputLayout.setError(error);
    }*/

    private boolean arePasswordsEquals() {
        return getTextInputLayoutPasswordValue().equals(getTextInputLayoutRepeatPasswordValue());
    }

//    private boolean arePasswordsEqualHelper() {
//        return password.equals(repeatPassword);
//    }

//    private boolean isEmailCorrectlyFilled() {
//        if (!isEmailValid()) {
//            getTextInputLayoutEmail().setError(getEmailPatternErrorMessage());
//            return false;
//        } else
//            return true;
//    }

    private void showUsernameAlreadyExistError() {
        getTextInputLayoutUsername().setError(getUsernameAlreadyExistErrorMessage());
    }

    private void showEmailAlreadyExistError() {
        getTextInputLayoutEmail().setError(getEmailAlreadyExistErrorMessage());
    }

    private void finish() {
        signUpActivity.finish();
    }

    private void showToastOnUiThread(int stringId) {
        signUpActivity.runOnUiThread(() -> {
            Toast.makeText(signUpActivity, getString(stringId), Toast.LENGTH_SHORT).show();
        });
    }

    public void onBackPressed() {
        signUpActivity.onBackPressed();
    }

    private Context getContext() {
        return signUpActivity.getApplicationContext();
    }

    private AccountDAO getAccountDAO() {
        return daoFactory.getAccountDAO(getStorageTechnology(Constants.getAccountStorageTechnology()));
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

    public FloatingActionButton getFloatingActionButtonHelp() {
        return signUpActivity.getFloatingActionButtonHelp();
    }

    private TextInputLayout getTextInputLayoutEmail() {
        return signUpActivity.getTextInputLayoutEmail();
    }

    public EditText getTextInputLayoutEmailEditText() {
        return signUpActivity.getTextInputLayoutEmailEditText();
    }

    private TextInputLayout getTextInputLayoutName() {
        return signUpActivity.getTextInputLayoutName();
    }

    public EditText getTextInputLayoutNameEditText() {
        return signUpActivity.getTextInputLayoutNameEditText();
    }

    private TextInputLayout getTextInputLayoutLastName() {
        return signUpActivity.getTextInputLayoutLastName();
    }

    public EditText getTextInputLayoutLastNameEditText() {
        return signUpActivity.getTextInputLayoutLastNameEditText();
    }

    private TextInputLayout getTextInputLayoutUsername() {
        return signUpActivity.getTextInputLayoutUsername();
    }

    public EditText getTextInputLayoutUsernameEditText() {
        return signUpActivity.getTextInputLayoutUsernameEditText();
    }

    private TextInputLayout getTextInputLayoutPassword() {
        return signUpActivity.getTextInputLayoutPassword();
    }

    public EditText getTextInputLayoutPasswordEditText() {
        return signUpActivity.getTextInputLayoutPasswordEditText();
    }

    private TextInputLayout getTextInputLayoutRepeatPassword() {
        return signUpActivity.getTextInputLayoutRepeatPassword();
    }

    public EditText getTextInputLayoutRepeatPasswordEditText() {
        return signUpActivity.getTextInputLayoutRepeatPasswordEditText();
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

    private String getTextInputLayoutPasswordValue() {
        return signUpActivity.getTextInputLayoutPasswordValue();
    }

    private String getTextInputLayoutRepeatPasswordValue() {
        return signUpActivity.getTextInputLayoutRepeatPasswordValue();
    }

//    private boolean isEmailEmpty() {
//        return email.isEmpty();
//    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isNameEmpty(String name) {
        return name.trim().isEmpty();
    }

    private boolean isLastNameEmpty(String lastName) {
        return lastName.trim().isEmpty();
    }

    private boolean isUsernameEmpty(String username) {
        return username.trim().isEmpty();
    }

    private boolean isPasswordLegit(String password) {
        return password.length() >= 8 && Pattern.compile("[0-9]").matcher(password).find();
    }

//    private boolean isPasswordEmpty() {
//        return password.isEmpty();
//    }
//
//    private boolean isRepeatPasswordEmpty() {
//        return repeatPassword.isEmpty();
//    }

    private boolean isEditTextEmailChanged(CharSequence charSequence) {
        return charSequence.hashCode() == getTextInputLayoutEmailEditText().getText().hashCode();
    }

    private boolean isEditTextNameChanged(CharSequence charSequence) {
        return charSequence.hashCode() == getTextInputLayoutNameEditText().getText().hashCode();
    }

    private boolean isEditTextLastNameChanged(CharSequence charSequence) {
        return charSequence.hashCode() == getTextInputLayoutLastNameEditText().getText().hashCode();
    }

    private boolean isEditTextUsernameChanged(CharSequence charSequence) {
        return charSequence.hashCode() == getTextInputLayoutUsernameEditText().getText().hashCode();
    }

    private boolean isEditTextPasswordChanged(CharSequence charSequence) {
        return charSequence.hashCode() == getTextInputLayoutPasswordEditText().getText().hashCode();
    }

    private boolean isEditTextRepeatPasswordChanged(CharSequence charSequence) {
        return charSequence.hashCode() == getTextInputLayoutRepeatPasswordEditText().getText().hashCode();
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
        return getString(R.string.username_already_exist);
    }

    private Resources getResources() {
        return signUpActivity.getResources();
    }

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }
}