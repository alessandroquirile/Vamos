package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.ProfileFragment;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class ProfileController implements View.OnClickListener {
    private ProfileFragment profileFragment;
    private Account account;
    private AccountDAO accountDAO;
    private DAOFactory daoFactory;

    public ProfileController(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
        /*account = new Account(profileFragment.getEditTextName().getText().toString(),
                profileFragment.getEditTextLastName().getText().toString(),
                profileFragment.getEditTextUsername().getText().toString(),
                profileFragment.getEditTextEmail().getText().toString(),
                profileFragment.getEditTextPassword().getText().toString());*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_action_button_edit_profile_fragment:
                showPasswordDialog();
                break;
        }
    }

    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(profileFragment.getActivity());
        LayoutInflater layoutInflater = profileFragment.requireActivity().getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.dialog_verify_password, null);
        builder.setView(dialogView)
                .setPositiveButton(R.string.verify, null);
        AlertDialog dialog = builder.create();
        dialog.show();
        setOnClickListenerDialogInsertPassword(dialog);
    }

    private void setOnClickListenerDialogInsertPassword(final AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText textInputEditTextDialogVerifyPassword = dialog.findViewById(R.id.text_input_edit_text_password_modify_profile);
                String dialogPassword = textInputEditTextDialogVerifyPassword.getText().toString();
                if (dialogPassword.equals("pass")) {
                    dialog.dismiss();
                    showInsertNewPasswordDialog();
                } else {
                    textInputEditTextDialogVerifyPassword.setError("Password errata", null);
                    //Toast.makeText(profileFragment.getContext(), "Le password non coincidono", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showInsertNewPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(profileFragment.getActivity());
        LayoutInflater layoutInflater = profileFragment.requireActivity().getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.dialog_insert_new_password, null);
        builder.setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(R.string.verify, null)
                .setNegativeButton(R.string.cancel, null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        setOnClickListenerDialogInsertNewPasswordPositiveButton(dialog);
    }

    private void setOnClickListenerDialogInsertNewPasswordPositiveButton(final AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText textInputEditTextNewPassword = dialog.findViewById(R.id.text_input_edit_text_insert_password);
                TextInputEditText textInputEditTextRepeatNewPassword = dialog.findViewById(R.id.text_input_edit_text_repeat_password);
                if (textInputEditTextNewPassword.getText().toString().equals(textInputEditTextRepeatNewPassword.getText().toString())) {
                    dialog.dismiss();
                    Toast.makeText(profileFragment.getContext(), "Implementare modifica password", Toast.LENGTH_SHORT).show();
                    doUpdatePassword(textInputEditTextNewPassword.getText().toString());
                } else {
                    textInputEditTextNewPassword.setError("Le password non coincidono", null);
                    textInputEditTextRepeatNewPassword.setError("Le password non coincidono", null);
                }
            }
        });
    }

    public void setListenersOnProfileFragment() {
        profileFragment.getFloatingActionButton().setOnClickListener(this);
    }

    private void doUpdatePassword(String newPassword) {
        daoFactory = DAOFactory.getInstance();
        accountDAO = daoFactory.getAccountDAO(ConfigFileReader.getProperty("account_storage_technology",
                profileFragment.requireActivity().getApplicationContext()));
        if (accountDAO.updatePassword(account, profileFragment.getContext(), newPassword)) {
            Toast.makeText(profileFragment.requireActivity().getApplicationContext(), "Password aggiornata", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(profileFragment.requireActivity().getApplicationContext(), "Errore, password non aggiornata, riprova", Toast.LENGTH_LONG).show();
        }
    }
}