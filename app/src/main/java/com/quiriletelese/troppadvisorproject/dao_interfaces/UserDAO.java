package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;
import android.graphics.Bitmap;

import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public interface UserDAO {

    void findByEmail(VolleyCallBack volleyCallBack, String email, Context context);

    void findByNameOrUsername(VolleyCallBack volleyCallBack, String value, Context context);

    void findLeaderboard(VolleyCallBack volleyCallBack, Context context);

    void updateUserImage(VolleyCallBack volleyCallBack, String email, Bitmap bitmap, Context context);

    void updateUserInformations(VolleyCallBack volleyCallBack, User user, Context context);

    void updateDailyUserLevel(VolleyCallBack volleyCallBack, String email, Context context);

}
