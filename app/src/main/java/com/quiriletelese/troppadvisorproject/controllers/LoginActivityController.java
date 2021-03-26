package com.quiriletelese.troppadvisorproject.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthResult;
import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.UserDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.ResetPasswordActivity;
import com.quiriletelese.troppadvisorproject.views.SignUpActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class LoginActivityController implements View.OnClickListener, TextWatcher {

    private final LoginActivity loginActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private String key, password;
    private final UserSharedPreferences userSharedPreferences;
    private AlertDialog alertDialogWaitForLoginResult;
    private View dialogView;

    public LoginActivityController(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        this.userSharedPreferences = new UserSharedPreferences(getContext());
    }

    @Override
    public void onClick(@NotNull View view) {
        onClickHelper(view);
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

    private void loginHelper(VolleyCallBack volleyCallBack) {
        getAccountDAO().login(volleyCallBack, createAccountForLogin(), getContext());
    }

    private void findUserByEmailHelper(VolleyCallBack volleyCallBack) {
        getUserDAO().findByEmail(volleyCallBack, getTextInputLayoutKey().getEditText().getText().toString(), getContext());
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

    public void findUserByEmail() {
        findUserByEmailHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallBackFindUserkOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                //showToastOnUiThred(R.string.unexpected_error_while_fetch_data);
            }
        });
    }

    private void volleyCallbackLoginOnSuccess(Object object) {
        writeLoginSharedPreferences((InitiateAuthResult) object);
        findUserByEmail();
        //finish(Activity.RESULT_OK);
    }

    private void volleyCallBackFindUserkOnSuccess(Object object) {
        writeUserSharedPreferences((User) object);
        finish(Activity.RESULT_OK);
    }

    private void volleyCallbackOnError(@NotNull String errorCode) {
        switch (errorCode) {
            case "500":
                handle500VolleyError();
                dismissWaitForLoginResultDialog();
                break;
        }
    }

    private void handle500VolleyError() {
        showToastOnUiThread(R.string.login_error);
    }

    private void onTextChangedHelper(CharSequence charSequence) {
        detectEditText(charSequence);
        setViewEnabled(getButtonLogin(), areFieldsCorrectlyInserted());
    }

    private void detectEditText(CharSequence charSequence) {
        if (isEditTextEmailChanged(charSequence))
            editTextEmailChanged(charSequence);
        else
            editTextPasswordChanged();
    }

    private boolean isEditTextEmailChanged(CharSequence charSequence) {
        return charSequence.hashCode() == getTextInputLayoutKey().getEditText().getText().hashCode();
    }

    private void editTextEmailChanged(CharSequence email) {
        if (!isEmailValid(email))
            setTextInputLayoutError(getTextInputLayoutKey(), R.string.email_pattern_error);
        else
            setTextInputLayoutErrorNull(getTextInputLayoutKey());
    }

    private void editTextPasswordChanged() {
        if (isPasswordEmpty())
            setTextInputLayoutError(getTextInputLayoutPassword(), R.string.field_cannot_be_empty);
        else
            setTextInputLayoutErrorNull(getTextInputLayoutPassword());
    }

    private void setViewEnabled(View view, boolean enable) {
        view.setEnabled(enable);
    }

    private void setTextInputLayoutError(TextInputLayout textInputLayout, int errorString) {
        textInputLayout.setError(getString(errorString));
    }

    private void setTextInputLayoutErrorNull(TextInputLayout textInputLayout) {
        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
    }

    private void showToastOnUiThread(int stringId) {
        loginActivity.runOnUiThread(() ->
                Toast.makeText(loginActivity, getString(stringId), Toast.LENGTH_LONG).show());
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                checkUserInformation();
                break;
            case R.id.text_view_forgot_password:
                startForgotPasswordActivity();
                break;
            case R.id.text_view_sign_in:
                startSignUpActivity();
                break;
            case R.id.text_view_cancel_login:
                finish(Activity.RESULT_CANCELED);
                break;
        }
    }

    public void setListenerOnViewComponents() {
        getButtonLogin().setOnClickListener(this);
        getTextInputLayoutKey().getEditText().addTextChangedListener(this);
        getTextInputLayoutPassword().getEditText().addTextChangedListener(this);
        getTextViewForgotPassword().setOnClickListener(this);
        getTextViewSignIn().setOnClickListener(this);
        getTextViewCancelLogin().setOnClickListener(this);
    }

    private void setUserInformations() {
        key = getTextInputLayoutKeyValue();
        password = getTextInputLayoutPasswordValue();
    }

    private void checkUserInformation() {
        setUserInformations();
        if (areFieldsCorrectlyInserted()) {
            showWaitForLoginResultDialog();
            login();
        }
    }

    public void startForgotPasswordActivity() {
        Intent intentForgotPasswordActivity = createForgotPasswordIntent();
        getContext().startActivity(intentForgotPasswordActivity);
    }

    public void startSignUpActivity() {
        Intent intentSignUpActivity = createSignUpActivityIntent();
        getContext().startActivity(intentSignUpActivity);
    }

    @NotNull
    private Intent createForgotPasswordIntent() {
        Intent intentSignUpActivity = new Intent(getContext(), ResetPasswordActivity.class);
        intentSignUpActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentSignUpActivity;
    }

    @NotNull
    private Intent createSignUpActivityIntent() {
        Intent intentSignUpActivity = new Intent(getContext(), SignUpActivity.class);
        intentSignUpActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentSignUpActivity;
    }

    private void finish(int result) {
        loginActivity.setResult(result, new Intent());
        loginActivity.finish();
    }

    @NotNull
    private Account createAccountForLogin() {
        Account account = new Account();
        account.setUsername(key);
        account.setPassword(password);
        return account;
    }

    private boolean areFieldsCorrectlyInserted() {
        return isKeyCorrectlyInserted() && !isPasswordEmpty();
    }

    private boolean isKeyCorrectlyInserted() {
        return isEmailValid(getTextInputLayoutKeyValue().trim());
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void writeLoginSharedPreferences(InitiateAuthResult initiateAuthResult) {
        userSharedPreferences.putStringSharedPreferences(Constants.getAccessToken(), getAccessToken(initiateAuthResult));
        userSharedPreferences.putStringSharedPreferences(Constants.getIdToken(), getIdToken(initiateAuthResult));
        userSharedPreferences.putStringSharedPreferences(Constants.getRefreshToken(), getRefreshToken(initiateAuthResult));
        userSharedPreferences.putStringSharedPreferences(Constants.getEmail(), getTextInputLayoutKey().getEditText().getText().toString());
    }

    private void writeUserSharedPreferences(User user) {
        userSharedPreferences.putLongSharedPreferences(Constants.getWallet(), user.getWallet());
    }

    private void showWaitForLoginResultDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        dialogView = getLayoutInflater().inflate(getAlertDialogLayout(), null);
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

    private boolean isPasswordEmpty() {
        return getTextInputLayoutPassword().getEditText().getText().toString().trim().isEmpty();
    }

    private AccountDAO getAccountDAO() {
        return daoFactory.getAccountDAO(getStorageTechnology(Constants.getAccountStorageTechnology()));
    }

    private UserDAO getUserDAO() {
        return daoFactory.getUserDAO(getStorageTechnology(Constants.getUserStorageTechnology()));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private Button getButtonLogin() {
        return loginActivity.getButtonLogin();
    }

    public TextView getTextViewForgotPassword() {
        return loginActivity.getTextViewForgotPassword();
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

    private String getIdToken(@NotNull InitiateAuthResult initiateAuthResult) {
        return initiateAuthResult.getAuthenticationResult().getIdToken();
    }

    private String getRefreshToken(@NotNull InitiateAuthResult initiateAuthResult) {
        return initiateAuthResult.getAuthenticationResult().getRefreshToken();
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
}
