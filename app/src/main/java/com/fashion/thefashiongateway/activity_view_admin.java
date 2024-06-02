package com.fashion.thefashiongateway;

import static com.fashion.thefashiongateway.activity_login.mAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.fashion.thefashiongateway.databinding.ActivityViewAdminBinding;

public class activity_view_admin extends AppCompatActivity {


    ActivityViewAdminBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), activity_login.class);
                startActivity(intent);
                finish();
            }
        });
        binding.btnCreateAdminAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activity_register.class);
                intent.putExtra("admin_register", true);
                startActivity(intent);
            }
        });
        binding.btnCreateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activity_create_product.class);
                startActivity(intent);
                finish();
            }
        });
        binding.btnViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activity_view_orders.class);
                startActivity(intent);
            }
        });
        binding.btnViewInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activity_view_stock.class);
                startActivity(intent);
            }
        });
    }



}