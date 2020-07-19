package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.SignUpActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public void doLogin() {
        String email = loginActivity.getEditTextEmail().getText().toString();
        String password = loginActivity.getEditTextPassword().getText().toString();
        if (!areEmpty(email, password)) {
            if (isValid(email)) {
                Account account = new Account(email, password);
                daoFactory = DAOFactory.getInstance();
                accountDAO = daoFactory.getAccountDAO(ConfigFileReader.getProperty("account_storage_technology",
                        loginActivity.getApplicationContext()));
                if (!accountDAO.authenticate(account, loginActivity.getApplicationContext())) {
                    Toast.makeText(loginActivity.getApplicationContext(), "Non è stata possibile l'autenticazione", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(loginActivity.getApplicationContext(), "La sintassi della mail non è corretta", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(loginActivity.getApplicationContext(), "Riempi tutti i campi", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isValid(String email) {
        String emailRegExp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPattern = Pattern.compile(emailRegExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email);
        return matcher.find();
    }

    private boolean areEmpty(String... strings) {
        for (String string : strings) {
            if (TextUtils.isEmpty(string) || string.contains(" "))
                return true;
        }
        return false;
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
                doLogin();
                break;
            case R.id.text_view_sign_in_login_activity:
                showSignUpActivity();
                break;
        }
    }
}
