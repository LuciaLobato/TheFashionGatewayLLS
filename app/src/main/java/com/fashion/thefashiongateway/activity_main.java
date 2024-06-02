package com.fashion.thefashiongateway;

import static com.fashion.thefashiongateway.classes.UserUtil.checkIfUserIsAdmin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.TextView;

import com.fashion.thefashiongateway.classes.UserUtil;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.fashion.thefashiongateway.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class activity_main  extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private View action_user;
    public static NavController navController;
    Activity activity;
    public  static androidx.appcompat.widget.SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        action_user = findViewById(R.id.action_user);
        setSupportActionBar(binding.appBarActivityMain.toolbar);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);

        // Pasar cada ID de menú como un conjunto de IDs porque cada
        // menú debe ser considerado como un destino de nivel superior.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_trousers, R.id.nav_coats,R.id.nav_skirts,R.id.nav_complements)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Cambia el nombre de usuario y email del navigation view
        TextView textViewUsername = headerView.findViewById(R.id.textViewUsername);
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            textViewUsername.setText(displayName);
            textViewEmail.setText(email);
        }




    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú; esto agrega elementos a la barra de acciones si está presente
        getMenuInflater().inflate(R.menu.activity_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_activity_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Manejar clic en el elemento action_user
        if (id == R.id.action_user) {
            checkIfUserIsAdmin(new UserUtil.OnAdminCheckCompleteListener() {
                @Override
                public void onAdminCheckComplete(boolean isAdmin) {
                    if (isAdmin){
                        Intent intent = new Intent(activity, activity_view_admin.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(activity, activity_view_profile.class);
                        startActivity(intent);
                    }
                }
            });




            return true;
        }

        // Carrito
        if (id == R.id.action_cart) {

            Intent intent = new Intent(activity, activity_cart.class);
            startActivity(intent);


            return true;
        }
        if (id == R.id.action_search) {


            navController.navigate(R.id.searchFragment);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}