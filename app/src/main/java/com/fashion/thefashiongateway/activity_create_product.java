package com.fashion.thefashiongateway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fashion.thefashiongateway.classes.Product;
import com.fashion.thefashiongateway.databinding.ActivityCreateProductBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class activity_create_product extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;



    private Uri imageUri;
    private String imageUrl;

    private StorageReference storageReference;
    private ActivityCreateProductBinding binding;
    private Boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener la referencia al Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.imgSelectProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        binding.btnCreateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProduct();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.imgSelectProduct.setImageURI(imageUri);
        }
    }

    private void uploadProduct() {
        String productName = binding.txtName.getText().toString().trim();
        String productColor = binding.txtColor.getText().toString().trim();
        String productType = binding.txtType.getText().toString().trim();
        String productStyle = binding.txtStyle.getText().toString().trim();
        String productPriceStr = binding.txtPrice.getText().toString().trim();
        String productStockStr = binding.txtStock.getText().toString().trim();


        if (productName.isEmpty() || productColor.isEmpty() || productType.isEmpty() || productStyle.isEmpty() ||
                productPriceStr.isEmpty() || productStockStr.isEmpty()) {
            Toast.makeText(activity_create_product.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null){
            Toast.makeText(activity_create_product.this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        double productPrice = Double.parseDouble(productPriceStr);
        int productStock = Integer.parseInt(productStockStr);

        // Subir la imagen al Firebase Storage
        StorageReference fileReference = storageReference.child("imagenes/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Obtener la URL de la imagen subida
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                imageUrl = uri.toString();
                                // Crear un nuevo objeto Producto con los datos proporcionados
                                Product product = new Product();
                                product.setName(productName);
                                product.setImageURL(imageUrl);
                                product.setColor(productColor);
                                product.setType(productType);
                                product.setStyle(productStyle);
                                product.setPrice(productPrice);
                                product.setStock(productStock);

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                //Se sube el producto a firebase
                                db.collection("products")
                                        .add(product)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(activity_create_product.this, "Producto creado exitosamente", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(activity_create_product.this, "Error al crear el producto", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity_create_product.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private String getFileExtension(Uri uri) {
        return Objects.requireNonNull(getContentResolver().getType(uri)).split("/")[1];
    }
}