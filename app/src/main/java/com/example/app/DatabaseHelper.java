

package com.example.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "banco_estrella.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla Cliente
    private static final String CREATE_TABLE_CLIENTE = "CREATE TABLE Cliente (" +
            "Rut TEXT PRIMARY KEY, " +
            "Nombre TEXT, " +
            "Apellido_Paterno TEXT, " +
            "Apellido_Materno TEXT, " +
            "Telefono TEXT, " +
            "Contrasena TEXT, " +
            "Correo_Electronico TEXT" +
            ")";

    // Tabla Cuenta
    private static final String CREATE_TABLE_CUENTA = "CREATE TABLE Cuenta (" +
            "IdCuenta INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "RutCliente TEXT, " +
            "Saldo REAL, " +
            "Detalle_de_Cuenta TEXT, " +
            "FOREIGN KEY (RutCliente) REFERENCES Cliente(Rut)" +
            ")";

    // Tabla Movimiento
    private static final String CREATE_TABLE_MOVIMIENTO = "CREATE TABLE Movimiento (" +
            "IdMovimiento INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "IdCuenta INTEGER, " +
            "Tipo_de_Transferencia TEXT, " +
            "Monto REAL, " +
            "Fecha TEXT, " +
            "Codigo_de_Validacion TEXT, " +
            "FOREIGN KEY (IdCuenta) REFERENCES Cuenta(IdCuenta)" +
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLIENTE);
        db.execSQL(CREATE_TABLE_CUENTA);
        db.execSQL(CREATE_TABLE_MOVIMIENTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Cliente");
        db.execSQL("DROP TABLE IF EXISTS Cuenta");
        db.execSQL("DROP TABLE IF EXISTS Movimiento");
        onCreate(db);
    }
}
