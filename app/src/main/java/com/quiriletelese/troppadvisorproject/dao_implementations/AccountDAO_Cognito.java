package com.quiriletelese.troppadvisorproject.dao_implementations;

import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.models.Account;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class AccountDAO_Cognito implements AccountDAO {

    @Override
    public boolean authenticate(Account account) {
        return false;
        // codice per autenticarsi via cognito
    }

    @Override
    public boolean create(Account account) {
        return false;
        // codice per creare un nuovo account via cognito
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return false;
        // Verificare che la mail sia disponibile, cioè non già impiegata
    }

    @Override
    public boolean isNicknameAvailable(String nickname) {
        return false;
        // Verificare che il nickname sia disponibile
    }
}
