package com.quiriletelese.troppadvisorproject.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.models.Hotel;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomePageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_button_menu_home_page_activity:
                startActivity(new Intent(getContext(), LoginActivity.class
                        /*SearchActivity.class*/ /*WriteReviewActivity.class*//*OverviewActivity.class*/));
                break;
            case R.id.map_button_menu_home_page_activity:
                //startActivity(new Intent(getContext(), MapsActivity.class));
                //addHotel();
                findHotelById("5f412f2f61fe347e4737bb8");
                break;
            case R.id.filter_button_menu_home_page_activity:
                break;
            case R.id.radio_button_subitem_all:
                break;
            case R.id.radio_button_subitem_hotel:
                break;
            case R.id.radio_button_subitem_restaurant:
                break;
            case R.id.radio_button_subitem_attractions:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private JSONObject createAddressJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "via");
            jsonObject.put("street", "Partenope");
            jsonObject.put("houseNumber", "38");
            jsonObject.put("city", "Napoli");
            jsonObject.put("province", "NA");
            jsonObject.put("postalCode", "80121");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject createJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", "Hotel Royal Continental");
            jsonObject.put("stars", 4);
            jsonObject.put("avaragePrice", 136.00);
            jsonObject.put("phoneNumber", "0812452068");
            jsonObject.put("address", createAddressJSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void addHotel() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/hotel/insert?latitude=40.830051&longitude=14.246908";
        final JSONObject jsonObject = createJSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                Hotel hotel = gson.fromJson(response.toString(), Hotel.class);
                System.out.println(hotel.getName() + "\n" + hotel.getAddedDate() + "\nLat: " + hotel.getPoint().getX() + "\nLon: " + hotel.getPoint().getY());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void findHotelById(String id){
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/hotel/find-by-id/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                Hotel hotel = gson.fromJson(response.toString(), Hotel.class);
                Toast.makeText(getActivity(), "Name: " + hotel.getName()
                        + "\nCity: " + hotel.getAddress().getCity()
                        + "\nAddedDate: " + hotel.getAddedDate(), Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {

            }
        }){
            @Override
            protected Response<JSONObject> parseNetworkResponse(final NetworkResponse response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), response.headers.get("Errore id") + "\nCode: " + response.statusCode, Toast.LENGTH_SHORT).show();
                    }
                });
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

}