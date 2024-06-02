package com.fashion.thefashiongateway.ui.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.fashion.thefashiongateway.R;
import com.fashion.thefashiongateway.activity_main;
import com.fashion.thefashiongateway.databinding.FragmentHomeBinding;
import com.fashion.thefashiongateway.ui.offers.OffersFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady);
        // Dentro de un Fragmento

        // Botón todos los productos
        binding.btnOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity_main.navController != null) {
                    activity_main.navController.navigate(R.id.offersFragment);
                }
            }
        });




        return root;
    }


    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Configurar el mapa, como el estilo, gestos, etc.
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Coordenadas para centrar el mapa en una dirección específica
        LatLng location = new LatLng(37.7749, -122.4194);
        googleMap.addMarker(new MarkerOptions().position(location).title("San Francisco"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}