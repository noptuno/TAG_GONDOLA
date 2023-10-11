package com.desarrollo.myapplication.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.desarrollo.myapplication.Clases.GruposRubros;

import java.util.ArrayList;


public class RubroDB {

    private SQLiteDatabase db;
    private RubroDB.DBHelper dbHelper;

    public RubroDB(Context context) {
        dbHelper = new RubroDB.DBHelper(context);
    }

    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {

        if (db != null) {
            db.close();
        }
    }

    private ContentValues clienteMapperContentValues(GruposRubros rubro) {

        ContentValues cv = new ContentValues();
        cv.put(ConstantsDB.RUBRO_CODIGO, rubro.getCodigoGrupoRubros());
        cv.put(ConstantsDB.RUBRO_NUMERO, rubro.getNumeroGrupoRubro());
        cv.put(ConstantsDB.RUBRO_DESCRIPCION, rubro.getDescripcionGrupoRubro());
        return cv;

    }

    public long insertarGrupoRubro(GruposRubros rubro) {
        this.openWriteableDB();
        long rowID = db.insert(ConstantsDB.TABLA_RUBRO, null, clienteMapperContentValues(rubro));
        this.closeDB();
        return rowID;
    }

    public void eliminarRubros() {

        this.openWriteableDB();
        db.delete(ConstantsDB.TABLA_RUBRO, null, null);
        this.closeDB();

    }

    public void actualizar(GruposRubros rubro) {

        this.openWriteableDB();
        String where = ConstantsDB.RUBRO_CODIGO + "= ?";
        db.update(ConstantsDB.TABLA_RUBRO, clienteMapperContentValues(rubro), where, new String[]{String.valueOf(rubro.getCodigoGrupoRubros())});
        db.close();
    }

    public void eliminarRubro(String rubro) {

        this.openWriteableDB();
        String where = ConstantsDB.RUBRO_CODIGO + "= ?";
        db.delete(ConstantsDB.TABLA_RUBRO, where, new String[]{String.valueOf(rubro)});
        db.close();


    }

    private static class DBHelper extends SQLiteOpenHelper {

        private RubroDB db;

        public DBHelper(Context context) {
            super(context, ConstantsDB.DB_NAME, null, ConstantsDB.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(ConstantsDB.TABLA_PRODUCTO_SQL);
            db.execSQL(ConstantsDB.TABLA_PRODUCTO_ITAU_SQL);
            db.execSQL(ConstantsDB.TABLA_LIST_PRODUCTO_SQL);
            db.execSQL(ConstantsDB.TABLA_LOGIN_SQL);
            db.execSQL(ConstantsDB.TABLA_RUBRO_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public boolean Buscar_tabla_rubro(String numero, String descripcion) {

        boolean error = false;

        this.openReadableDB();
        String where = ConstantsDB.RUBRO_NUMERO+ "= ?" + " OR " + ConstantsDB.RUBRO_DESCRIPCION + "= ?" ;
        String[] whereArgs = {numero,descripcion};
        Cursor c = db.query(ConstantsDB.TABLA_RUBRO, null, where, whereArgs, null, null, null, null);

        try{

            if( c.getCount()>0) {
                c.close();
                error = true;
            }

        }catch (Exception ex){
            Log.e("ProductoDB," +
                    "", "error al leer");
            error = false;
        }

        return error;
    }


    public ArrayList loadGrupoRubro() {

        ArrayList<GruposRubros> list = new ArrayList<>();
        String where = ConstantsDB.RUBRO_CODIGO;
        this.openReadableDB();
        String[] campos = new String[]{ConstantsDB.RUBRO_CODIGO, ConstantsDB.RUBRO_NUMERO, ConstantsDB.RUBRO_DESCRIPCION};
        Cursor c = db.query(ConstantsDB.TABLA_RUBRO, campos, null, null, null, null, where + " DESC");

        try {

            while (c.moveToNext()) {
                GruposRubros rubro = new GruposRubros();
                rubro.setCodigoGrupoRubros(c.getInt(0));
                rubro.setNumeroGrupoRubro(c.getString(1));
                rubro.setDescripcionGrupoRubro(c.getString(2));
                list.add(rubro);
            }

        } finally {
            c.close();
        }

        this.closeDB();
        return list;

    }

}






