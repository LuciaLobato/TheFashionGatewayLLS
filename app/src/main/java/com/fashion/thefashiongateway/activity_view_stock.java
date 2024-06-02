package com.fashion.thefashiongateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fashion.thefashiongateway.adapters.InventoryItemAdapter;
import com.fashion.thefashiongateway.classes.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class activity_view_stock extends AppCompatActivity {

    private static final String TAG = "activity_view_stock";
    private RecyclerView recyclerView;
    private List<Product> productList;
    private ImageView btnBack;
    private InventoryItemAdapter inventoryItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stock);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Inicializar la lista de productos
        productList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view_stock);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);        // Obtener la referencia a la colección "products" en Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("products");

        // Obtener todos los productos de la base de datos
        productsRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Limpiar la lista de productos antes de cargar nuevos datos
                        productList.clear();
                        System.out.println(queryDocumentSnapshots.getDocuments().size());
                        // Iterar sobre los documentos de productos y agregarlos a la lista
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Convertir el documento en un objeto Product
                            Product product = document.toObject(Product.class);
                            product.setProductId(document.getId());
                            productList.add(product);
                        }

                        // Inicializar el adaptador y configurarlo en la ListView
                        inventoryItemAdapter = new InventoryItemAdapter(activity_view_stock.this, productList);
                        recyclerView.setAdapter(inventoryItemAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener productos: " + e.getMessage());
                        Toast.makeText(activity_view_stock.this, "Error al obtener productos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}