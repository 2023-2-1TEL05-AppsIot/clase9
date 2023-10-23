package com.example.clase9;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.clase9.databinding.ActivityMain2Binding;
import com.example.clase9.dtos.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity2 extends AppCompatActivity {

    ActivityMain2Binding binding;
    FirebaseFirestore db;
    ListenerRegistration snapshotListener;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        binding.button.setOnClickListener(view -> {
            String nombre = binding.textFieldNombre.getEditText().getText().toString();
            String apellido = binding.textFieldApellido.getEditText().getText().toString();
            String edadStr = binding.textFieldEdad.getEditText().getText().toString();
            String dni = binding.textFieldDni.getEditText().getText().toString();

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEdad(Integer.parseInt(edadStr));

            if(currentUser != null){
                String uid = currentUser.getUid();
                //uid = "3bn8uBYB1pV7rhi7W7xGBOPJlf82";
                db.collection("usuarios_por_auth")
                        .document(uid)
                        .collection("mis_usuarios")
                        .add(usuario)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Usuario grabado", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Algo pasó al guardar ", Toast.LENGTH_SHORT).show();
                        });
            }else{
                Toast.makeText(MainActivity2.this, "No está logueado",Toast.LENGTH_SHORT).show();
            }

        });

        binding.btnTiempoReal.setOnClickListener(view -> {

            if(currentUser != null) {
                String uid = currentUser.getUid();

                snapshotListener = db.collection("usuarios_por_auth")
                        .document(uid)
                        .collection("mis_usuarios")
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
            }else{
                Toast.makeText(MainActivity2.this, "No está logueado",Toast.LENGTH_SHORT).show();
            }


        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (snapshotListener != null)
            snapshotListener.remove();
    }
}