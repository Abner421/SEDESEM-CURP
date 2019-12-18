package com.example.sedesem.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "SEDESEM";
    public static final String TABLE_NAME = "registros";
    public static final String COLUMN_ID = "curp";
    public static final String COLUMN_NOMBRE = "Nombre";
    public static final String COLUMN_APPAT = "ApPat";
    public static final String COLUMN_APMAT = "ApMat";
    public static final String COLUMN_SEXO = "Sexo";
    public static final String COLUMN_FECHANAC = "FechaNac";
    public static final String COLUMN_ENTIDAD = "Entidad";
    public static final String COLUMN_REGION = "Region";
    public static final String COLUMN_STATUS = "status";

    /*public static final String DB_NAMEaux = "Sedesem";
    public static final String TABLE_NAMEaux = "registros";
    public static final String COLUMN_IDaux = "curp";
    public static final String COLUMN_NAMEaux = "apPat";
    public static final String COLUMN_STATUSaux = "status";*/

    //database version
    private static final int DB_VERSION = 1;

    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME +
                " VARCHAR, " + COLUMN_STATUS +
                " TINYINT);";
        db.execSQL(sql);*/

        String stmt = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_ID +
                " VARCHAR PRIMARY KEY NOT NULL, " + COLUMN_NOMBRE +
                " VARCHAR, " + COLUMN_APPAT +
                " VARCHAR, " + COLUMN_APMAT +
                " VARCHAR, " + COLUMN_SEXO +
                " VARCHAR, " + COLUMN_FECHANAC +
                " DATE, " + COLUMN_ENTIDAD +
                " INTEGER, " + COLUMN_REGION +
                " INTEGER, " + COLUMN_STATUS +
                " TINYINT);";
        db.execSQL(stmt);
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS registros";
        db.execSQL(sql);
        onCreate(db);
    }

    /*
     * This method is taking two arguments
     * first one is the name that is to be saved
     * second one is the status
     * 0 means the name is synced with the server
     * 1 means the name is not synced with the server
     * */
    public boolean addName(String name_id, String nombre, String apPat, String apMat, String Sexo, String fechaNac, String Entidad, int Region, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_ID, name_id);
        contentValues.put(COLUMN_NOMBRE, nombre);
        contentValues.put(COLUMN_APPAT, apPat);
        contentValues.put(COLUMN_APMAT, apMat);
        contentValues.put(COLUMN_SEXO, Sexo);
        contentValues.put(COLUMN_FECHANAC, fechaNac);
        contentValues.put(COLUMN_ENTIDAD, Entidad);
        contentValues.put(COLUMN_REGION, Region);
        contentValues.put(COLUMN_STATUS, status);

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    /*
     * This method taking two arguments
     * first one is the id of the name for which
     * we have to update the sync status
     * and the second one is the status that will be changed
     * */
    public boolean updateNameStatus(String id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        db.close();
        return true;
    }

    /*
     * this method will give us all the name stored in sqlite
     * */
    public Cursor getNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getNombres() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_NOMBRE + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getApPats() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_APPAT + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getApMats() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_APMAT + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getSexos() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_SEXO + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getFechaNacs() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_FECHANAC + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getEntidades() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ENTIDAD + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getRegiones() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_REGION + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public int getCounter(){
        String counterSQL = "SELECT * from " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(counterSQL, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}

