package com.example.app;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DepositActivity extends AppCompatActivity {

    private EditText etMonto;
    private Button btnDepositar;
    private DatabaseHelper dbHelper;
    private String rut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        etMonto = findViewById(R.id.etMonto);
        btnDepositar = findViewById(R.id.btnDepositar);
        dbHelper = new DatabaseHelper(this);
        rut = getIntent().getStringExtra("Rut");

        btnDepositar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                depositar();
            }
        });
    }

    private void depositar() {
        String montoStr = etMonto.getText().toString();
        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un monto", Toast.LENGTH_SHORT).show();
            return;
        }

        double monto = Double.parseDouble(montoStr);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.execSQL("UPDATE Cuenta SET Saldo = Saldo + ? WHERE RutCliente = ?", new Object[]{monto, rut});
            Toast.makeText(this, "Depósito realizado con éxito", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad y regresa a MainActivity
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al realizar el depósito", Toast.LENGTH_SHORT).show();
        }
    }
}
