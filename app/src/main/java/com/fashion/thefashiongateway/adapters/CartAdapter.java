package com.fashion.thefashiongateway.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fashion.thefashiongateway.R;
import com.fashion.thefashiongateway.classes.Product;
import com.fashion.thefashiongateway.classes.ProductsUtil;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> productList;
    private Context context;
    DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Define el formato que deseas para el precio
    double globalPrice;
    TextView txtGlobalPrice;
    boolean initialized = false;
    public CartAdapter(List<Product> productList, Context context,double globalPrice, TextView txtGlobalPrice) {
        this.productList = productList;
        this.context = context;
        this.globalPrice = globalPrice;
        this.txtGlobalPrice = txtGlobalPrice;

    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.textViewProductName.setText(product.getName());
        holder.textViewProductPrice.setText("Precio unidad: " + product.getPrice() + "€");
        holder.textViewProductQuantity.setText(String.valueOf(product.getStock())); // Cantidad inicial
        // Calcular el precio total y formatearlo
        double totalPrice = product.getPrice() * product.getStock();
        String formattedTotalPrice = decimalFormat.format(totalPrice); // Formatea el precio
        holder.textViewTotalPrice.setText("Precio total: " + formattedTotalPrice + "€");
        if (!initialized){
            updateGlobalPrice();
            initialized = true;
        }
        Picasso.get().load(product.getImageURL()).into(holder.imageViewProduct);

        // Configurar el listener del botón de disminuir cantidad
        holder.btnDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = product.getStock();
                if (currentQuantity > 1) {
                    currentQuantity--;
                    product.setStock(currentQuantity);
                    ProductsUtil.updateQuantityInDatabase(product, currentQuantity, context); // Actualizar la cantidad en la base de datos
                    notifyDataSetChanged();
                    updateGlobalPrice(); // Actualizar el precio total global
                }
            }
        });

        // Configurar el listener del botón de aumentar cantidad
        holder.btnIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = product.getStock();
                currentQuantity++;
                product.setStock(currentQuantity);
                ProductsUtil.updateQuantityInDatabase(product, currentQuantity, context); // Actualizar la cantidad en la base de datos
                notifyDataSetChanged();
                updateGlobalPrice(); // Actualizar el precio total global
            }
        });

        // Configurar el listener del botón de eliminar producto
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productList.remove(product);
                ProductsUtil.deleteProductFromDatabase(product, context); // Eliminar el producto de la base de datos
                notifyDataSetChanged();
                updateGlobalPrice(); // Actualizar el precio total global
            }
        });
    }

    // Método para calcular el precio total global
    private void updateGlobalPrice() {
        globalPrice = calculateTotalPrice(productList);
        String formattedGlobalPrice = decimalFormat.format(globalPrice);
        txtGlobalPrice.setText("Total: " + formattedGlobalPrice + "€");
    }

    // Método para calcular el precio total de todos los productos en el carrito
    private double calculateTotalPrice(List<Product> productList) {
        double totalPrice = 0.0;
        for (Product product : productList) {
            totalPrice += (product.getPrice() * product.getStock());
        }
        return totalPrice;
    }



    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;
        EditText textViewProductQuantity;
        TextView textViewTotalPrice;
        ImageButton btnDecreaseQuantity;
        ImageButton btnIncreaseQuantity;
        ImageButton btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewProductQuantity = itemView.findViewById(R.id.textViewProductQuantity);
            textViewTotalPrice = itemView.findViewById(R.id.textViewTotalPrice);
            btnDecreaseQuantity = itemView.findViewById(R.id.btnDecreaseQuantity);
            btnIncreaseQuantity = itemView.findViewById(R.id.btnIncreaseQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
