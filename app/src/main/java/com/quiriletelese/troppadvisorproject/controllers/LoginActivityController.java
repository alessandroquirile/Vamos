package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.cognitoidentityprovider.model.AttributeType;
import com.amazonaws.services.cognitoidentityprovider.model.GetUserResult;
import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthResult;
import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.util_interfaces.Constants;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.SignUpActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class LoginActivityController implements View.OnClickListener, Constants {

    private final LoginActivity loginActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private String key, password;
    private final UserSharedPreferences userSharedPreferences;
    private AlertDialog alertDialogWaitForLoginResult;
    private View dialogView;
    private AccountDAO accountDAO;
    private Account account;

    public LoginActivityController(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        this.userSharedPreferences = new UserSharedPreferences(getContext());
    }

    @Override
    public void onClick(@NotNull View view) {
        onClickHelper(view);
    }

    private void loginHelper(VolleyCallBack volleyCallBack) {
        getAccountDAO().login(volleyCallBack, createAccountForLogin(), getContext());
    }

    private void getUserDetailsHelper(VolleyCallBack volleyCallBack) {
        getAccountDAO().getUserDetails(volleyCallBack, getAccessToken(), getContext());
    }

    private void login() {
        loginHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackLoginOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        });
    }

    private void getUserDetails() {
        getUserDetailsHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackGetUserDetailsOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        });
    }

    private void volleyCallbackLoginOnSuccess(Object object) {
        writeLoginSharedPreferences((InitiateAuthResult) object);
        setAlertDialogTextViewText(getString(R.string.retrieving_user_details));
        getUserDetails();
    }

    private void volleyCallbackGetUserDetailsOnSuccess(Object object) {
        writeUserDetailsSharedPreferences((GetUserResult) object);
        dismissWaitForLoginResultDialog();
        finish();
    }

    private void volleyCallbackOnError(@NotNull String errorCode) {
        switch (errorCode) {
            case INTERNAL_ERROR_SERVER:
                clearSharedPreferences();
                handle500VolleyError();
                dismissWaitForLoginResultDialog();
                break;
        }
    }

    private void handle500VolleyError() {
        showToastOnUiThread(R.string.login_error);
    }

    private void showToastOnUiThread(int stringId) {
        loginActivity.runOnUiThread(() ->
                Toast.makeText(loginActivity, getString(stringId), Toast.LENGTH_LONG).show());
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                checkUserInformations();
                break;
            case R.id.text_view_sign_in:
                startSignUpActivity();
                break;
            case R.id.text_view_cancel_login:
                finish();
                break;
        }
    }

    public void setListenerOnViewComponents() {
        getButtonLogin().setOnClickListener(this);
        getTextViewSignIn().setOnClickListener(this);
        getTextViewCancelLogin().setOnClickListener(this);
    }

    private void setUserInformations() {
        key = getTextInputLayoutKeyValue();
        password = getTextInputLayoutPasswordValue();
    }

    private void checkUserInformations() {
        setUserInformations();
        if (areFieldsCorrectlyInserted()) {
            showWaitForLoginResultDialog();
            login();
        }
    }

    public void startSignUpActivity() {
        Intent intentSignUpActivity = createSignUpActivityIntent();
        getContext().startActivity(intentSignUpActivity);
    }

    @NotNull
    private Intent createSignUpActivityIntent() {
        Intent intentSignUpActivity = new Intent(getContext(), SignUpActivity.class);
        intentSignUpActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentSignUpActivity;
    }

    private void finish() {
        loginActivity.finish();
    }

    @NotNull
    private Account createAccountForLogin() {
        account = new Account();
        account.setUsername(key);
        account.setPassword(password);
        return account;
    }

    private boolean areFieldsCorrectlyInserted() {
        return isKeyCorrectlyInserted() && isPasswordCorrectlyInserted();
    }

    private boolean isKeyCorrectlyInserted() {
        if (isKeyEmpty()) {
            showFieldErrorMessage(getTextInputLayoutKey(), getFieldCannotBeEmptyErrorMessage());
            return false;
        } else {
            showFieldErrorMessage(getTextInputLayoutKey(), null);
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

    private void showFieldErrorMessage(@NotNull TextInputLayout textInputLayout, String error) {
        textInputLayout.setError(error);
    }

    private void writeLoginSharedPreferences(InitiateAuthResult initiateAuthResult) {
        userSharedPreferences.putStringSharedPreferences(ACCESS_TOKEN, getAccessToken(initiateAuthResult));
        userSharedPreferences.putStringSharedPreferences(ID_TOKEN, getIdToken(initiateAuthResult));
        userSharedPreferences.putStringSharedPreferences(REFRESH_TOKEN, getRefreshToken(initiateAuthResult));
    }

    private void writeUserDetailsSharedPreferences(GetUserResult getUserResult) {
        userSharedPreferences.putStringSharedPreferences(USERNAME, getUserName(getUserResult));
        userSharedPreferences.putStringSharedPreferences(USER_FIRST_NAME, getName(getUserResult.getUserAttributes()));
        userSharedPreferences.putStringSharedPreferences(FAMILY_NAME, getFamilyName(getUserResult.getUserAttributes()));
        userSharedPreferences.putStringSharedPreferences(EMAIL, getEmail(getUserResult.getUserAttributes()));
    }

    private void clearSharedPreferences() {
        userSharedPreferences.putStringSharedPreferences(ACCESS_TOKEN, "");
        userSharedPreferences.putStringSharedPreferences(ID_TOKEN, "");
        userSharedPreferences.putStringSharedPreferences(REFRESH_TOKEN, "");
        userSharedPreferences.putStringSharedPreferences(USERNAME, "");
        userSharedPreferences.putStringSharedPreferences(USER_FIRST_NAME, "");
        userSharedPreferences.putStringSharedPreferences(FAMILY_NAME, "");
        userSharedPreferences.putStringSharedPreferences(EMAIL, "");
    }

    private void showWaitForLoginResultDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        LayoutInflater layoutInflater = getLayoutInflater();
        dialogView = layoutInflater.inflate(getAlertDialogLayout(), null);
        alertDialogBuilder.setView(dialogView);
        alertDialogWaitForLoginResult = alertDialogBuilder.create();
        alertDialogWaitForLoginResult.show();
    }

    @NotNull
    @Contract(" -> new")
    private AlertDialog.Builder createAlertDialogBuilder() {
        return new AlertDialog.Builder(loginActivity);
    }

    private void dismissWaitForLoginResultDialog() {
        alertDialogWaitForLoginResult.dismiss();
    }

    private boolean isKeyEmpty() {
        return key.isEmpty();
    }

    private boolean isPasswordEmpty() {
        return password.isEmpty();
    }

    private AccountDAO getAccountDAO() {
        accountDAO = daoFactory.getAccountDAO(getStorageTechnology(ACCOUNT_STORAGE_TECHNOLOGY));
        return accountDAO;
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private Button getButtonLogin() {
        return loginActivity.getButtonLogin();
    }

    private TextView getTextViewSignIn() {
        return loginActivity.getTextViewSignIn();
    }

    private TextView getTextViewCancelLogin() {
        return loginActivity.getTextViewCancelLogin();
    }

    private TextInputLayout getTextInputLayoutKey() {
        return loginActivity.getTextInputLayoutKey();
    }

    private TextInputLayout getTextInputLayoutPassword() {
        return loginActivity.getTextInputLayoutPassword();
    }

    private String getTextInputLayoutKeyValue() {
        return loginActivity.getTextInputLayoutKeyValue();
    }

    private String getTextInputLayoutPasswordValue() {
        return loginActivity.getTextInputLayoutPasswordValue();
    }

    @NotNull
    private LayoutInflater getLayoutInflater() {
        return loginActivity.getLayoutInflater();
    }

    private int getAlertDialogLayout() {
        return R.layout.dialog_wait_login_layout;
    }

    private String getAccessToken(@NotNull InitiateAuthResult initiateAuthResult) {
        return initiateAuthResult.getAuthenticationResult().getAccessToken();
    }

    private String getAccessToken() {
        return userSharedPreferences.getStringSharedPreferences(ACCESS_TOKEN);
    }

    private String getIdToken(@NotNull InitiateAuthResult initiateAuthResult) {
        return initiateAuthResult.getAuthenticationResult().getIdToken();
    }

    private String getRefreshToken(@NotNull InitiateAuthResult initiateAuthResult) {
        return initiateAuthResult.getAuthenticationResult().getRefreshToken();
    }

    private String getUserName(@NotNull GetUserResult getUserResult) {
        return getUserResult.getUsername();
    }

    private String getName(@NotNull List<AttributeType> userAttributes) {
        return userAttributes.get(2).getValue();
    }

    private String getFamilyName(@NotNull List<AttributeType> userAttributes) {
        return userAttributes.get(3).getValue();
    }

    private String getEmail(@NotNull List<AttributeType> userAttributes) {
        return userAttributes.get(4).getValue();
    }

    @NotNull
    private String getFieldCannotBeEmptyErrorMessage() {
        return loginActivity.getResources().getString(R.string.field_cannot_be_empty);
    }

    private Context getContext() {
        return loginActivity.getApplicationContext();
    }

    private Resources getResources() {
        return loginActivity.getResources();
    }

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }

    private TextView getAlertDialogWaitForLoginTextView() {
        return dialogView.findViewById(R.id.text_view_wait_for_login_message);
    }

    private void setAlertDialogTextViewText(String value) {
        //alertDialogWaitForLoginResult.setMessage(value);
        getAlertDialogWaitForLoginTextView().setText(value);
    }

}
