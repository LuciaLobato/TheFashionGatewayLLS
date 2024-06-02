package com.fashion.thefashiongateway.classes;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fashion.thefashiongateway.adapters.ProductAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductsUtil {
    public ProductsUtil() {
    }

    // Método para actualizar la cantidad del producto en el carrito en la base de datos
    public static void updateQuantityInDatabase(Product product, int quantity, Context context) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cartRef = db.collection("users").document(userId).collection("cart");

        // Obtener la referencia del documento del producto en el carrito por su ID
        DocumentReference productRef = cartRef.document(product.getProductId());

        // Actualizar la cantidad del producto en el carrito
        productRef.update("quantity", quantity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Éxito al actualizar la cantidad en la base de datos
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al actualizar la cantidad en la base de datos
                        Toast.makeText(context, "Error al actualizar la cantidad en el carrito", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para eliminar un producto del carrito en la base de datos
    public static void deleteProductFromDatabase(Product product, Context context) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cartRef = db.collection("users").document(userId).collection("cart");

        // Obtener la referencia del documento del producto en el carrito por su ID
        DocumentReference productRef = cartRef.document(product.getProductId());

        // Eliminar el documento del producto en el carrito de la base de datos
        productRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Éxito al eliminar el producto del carrito en la base de datos
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al eliminar el producto del carrito en la base de datos
                        Toast.makeText(context, "Error al eliminar el producto del carrito", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // El spinner se adapta al type
    public static void getFilters(String type,String productType, Spinner spinner, Context context, FiltersObtainedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> tiposProductos = new ArrayList<>();
        tiposProductos.add("Todos");

        CollectionReference productsRef = db.collection("products");
        Query query = productsRef.whereEqualTo("type", productType);;

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String tipo = documentSnapshot.getString(type);
                            if (tipo != null && !tiposProductos.contains(tipo)) {
                                tiposProductos.add(tipo);
                            }
                        }
                        // Crear un ArrayAdapter con la lista de tipos de productos y configurar el Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, tiposProductos);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);

                        // Notificar al listener que se han obtenido los filtros
                        if (listener != null) {
                            listener.onFiltersObtained(tiposProductos);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error", "Error al obtener los tipos de productos", e);
                    }
                });
    }
    public interface FiltersObtainedListener {
        void onFiltersObtained(List<String> filters);
    }
//-------------------------
    //El spinner filtra por color
    public static void getColorFilters(String productType, Spinner spinner, Context context, FiltersObtainedListener listener, String colorType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> filters = new ArrayList<>();
        filters.add("Todos");

        CollectionReference productsRef = db.collection("products");
        Query query = productsRef; // No aplicamos ningún filtro inicial

        if (colorType!= null &&!colorType.isEmpty() &&!colorType.equals("Todos")) {
            query = query.whereEqualTo("color", colorType);
        }

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String filterValue = documentSnapshot.getString(productType);
                            if (filterValue!= null &&!filters.contains(filterValue)) {
                                filters.add(filterValue);
                            }
                        }
                        // Crear un ArrayAdapter con la lista de filtros y configurar el Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, filters);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);

                        // Notificar al listener que se han obtenido los filtros
                        if (listener!= null) {
                            listener.onFiltersObtained(filters);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error", "Error al obtener los filtros", e);
                    }
                });
    }

    public static void filterColor(RecyclerView recyclerView, Context context, String type, String colorFilter, String styleFilter, String priceOrder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("products");
        Query query = productsRef; // No aplicamos ningún filtro inicial

        // Aplicar filtros adicionales
        if (type!= null &&!type.isEmpty() &&!type.equals("Todos")) {
            query = query.whereEqualTo("type", type);
        }
        if (colorFilter!= null &&!colorFilter.isEmpty() &&!colorFilter.equals("Todos")) {
            query = query.whereEqualTo("color", colorFilter);
        }
        if (styleFilter!= null &&!styleFilter.isEmpty() &&!styleFilter.equals("Todos")) {
            query = query.whereEqualTo("style", styleFilter);
        }
        // Construir la consulta base

        // Consultar los productos en Firestore
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Product> productList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Obtener datos de cada producto
                    String id = documentSnapshot.getId(); // Obtener el ID del documento
                    String name = documentSnapshot.getString("name");
                    Double price = documentSnapshot.getDouble("price");
                    String imageURL = documentSnapshot.getString("imageURL");

                    // Crear un objeto Product con el ID y agregarlo a la lista
                    productList.add(new Product(id, imageURL, name, price));
                }

                // Aplicar ordenamiento por precio si es necesario
                if (priceOrder!= null &&!priceOrder.isEmpty()) {
                    Collections.sort(productList, new Comparator<Product>() {
                        @Override
                        public int compare(Product p1, Product p2) {
                            // Comparar los precios de los productos
                            if (priceOrder.equals("Más caro")) {
                                return Double.compare(p2.getPrice(), p1.getPrice()); // Orden descendente
                            } else {
                                return Double.compare(p1.getPrice(), p2.getPrice()); // Orden ascendente
                            }
                        }
                    });
                }

                // Crear y asignar adaptador al RecyclerView
                ProductAdapter productAdapter = new ProductAdapter(productList, context);
                GridLayoutManager layoutManager = new GridLayoutManager(context, 2);

                recyclerView.setAdapter(productAdapter);
                recyclerView.setLayoutManager(layoutManager);
            }
        });
    }
