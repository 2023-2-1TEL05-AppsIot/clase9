package com.example.clase9;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.clase9.databinding.ActivityMainBinding;
import com.example.clase9.dtos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ActivityMainBinding binding;
    ListenerRegistration snapshotListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.button.setOnClickListener(view -> {
            String nombre = binding.textFieldNombre.getEditText().getText().toString();
            String apellido = binding.textFieldApellido.getEditText().getText().toString();
            String edadStr = binding.textFieldEdad.getEditText().getText().toString();
            String dni = binding.textFieldDni.getEditText().getText().toString();

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEdad(Integer.parseInt(edadStr));

            db.collection("usuarios")
                    .document(dni)
                    .set(usuario)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Usuario grabado", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Algo pasó al guardar ", Toast.LENGTH_SHORT).show();
                    });
        });

        binding.fabNextActivity.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GatoActivity.class);
            startActivity(intent);
        });

        binding.btnListarUsuarios.setOnClickListener(view -> {
            binding.btnListarUsuarios.setEnabled(false);
            String dni = binding.textFieldDni.getEditText().getText().toString();

            db.collection("usuarios")
                    .document(dni)
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Log.d("msg-test", "DocumentSnapshot data: " + documentSnapshot.getData());

                                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                Toast.makeText(this, "Nombre: " + usuario.getNombre() + " | apellido: " + usuario.getApellido(), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                            }
                        }

                        binding.btnListarUsuarios.setEnabled(true);
                    });
        });

        binding.btnTiempoReal.setOnClickListener(view -> {

            snapshotListener = db.collection("usuarios")
                    .addSnapshotListener((collection, error) -> {

                        if (error != null) {
                            Log.w("msg-test", "Listen failed.", error);
                            return;
                        }

                        Log.d("msg-test", "---- Datos en tiempo real ----");
                        for (QueryDocumentSnapshot doc : collection) {
                            Usuario usuario = doc.toObject(Usuario.class);
                            Log.d("msg-test", "Nombre: " + usuario.getNombre() + " | apellido: " + usuario.getApellido());
                        }

                    });
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        snapshotListener.remove();
    }
}