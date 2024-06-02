package com.fashion.thefashiongateway.ui.skirts;

import static com.fashion.thefashiongateway.classes.ProductsUtil.filterProducts;
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

import com.fashion.thefashiongateway.adapters.ProductAdapter;
import com.fashion.thefashiongateway.classes.Product;
import com.fashion.thefashiongateway.classes.ProductsUtil;
import com.fashion.thefashiongateway.databinding.FragmentSkirtsBinding;

import java.util.ArrayList;
import java.util.List;

public class SkirtsFragment extends Fragment {

    private FragmentSkirtsBinding binding;

    private RecyclerView recyclerView;
    private String type = "faldas";
    String selectedColor,selectedPriceOption,selectedStyle;
    public static SkirtsFragment newInstance() {
        return new SkirtsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento y asignar la vista al enlace de datos
        binding = FragmentSkirtsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        List<Product> productList = new ArrayList<>();
        ProductAdapter productAdapter = new ProductAdapter(productList,getContext());
        recyclerView = binding.skirtsRecyclerView;
        recyclerView.setAdapter(productAdapter);
        // Obtener referencia al Spinner desde el enlace de datos
        getFilters("color", type,binding.filterSpinnerColor, getContext(), new ProductsUtil.FiltersObtainedListener() {
            @Override
            public void onFiltersObtained(List<String> filters) {
                binding.filterSpinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        selectedColor = adapterView.getItemAtPosition(i).toString();
                        filterProducts(recyclerView,getContext(),type,selectedColor,selectedStyle,selectedPriceOption);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });
        getFilters("style", type,binding.filterSpinnerEstilo, getContext(), new ProductsUtil.FiltersObtainedListener() {
            @Override
            public void onFiltersObtained(List<String> filters) {
                binding.filterSpinnerEstilo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        selectedStyle = adapterView.getItemAtPosition(i).toString();
                        filterProducts(recyclerView,getContext(),type,selectedColor,selectedStyle,selectedPriceOption);

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
                filterProducts(recyclerView,getContext(),type,selectedColor,selectedStyle,selectedPriceOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Manejar la opción cuando no se selecciona nada
            }
        });



        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);


        recyclerView.setLayoutManager(layoutManager);


        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        filterProducts(recyclerView,getContext(),type,selectedColor,selectedStyle,selectedPriceOption);
    }
}
