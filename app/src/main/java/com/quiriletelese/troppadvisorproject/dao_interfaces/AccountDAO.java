package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.models.Account;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public interface AccountDAO {

    boolean authenticate(Account account, Context context);

    boolean create(Account account, Context context);

    boolean updatePassword(Account account, Context context, String newPassword);

}
