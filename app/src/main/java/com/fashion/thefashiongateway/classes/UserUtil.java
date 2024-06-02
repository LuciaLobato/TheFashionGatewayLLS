package com.fashion.thefashiongateway.classes;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserUtil {

    public static void checkIfUserIsAdmin(final OnAdminCheckCompleteListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore.getInstance().collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                boolean isAdmin = document.getBoolean("admin");
                                listener.onAdminCheckComplete(isAdmin);
                            } else {
                                // El documento del usuario no existe
                                listener.onAdminCheckComplete(false);
                            }
                        } else {
                            // Error al obtener los datos del usuario
                            listener.onAdminCheckComplete(false);
                        }
                    });
        } else {
            // No hay usuario autenticado
            listener.onAdminCheckComplete(false);
        }
    }

    public interface OnAdminCheckCompleteListener {
        void onAdminCheckComplete(boolean isAdmin);
    }

}
