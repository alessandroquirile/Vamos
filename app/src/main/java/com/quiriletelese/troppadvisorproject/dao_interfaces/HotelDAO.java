package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import java.util.Optional;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public interface HotelDAO {

    void findByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch, Context context, int page, int size);

}
