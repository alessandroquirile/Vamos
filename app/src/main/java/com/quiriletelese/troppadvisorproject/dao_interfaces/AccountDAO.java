package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.model_helpers.ChangeUserPassword;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallbackCreateUser;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallbackLogin;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallbackUpdatePassword;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public interface AccountDAO {

    void login(VolleyCallbackLogin volleyCallbackLogin, Account account, Context context);

    void createAccount(VolleyCallbackCreateUser volleyCallbackCreateUser, Account account, Context context);

    void updatePassword(VolleyCallbackUpdatePassword volleyCallbackUpdatePassword, ChangeUserPassword changeUserPassword, Context context);

}
