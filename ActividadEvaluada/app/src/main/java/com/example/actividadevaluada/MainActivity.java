package com.example.actividadevaluada;

//importaciones

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

//importaciones del firebase

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//Public class del mainactivity
public class MainActivity extends AppCompatActivity {

    //datos y variables
    private Spinner spPerfil;
    private EditText txtCorreo, txtNombre, txtRut, txtDireccion;
    private ListView lista;
    private FirebaseFirestore db;
//Tipos de trabajador para elegir en el spinner
    String[] TiposTrabajor = {"Independiente", "De empresa", "Parcial"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtCorreo = findViewById(R.id.txtCorreo);

        txtNombre = findViewById(R.id.txtNombre);

        txtRut = findViewById(R.id.txtRut);

        txtDireccion = findViewById(R.id.txtDireccion);

        spPerfil = findViewById(R.id.spTrabajor);

        lista = findViewById(R.id.lista);

        db = FirebaseFirestore.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TiposTrabajor);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Cambiado a dropdown
        spPerfil.setAdapter(adapter);

        CargarListaFirestore();
    }
//public void para mandar los datos y el mensaje que dara dependiendo de la situacion
    public void enviarDatosFirestore(View view) {
        String correo = txtCorreo.getText().toString();
        String nombre = txtNombre.getText().toString();
        String rut = txtRut.getText().toString();
        String direccion = txtDireccion.getText().toString();
        String TiposTrabajor = spPerfil.getSelectedItem().toString();

        Map<String, Object> trabajador = new HashMap<>();
        trabajador.put("correo", correo);
        trabajador.put("nombre", nombre);
        trabajador.put("rut", rut);
        trabajador.put("direccion", direccion);
        trabajador.put("TiposTrabajor", TiposTrabajor);

        db.collection("Trabajador")

                .document(correo)
                .set(trabajador)
                .addOnSuccessListener(aVoid -> {
                //si se envian correctamente
                    Toast.makeText(MainActivity.this, "Datos enviados exitosamente", Toast.LENGTH_SHORT).show();

                    CargarListaFirestore();
                })

                //si no se envian correctamente
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error al mandar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }


    public void CargarLista(View view) {
        CargarListaFirestore();
    }

    public void CargarListaFirestore() {
        db.collection("Trabajor")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> listaTrabajadores = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String linea = "||" + document.getString("correo") + "||" +
                                        document.getString("nombre") + "||" +
                                        document.getString("rut") + "||" +
                                        document.getString("direccion");
                                listaTrabajadores.add(linea);
                            }
                            ArrayAdapter<String> adaptador = new ArrayAdapter<>(

                                    MainActivity.this,

                                    android.R.layout.simple_list_item_1,

                                    listaTrabajadores
                            );

                            lista.setAdapter(adaptador);

                        } else {

                            Log.e("TAG", "Error al conseguir los datos", task.getException());
                        }

                    }

                });
    }
}