package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.SignUpActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class CreateAccountController implements View.OnClickListener, DialogInterface.OnDismissListener,
        Constants {

    private SignUpActivity signUpActivity;
    private AccountDAO accountDAO;
    private DAOFactory daoFactory;
    private String email, name, lastName, username, password, repeatPassword;

    public CreateAccountController(SignUpActivity signUpActivity) {
        this.signUpActivity = signUpActivity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_up_sign_up_activity:
                checkUserInformations();
                break;
            case R.id.floating_action_button_go_back_sign_up_activity:
                goBack();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        startLoginActivity();
    }

    public void setListenerOnViewComponents() {
        signUpActivity.getButtonSignUp().setOnClickListener(this);
        signUpActivity.getFloatingActionButtonGoBack().setOnClickListener(this);
    }

    public void goBack() {
        signUpActivity.onBackPressed();
    }

    private void checkUserInformations() {
        getAccountInformation();
        if (areFieldsCorrectlyInserted() && isEmailCorrectlyFilled() && arePasswordsEquals())
            createAccount();
    }

    private void createAccountHelper(VolleyCallBack volleyCallBack, Account account, Context context) {
        daoFactory = DAOFactory.getInstance();
        AccountDAO accountDAO = daoFactory.getAccountDAO(ConfigFileReader.getProperty(ACCOUNT_STORAGE_TECHNOLOGY,
                signUpActivity.getApplicationContext()));
        accountDAO.createAccount(volleyCallBack, account, context);
    }

    public void createAccount() {
        createAccountHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                writeSharedPreferences();
                signUpActivity.runOnUiThread(() -> {
                    showConfirmAccountDialog();
                });
            }

            @Override
            public void onError(String errorCode) {
                checkCreateAccountError(errorCode);
            }
        }, createAccountForSignUp(), signUpActivity.getApplicationContext());
    }

    private void getAccountInformation() {
        email = signUpActivity.getTextInputLayoutEmailValue().trim();
        name = signUpActivity.getTextInputLayoutNameValue().trim();
        lastName = signUpActivity.getTextInputLayoutLastNameValue().trim();
        username = signUpActivity.getTextInputLayoutUsernameValue().trim();
        password = signUpActivity.getTextInputLayoutPasswordValue().trim();
        repeatPassword = signUpActivity.getTextInputLayoutRepeatPasswordValue().trim();
    }

    private Account createAccountForSignUp() {
        Account account = new Account();
        account.setEmail(email);
        account.setName(name);
        account.setLastname(lastName);
        account.setUsername(username);
        account.setPassword(password);
        return account;
    }

    private void showConfirmAccountDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(signUpActivity);
        LayoutInflater layoutInflater = signUpActivity.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.dialog_confirm_account, null);
        alertDialogBuilder.setView(dialogView).
                setPositiveButton("ok", null);
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
        dialog.setOnDismissListener(this);
    }

    private void writeSharedPreferences() {
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(signUpActivity.getApplicationContext());
        userSharedPreferences.putSharedPreferencesString(USERNAME, username);
        userSharedPreferences.putSharedPreferencesString(EMAIL, email);
        userSharedPreferences.putSharedPreferencesString(PASSWORD, password);
    }

    private void checkCreateAccountError(String error) {
        switch (error) {
            case USERNAME_ERROR:
                showUsernameAlreadyExistError();
                break;
            case EMAIL_ERROR:
                showEmailAlreadyExistError();
                break;
            default:
                showToastUnexpectedErrorDuringSignUp();
                break;
        }
    }

    private boolean areFieldsCorrectlyInserted() {
        return isEmailCorrectlyInserted() && isNameCorrectlyInserted() && isLastNameCorrectlyInserted()
                && isUsernameCorrectlyInserted() && isPasswordCorrectlyInserted() && isRepeatPasswordCorrectlyInserted();
    }

    private boolean isEmailCorrectlyInserted() {
        if (isEmailEmpty()) {
            showFieldErrorMessage(signUpActivity.getTextInputLayoutEmail(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(signUpActivity.getTextInputLayoutEmail(), null);
            return true;
        }
    }

    private boolean isNameCorrectlyInserted() {
        if (isNameEmpty()) {
            showFieldErrorMessage(signUpActivity.getTextInputLayoutName(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(signUpActivity.getTextInputLayoutName(), null);
            return true;
        }
    }

    private boolean isLastNameCorrectlyInserted() {
        if (isLastNameEmpty()) {
            showFieldErrorMessage(signUpActivity.getTextInputLayoutLastName(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(signUpActivity.getTextInputLayoutLastName(), null);
            return true;
        }
    }

    private boolean isUsernameCorrectlyInserted() {
        if (isUsernameEmpty()) {
            showFieldErrorMessage(signUpActivity.getTextInputLayoutUsername(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(signUpActivity.getTextInputLayoutUsername(), null);
            return true;
        }
    }

    private boolean isPasswordCorrectlyInserted() {
        if (isPasswordEmpty()) {
            showFieldErrorMessage(signUpActivity.getTextInputLayoutPassword(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(signUpActivity.getTextInputLayoutPassword(), null);
            return true;
        }
    }

    private boolean isRepeatPasswordCorrectlyInserted() {
        if (isRepeatPasswordEmpty()) {
            showFieldErrorMessage(signUpActivity.getTextInputLayoutRepeatPassword(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(signUpActivity.getTextInputLayoutRepeatPassword(), null);
            return true;
        }
    }

    private void showFieldErrorMessage(TextInputLayout textInputLayout, String error) {
        textInputLayout.setError(error);
    }

    private boolean arePasswordsEquals() {
        if (!password.equals(repeatPassword)) {
            signUpActivity.getTextInputLayoutPassword().setError(getPasswordsNotMatchErrorMessage());
            signUpActivity.getTextInputLayoutRepeatPassword().setError(getPasswordsNotMatchErrorMessage());
            return false;
        } else
            return true;
    }

    private boolean isEmailCorrectlyFilled() {
        if (!isEmailValid()) {
            signUpActivity.getTextInputLayoutEmail().setError(getEmailPatternErrorMessage());
            return false;
        } else
            return true;
    }

    private void showUsernameAlreadyExistError() {
        signUpActivity.getTextInputLayoutUsername().setError(getUsernameAlreadyExistErrorMessage());
    }

    private void showEmailAlreadyExistError() {
        signUpActivity.getTextInputLayoutEmail().setError(getEmailAlreadyExistErrorMessage());
    }

    private void startLoginActivity() {
        signUpActivity.startActivity(new Intent(signUpActivity.getApplicationContext(), LoginActivity.class));
    }

    private void showToastUnexpectedErrorDuringSignUp() {
        signUpActivity.runOnUiThread(() -> {
            Toast.makeText(signUpActivity, getUnexpectedErrorDuringSignUpErrorMessage(), Toast.LENGTH_SHORT).show();
        });
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
        return password.isEmpty();
    }

    private boolean isRepeatPasswordEmpty() {
        return repeatPassword.isEmpty();
    }

    private String getFieldCannotBeEmptyErrorMessage() {
        return signUpActivity.getResources().getString(R.string.field_cannot_be_empty);
    }

    private String getPasswordsNotMatchErrorMessage() {
        return signUpActivity.getResources().getString(R.string.passwords_not_match);
    }

    private String getEmailPatternErrorMessage() {
        return signUpActivity.getResources().getString(R.string.email_pattern_error);
    }

    private String getEmailAlreadyExistErrorMessage() {
        return signUpActivity.getResources().getString(R.string.email_already_exist);
    }

    private String getUsernameAlreadyExistErrorMessage() {
        return signUpActivity.getResources().getString(R.string.username_already_exist);
    }

    private String getUnexpectedErrorDuringSignUpErrorMessage() {
        return signUpActivity.getResources().getString(R.string.unexpected_error_during_sign_up);
    }
}