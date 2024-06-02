package com.fashion.thefashiongateway.ui.offers;

import static com.fashion.thefashiongateway.classes.ProductsUtil.filterProducts;
import static com.fashion.thefashiongateway.classes.ProductsUtil.filterProductsAll;
import static com.fashion.thefashiongateway.classes.ProductsUtil.getAllProductsFilters;
import static com.fashion.thefashiongateway.classes.ProductsUtil.getFilters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.fashion.thefashiongateway.classes.ProductsUtil;
import com.fashion.thefashiongateway.databinding.FragmentOffersBinding;

import java.util.ArrayList;
import java.util.List;


public class OffersFragment extends Fragment {


    private FragmentOffersBinding binding;
    private RecyclerView recyclerView;

    String selectedColor,selectedPriceOption,selectedStyle;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOffersBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // Obtener referencia al Spinner desde el enlace de datos
        getAllProductsFilters("color", binding.filterSpinnerColor, getContext(), new ProductsUtil.FiltersObtainedListener() {
            @Override
            public void onFiltersObtained(List<String> filters) {
                binding.filterSpinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedColor = adapterView.getItemAtPosition(i).toString();
                        filterProductsAll(recyclerView, getContext(), "Todos", selectedColor, selectedStyle, selectedPriceOption);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });

        getAllProductsFilters("style", binding.filterSpinnerEstilo, getContext(), new ProductsUtil.FiltersObtainedListener() {
            @Override
            public void onFiltersObtained(List<String> filters) {
                binding.filterSpinnerEstilo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedStyle = adapterView.getItemAtPosition(i).toString();
                        filterProductsAll(recyclerView, getContext(), "Todos", selectedColor, selectedStyle, selectedPriceOption);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });

        // Datos de ejemplo para el Spinner
        ArrayList<String> opciones = new ArrayList<>();
        opciones.add("Más caro");
        opciones.add("Más barato");
        // Crear un ArrayAdapter usando el contexto de la actividad y el layout predeterminado para los elementos del Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, opciones);
        // Especificar el diseño del menú desplegable
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Asignar el ArrayAdapter al Spinner
        binding.filterSpinnerPrecio.setAdapter(adapter);
        // Configurar el listener para el Spinner
        binding.filterSpinnerPrecio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // Obtener la opción seleccionada
                selectedPriceOption = adapterView.getItemAtPosition(i).toString();
                filterProductsAll(recyclerView, getContext(), "Todos", selectedColor, selectedStyle, selectedPriceOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Manejar la opción cuando no se selecciona nada
            }
        });



        // Configurar RecyclerView
        recyclerView = binding.productsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);


        recyclerView.setLayoutManager(layoutManager);


        return rootView;
    }




    @Override
    public void onResume() {
        super.onResume();
        filterProductsAll(recyclerView,getContext(), "Todos", selectedColor, selectedStyle, selectedPriceOption);
    }


}