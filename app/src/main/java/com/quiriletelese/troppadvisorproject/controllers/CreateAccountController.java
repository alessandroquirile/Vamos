package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
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
public class CreateAccountController implements View.OnClickListener {

    private SignUpActivity signUpActivity;
    private AccountDAO accountDAO;
    private DAOFactory daoFactory;

    public CreateAccountController(SignUpActivity signUpActivity) {
        this.signUpActivity = signUpActivity;
    }

    public static boolean isValid(String email) {
        String emailRegExp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPattern = Pattern.compile(emailRegExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email);
        return matcher.find();
    }

    public static boolean areEmpty(String... strings) {
        for (String string : strings)
            if (string.equals(""))
                return true;
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_up_sign_up_activity:
                doCreate();
                break;
            case R.id.floating_action_button_go_back_sign_up_activity:
                showLoginActivity();
                break;
        }
    }

    public void setListenersOnSignUpActivity() {
        signUpActivity.getButtonSignUp().setOnClickListener(this);
        signUpActivity.getFloatingActionButtonGoBack().setOnClickListener(this);
    }

    public void showLoginActivity() {
        Intent intent = new Intent(signUpActivity.getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        signUpActivity.getApplicationContext().startActivity(intent);
    }

    public void doCreate() {
        String email = signUpActivity.getEditTextEmail().getText().toString();
        String name = signUpActivity.getEditTextName().getText().toString();
        String lastName = signUpActivity.getEditTextLastName().getText().toString();
        String username = signUpActivity.getEditTextUsername().getText().toString();
        String password = signUpActivity.getEditTextPassword().getText().toString();
        String repeatPassword = signUpActivity.getEditTextRepeatPassword().getText().toString();
        if (!areEmpty(email, name, lastName, username, password, repeatPassword)) {
            if (isValid(email)) {
                if (password.equals(repeatPassword)) {
                    daoFactory = DAOFactory.getInstance();
                    accountDAO = daoFactory.getAccountDAO(ConfigFileReader.getProperty("account_storage_technology",
                            signUpActivity.getApplicationContext()));
                    //Account account = new Account(name, lastName, username, email, password);
                    /*if (!accountDAO.createAccount(account, signUpActivity.getApplicationContext()))
                        Toast.makeText(signUpActivity.getApplicationContext(), "Account non creato", Toast.LENGTH_LONG).show();*/
                } else
                    Toast.makeText(signUpActivity.getApplicationContext(), "Le password non coincidono", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(signUpActivity.getApplicationContext(), "La sintassi della mail non è corretta", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(signUpActivity.getApplicationContext(), "Riempi tutti i campi", Toast.LENGTH_SHORT).show();
    }

    private void showConfirmAccountDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(signUpActivity.getApplicationContext());
        LayoutInflater layoutInflater = signUpActivity.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.dialog_confirm_account, null);
        alertDialogBuilder.setView(dialogView).
                setPositiveButton("ok", null);
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }
}