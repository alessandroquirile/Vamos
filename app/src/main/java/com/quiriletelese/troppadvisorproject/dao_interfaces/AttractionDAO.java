package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public interface AttractionDAO {

    void findByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch, Context context, int page, int size);

}
