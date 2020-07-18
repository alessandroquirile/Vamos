package com.quiriletelese.troppadvisorproject.controllers;

import android.view.View;
import android.widget.Toast;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.ProfileFragment;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class UpdateProfileController implements View.OnClickListener {
    private ProfileFragment profileFragment;
    private AccountDAO accountDAO;
    private DAOFactory daoFactory;

    public UpdateProfileController(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
    }

    public void setListenersOnProfileFragment() {
        profileFragment.getFloatingActionButton().setOnClickListener(this);
    }

    public void enablePasswordField() {
        profileFragment.getEditTextPassword().setEnabled(true);
    }

    public void changePassword() {
        daoFactory = DAOFactory.getInstance();
        accountDAO = daoFactory.getAccountDAO(ConfigFileReader.getProperty("account_storage_technology",
                profileFragment.requireActivity().getApplicationContext()));
        if (accountDAO.updatePassword(profileFragment.getEditTextPassword().getText().toString())) {
            Toast.makeText(profileFragment.requireActivity().getApplicationContext(), "PAssowrd mod", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(profileFragment.requireActivity().getApplicationContext(), "PAssowrd NON mod", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_action_button_edit_profile_fragment:
                // TODO: mostra popup per inserire la vecchia password. Se giusta:
                enablePasswordField();
                // inserisce nuova password
                // conferma
                changePassword();
                break;
        }
    }
}
