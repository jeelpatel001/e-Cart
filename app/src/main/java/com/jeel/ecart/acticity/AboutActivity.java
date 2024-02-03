package com.jeel.ecart.acticity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.jeel.ecart.models.Documents;

import java.util.ArrayList;

public class AboutActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityAboutBinding binding;
    AdminDocumentsAdapter adminDocumentsAdapter;
    ArrayList<Documents> documents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        documents = new ArrayList<>();

        documents.add(new Documents("https://img.freepik.com/free-psd/vertical-poster-template-streetwear-fashion-shopping_23-2150573120.jpg"));
        documents.add(new Documents("https://img.freepik.com/premium-psd/vertical-poster-template-thrift-shop-fashion-sale_23-2148979804.jpg"));
        documents.add(new Documents("https://img.freepik.com/free-psd/vertical-poster-retail-sale_23-2148758255.jpg"));
        documents.add(new Documents("https://img.freepik.com/free-vector/flat-supermarket-vertical-poster-template_23-2149376447.jpg"));
        documents.add(new Documents("https://content.wepik.com/statics/13264850/preview-page0.jpg"));
        documents.add(new Documents("https://img.freepik.com/free-psd/vertical-poster-template-streetwear-fashion-shopping_23-2150573120.jpg"));
        documents.add(new Documents("https://img.freepik.com/premium-psd/vertical-poster-template-thrift-shop-fashion-sale_23-2148979804.jpg"));
        documents.add(new Documents("https://img.freepik.com/free-psd/vertical-poster-retail-sale_23-2148758255.jpg"));
        documents.add(new Documents("https://img.freepik.com/free-vector/flat-supermarket-vertical-poster-template_23-2149376447.jpg"));
        documents.add(new Documents("https://content.wepik.com/statics/13264850/preview-page0.jpg"));

        adminDocumentsAdapter = new AdminDocumentsAdapter(this, documents);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        binding.documents.setLayoutManager(linearLayoutManager);
        binding.documents.setAdapter(adminDocumentsAdapter);

        getCategoriesCount();
        getProductCount();

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng myLocation = new LatLng(22.302056769040426, 70.7989762892026);
        googleMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .title("Our Shop"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 6.03f));
    }

}