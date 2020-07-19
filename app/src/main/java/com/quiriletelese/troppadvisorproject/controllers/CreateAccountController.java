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

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class CreateAccountController implements View.OnClickListener {

    private SignUpActivity signUpActivity;
    private AccountDAO accountDAO;
    private DAOFactory daoFactory;

    public CreateAccountController(SignUpActivity signUpActivity) {
        this.signUpActivity = signUpActivity;
    }

    public void setListenersOnSignUpActivity() {
        signUpActivity.getButtonSignUp().setOnClickListener(this);
        signUpActivity.getFloatingActionButtonGoBack().setOnClickListener(this);
    }

    public void showLoginActivity() {
        Intent intent = new Intent(signUpActivity.getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // necessario per fare lo start di un'activity da una classe che non estende Activity
        signUpActivity.getApplicationContext().startActivity(intent);
    }

    public void doUpdate() {
        String email = signUpActivity.getEditTextEmail().getText().toString();
        String name = signUpActivity.getEditTextName().getText().toString();
        String lastName = signUpActivity.getEditTextLastName().getText().toString();
        String nickname = signUpActivity.getEditTextNickname().getText().toString();
        String password = signUpActivity.getEditTextPassword().getText().toString();
        String repeatPassword = signUpActivity.getEditTextRepeatPassword().toString();

        if (!areEmpty(email, name, lastName, nickname, password, repeatPassword)) {
            daoFactory = DAOFactory.getInstance();
            accountDAO = daoFactory.getAccountDAO(ConfigFileReader.getProperty("account_storage_technology",
                    signUpActivity.getApplicationContext()));
            // Va verificato se il nickname è già disponibile?
            if (/*accountDAO.isNicknameAvailable(nickname)*/ true) {
                Account account = new Account(name, lastName, nickname, email, password);
                //Toast.makeText(signUpActivity.getApplicationContext(), account.toString(), Toast.LENGTH_LONG).show();
                if (!accountDAO.create(account, signUpActivity.getApplicationContext())) {
                    Toast.makeText(signUpActivity.getApplicationContext(), "Account non creato", Toast.LENGTH_LONG).show();
                }/*else {
                    // TODO: Mostrare il dialog per inserire il codice di conferma ricevuto per email.
                    Toast.makeText(signUpActivity.getApplicationContext(), "Creato", Toast.LENGTH_LONG).show();
                }*/
            } /*else {
                Toast.makeText(signUpActivity.getApplicationContext(), "Il nickname " + nickname +
                        " è già occupato", Toast.LENGTH_LONG).show();
            }*/
        } else {
            Toast.makeText(signUpActivity.getApplicationContext(), "Riempi tutti i campi", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean areEmpty(String... strings) {
        for (String string : strings) {
            if (TextUtils.isEmpty(string) || string.contains(" "))
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_up_sign_up_activity:
                doUpdate();
                break;
            case R.id.floating_action_button_go_back_sign_up_activity:
                showLoginActivity();
                break;
        }
    }
}