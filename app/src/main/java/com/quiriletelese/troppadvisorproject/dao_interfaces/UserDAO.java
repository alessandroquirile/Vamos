package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public interface UserDAO {

    void findByEmail(VolleyCallBack volleyCallBack, String email, Context context);
    void findByUsername(VolleyCallBack volleyCallBack, String username, Context context);

}
