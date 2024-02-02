package com.jeel.ecart.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jeel.ecart.R;
import com.jeel.ecart.acticity.ProductActivity;
import com.jeel.ecart.databinding.ItelProductBinding;
import com.jeel.ecart.models.Product;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProductAdaptor extends RecyclerView.Adapter<ProductAdaptor.ProductViewHolder> {
    Context context;
    ArrayList<Product> product;

    public ProductAdaptor(Context context, ArrayList<Product> product) {
        this.context = context;
        this.product = product;
    }

    @NonNull
    @Override
    public ProductAdaptor.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.itel_product,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdaptor.ProductViewHolder holder, int position) {
        Product products = product.get(position);

        Glide.with(context)
                .load(products.getImage())
                .into(holder.binding.imageView);
        holder.binding.textViewTitle.setText(products.getName());
        holder.binding.textViewPrice.setText("Rs. " + products.getPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductActivity.class);
                intent.putExtra("name", products.getName());
                intent.putExtra("image", products.getImage());
                intent.putExtra("id", products.getId());
                intent.putExtra("price", products.getPrice());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return product.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ItelProductBinding binding;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItelProductBinding.bind(itemView);
        }
    }
}
