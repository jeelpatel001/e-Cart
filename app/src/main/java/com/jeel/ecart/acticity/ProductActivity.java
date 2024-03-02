package com.jeel.ecart.acticity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;
import com.jeel.ecart.adapter.ImageAdapter;
import com.jeel.ecart.databinding.ActivityProductBinding;
import com.jeel.ecart.helper.Constants;
import com.jeel.ecart.models.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    ActivityProductBinding binding;
    Product currentProduct;
    Cart cart;
    String image;
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Prevent Screen Shots
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        cart = TinyCartHelper.getCart();


        String name = getIntent().getStringExtra("name");
        image = getIntent().getStringExtra("image");
        int id = getIntent().getIntExtra("id", 0);
        double price = getIntent().getDoubleExtra("price", 0);

        binding.productTitle.setText(name);
        binding.productPrice.setText("Rs. " + price);
        initImage(id);

        binding.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart.addItem(currentProduct, 1);
                binding.addToCart.setText("ALREADY SELECTED");
            }
        });

        binding.topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (isMobileDataAvailable()) {
            // Mobile data is available
            binding.networkError.setVisibility(View.GONE);
            binding.nestedScrollView.setVisibility(View.VISIBLE);
        } else {
            // Mobile data is not available
            binding.offline.setVisibility(View.GONE);
            binding.nestedScrollView.setVisibility(View.GONE);
            binding.networkError.setVisibility(View.VISIBLE);
        }

        binding.retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }

    ArrayList<String> arrayList;

    void initImage(int id) {

        arrayList = new ArrayList<>();

        getProductDetails(id);

        adapter = new ImageAdapter(ProductActivity.this, arrayList);
        binding.recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onClick(ImageView imageView, String path) {
                startActivity(new Intent(ProductActivity.this, ImageViewActivity.class).putExtra("image", path),
                        ActivityOptions.makeSceneTransitionAnimation(ProductActivity.this, imageView, "image").toBundle());
            }
        });
    }

    void getProductDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCT_DETAILS_URL + id;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status").equals("success")) {
                        JSONObject product = object.getJSONObject("product");
                        String description = product.getString("description");
                        binding.productDescription.setText(
                                Html.fromHtml(description)
                        );

                        currentProduct = new Product(
                                product.getString("name"),
                                Constants.PRODUCTS_IMAGE_URL + product.getString("image"),
                                product.getString("status"),
                                product.getDouble("price"),
                                product.getDouble("price_discount"),
                                product.getInt("stock"),
                                product.getInt("id")
                        );

                        JSONArray productImage = product.getJSONArray("product_images");

                        Log.d("Jeel", "PRODUCT Length : " + productImage.length());

                        if (productImage.length() == 0) {
                            binding.recycler.setVisibility(View.GONE);
                            binding.imageView.setVisibility(View.VISIBLE);
                            Glide.with(ProductActivity.this)
                                    .load(Constants.PRODUCTS_IMAGE_URL + product.getString("image"))
                                    .into(binding.imageView);
                            binding.imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        startActivity(new Intent(ProductActivity.this, ImageViewActivity.class).putExtra("image", Constants.PRODUCTS_IMAGE_URL + product.getString("image")),
                                                ActivityOptions.makeSceneTransitionAnimation(ProductActivity.this, binding.imageView, "image").toBundle());
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        } else {
                            binding.recycler.setVisibility(View.VISIBLE);
                            binding.imageView.setVisibility(View.GONE);
                            for (int i = 0; i < productImage.length(); i++) {
                                JSONObject childObj = productImage.getJSONObject(i);

                                Log.d("Jeel", "PRODUCT IMAGE : " + productImage);
                                Log.d("Jeel", "PRODUCT IMAGE : " + childObj);

                                arrayList.add(Constants.PRODUCTS_IMAGE_URL + childObj.getString("name"));

                            }
                            adapter.notifyDataSetChanged();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);


        binding.addToCart.setVisibility(View.VISIBLE);
        binding.offline.setVisibility(View.GONE);
        binding.nestedScrollView.setVisibility(View.VISIBLE);
    }

    public boolean isMobileDataAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}