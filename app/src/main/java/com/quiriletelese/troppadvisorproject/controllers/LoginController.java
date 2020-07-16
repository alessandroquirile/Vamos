package com.quiriletelese.troppadvisorproject.controllers;

import android.view.View;
import android.widget.Toast;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class LoginController implements View.OnClickListener { ;
    private LoginActivity loginActivity;
    private AccountDAO accountDAO;
    private DAOFactory daoFactory;

    public LoginController(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    public void setListeners() {
        loginActivity.getButtonLogin().setOnClickListener(this);
    }

    public void doLogin(String email, String password) {
        Account account = new Account(email, password);
        daoFactory = DAOFactory.getInstance();
        accountDAO = daoFactory.getAccountDAO("cognito");
        System.out.println(account.toString());
        if (accountDAO.authenticate(account))
            Toast.makeText(loginActivity.getApplicationContext(), "Authenticated", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(loginActivity.getApplicationContext(), "Authenticated (exp) " + account.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login_login_activity:
                doLogin(loginActivity.getEditTextEmail().getText().toString(), loginActivity.getEditTextPassword().getText().toString());
                break;
        }
    }
}
