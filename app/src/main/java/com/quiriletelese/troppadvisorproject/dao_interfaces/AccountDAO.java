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

    void refreshToken(VolleyCallBack volleyCallBack, String refreshToken, Context context);

    void getUserDetails(VolleyCallBack volleyCallBack, String accessToken, Context context);

}
