package com.jeel.ecart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jeel.ecart.R;
import com.jeel.ecart.databinding.ItemAdminDocumentsBinding;
import com.jeel.ecart.models.Documents;

import java.util.ArrayList;

public class AdminDocumentsAdapter extends RecyclerView.Adapter<AdminDocumentsAdapter.AdminDocumentsHolder> {

    ArrayList<Documents> documents;
    Context context;

    public AdminDocumentsAdapter( Context context, ArrayList<Documents> documents) {
        this.documents = documents;
        this.context = context;
    }

    @NonNull
    @Override
    public AdminDocumentsAdapter.AdminDocumentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdminDocumentsHolder(LayoutInflater.from(context).inflate(R.layout.item_admin_documents, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminDocumentsAdapter.AdminDocumentsHolder holder, int position) {
        Documents doc = documents.get(position);
        Glide.with(context)
                .load(doc.getDocImage())
                .into(holder.binding.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public static class AdminDocumentsHolder extends RecyclerView.ViewHolder {
        ItemAdminDocumentsBinding binding;

        public AdminDocumentsHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAdminDocumentsBinding.bind(itemView);
        }
    }
}
