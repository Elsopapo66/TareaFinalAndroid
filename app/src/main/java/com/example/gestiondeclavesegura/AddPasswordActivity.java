package com.example.gestiondeclavesegura;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

public class AddPasswordActivity extends AppCompatActivity {

    private EditText etSiteName, etUsername, etPassword, etNotes;
    private Button btnSavePassword;
    private FirebaseFirestore db;
    private String passwordId; // ID del documento Firestore para saber si es edición

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        // Inicializar vistas
        etSiteName = findViewById(R.id.etSiteName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etNotes = findViewById(R.id.etNotes);
        btnSavePassword = findViewById(R.id.btnSavePassword);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Verificar si se está editando
        passwordId = getIntent().getStringExtra("passwordId");
        if (passwordId != null) {
            // Rellenar campos con los datos existentes
            etSiteName.setText(getIntent().getStringExtra("siteName"));
            etUsername.setText(getIntent().getStringExtra("username"));
            etPassword.setText(getIntent().getStringExtra("password"));
            etNotes.setText(getIntent().getStringExtra("notes"));
            btnSavePassword.setText("Actualizar Contraseña");
        }

        // Configurar acción del botón
        btnSavePassword.setOnClickListener(v -> saveOrUpdatePassword());
    }

    private void saveOrUpdatePassword() {
        String siteName = etSiteName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (siteName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Generar clave única para cifrar
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            SecretKey key = EncryptionUtils.generateKey(userId);

            // Cifrar contraseña
            String encryptedPassword = EncryptionUtils.encrypt(password, key);

            // Crear mapa de datos para Firestore
            Map<String, Object> passwordData = new HashMap<>();
            passwordData.put("siteName", siteName);
            passwordData.put("username", username);
            passwordData.put("password", encryptedPassword);
            passwordData.put("notes", notes);
            passwordData.put("userId", userId);

            if (passwordId != null) {
                // Si hay un ID, actualizamos
                db.collection("passwords")
                        .document(passwordId)
                        .update(passwordData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Si no hay ID, creamos una nueva
                db.collection("passwords")
                        .add(passwordData)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Contraseña guardada exitosamente", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error en el cifrado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
