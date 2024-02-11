package com.jeel.ecart.acticity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jeel.ecart.R;
import com.jeel.ecart.adapter.CategoryAdaptor;
import com.jeel.ecart.adapter.ProductAdaptor;
import com.jeel.ecart.databinding.ActivityMainBinding;
import com.jeel.ecart.helper.Constants;
import com.jeel.ecart.models.Category;
import com.jeel.ecart.models.Product;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {

    CategoryAdaptor categoryAdaptor;
    ProductAdaptor productAdaptor;

    ArrayList<Product> products;
    ArrayList<Category> categories;

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Prevent Screen Shots
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {

            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query", text.toString());
                startActivity(intent);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        binding.navigationBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.cart) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                return true;
            } else if (item.getItemId() == R.id.home) {
                return true;
            } else if (item.getItemId() == R.id.offers) {
                startActivity(new Intent(MainActivity.this, OfferActivity.class));
                return true;
            } else if (item.getItemId() == R.id.about) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            }
            return false;
        });

        initCategories();
        initProducts();
        initSlider();

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

    private void initProducts() {

        products = new ArrayList<>();
        productAdaptor = new ProductAdaptor(this, products);

        getRecentProducts();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.productList.setLayoutManager(gridLayoutManager);
        binding.productList.setAdapter(productAdaptor);

    }

    void getRecentProducts() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.GET_PRODUCTS_URL + "?count=8";
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
                    binding.offline.setVisibility(View.GONE);
                    binding.nestedScrollView.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });

        queue.add(request);
    }

    private void initCategories() {
        categories = new ArrayList<>();
        categoryAdaptor = new CategoryAdaptor(this, categories);

        getCategories();
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
//        binding.categoriesList.setLayoutManager(linearLayoutManager);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        binding.categoriesList.setLayoutManager(gridLayoutManager);
        binding.categoriesList.setAdapter(categoryAdaptor);

    }

    void getCategories() {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_CATEGORIES_URL, response -> {
            try {
                Log.d("Jeel", "Hello : " + response);

                JSONObject mainObj = new JSONObject(response);

                if (mainObj.getString("status").equals("success")) {
                    JSONArray categoriesArray = mainObj.getJSONArray("categories");
                    for (int i = 0; i < categoriesArray.length(); i++) {
                        JSONObject object = categoriesArray.getJSONObject(i);
                        Category category = new Category(
                                object.getString("name"),
                                Constants.CATEGORIES_IMAGE_URL + object.getString("icon"),
                                object.getString("color"),
                                object.getString("brief"),
                                object.getInt("id")
                        );
                        categories.add(category);
                    }
                    categoryAdaptor.notifyDataSetChanged();
                } else {
                    // DO nothing
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


    private void initSlider() {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_OFFERS_URL, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray offerArray = object.getJSONArray("news_infos");
                    for (int i = 0; i < offerArray.length(); i++) {
                        JSONObject childObj = offerArray.getJSONObject(i);
                        binding.carousel.addData(
                                new CarouselItem(
                                        Constants.NEWS_IMAGE_URL + childObj.getString("image"),
                                        childObj.getString("title")
                                )
                        );
                    }
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