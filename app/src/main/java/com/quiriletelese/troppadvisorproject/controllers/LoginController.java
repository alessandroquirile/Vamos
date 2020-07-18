package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.personal_exceptions.TecnologiaNonAncoraSupportataException;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.SignUpActivity;

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

    public void setListenersOnLoginActivity() {
        loginActivity.getButtonLogin().setOnClickListener(this);
        loginActivity.getTextViewSignIn().setOnClickListener(this);
    }

    public void doLogin(String email, String password) {
        Account account = new Account(email, password);
        daoFactory = DAOFactory.getInstance();
        try {
            accountDAO = daoFactory.getAccountDAO(ConfigFileReader.getProperty("account_storage_technology",
                    loginActivity.getApplicationContext()));
        } catch (TecnologiaNonAncoraSupportataException e) {
            e.printStackTrace();
        }
        if (accountDAO.authenticate(account))
            Toast.makeText(loginActivity.getApplicationContext(), "Authenticated", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(loginActivity.getApplicationContext(), "Authenticated (exp) " + account.toString(), Toast.LENGTH_SHORT).show();
    }

    public void showSignUpActivity() {
        Intent intent = new Intent(loginActivity.getApplicationContext(), SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // necessario per fare lo start di un'activity da una classe che non estende Activity
        loginActivity.getApplicationContext().startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login_login_activity:
                doLogin(loginActivity.getEditTextEmail().getText().toString(), loginActivity.getEditTextPassword().getText().toString());
                break;
            case R.id.text_view_sign_in_login_activity:
                showSignUpActivity();
                break;
        }
    }
}
