package com.fashion.thefashiongateway;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class activity_view_profile extends AppCompatActivity {
    TextView txtName, txtSurname, txtEmail, txtTlf, txtAddress;
    ImageView btnBack;
    Button btnLogout;
    FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        txtName = findViewById(R.id.txtName);
        txtSurname = findViewById(R.id.txtSurname);
        txtEmail = findViewById(R.id.txtEmail);
        txtTlf = findViewById(R.id.txtTlf);
        txtAddress = findViewById(R.id.txtAddress);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        progressBar.setVisibility(View.VISIBLE);

        // Obtener el usuario actualmente autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Obtener el ID del usuario actual
            String userId = currentUser.getUid();

            // Obtener los datos del usuario desde Firestore
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        progressBar.setVisibility(View.GONE);

                        // Obtener los datos del documento y mostrarlos en los TextViews
                        String name = documentSnapshot.getString("name");
                        String surname = documentSnapshot.getString("surname");
                        String email = documentSnapshot.getString("email");
                        String tlf = documentSnapshot.getString("tlf");
                        String address = documentSnapshot.getString("address");

                        txtName.setText(name);
                        txtSurname.setText(surname);
                        txtEmail.setText( email);
                        txtTlf.setText(tlf);
                        txtAddress.setText(address);
                    } else {
                        progressBar.setVisibility(View.GONE);

                        // El documento del usuario no existe
                        Toast.makeText(getApplicationContext(), "El usuario no tiene datos almacenados", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error al obtener los datos del usuario
                    Toast.makeText(getApplicationContext(), "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                }
            });
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Configurar el botón para cerrar sesión
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), activity_login.class);
                startActivity(intent);
                finish();
            }
        });

    }
}