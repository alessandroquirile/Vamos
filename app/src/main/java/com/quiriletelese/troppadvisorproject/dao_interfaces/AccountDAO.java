package com.quiriletelese.troppadvisorproject.dao_interfaces;

import com.quiriletelese.troppadvisorproject.models.Account;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public interface AccountDAO {
    boolean authenticate(Account account);

    boolean create(Account account);

    boolean isEmailAvailable(String email);

    boolean isNicknameAvailable(String nickname);

    boolean updatePassword(String password);
}