//--------------------------------
    // El spinner filtra todos los productos
    public static void getAllProductsFilters(String productType, Spinner spinner, Context context, FiltersObtainedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> filters = new ArrayList<>();
        filters.add("Todos");

        CollectionReference productsRef = db.collection("products");
        Query query = productsRef; // No aplicamos ningún filtro inicial

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String filterValue = documentSnapshot.getString(productType);
                            if (filterValue!= null &&!filters.contains(filterValue)) {
                                filters.add(filterValue);
                            }
                        }
                        // Crear un ArrayAdapter con la lista de filtros y configurar el Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, filters);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);

                        // Notificar al listener que se han obtenido los filtros
                        if (listener!= null) {
                            listener.onFiltersObtained(filters);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error", "Error al obtener los filtros", e);
                    }
                });
    }

    public static void filterProductsAll(RecyclerView recyclerView, Context context, String type, String colorFilter, String styleFilter, String priceOrder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("products");
        Query query = productsRef; // No aplicamos ningún filtro inicial

        // Aplicar filtros adicionales
        if (type != null && !type.isEmpty() && !type.equals("Todos")) {
            query = query.whereEqualTo("type", type);
        }
        if (colorFilter != null && !colorFilter.isEmpty() && !colorFilter.equals("Todos")) {
            query = query.whereEqualTo("color", colorFilter);
        }
        if (styleFilter != null && !styleFilter.isEmpty() && !styleFilter.equals("Todos")) {
            query = query.whereEqualTo("style", styleFilter);
        }
        // Construir la consulta base

        // Consultar los productos en Firestore
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Product> productList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Obtener datos de cada producto
                    String id = documentSnapshot.getId(); // Obtener el ID del documento
                    String name = documentSnapshot.getString("name");
                    Double price = documentSnapshot.getDouble("price");
                    String imageURL = documentSnapshot.getString("imageURL");

                    // Crear un objeto Product con el ID y agregarlo a la lista
                    productList.add(new Product(id, imageURL, name, price));
                }

                // Aplicar ordenamiento por precio si es necesario
                if (priceOrder != null && !priceOrder.isEmpty()) {
                    Collections.sort(productList, new Comparator<Product>() {
                        @Override
                        public int compare(Product p1, Product p2) {
                            // Comparar los precios de los productos
                            if (priceOrder.equals("Más caro")) {
                                return Double.compare(p2.getPrice(), p1.getPrice()); // Orden descendente
                            } else {
                                return Double.compare(p1.getPrice(), p2.getPrice()); // Orden ascendente
                            }
                        }
                    });
                }

                // Crear y asignar adaptador al RecyclerView
                ProductAdapter productAdapter = new ProductAdapter(productList, context);
                GridLayoutManager layoutManager = new GridLayoutManager(context, 2);

                recyclerView.setAdapter(productAdapter);
                recyclerView.setLayoutManager(layoutManager);
            }
        });
    }

    // Método para obtener un producto por su ID
    public static void getProductById(String productId, OnProductFetchListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("products");

        // Consultar el producto por su ID
        productsRef.document(productId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener datos del documento
                            String id = documentSnapshot.getId(); // Obtener el ID del documento
                            String name = documentSnapshot.getString("name");
                            Double price = documentSnapshot.getDouble("price");
                            String imageURL = documentSnapshot.getString("imageURL");

                            // Crear un objeto Product y pasarlo al listener
                            Product product = new Product(id, imageURL, name, price);
                            listener.onProductFetchSuccess(product);
                        } else {
                            // El documento no existe
                            listener.onProductFetchFailure("El producto no existe");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar errores
                        listener.onProductFetchFailure(e.getMessage());
                    }
                });
    }

    // Interfaz para manejar los resultados de la búsqueda de productos
    public interface OnProductFetchListener {
        void onProductFetchSuccess(Product product);
        void onProductFetchFailure(String errorMessage);
    }

    public static void filterProducts(RecyclerView recyclerView, Context context, String type, String colorFilter, String styleFilter, String priceOrder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("products");
        Query query = productsRef.whereEqualTo("type", type);;

        // Aplicar filtros adicionales
        if (colorFilter != null && !colorFilter.isEmpty() && !colorFilter.equals("Todos")) {
            query = query.whereEqualTo("color", colorFilter);
        }
        if (styleFilter != null && !styleFilter.isEmpty() && !styleFilter.equals("Todos")) {
            query = query.whereEqualTo("style", styleFilter);
        }
        // Construir la consulta base


        // Consultar los productos en Firestore
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Product> productList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Obtener datos de cada producto
                    String id = documentSnapshot.getId(); // Obtener el ID del documento
                    String name = documentSnapshot.getString("name");
                    Double price = documentSnapshot.getDouble("price");
                    String imageURL = documentSnapshot.getString("imageURL");

                    // Crear un objeto Product con el ID y agregarlo a la lista
                    productList.add(new Product(id, imageURL, name, price));
                }

                // Aplicar ordenamiento por precio si es necesario
                if (priceOrder != null && !priceOrder.isEmpty()) {
                    Collections.sort(productList, new Comparator<Product>() {
                        @Override
                        public int compare(Product p1, Product p2) {
                            // Comparar los precios de los productos
                            if (priceOrder.equals("Más caro")) {
                                return Double.compare(p2.getPrice(), p1.getPrice()); // Orden descendente
                            } else {
                                return Double.compare(p1.getPrice(), p2.getPrice()); // Orden ascendente
                            }
                        }
                    });
                }

                // Crear y asignar adaptador al RecyclerView
                ProductAdapter productAdapter = new ProductAdapter(productList, context);
                GridLayoutManager layoutManager = new GridLayoutManager(context, 2);

                recyclerView.setAdapter(productAdapter);
                recyclerView.setLayoutManager(layoutManager);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar errores al recuperar datos de Firestore
            }
        });
    }

    public static void fetchProductsFromFirestoreByName(RecyclerView recyclerView, Context context, String productName){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("products");

        // Consultar todos los productos en Firestore
        productsRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Product> productList = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Obtener datos de cada producto
                            String id = documentSnapshot.getId();
                            String name = documentSnapshot.getString("name");
                            Double price = documentSnapshot.getDouble("price");
                            String imageURL = documentSnapshot.getString("imageURL");

                            // Filtrar por nombre sin importar mayúsculas o minúsculas
                            if (name != null && name.toLowerCase().contains(productName.toLowerCase())) {
                                // Crear un objeto Product y agregarlo a la lista
                                productList.add(new Product(id, imageURL, name, price));
                            }
                        }

                        // Crear y asignar adaptador al RecyclerView
                        ProductAdapter productAdapter = new ProductAdapter(productList, context);
                        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);

                        recyclerView.setAdapter(productAdapter);
                        recyclerView.setLayoutManager(layoutManager);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar errores al recuperar datos de Firestore
                    }
                });
    }

    public static void fetchProductsFromFirestore(RecyclerView recyclerView, Context context, String type){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("products");
        Query query = productsRef;
        if (!type.equals("any")){
            query = productsRef.whereEqualTo("type", type);

        }

        // Consultar los abrigos en Firestore
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Product> productList = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Obtener datos de cada abrigo
                            String id = documentSnapshot.getId(); // Obtener el ID del documento
                            String name = documentSnapshot.getString("name");
                            Double price = documentSnapshot.getDouble("price");
                            String imageURL = documentSnapshot.getString("imageURL");

                            // Crear un objeto Product con el ID y agregarlo a la lista
                            productList.add(new Product(id, imageURL, name, price));
                        }

                        // Crear y asignar adaptador al RecyclerView
                        ProductAdapter productAdapter = new ProductAdapter(productList, context);
                        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);

                        recyclerView.setAdapter(productAdapter);
                        recyclerView.setLayoutManager(layoutManager);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar errores al recuperar datos de Firestore
                    }
                });
    }

    public static void updateStock(String productId, int quantity, OnStockUpdateListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("products");

        // Obtener la referencia del documento del producto
        System.out.println(productId);
        DocumentReference productRef = productsRef.document(productId);

        // Atomically reduce the stock by the purchased quantity
        productRef.update("stock", quantity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Stock updated successfully
                        listener.onStockUpdateSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to update stock
                        listener.onStockUpdateFailure(e.getMessage());
                    }
                });
    }
    public static void reduceStock(String productId, int quantity, OnStockUpdateListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("products");

        // Obtener la referencia del documento del producto
        DocumentReference productRef = productsRef.document(productId);

        // Atomically reduce the stock by the purchased quantity
        productRef.update("stock", FieldValue.increment(-quantity))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Stock updated successfully
                        listener.onStockUpdateSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to update stock
                        listener.onStockUpdateFailure(e.getMessage());
                    }
                });
    }

    public interface OnStockUpdateListener {
        void onStockUpdateSuccess();
        void onStockUpdateFailure(String errorMessage);
    }

    public static void checkStock(String productId, int requestedQuantity, OnStockCheckListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("products");

        // Consultar el producto por su ID
        productsRef.document(productId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener el stock del producto
                            int stock = documentSnapshot.getLong("stock").intValue();

                            // Comprobar si hay suficiente stock
                            if (stock >= requestedQuantity ) {
                                // Hay suficiente stock
                                listener.onStockCheckSuccess(true);
                            } else {
                                // No hay suficiente stock
                                listener.onStockCheckSuccess(false);
                            }
                        } else {
                            // El documento no existe
                            listener.onStockCheckFailure("El producto no existe");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar errores
                        listener.onStockCheckFailure(e.getMessage());
                    }
                });
    }

    public static void emptyCart(Context context, OnCartEmptyListener listener) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cartRef = db.collection("users").document(userId).collection("cart");

        // Eliminar todos los documentos del carrito del usuario
        cartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Iterar sobre los documentos y eliminarlos
                    for (DocumentSnapshot document : task.getResult()) {
                        cartRef.document(document.getId()).delete();
                    }
                    // Notificar al listener que el carrito ha sido vaciado
                    if (listener != null) {
                        listener.onCartEmptySuccess();
                    }
                } else {
                    // Error al obtener los documentos del carrito
                    Toast.makeText(context, "Error al vaciar el carrito", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface OnStockCheckListener {
        void onStockCheckSuccess(boolean isAvailable);
        void onStockCheckFailure(String errorMessage);
    }

    public interface OnCartEmptyListener {
        void onCartEmptySuccess();
    }
}