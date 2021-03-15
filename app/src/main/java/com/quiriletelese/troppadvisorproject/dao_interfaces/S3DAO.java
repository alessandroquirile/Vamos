package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

public interface S3DAO {

    void uploadFile(VolleyCallBack volleyCallBack, byte[] file, Context context);

    void deleteFile(VolleyCallBack volleyCallBack, String image, Context context);

}
