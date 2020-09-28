package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthResult;
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

import org.jetbrains.annotations.NotNull;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class LoginActivityController implements View.OnClickListener, Constants {

    private LoginActivity loginActivity;
    private DAOFactory daoFactory;
    private String key, password;
    private AlertDialog dialog;

    public LoginActivityController(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Override
    public void onClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.button_login:
                checkUserInformations();
                break;
            case R.id.text_view_sign_in:
                showSignUpActivity();
                break;
        }
    }

    public void setListenerOnViewComponents() {
        loginActivity.getButtonLogin().setOnClickListener(this);
        loginActivity.getTextViewSignIn().setOnClickListener(this);
    }

    private void getUserInformation() {
        key = loginActivity.getTextInputLayoutKeyValue().trim();
        password = loginActivity.getTextInputLayoutPasswordValue().trim();
    }

    private void checkUserInformations() {
        getUserInformation();
        if (isFieldsCorrectlyInserted()) {
            showWaitForLoginResultDialog();
            login();
        }
    }

    private void loginHelper(VolleyCallBack volleyCallBack, Account account, Context context) {
        daoFactory = DAOFactory.getInstance();
        AccountDAO accountDAO = daoFactory.getAccountDAO(ConfigFileReader.getProperty(ACCOUNT_STORAGE_TECHNOLOGY,
                loginActivity.getApplicationContext()));
        accountDAO.login(volleyCallBack, account, context);
    }

    private void login() {
        loginHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                dialog.dismiss();
                writeSharedPreferences(object);
            }

            @Override
            public void onError(String errorCode) {
                dialog.dismiss();
                if (errorCode.equals(INTERNAL_ERROR_SERVER))
                    showToastLoginError();
            }
        }, createAccountForLogin(), loginActivity.getApplicationContext());
    }

    public void showSignUpActivity() {
        Intent intent = new Intent(loginActivity.getApplicationContext(), SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginActivity.getApplicationContext().startActivity(intent);
    }

    private Account createAccountForLogin() {
        Account account = new Account();
        account.setUsername(key);
        account.setPassword(password);
        return account;
    }

    private boolean isFieldsCorrectlyInserted() {
         return isKeyCorrectlyInserted() && isPasswordCorrectlyInserted();
    }

    private boolean isKeyCorrectlyInserted() {
        if (isKeyEmpty()) {
            showFieldErrorMessage(loginActivity.getTextInputLayoutKey(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(loginActivity.getTextInputLayoutKey(), null);
            return true;
        }
    }

    private boolean isPasswordCorrectlyInserted() {
        if (isPasswordEmpty()) {
            showFieldErrorMessage(loginActivity.getTextInputLayoutPassword(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(loginActivity.getTextInputLayoutPassword(), null);
            return true;
        }
    }

    private void showFieldErrorMessage(TextInputLayout textInputLayout, String error) {
        textInputLayout.setError(error);
    }

    private void writeSharedPreferences(Object object) {
        InitiateAuthResult initiateAuthResult = (InitiateAuthResult) object;
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(loginActivity.getApplicationContext());
        userSharedPreferences.putSharedPreferencesString(ACCESS_TOKEN, initiateAuthResult.getAuthenticationResult().getAccessToken());
        userSharedPreferences.putSharedPreferencesString(ID_TOKEN, initiateAuthResult.getAuthenticationResult().getIdToken());
    }

    private void showWaitForLoginResultDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(loginActivity);
        LayoutInflater layoutInflater = loginActivity.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.dialog_wait_login, null);
        alertDialogBuilder.setView(dialogView);
        dialog = alertDialogBuilder.create();
        dialog.show();
    }

    private boolean isKeyEmpty() {
        return key.isEmpty();
    }

    private boolean isPasswordEmpty() {
        return password.isEmpty();
    }

    private void showToastLoginError() {
        loginActivity.runOnUiThread(() -> {
            Toast.makeText(loginActivity, getLoginErrorMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private String getFieldCannotBeEmptyErrorMessage() {
        return loginActivity.getResources().getString(R.string.field_cannot_be_empty);
    }

    private String getLoginErrorMessage() {
        return loginActivity.getResources().getString(R.string.login_error);
    }

}
