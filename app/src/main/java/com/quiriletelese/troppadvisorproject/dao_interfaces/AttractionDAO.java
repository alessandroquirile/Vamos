package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public interface AttractionDAO {

    void findByRsql(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery,
                    Context context, int page, int size, boolean canPutPointSearch);

    void findById(VolleyCallBack volleyCallBack, String id, Context context);

    void findByNameLikeIgnoreCase(VolleyCallBack volleyCallBack, String name, Context context,
                                  int page, int size);

    void findAttractionsName(VolleyCallBack volleyCallBack, String name, Context context);

}
