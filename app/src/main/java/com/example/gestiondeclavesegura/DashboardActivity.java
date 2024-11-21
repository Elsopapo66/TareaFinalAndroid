package com.example.gestiondeclavesegura;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.crypto.SecretKey;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView rvPasswords;
    private PasswordsRecyclerAdapter adapter;
    private List<PasswordModel> passwordList;
    private FirebaseFirestore db;
    private FloatingActionButton fabAddPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Inicializar RecyclerView
        rvPasswords = findViewById(R.id.rvPasswords);
        rvPasswords.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista de contraseñas y Adapter
        passwordList = new ArrayList<>();
        adapter = new PasswordsRecyclerAdapter(passwordList, new PasswordsRecyclerAdapter.OnPasswordActionListener() {
            @Override
            public void onEdit(PasswordModel password) {
                Intent intent = new Intent(DashboardActivity.this, AddPasswordActivity.class);
                intent.putExtra("passwordId", password.getId());
                intent.putExtra("siteName", password.getSiteName());
                intent.putExtra("username", password.getUsername());
                intent.putExtra("password", password.getPassword());
                intent.putExtra("notes", password.getNotes());
                startActivity(intent);
            }

            @Override
            public void onDelete(PasswordModel password) {
                db.collection("passwords").document(password.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(DashboardActivity.this, "Contraseña eliminada", Toast.LENGTH_SHORT).show();
                            loadPasswords();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(DashboardActivity.this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
        rvPasswords.setAdapter(adapter);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Configurar botón flotante
        fabAddPassword = findViewById(R.id.fabAddPassword);
        fabAddPassword.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddPasswordActivity.class);
            startActivity(intent);
        });

        // Autenticación biométrica antes de cargar las contraseñas
        authenticateUser();
    }

    private void authenticateUser() {
        BiometricManager biometricManager = BiometricManager.from(this);

        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                showBiometricPrompt();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "El dispositivo no tiene hardware biométrico", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "El hardware biométrico no está disponible", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No hay huellas digitales o datos biométricos registrados", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Error desconocido con el hardware biométrico", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Cargar contraseñas después de la autenticación exitosa
                loadPasswords();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(DashboardActivity.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(DashboardActivity.this, "Error: " + errString, Toast.LENGTH_SHORT).show();
                finish(); // Cierra la actividad si falla la autenticación
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación Biométrica")
                .setSubtitle("Autentícate para ver tus contraseñas")
                .setNegativeButtonText("Cancelar")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void loadPasswords() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("passwords")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    passwordList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        PasswordModel password = document.toObject(PasswordModel.class);
                        password.setId(document.getId());

                        try {
                            SecretKey key = EncryptionUtils.generateKey(userId);
                            String decryptedPassword = EncryptionUtils.decrypt(password.getPassword(), key);
                            password.setPassword(decryptedPassword);
                        } catch (Exception e) {
                            Log.e("DashboardActivity", "Error al descifrar contraseña", e);
                        }

                        passwordList.add(password);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar contraseñas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
