package com.example.clase9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.clase9.databinding.ActivityGatoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;

public class GatoActivity extends AppCompatActivity {

    ActivityGatoBinding binding;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGatoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.button.setOnClickListener(view -> {
            String nombre = binding.textFieldNombre.getEditText().getText().toString();
            String raza = binding.textFieldRaza.getEditText().getText().toString();

            HashMap<String, Object> gatoMap = new HashMap<>();
            gatoMap.put("nombre", nombre);
            gatoMap.put("raza", raza);
            gatoMap.put("fecha_server", FieldValue.serverTimestamp());
            gatoMap.put("fecha_android", new Date().toString());

            db.collection("gatos")
                    .add(gatoMap)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Gato guardado exitosamente", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("msg-test", e.getMessage());
                        e.printStackTrace();
                    });

        });

        binding.btnListarGatos.setOnClickListener(view -> {

            db.collection("gatos")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot gatosCollection = task.getResult();

                            for (QueryDocumentSnapshot document : gatosCollection) {

                                String nombre = (String) document.get("nombre");
                                String raza = document.getString("raza");
                                String documentId = document.getId();
                                if (document.contains("fecha_server")) {
                                    Date date = document.getDate("fecha_server");
                                    Log.d("msg-test", "documentId: " + documentId + " | nombre: " + nombre + " | raza: " + raza + " | fecha_server: " + date.toString());
                                } else {
                                    Log.d("msg-test", "documentId: " + documentId + " | nombre: " + nombre + " | raza: " + raza);
                                }

                            }

                        }
                    });


        });

    }
}