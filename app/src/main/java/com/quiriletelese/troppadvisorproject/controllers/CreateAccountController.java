package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.SignUpActivity;

import java.io.IOException;

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

    public void checkFields() {
        // Verificare che i campi non siano vuoti, la lunghezza minima e massima, l'uguaglianza delle pwd e gli spazi bianchi che non ci siano.
        // Se è tutto ok:
        daoFactory = DAOFactory.getInstance();
        try {
            accountDAO = daoFactory.getAccountDAO(ConfigFileReader.getProperty("account_storage_technology",
                    signUpActivity.getApplicationContext()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (accountDAO.isEmailAvailable(signUpActivity.getEditTextEmail().getText().toString())) {
            if (accountDAO.isNicknameAvailable(signUpActivity.getEditTextNickname().getText().toString())) {
                Account account = new Account(signUpActivity.getEditTextName().getText().toString(),
                        signUpActivity.getEditTextLastName().getText().toString(),
                        signUpActivity.getEditTextNickname().getText().toString(),
                        signUpActivity.getEditTextEmail().getText().toString(),
                        signUpActivity.getEditTextPassword().getText().toString());
                Toast.makeText(signUpActivity.getApplicationContext(), account.toString(), Toast.LENGTH_LONG).show();
                if (accountDAO.create(account))
                    Toast.makeText(signUpActivity.getApplicationContext(), "Account creato", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(signUpActivity.getApplicationContext(), "Non creato (expected) metodo non ancora implementato", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(signUpActivity.getApplicationContext(), "Il nickname " + signUpActivity.getEditTextNickname().getText().toString() +
                        " è già occupato", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(signUpActivity.getApplicationContext(), "La mail " + signUpActivity.getEditTextEmail().getText().toString() +
                    " è già occupata", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_up_sign_up_activity:
                //Toast.makeText(signUpActivity.getApplicationContext(), "Cliccato sign up", Toast.LENGTH_LONG).show();
                checkFields();
                break;
            case R.id.floating_action_button_go_back_sign_up_activity:
                showLoginActivity();
                break;
        }
    }
}