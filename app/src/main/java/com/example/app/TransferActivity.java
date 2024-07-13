package com.example.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TransferActivity extends AppCompatActivity {

    private EditText etCuentaDestino, etMonto, etTipoTransferencia, etFecha, etCodigoValidacion;
    private Button btnTransferir;
    private DatabaseHelper dbHelper;
    private String rut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        etCuentaDestino = findViewById(R.id.etCuentaDestino);
        etMonto = findViewById(R.id.etMonto);
        etTipoTransferencia = findViewById(R.id.etTipoTransferencia);
        etFecha = findViewById(R.id.etFecha);
        etCodigoValidacion = findViewById(R.id.etCodigoValidacion);
        btnTransferir = findViewById(R.id.btnTransferir);

        dbHelper = new DatabaseHelper(this);
        rut = getIntent().getStringExtra("Rut");

        btnTransferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cuentaDestino = etCuentaDestino.getText().toString().trim();
                String montoStr = etMonto.getText().toString().trim();
                String tipoTransferencia = etTipoTransferencia.getText().toString().trim();
                String fecha = etFecha.getText().toString().trim();
                String codigoValidacion = etCodigoValidacion.getText().toString().trim();

                if (cuentaDestino.isEmpty() || montoStr.isEmpty() || tipoTransferencia.isEmpty() ||
                        fecha.isEmpty() || codigoValidacion.isEmpty()) {
                    Toast.makeText(TransferActivity.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                double monto;
                try {
                    monto = Double.parseDouble(montoStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(TransferActivity.this, "Ingrese un monto válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (monto <= 0) {
                    Toast.makeText(TransferActivity.this, "Ingrese un monto mayor que cero", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (realizarTransferencia(cuentaDestino, monto, tipoTransferencia, fecha, codigoValidacion)) {
                    Toast.makeText(TransferActivity.this, "Transferencia realizada con éxito", Toast.LENGTH_SHORT).show();
                    iniciarResumenTransferActivity(cuentaDestino, monto, tipoTransferencia, fecha, codigoValidacion);
                } else {
                    Toast.makeText(TransferActivity.this, "Error en la transferencia", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean realizarTransferencia(String cuentaDestino, double monto, String tipoTransferencia, String fecha, String codigoValidacion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursorOrigen = db.rawQuery("SELECT IdCuenta, Saldo FROM Cuenta WHERE RutCliente = ?", new String[]{rut});
        if (cursorOrigen.moveToFirst()) {
            int idCuentaOrigen = cursorOrigen.getInt(0);
            double saldoOrigen = cursorOrigen.getDouble(1);

            if (saldoOrigen >= monto) {
                ContentValues valuesOrigen = new ContentValues();
                valuesOrigen.put("Saldo", saldoOrigen - monto);
                int rowsAffectedOrigen = db.update("Cuenta", valuesOrigen, "IdCuenta = ?", new String[]{String.valueOf(idCuentaOrigen)});

                if (rowsAffectedOrigen <= 0) {
                    return false;
                }

                Cursor cursorDestino = db.rawQuery("SELECT IdCuenta, Saldo FROM Cuenta WHERE IdCuenta = ?", new String[]{cuentaDestino});
                if (cursorDestino.moveToFirst()) {
                    int idCuentaDestino = cursorDestino.getInt(0);
                    double saldoDestino = cursorDestino.getDouble(1);

                    ContentValues valuesDestino = new ContentValues();
                    valuesDestino.put("Saldo", saldoDestino + monto);
                    int rowsAffectedDestino = db.update("Cuenta", valuesDestino, "IdCuenta = ?", new String[]{String.valueOf(idCuentaDestino)});

                    if (rowsAffectedDestino <= 0) {
                        return false;
                    }

                    ContentValues movimiento = new ContentValues();
                    movimiento.put("IdCuenta", idCuentaDestino);
                    movimiento.put("Tipo_de_Transferencia", tipoTransferencia);
                    movimiento.put("Monto", monto);
                    movimiento.put("Fecha", fecha);
                    movimiento.put("Codigo_de_Validacion", codigoValidacion);
                    long movimientoId = db.insert("Movimiento", null, movimiento);

                    if (movimientoId == -1) {
                        return false;
                    }

                    cursorDestino.close();
                    cursorOrigen.close();
                    return true;
                }
                cursorDestino.close();
            } else {
                Toast.makeText(TransferActivity.this, "Saldo insuficiente en la cuenta origen", Toast.LENGTH_SHORT).show();
            }
        }
        cursorOrigen.close();
        return false;
    }


    private void iniciarResumenTransferActivity(String cuentaDestino, double monto, String tipoTransferencia, String fecha, String codigoValidacion) {
        Intent intent = new Intent(TransferActivity.this, ResumenTransferActivity.class);
        intent.putExtra("CuentaDestino", cuentaDestino);
        intent.putExtra("Monto", monto);
        intent.putExtra("TipoTransferencia", tipoTransferencia);
        intent.putExtra("Fecha", fecha);
        intent.putExtra("CodigoValidacion", codigoValidacion);
        startActivity(intent);
        finish();
    }

}
