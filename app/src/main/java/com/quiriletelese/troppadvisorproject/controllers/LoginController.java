package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthResult;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.SignUpActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallbackLogin;

import org.jetbrains.annotations.NotNull;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class LoginController implements View.OnClickListener {

    private LoginActivity loginActivity;
    private AccountDAO accountDAO;
    private DAOFactory daoFactory;

    public LoginController(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    private static boolean areEmpty(@NotNull String... strings) {
        for (String string : strings)
            if (string.equals(""))
                return true;
        return false;
    }

    @Override
    public void onClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.button_login_login_activity:
                login();
                break;
            case R.id.text_view_sign_in_login_activity:
                showSignUpActivity();
                break;
        }
    }

    public void setListenersOnLoginActivity() {
        loginActivity.getButtonLogin().setOnClickListener(this);
        loginActivity.getTextViewSignIn().setOnClickListener(this);
    }

    public void login() {
        String username = loginActivity.getEditTextEmail().getText().toString();
        String password = loginActivity.getEditTextPassword().getText().toString();
        if (!areEmpty(username, password)) {
            Account account = createAccountForLogin(username, password);
            daoFactory = DAOFactory.getInstance();
            accountDAO = daoFactory.getAccountDAO(ConfigFileReader.getProperty("account_storage_technology",
                    loginActivity.getApplicationContext()));
            loginHelper(account);
        } else
            Toast.makeText(loginActivity.getApplicationContext(), "Riempi tutti i campi", Toast.LENGTH_SHORT).show();
    }

    private void loginHelper(Account account) {
        accountDAO.login(new VolleyCallbackLogin() {
            @Override
            public void onSuccess(InitiateAuthResult initiateAuthResult) {
                System.out.println(initiateAuthResult.toString());
                Toast.makeText(loginActivity, "Login success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(loginActivity, "Login error code: " + error, Toast.LENGTH_SHORT).show();
            }
        }, account, loginActivity.getApplicationContext());
    }

    public void showSignUpActivity() {
        Intent intent = new Intent(loginActivity.getApplicationContext(), SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginActivity.getApplicationContext().startActivity(intent);
    }

    private Account createAccountForLogin(String username, String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        return account;
    }
}
