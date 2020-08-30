package com.quiriletelese.troppadvisorproject.volley_interfaces;

import java.util.List;

public interface VolleyCallBack {
    void onSuccess(List accomodation);

    void onError(List accomodation, String error);
}
