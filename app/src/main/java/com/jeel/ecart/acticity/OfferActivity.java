package com.jeel.ecart.acticity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jeel.ecart.R;
import com.jeel.ecart.adapter.OffersAdapter;
import com.jeel.ecart.adapter.ProductAdaptor;
import com.jeel.ecart.databinding.ActivityOfferBinding;
import com.jeel.ecart.helper.Constants;
import com.jeel.ecart.models.Offer;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OfferActivity extends AppCompatActivity {

    ActivityOfferBinding binding;
    ArrayList<Offer> offers;
    OffersAdapter offersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOfferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Prevent Screen Shots
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);


        initOffers();


        binding.topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (isMobileDataAvailable()) {
            // Mobile data is available
            binding.networkError.setVisibility(View.GONE);
            binding.offer.setVisibility(View.VISIBLE);
        } else {
            // Mobile data is not available
            binding.offline.setVisibility(View.GONE);
            binding.offer.setVisibility(View.GONE);
            binding.networkError.setVisibility(View.VISIBLE);
        }

        binding.retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

    }

    private void initOffers() {
        offers = new ArrayList<>();
        initSlider();
        // Removed the initialization of the adapter and layout manager here
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

                        Log.d("Jeel", "Kaik Vandho Chhe : " + Constants.NEWS_IMAGE_URL + childObj.getString("image"));

                        offers.add(new Offer(Constants.NEWS_IMAGE_URL + childObj.getString("image")));
                    }
                    Log.d("Jeel", "ARRAYLIST 2" + offers);

                    // Notify the adapter that the data has changed
                    offersAdapter = new OffersAdapter(this, offers);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
                    binding.offer.setLayoutManager(gridLayoutManager);
                    binding.offer.setAdapter(offersAdapter);

                    binding.offline.setVisibility(View.GONE);
                    binding.nestedScrollView.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Handle error
        });
        queue.add(request);
    }


    public boolean isMobileDataAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


//    private void initOffers() {
//
//        offers = new ArrayList<>();
////        offers.add(new Offer("https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080"));
////        offers.add(new Offer("https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080"));
////        offers.add(new Offer("https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080"));
////        offers.add(new Offer("https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080"));
//        initSlider();
//        Log.d("Jeel", "ARRAY LIST : " + offers);
//
//        offersAdapter = new OffersAdapter(this, offers);
//
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
//        binding.offer.setLayoutManager(gridLayoutManager);
//        binding.offer.setAdapter(offersAdapter);
//
//    }
//
//
//    private void initSlider() {
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_OFFERS_URL, response -> {
//            try {
//                JSONObject object = new JSONObject(response);
//                if (object.getString("status").equals("success")) {
//                    JSONArray offerArray = object.getJSONArray("news_infos");
//                    for (int i = 0; i < offerArray.length(); i++) {
//                        JSONObject childObj = offerArray.getJSONObject(i);
//
//                        Log.d("Jeel", "Kaik Vandho Chhe : "+ Constants.NEWS_IMAGE_URL + childObj.getString("image"));
//
//                        offers.add(new Offer(childObj.getString("image")));
//                    }
//                    Log.d("Jeel","ARRAYLIST 2" + offers);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> {
//        });
//        queue.add(request);
//
//    }
}