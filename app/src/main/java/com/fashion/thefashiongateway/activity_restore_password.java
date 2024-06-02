package com.fashion.thefashiongateway;

import static com.fashion.thefashiongateway.activity_login.mAuth;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.fashion.thefashiongateway.databinding.ActivityRestorePasswordBinding;

public class activity_restore_password extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityRestorePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRestorePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPasswordResetEmail();
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void sendPasswordResetEmail() {
        TextView txt_email = findViewById(R.id.txtEmail);
        String email = txt_email.getText().toString();

        if (!email.isEmpty()) {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Se ha enviado un correo para cambiar la contraseña", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Fallo al enviar el correo, prueba otra vez", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(),"Alguno de los campos está vacío ", Toast.LENGTH_SHORT).show();
        }
    }

}