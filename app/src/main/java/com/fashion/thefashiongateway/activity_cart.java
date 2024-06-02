package com.fashion.thefashiongateway;

import static com.fashion.thefashiongateway.classes.ProductsUtil.checkStock;
import static com.fashion.thefashiongateway.classes.ProductsUtil.emptyCart;
import static com.fashion.thefashiongateway.classes.ProductsUtil.reduceStock;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fashion.thefashiongateway.adapters.CartAdapter;
import com.fashion.thefashiongateway.classes.Product;
import com.fashion.thefashiongateway.classes.ProductsUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class activity_cart extends AppCompatActivity {

    ImageView btnBack;
    RecyclerView cartRecyclerView;
    TextView txtGlobalPrice;
    List<Product> productList;
    CartAdapter cartAdapter;
    ProgressBar progressBar;
    double globalPrice = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cartRecyclerView = findViewById(R.id.cart_recyclerview);
        txtGlobalPrice = findViewById(R.id.txt_total);
        txtGlobalPrice.setText("Total: 0.00€");
        progressBar = findViewById(R.id.progressBar);
        // Inicializar la lista de productos del carrito
        productList = new ArrayList<>();

        // Obtener el ID del usuario actualmente autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtener la referencia a la colección "cart" dentro del documento del usuario
        CollectionReference cartRef = FirebaseFirestore.getInstance().collection("users").document(userId).collection("cart");
        progressBar.setVisibility(View.VISIBLE);
        // Consultar los productos en el carrito
        cartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);

                    // Iterar sobre los documentos obtenidos y agregar los productos a la lista
                    for (DocumentSnapshot document : task.getResult()) {
                        String productId = document.getId();

                        ProductsUtil.getProductById(productId, new ProductsUtil.OnProductFetchListener() {
                            @Override
                            public void onProductFetchSuccess(Product product) {
                                // Establecer la cantidad del producto
                                int quantity = document.getLong("quantity").intValue();
                                product.setStock(quantity);

                                // Agregar producto a la lista
                                productList.add(product);

                                // Notificar al adaptador sobre los cambios en los datos
                                cartAdapter.notifyDataSetChanged();
                                //double globalPrice = calculateTotalPrice(productList);
                                //String formattedTotalPrice = decimalFormat.format(globalPrice); // Formatea el precio
                                //txtGlobalPrice.setText("Total: " + formattedTotalPrice + "€");

                            }

                            @Override
                            public void onProductFetchFailure(String errorMessage) {
                                // Manejar el caso de falla al obtener el producto
                                Toast.makeText(activity_cart.this, "Error al obtener el producto: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    // Configurar el RecyclerView y el adaptador
                    cartAdapter = new CartAdapter(productList, activity_cart.this,globalPrice, txtGlobalPrice);
                    cartRecyclerView.setLayoutManager(new LinearLayoutManager(activity_cart.this));
                    cartRecyclerView.setAdapter(cartAdapter);

                    // Calcular y mostrar el precio total

                } else {
                    // Manejar la falla al obtener los productos del carrito
                    Toast.makeText(activity_cart.this, "Error al obtener los productos del carrito", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeOrder();
            }
        });
    }


    private void makeOrder() {
        final boolean[] allProductsAvailable = {true};
        StringBuilder unavailableProduct = new StringBuilder();
        if (productList.size()<1){
            Toast.makeText(activity_cart.this, "No hay ningún producto en el carrito", Toast.LENGTH_SHORT).show();
            return;
        }
        // Añade una variable de contador para llevar la cuenta de las operaciones asíncronas completadas
        final AtomicInteger completedOperationsCount = new AtomicInteger(0);

        for (Product product : productList) {
            checkStock(product.getProductId(), product.getStock(), new ProductsUtil.OnStockCheckListener() {
                @Override
                public void onStockCheckSuccess(boolean isAvailable) {
                    if (!isAvailable) {
                        allProductsAvailable[0] = false;
                        unavailableProduct.append(product.getName()).append(", ");
                    }

                    // Incrementa el contador de operaciones completadas
                    int count = completedOperationsCount.incrementAndGet();

                    // Verifica si todas las operaciones asíncronas se han completado
                    if (count == productList.size()) {
                        // Todas las operaciones asíncronas se han completado, puedes continuar
                        if (!allProductsAvailable[0]) {
                            String message = "No hay stock para: " + unavailableProduct.toString();
                            Toast.makeText(activity_cart.this, message, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Si todos los productos están disponibles, proceder a realizar el pedido
                        for (Product product : productList) {
                            reduceStock(product.getProductId(), product.getStock(), new ProductsUtil.OnStockUpdateListener() {
                                @Override
                                public void onStockUpdateSuccess() {
                                    // Éxito al reducir el stock
                                }

                                @Override
                                public void onStockUpdateFailure(String errorMessage) {
                                    // Error al reducir el stock
                                }
                            });
                        }

                        emptyCart(getApplicationContext(), new ProductsUtil.OnCartEmptyListener() {
                            @Override
                            public void onCartEmptySuccess() {

                            }
                        });

                        createOrder(productList);

                        txtGlobalPrice.setText("Total: 0.00€");
                    }
                }

                @Override
                public void onStockCheckFailure(String errorMessage) {
                    // Manejar el error al verificar el stock
                    Toast.makeText(activity_cart.this, "Error al verificar el stock: " + errorMessage, Toast.LENGTH_SHORT).show();

                    // Incrementa el contador de operaciones completadas
                    int count = completedOperationsCount.incrementAndGet();

                    // Verifica si todas las operaciones asíncronas se han completado
                    if (count == productList.size()) {
                        // Todas las operaciones asíncronas se han completado, puedes continuar
                        if (!allProductsAvailable[0]) {
                            String message = "No hay suficiente stock para los siguientes productos: " + unavailableProduct.toString();
                            Toast.makeText(activity_cart.this, message, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Si todos los productos están disponibles, proceder a realizar el pedido
                        for (Product product : productList) {
                            reduceStock(product.getProductId(), product.getStock(), new ProductsUtil.OnStockUpdateListener() {
                                @Override
                                public void onStockUpdateSuccess() {
                                    // Éxito al reducir el stock
                                }

                                @Override
                                public void onStockUpdateFailure(String errorMessage) {
                                    // Error al reducir el stock
                                }
                            });
                        }

                        emptyCart(getApplicationContext(), new ProductsUtil.OnCartEmptyListener() {
                            @Override
                            public void onCartEmptySuccess() {

                            }
                        });

                        createOrder(productList);

                        txtGlobalPrice.setText("Total: 0.00€");
                    }
                }
            });
        }


    }


    private void createOrder(List<Product> productList) {
        // Obtener la referencia a la colección "orders" en Firestore
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ordersRef = db.collection("orders");

        // Obtener la fecha y hora actual como marca de tiempo para el pedido
        Timestamp timestamp = Timestamp.now();

        // Crear un mapa para almacenar los datos del pedido
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId); // ID del usuario que realizó el pedido
        orderData.put("timestamp", timestamp); // Fecha y hora del pedido

        // Agregar el pedido a la colección de pedidos
        ordersRef.add(orderData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Éxito al agregar el pedido a la base de datos
                        Log.d("Pedido", "Pedido realizado con éxito");

                        // Obtener la referencia al documento del pedido recién creado
                        String orderId = documentReference.getId();
                        DocumentReference orderDocRef = ordersRef.document(orderId);

                        // Crear una subcolección "products" dentro del documento del pedido
                        CollectionReference productsRef = orderDocRef.collection("products");

                        // Agregar los productos a la subcolección "products"
                        for (Product product : productList) {
                            Map<String, Object> productData = new HashMap<>();
                            productData.put("productId", product.getProductId());
                            productData.put("productName", product.getName());
                            productData.put("quantity", product.getStock());

                            // Agregar el producto a la subcolección "products"
                            productsRef.add(productData)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            // Éxito al agregar el producto al pedido
                                            Log.d("Pedido", "Producto agregado con ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error al agregar el producto al pedido
                                            Log.e("Pedido", "Error al agregar el producto al pedido", e);
                                        }
                                    });
                        }

                        // Vaciar el carrito después de realizar el pedido
                        productList.clear();
                        cartAdapter.notifyDataSetChanged();

                        // Mostrar un mensaje de éxito
                        Toast.makeText(activity_cart.this, "Pedido realizado con éxito", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al agregar el pedido a la base de datos
                        Log.e("Pedido", "Error al realizar el pedido", e);
                        Toast.makeText(activity_cart.this, "Error al realizar el pedido", Toast.LENGTH_SHORT).show();
                    }
                });

    }



}
