package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.model_helpers.ChangeUserPassword;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public interface AccountDAO {

    void login(VolleyCallBack volleyCallBack, Account account, Context context);

    void createAccount(VolleyCallBack volleyCallBack, Account account, Context context);

    void updatePassword(VolleyCallBack volleyCallBack, ChangeUserPassword changeUserPassword, Context context);

}
