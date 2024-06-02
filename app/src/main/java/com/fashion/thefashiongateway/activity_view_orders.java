package com.fashion.thefashiongateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fashion.thefashiongateway.adapters.OrderAdapter;
import com.fashion.thefashiongateway.classes.Order;
import com.fashion.thefashiongateway.classes.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class activity_view_orders extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private OrderAdapter mOrderAdapter;
    private List<Order> mOrderList;
    private ImageView btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        mRecyclerView = findViewById(R.id.recycler_view_orders);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnBack = findViewById(R.id.btnBack);
        mOrderList = new ArrayList<>();
        mOrderAdapter = new OrderAdapter(this, mOrderList);
        mRecyclerView.setAdapter(mOrderAdapter);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        loadOrders();
    }


    private void loadOrders() {
        // Obtener la referencia a la colección "orders" en Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ordersRef = db.collection("orders");

        ordersRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Limpiar la lista de pedidos antes de cargar nuevos datos
                        mOrderList.clear();
                        // Iterar sobre los documentos de pedido y agregarlos a la lista
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Order order = document.toObject(Order.class);
                            order.setId(document.getId());

                            // Obtener la referencia a la colección de productos para este pedido
                            CollectionReference productsRef = document.getReference().collection("products");
                            productsRef.get().addOnCompleteListener(productsTask -> {
                                if (productsTask.isSuccessful()) {
                                    List<Product> productList = new ArrayList<>();
                                    for (QueryDocumentSnapshot productDocument : productsTask.getResult()) {
                                        Product product = new Product();
                                        String name = (String) productDocument.get("productName");

                                        Long longQuantity = (Long) productDocument.get("quantity");
                                        int quantity = longQuantity.intValue();
                                        product.setName(name);
                                        product.setStock(quantity);
                                        productList.add(product);
                                    }
                                    order.setProducts(productList);
                                    mOrderList.add(order);
                                    mOrderAdapter.notifyDataSetChanged();
                                } else {
                                    // Manejar errores en la carga de productos
                                    // Puedes mostrar un mensaje de error o realizar otras acciones
                                }
                            });
                        }
                    } else {
                        // Manejar errores en la carga de pedidos
                        // Aquí puedes mostrar un mensaje de error o realizar otras acciones
                    }
                });
    }
}

