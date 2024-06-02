package com.fashion.thefashiongateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class activity_register extends AppCompatActivity {
    EditText txtName, txtSurname, txtEmail, txtTlf, txtAddress,txtPassword;
    Button btnCreateAccount;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    Boolean adminRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Intent intent = getIntent();
        if (intent != null) {
            adminRegister = intent.getBooleanExtra("admin_register",false);
        }

        txtName = findViewById(R.id.txtName);
        txtSurname = findViewById(R.id.txtSurname);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtTlf = findViewById(R.id.txtTlf);
        txtAddress = findViewById(R.id.txtAddress);
        btnCreateAccount = findViewById(R.id.button2);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores de los campos de texto
                String name = txtName.getText().toString().trim();
                String surname = txtSurname.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String tlf = txtTlf.getText().toString().trim();
                String address = txtAddress.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                // Validar que ningún campo esté vacío
                if (name.isEmpty()) {
                    // Mostrar un mensaje de error si el campo nombre está vacío
                    Toast.makeText(activity_register.this, "Por favor, ingresa tu nombre", Toast.LENGTH_SHORT).show();
                } else if (surname.isEmpty()) {
                    // Mostrar un mensaje de error si el campo apellido está vacío
                    Toast.makeText(activity_register.this, "Por favor, ingresa tu apellido", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    // Mostrar un mensaje de error si el campo email está vacío
                    Toast.makeText(activity_register.this, "Por favor, ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
                } else if (tlf.isEmpty()) {
                    // Mostrar un mensaje de error si el campo teléfono está vacío
                    Toast.makeText(activity_register.this, "Por favor, ingresa tu número de teléfono", Toast.LENGTH_SHORT).show();
                } else if (address.isEmpty()) {
                    // Mostrar un mensaje de error si el campo dirección está vacío
                    Toast.makeText(activity_register.this, "Por favor, ingresa tu dirección", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    // Mostrar un mensaje de error si el campo contraseña está vacío
                    Toast.makeText(activity_register.this, "Por favor, ingresa tu contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    // Si todos los campos están completos, intentar registrar al usuario
                    registerUser(name, surname, email, tlf, password, address);
                }
            }
        });
    }

    private void registerUser(final String name, final String surname, final String email, final String tlf, final String password, final String address) {


        // Validación del formato del correo electrónico
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(activity_register.this, "Por favor, ingresa un correo electrónico válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de la fortaleza de la contraseña
        if (password.length() < 6) {
            Toast.makeText(activity_register.this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String user_id = mAuth.getCurrentUser().getUid();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", name);
                            userData.put("surname", surname);
                            userData.put("email", email);
                            userData.put("password", password);
                            if (adminRegister == true){
                                userData.put("admin", true);
                            } else {
                                userData.put("admin", false);

                            }
                            userData.put("tlf", tlf);
                            userData.put("address", address);
                            // Agregar la colección "cart" al documento del usuario
                            Map<String, Object> cartData = new HashMap<>();
                            cartData.put("products", new ArrayList<>()); // Inicializar la lista de productos del carrito

                            db.collection("users").document(user_id)
                                    .set(userData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast.makeText(activity_register.this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(activity_register.this, "Error al crear la cuenta. Por favor, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(activity_register.this, "El correo electrónico ya está en uso. Por favor, utiliza otro correo electrónico.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity_register.this, "Error al crear la cuenta. Por favor, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


}
