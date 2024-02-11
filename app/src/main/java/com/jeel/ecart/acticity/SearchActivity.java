package com.jeel.ecart.acticity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jeel.ecart.R;
import com.jeel.ecart.adapter.ProductAdaptor;
import com.jeel.ecart.databinding.ActivitySearchBinding;
import com.jeel.ecart.helper.Constants;
import com.jeel.ecart.models.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding binding;
    ArrayList<Product> products;
    ProductAdaptor productAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Prevent Screen Shots
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);


        products = new ArrayList<>();
        productAdaptor = new ProductAdaptor(this, products);


        String query = getIntent().getStringExtra("query");

        binding.topAppBar.setTitle("Search for " + query);

        getProducts(query);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.searchList.setLayoutManager(layoutManager);
        binding.searchList.setAdapter(productAdaptor);

        binding.topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (isMobileDataAvailable()) {
            // Mobile data is available
            binding.networkError.setVisibility(View.GONE);
            binding.searchList.setVisibility(View.VISIBLE);
        } else {
            // Mobile data is not available
            binding.searchList.setVisibility(View.GONE);
            binding.networkError.setVisibility(View.VISIBLE);
        }

        binding.retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }

    private void getProducts(String query) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.GET_PRODUCTS_URL + "?q=" + query;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray productsArray = object.getJSONArray("products");
                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject childObj = productsArray.getJSONObject(i);
                        Product product = new Product(
                                childObj.getString("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString("image"),
                                childObj.getString("status"),
                                childObj.getDouble("price"),
                                childObj.getDouble("price_discount"),
                                childObj.getInt("stock"),
                                childObj.getInt("id")

                        );
                        products.add(product);
                    }
                    productAdaptor.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });

        queue.add(request);
    }

    public boolean isMobileDataAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}