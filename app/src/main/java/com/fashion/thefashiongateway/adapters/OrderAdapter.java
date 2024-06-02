package com.fashion.thefashiongateway.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fashion.thefashiongateway.R;
import com.fashion.thefashiongateway.classes.Order;
import com.fashion.thefashiongateway.classes.Product;
import com.fashion.thefashiongateway.classes.ProductsUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context mContext;
    private List<Order> mOrderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        mContext = context;
        mOrderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {


        if (mOrderList != null && position < mOrderList.size()) {
            Order order= mOrderList.get(position);

            // Obtener y mostrar el ID del pedido
            String orderId = order.getId();
            holder.textOrderId.setText("ID del Pedido: " + orderId);
            CollectionReference productsRef = FirebaseFirestore.getInstance().collection("orders").document(orderId).collection("products");
            // Obtener y mostrar la fecha del pedido
            Timestamp timestamp = order.getTimestamp();
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String orderDate = sdf.format(date);
            holder.textOrderDate.setText("Fecha del Pedido: " + orderDate);
            List<Product> productsList =order.getProducts();
            if (productsList != null) {
                StringBuilder productsStringBuilder = new StringBuilder();
                for (Product product : productsList) {
                    String productName = product.getName();
                    int quantity = 1; // Valor predeterminado para la cantidad si no se proporciona en los datos del producto

                        quantity = product.getStock(); // Obtener la cantidad del producto si está disponible

                    productsStringBuilder.append(productName).append(" (").append(quantity).append("), ");
                }
                String products = productsStringBuilder.toString();
                if (products.length() > 0) {
                    products = products.substring(0, products.length() - 2); // Eliminar la coma y el espacio al final
                }
                holder.textOrderProducts.setText("Productos del Pedido: " + products);
            } else {
                holder.textOrderProducts.setText("No se encontraron productos para este pedido");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderId, textOrderDate, textOrderProducts, textOrderStatus;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.text_order_id);
            textOrderDate = itemView.findViewById(R.id.text_order_date);
            textOrderProducts = itemView.findViewById(R.id.text_order_products);
        }
    }
}
