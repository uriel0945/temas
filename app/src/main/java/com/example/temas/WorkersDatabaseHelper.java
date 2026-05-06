package com.example.temas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

class WorkersDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "trabajadores.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_WORKERS = "trabajadores";

    WorkersDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_WORKERS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre TEXT NOT NULL, "
                + "apellidos TEXT NOT NULL, "
                + "edad INTEGER NOT NULL, "
                + "oficio TEXT NOT NULL, "
                + "descripcion_oficio TEXT NOT NULL, "
                + "numero_contacto TEXT NOT NULL"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKERS);
        onCreate(db);
    }

    long insertWorker(String nombre, String apellidos, int edad, String oficio,
            String descripcionOficio, String numeroContacto) {
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("apellidos", apellidos);
        values.put("edad", edad);
        values.put("oficio", oficio);
        values.put("descripcion_oficio", descripcionOficio);
        values.put("numero_contacto", numeroContacto);
        return getWritableDatabase().insert(TABLE_WORKERS, null, values);
    }

    boolean deleteWorker(long workerId) {
        int deletedRows = getWritableDatabase().delete(
                TABLE_WORKERS,
                "id = ?",
                new String[]{String.valueOf(workerId)}
        );
        return deletedRows > 0;
    }

    List<Worker> getAllWorkers() {
        List<Worker> workers = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_WORKERS,
                null,
                null,
                null,
                null,
                null,
                "nombre COLLATE NOCASE ASC, apellidos COLLATE NOCASE ASC"
        )) {
            while (cursor.moveToNext()) {
                workers.add(new Worker(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("apellidos")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("edad")),
                        cursor.getString(cursor.getColumnIndexOrThrow("oficio")),
                        cursor.getString(cursor.getColumnIndexOrThrow("descripcion_oficio")),
                        cursor.getString(cursor.getColumnIndexOrThrow("numero_contacto"))
                ));
            }
        }
        return workers;
    }
}
