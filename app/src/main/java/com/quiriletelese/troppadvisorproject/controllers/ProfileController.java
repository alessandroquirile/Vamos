package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.services.cognitoidentityprovider.model.ChangePasswordResult;
import com.google.android.material.textfield.TextInputEditText;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.ChangeUserPassword;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.ProfileFragment;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallbackUpdatePassword;

import java.util.Objects;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class ProfileController implements View.OnClickListener {
    private ProfileFragment profileFragment;
    private TextInputEditText textInputEditTextNewPassword, textInputEditTextRepeatNewPassword;
    private Account account;
    private AccountDAO accountDAO;
    private DAOFactory daoFactory;

    public ProfileController(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_action_button_edit_profile_fragment:
                showRequestPasswordDialog();
                break;
        }
    }

    private void showRequestPasswordDialog() {
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
                String dialogPassword = Objects.requireNonNull(textInputEditTextDialogVerifyPassword.getText()).toString();
                if (dialogPassword.equals(profileFragment.getTextViewPasswordProfile().getText().toString())) {
                    dialog.dismiss();
                    showInsertNewPasswordDialog();
                } else {
                    textInputEditTextDialogVerifyPassword.setError(profileFragment.getResources().getString(R.string.password_error), null);
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
                textInputEditTextNewPassword = dialog.findViewById(R.id.text_input_edit_text_insert_password);
                textInputEditTextRepeatNewPassword = dialog.findViewById(R.id.text_input_edit_text_repeat_password);
                if (Objects.requireNonNull(textInputEditTextNewPassword.getText()).toString()
                        .equals(Objects.requireNonNull(textInputEditTextRepeatNewPassword.getText()).toString())) {
                    dialog.dismiss();
                    updatePassword(createChangeUserPassword());
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

    private void updatePassword(ChangeUserPassword changeUserPassword) {
        daoFactory = DAOFactory.getInstance();
        accountDAO = daoFactory.getAccountDAO(ConfigFileReader.getProperty("account_storage_technology",
                profileFragment.requireActivity().getApplicationContext()));
        accountDAO.updatePassword(new VolleyCallbackUpdatePassword() {
            @Override
            public void onSuccess(ChangePasswordResult changePasswordResult) {
                Toast.makeText(profileFragment.getContext(), R.string.password_updated_correctly, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(profileFragment.getContext(), R.string.password_update_error, Toast.LENGTH_SHORT).show();
            }
        }, changeUserPassword, profileFragment.getContext());
    }

    private ChangeUserPassword createChangeUserPassword() {
        ChangeUserPassword changeUserPassword = new ChangeUserPassword();
        changeUserPassword.setAccessToken("");
        changeUserPassword.setPreviousPassword(profileFragment.getTextViewPasswordProfile().getText().toString());
        changeUserPassword.setProposedPassword(Objects.requireNonNull(textInputEditTextNewPassword.getText()).toString());
        return changeUserPassword;
    }

}