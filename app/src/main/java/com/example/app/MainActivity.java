package com.example.app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvSaldo;
    private Button btnTransferir, btnDepositar;
    private DatabaseHelper dbHelper;
    private String rut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSaldo = findViewById(R.id.tvSaldo);
        btnTransferir = findViewById(R.id.btnTransferir);
        btnDepositar = findViewById(R.id.btnDepositar);

        dbHelper = new DatabaseHelper(this);
        rut = getIntent().getStringExtra("Rut");

        mostrarSaldo();

        btnTransferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TransferActivity.class);
                intent.putExtra("Rut", rut);
                startActivity(intent);
            }
        });

        btnDepositar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DepositActivity.class);
                intent.putExtra("Rut", rut);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarSaldo();
    }

    private void mostrarSaldo() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Saldo FROM Cuenta WHERE RutCliente = ?", new String[]{rut});
            if (cursor.moveToFirst()) {
                double saldo = cursor.getDouble(0);
                tvSaldo.setText("Saldo: $" + saldo);
            } else {
                // Manejo de caso donde no se encuentra el saldo
                tvSaldo.setText("Saldo no encontrado");
            }
        } catch (Exception e) {
            // Manejo de excepciones, por ejemplo loggear el error
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
