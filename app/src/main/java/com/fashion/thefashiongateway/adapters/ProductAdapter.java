package com.fashion.thefashiongateway.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fashion.thefashiongateway.R;
import com.fashion.thefashiongateway.classes.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public List<Product> productList = new ArrayList<>();
    Context context;
    FirebaseFirestore db;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
        db = FirebaseFirestore.getInstance();

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        System.out.println("image url: " + product.getImageURL());
        Picasso.get().load(product.getImageURL()).into(holder.imageViewProduct, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                // Manejar errores de carga de imagen aquí
                holder.progressBar.setVisibility(View.GONE);
            }
        });

        holder.textViewProductName.setText(product.getName());
        holder.textViewProductPrice.setText(product.getPrice().toString() + "€");

        // Agregar OnClickListener al botón "Añadir"
        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;
        ProgressBar progressBar;
        ImageButton btnAddToCart; // Nuevo botón para añadir al carrito

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            progressBar = itemView.findViewById(R.id.progressBar);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }

    private void addToCart(Product product) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(userId);
        CollectionReference cartRef = userRef.collection("cart");

        // Verificar si el producto ya está en el carrito
        DocumentReference productRef = cartRef.document(product.getProductId());
        productRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Si el producto ya está en el carrito, actualizar la cantidad
                        int currentQuantity = document.getLong("quantity").intValue();
                        int newQuantity = currentQuantity + 1;
                        productRef.update("quantity", newQuantity)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error al actualizar la cantidad en el carrito", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Si el producto no está en el carrito, agregarlo con cantidad 1
                        Map<String, Object> productData = new HashMap<>();
                        productData.put("quantity", 1);
                        cartRef.document(product.getProductId()).set(productData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Producto añadido al carrito", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error al añadir el producto al carrito", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(context, "Error al verificar el producto en el carrito", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
