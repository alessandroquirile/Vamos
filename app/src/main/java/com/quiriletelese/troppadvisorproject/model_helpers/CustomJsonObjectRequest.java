package com.quiriletelese.troppadvisorproject.model_helpers;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class CustomJsonObjectRequest extends JsonRequest<JSONObject> {

    public CustomJsonObjectRequest(int method, String url, JSONArray jsonRequest,
                                   Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(@NotNull NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException | JSONException e) {
            return Response.error(new ParseError(e));
        }
    }
}
