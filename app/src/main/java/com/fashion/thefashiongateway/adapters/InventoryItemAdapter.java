package com.fashion.thefashiongateway.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fashion.thefashiongateway.R;
import com.fashion.thefashiongateway.classes.Product;
import com.fashion.thefashiongateway.classes.ProductsUtil;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import static com.fashion.thefashiongateway.classes.ProductsUtil.updateStock;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.InventoryItemViewHolder> {
    private Context mContext;
    private List<Product> mProductList;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Define el formato que deseas para el precio

    public InventoryItemAdapter(Context context, List<Product> productList) {
        mContext = context;
        mProductList = productList;
    }

    @NonNull
    @Override
    public InventoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new InventoryItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryItemViewHolder holder, int position) {
        Product product = mProductList.get(position);

        // Set data to views
        Picasso.get().load(product.getImageURL()).into(holder.imageViewProduct);
        holder.textViewProductName.setText(product.getName());
        double totalPrice = product.getPrice() ;
        String formattedTotalPrice = decimalFormat.format(totalPrice);
        holder.textViewProductPrice.setText("Precio unidad: " + formattedTotalPrice + "€");
        holder.textViewProductQuantity.setText(String.valueOf(product.getStock()));

        // Decrease quantity button click listener
        holder.btnDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = product.getStock();
                if (currentQuantity >= 1) {
                    currentQuantity--;
                    product.setStock(currentQuantity);
                    holder.textViewProductQuantity.setText(String.valueOf(currentQuantity));

                    notifyDataSetChanged();

                }

            }
        });


        // Increase quantity button click listener
        holder.btnIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = product.getStock();
                currentQuantity++;
                product.setStock(currentQuantity);
                holder.textViewProductQuantity.setText(String.valueOf(currentQuantity));

                notifyDataSetChanged();
            }
        });

        // Save button click listener
        holder.btnSave.setOnClickListener(view -> {
            int quantity = Integer.parseInt(holder.textViewProductQuantity.getText().toString());

            updateStock(product.getProductId(), quantity, new ProductsUtil.OnStockUpdateListener() {
                @Override
                public void onStockUpdateSuccess() {
                    Toast.makeText(mContext, "Stock actualizado con éxito", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStockUpdateFailure(String errorMessage) {
                    // Handle failure
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    static class InventoryItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;
        EditText textViewProductQuantity;
        ImageButton btnDecreaseQuantity;
        ImageButton btnIncreaseQuantity;
        ImageButton btnSave;

        InventoryItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewProductQuantity = itemView.findViewById(R.id.textViewProductQuantity);
            btnDecreaseQuantity = itemView.findViewById(R.id.btnDecreaseQuantity);
            btnIncreaseQuantity = itemView.findViewById(R.id.btnIncreaseQuantity);
            btnSave = itemView.findViewById(R.id.btnSave);
        }
    }
}
