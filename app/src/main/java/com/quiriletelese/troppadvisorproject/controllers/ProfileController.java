package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.views.ProfileFragment;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class ProfileController implements View.OnClickListener {
    private ProfileFragment profileFragment;
    private AccountDAO accountDAO;
    private DAOFactory daoFactory;

    public ProfileController(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_action_button_edit_profile_fragment:
                showPasswordDialog();
                enablePasswordField();
                /*floatingActionButton = profileFragment.getFloatingActionButton();
                floatingActionButton.setImageResource(R.drawable.icon_map_white);*/
                // inserisce nuova password
                // conferma
                doUpdatePassword();
                break;
        }
    }

    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(profileFragment.getActivity());
        LayoutInflater layoutInflater = profileFragment.requireActivity().getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.dialog_request_password, null);
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
                EditText editTextPasswordInsertedInDialog = dialog.findViewById(R.id.text_input_edit_text_password_modify_profile);
                String actualPassword = profileFragment.getEditTextPassword().getText().toString();
                String passwordInsetedInDialog = editTextPasswordInsertedInDialog.getText().toString();
                if (actualPassword.equals(passwordInsetedInDialog)) {
                    dialog.dismiss();
                    showInsertNewPasswordDialog();
                } else {
                    Toast.makeText(profileFragment.getContext(), "Le password non coincidono", Toast.LENGTH_SHORT).show();
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
        setOnClickListenerDialogInsertNewPasswordNegativeButton(dialog);
    }

    private void setOnClickListenerDialogInsertNewPasswordPositiveButton(final AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TextInputLayout textInputLayoutPassword = dialogView.findViewById(R.id.text_input_edit_text_insert_password);
                TextInputLayout textInputLayoutRepeatPassword = dialogView.findViewById(R.id.text_input_layout_repeat_password);
                if (areEquals(Objects.requireNonNull(textInputLayoutPassword.getEditText()).getText().toString(), Objects.requireNonNull(textInputLayoutRepeatPassword.getEditText()).getText().toString())) {
                    dialog.dismiss();
                    Toast.makeText(profileFragment.getActivity(), "Implementare modifica password", Toast.LENGTH_SHORT).show();
                } else {
                    textInputLayoutPassword.getEditText().setError("Le password non coincidono");
                    textInputLayoutRepeatPassword.getEditText().setError("Le password non coincidono");
                }*/

                // Alternativa:
                EditText editTextNewPassword = dialog.findViewById(R.id.text_input_edit_text_insert_password);
                EditText editTextRepeatNewPassword = dialog.findViewById(R.id.text_input_edit_text_repeat_password);
                if (editTextNewPassword.getText().toString().equals(editTextRepeatNewPassword.getText().toString())) {
                    dialog.dismiss();
                    Toast.makeText(profileFragment.getContext(), "Implementare modifica password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(profileFragment.getContext(), "Le password non coincidono", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*private boolean areEquals(String newPassword, String newPasswordRepeated) {
        return newPassword.equals(newPasswordRepeated);
    }*/

    private void setOnClickListenerDialogInsertNewPasswordNegativeButton(final AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void setListenersOnProfileFragment() {
        profileFragment.getFloatingActionButton().setOnClickListener(this);
    }

    private void enablePasswordField() {
        profileFragment.getEditTextPassword().setEnabled(true);
    }

    private void doUpdatePassword() {
        /*daoFactory = DAOFactory.getInstance();
        accountDAO = daoFactory.getAccountDAO(ConfigFileReader.getProperty("account_storage_technology",
                profileFragment.requireActivity().getApplicationContext()));
        if (accountDAO.updatePassword(/*argomenti qui) {
            Toast.makeText(profileFragment.requireActivity().getApplicationContext(), "PAssowrd mod", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(profileFragment.requireActivity().getApplicationContext(), "PAssowrd NON mod", Toast.LENGTH_LONG).show();
        }*/
    }
}
