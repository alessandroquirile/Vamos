package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.quiriletelese.troppadvisorproject.cognito.CognitoSettings;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.models.Account;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class AccountDAO_Cognito implements AccountDAO {

    @Override
    public boolean authenticate(Account account, Context context) {
        CognitoSettings cognitoSettings = new CognitoSettings(context);
        cognitoSettings.userLogin(account.getEmail(), account.getPassword());
        return true;
    }

    @Override
    public boolean create(Account account, Context context) {
        CognitoSettings cognitoSettings = new CognitoSettings(context);
        setCognitoSettingsAttributes(cognitoSettings, account);
        cognitoSettings.signUpInBackground(account.getEmail(), account.getPassword());
        return true;
    }

    @Override
    public boolean isNicknameAvailable(String nickname) {
        // Verificare che il nickname sia disponibile (?)
        return true;
    }

    @Override
    public boolean updatePassword(Account account, Context context, String newPassword) {
        final GenericHandler genericHandler = new GenericHandler() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        };
        CognitoSettings cognitoSettings = new CognitoSettings(context);
        cognitoSettings
                .getCognitoUserPool()
                .getUser(account.getEmail())
                .changePasswordInBackground(account.getPassword(), newPassword, genericHandler);
        return true;
    }

    private void setCognitoSettingsAttributes(CognitoSettings cognitoSettings, Account account) {
        cognitoSettings.addAttribute("email", account.getEmail());
        cognitoSettings.addAttribute("name", account.getName());
        cognitoSettings.addAttribute("family_name", account.getLastname());
        cognitoSettings.addAttribute("nickname", account.getNickname());
    }
}
