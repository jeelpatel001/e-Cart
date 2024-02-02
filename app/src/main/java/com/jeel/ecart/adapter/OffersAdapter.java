package com.jeel.ecart.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jeel.ecart.R;
import com.jeel.ecart.acticity.CategoryActivity;
import com.jeel.ecart.acticity.MainActivity;
import com.jeel.ecart.acticity.OfferActivity;
import com.jeel.ecart.databinding.ItemCategoriesBinding;
import com.jeel.ecart.databinding.ItemOfferBinding;
import com.jeel.ecart.models.Category;
import com.jeel.ecart.models.Offer;

import java.util.ArrayList;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferViewHolder> {

    Context context;
    ArrayList<Offer> offers;

    public OffersAdapter(Context context, ArrayList<Offer> offers) {
        this.context = context;
        this.offers = offers;
    }

    @NonNull
    @Override
    public OffersAdapter.OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OfferViewHolder(LayoutInflater.from(context).inflate(R.layout.item_offer,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull OffersAdapter.OfferViewHolder holder, int position) {
        Offer offer = offers.get(position);

        Glide.with(context)
                .load(offer.getImageURL())
                .into(holder.binding.imageViewOfffer);

        Log.d("Jeel", "Oooooooooisudfosdfu : " +offer.getImageURL());
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {

        ItemOfferBinding binding;
        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemOfferBinding.bind(itemView);
        }
    }
}
