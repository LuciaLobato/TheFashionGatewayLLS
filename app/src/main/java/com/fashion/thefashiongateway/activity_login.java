package com.fashion.thefashiongateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class activity_login extends AppCompatActivity {
    Button btn_login, btn_register,btn_forgot;
    TextView txt_no_register;
    EditText emailEditText, passwordEditText;
    Activity activity;
    public static FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_forgot = findViewById(R.id.btn_forgot);
        emailEditText = findViewById(R.id.txtEmail);
        passwordEditText = findViewById(R.id.txtPassword);
        activity = this;
        mAuth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, activity_register.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(activity_login.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(email, password);
                }
            }
        });
        btn_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, activity_restore_password.class);
                startActivity(intent);
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();
        // Verificar si hay un usuario actualmente autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Si hay un usuario autenticado, redirigirlo a la actividad principal
            Intent intent = new Intent(activity_login.this, activity_main.class);
            startActivity(intent);
            finish(); // Finalizar la actividad actual para que el usuario no pueda regresar a ella con el botón Atrás
        }
    }
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(activity, activity_main.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Error en el inicio de sesión
                            Toast.makeText(activity_login.this, "Error al iniciar sesión. Por favor, verifica tus credenciales", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
