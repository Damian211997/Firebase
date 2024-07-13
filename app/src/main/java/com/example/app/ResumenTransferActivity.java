package com.example.app;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResumenTransferActivity extends AppCompatActivity {

    private TextView tvResumen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_transfer);

        tvResumen = findViewById(R.id.tvResumen);

        String cuentaDestino = getIntent().getStringExtra("CuentaDestino");
        double monto = getIntent().getDoubleExtra("Monto", 0);
        String tipoTransferencia = getIntent().getStringExtra("TipoTransferencia");
        String fecha = getIntent().getStringExtra("Fecha");
        String codigoValidacion = getIntent().getStringExtra("CodigoValidacion");

        String resumen = "Transferencia realizada a la cuenta " + cuentaDestino + " por un monto de $" + monto +
                "\nTipo de Transferencia: " + tipoTransferencia +
                "\nFecha: " + fecha +
                "\nCódigo de Validación: " + codigoValidacion;
        tvResumen.setText(resumen);
    }
}
