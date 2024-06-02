package com.fashion.thefashiongateway.ui;

import static com.fashion.thefashiongateway.classes.ProductsUtil.fetchProductsFromFirestoreByName;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.fashion.thefashiongateway.R;
import com.fashion.thefashiongateway.activity_main;
import com.fashion.thefashiongateway.classes.ProductsUtil;
import com.fashion.thefashiongateway.databinding.FragmentSearchBinding;


public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // Configurar RecyclerView
        recyclerView = binding.productsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);


        recyclerView.setLayoutManager(layoutManager);

        ProductsUtil.fetchProductsFromFirestore(recyclerView, getContext(), "any");
        androidx.appcompat.widget.SearchView searchView = activity_main.searchView;
        if (searchView != null) {

            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    fetchProductsFromFirestoreByName(recyclerView, getContext(), newText);

                    return false;
                }
            });

        }
        return rootView;
        }
    }
