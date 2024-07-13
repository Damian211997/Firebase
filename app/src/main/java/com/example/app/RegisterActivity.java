package com.example.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRut, etNombre, etApellidoP, etApellidoM, etTelefono, etContrasena, etCorreo;
    private Button btnRegister;
    private DatabaseHelper dbHelper;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRut = findViewById(R.id.etRut);
        etNombre = findViewById(R.id.etNombre);
        etApellidoP = findViewById(R.id.etApellidoP);
        etApellidoM = findViewById(R.id.etApellidoM);
        etTelefono = findViewById(R.id.etTelefono);
        etContrasena = findViewById(R.id.etContrasena);
        etCorreo = findViewById(R.id.etCorreo);
        btnRegister = findViewById(R.id.btnRegister);

        dbHelper = new DatabaseHelper(this);

        // Inicializa la referencia de Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("transacciones");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rut = etRut.getText().toString();
                String nombre = etNombre.getText().toString();
                String apellidoP = etApellidoP.getText().toString();
                String apellidoM = etApellidoM.getText().toString();
                String telefono = etTelefono.getText().toString();
                String contrasena = etContrasena.getText().toString();
                String correo = etCorreo.getText().toString();

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("Rut", rut);
                values.put("Nombre", nombre);
                values.put("Apellido_Paterno", apellidoP);
                values.put("Apellido_Materno", apellidoM);
                values.put("Telefono", telefono);
                values.put("Contrasena", contrasena);
                values.put("Correo_Electronico", correo);

                long newRowId = db.insert("Cliente", null, values);
                if (newRowId != -1) {
                    ContentValues cuentaValues = new ContentValues();
                    cuentaValues.put("RutCliente", rut);
                    cuentaValues.put("Saldo", 1000); // Saldo inicial
                    cuentaValues.put("Detalle_de_Cuenta", "Cuenta estándar");
                    db.insert("Cuenta", null, cuentaValues);

                    Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                    // Generar y almacenar el código de validación en Firebase
                    String codigoValidacion = UUID.randomUUID().toString();
                    Map<String, String> transaccion = new HashMap<>();
                    transaccion.put("codigoValidacion", codigoValidacion);
                    databaseReference.child(codigoValidacion).setValue(transaccion);

                    // Imprimir el código de validación en el log
                    Log.d("RegisterActivity", "Código de validación: " + codigoValidacion);

                    // Redirigir a LoginActivity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Finaliza RegisterActivity para evitar volver atrás con el botón de retroceso
                } else {
                    Toast.makeText(RegisterActivity.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
