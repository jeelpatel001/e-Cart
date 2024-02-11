package com.jeel.ecart.acticity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeel.ecart.R;
import com.jeel.ecart.adapter.AdminDocumentsAdapter;
import com.jeel.ecart.adapter.OffersAdapter;
import com.jeel.ecart.databinding.ActivityAboutBinding;
import com.jeel.ecart.helper.Constants;
import com.jeel.ecart.models.Category;
import com.jeel.ecart.models.Documents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AboutActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityAboutBinding binding;
    AdminDocumentsAdapter adminDocumentsAdapter;
    ArrayList<Documents> documents;
    String location;
    Float string1;
    Float string2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        documents = new ArrayList<>();
        getAdminData();

        adminDocumentsAdapter = new AdminDocumentsAdapter(this, documents);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        binding.documents.setLayoutManager(linearLayoutManager);
        binding.documents.setAdapter(adminDocumentsAdapter);

        getCategoriesCount();
        getProductCount();

        if (isMobileDataAvailable()) {
            // Mobile data is available
            binding.networkError.setVisibility(View.GONE);
        } else {
            // Mobile data is not available
            binding.offline.setVisibility(View.GONE);
            binding.networkError.setVisibility(View.VISIBLE);
        }

        binding.retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

    }

    private void getProductCount() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.PRODUCT_COUNT;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                binding.productCount.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    private void getCategoriesCount() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.CATEGORY_COUNT;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                binding.categoriesCount.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    void getAdminData() {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, Constants.ADMIN_DATA, response -> {
            try {

                JSONObject mainObj = new JSONObject(response);
                binding.shopName.setText(mainObj.getString("aShopName"));
                binding.shopOwnerName.setText(mainObj.getString("aShopOwnerName"));
                binding.shopAddress.setText(mainObj.getString("aShopAddress"));

                location = mainObj.getString("google_cordinate");


                String inputString = location;
                String[] result = inputString.split(", ");

                if (result.length == 2) {
                     string1 = Float.valueOf(result[0]);
                     string2 = Float.valueOf(result[1]);
                    Log.d("Jeel","String 1: " + string1 + " String 2: " + string2);


                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                } else {
                    Log.d("Jeel","Input string does not contain a comma.");
                }


                binding.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String posted_by = null;
                        try {
                            posted_by = mainObj.getString("aShopNumber");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        String uri = "tel:" + posted_by.trim();
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }
                });

                binding.whatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phoneNumberWithCountryCode = null;
                        try {
                            phoneNumberWithCountryCode = mainObj.getString("aShopWpNumber");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        String message = "Hallo";

                        startActivity(
                                new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(
                                                String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message)
                                        )
                                )
                        );
                    }
                });

                Glide.with(this)
                        .load(Constants.DOCUMENTS_IMAGE_URL + mainObj.getString("userImage"))
                        .into(binding.userImage);
                for (int i = 1; i <= 6; i++) {
                    String name = "doc" + i;
                    if (mainObj.getString(name).equals("")) {
//                        binding.docTitle.setVisibility(View.GONE);
                    } else {
//                        binding.docTitle.setVisibility(View.VISIBLE);
                        documents.add(new Documents(Constants.DOCUMENTS_IMAGE_URL + mainObj.getString(name)));
                    }

                    adminDocumentsAdapter.notifyDataSetChanged();

                    binding.offline.setVisibility(View.GONE);
                    binding.nestedScrollView.setVisibility(View.VISIBLE);

                }



            } catch (JSONException e) {
                e.printStackTrace();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        queue.add(request);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng myLocation = new LatLng(string1, string2);
        googleMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .title("Our Shop"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12.03f));
    }


    public boolean isMobileDataAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}