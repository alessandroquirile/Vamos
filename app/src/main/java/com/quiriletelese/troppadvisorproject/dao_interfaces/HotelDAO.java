package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import androidx.paging.PagedList;

import com.quiriletelese.troppadvisorproject.interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Hotel;

import java.util.List;
import java.util.Optional;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public interface HotelDAO {

    Optional<Hotel> findById(String id);

    void findByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch, Context context);

}
