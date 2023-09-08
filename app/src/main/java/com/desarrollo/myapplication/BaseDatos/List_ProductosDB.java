package com.desarrollo.myapplication.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.desarrollo.myapplication.Clases.List_Producto;
import com.desarrollo.myapplication.Clases.Producto;

import java.util.ArrayList;

public class List_ProductosDB {

    private SQLiteDatabase db;
    private List_ProductosDB.DBHelper dbHelper;

    public List_ProductosDB(Context context) {
        dbHelper = new List_ProductosDB.DBHelper(context);
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



    private ContentValues clienteMapperContentValues(List_Producto producto) {
        ContentValues cv = new ContentValues();
        cv.put(ConstantsDB.LIST_CODIGOPRODUCTO, producto.getCodigoProducto());
        cv.put(ConstantsDB.LIST_DESCRIPCION1, producto.getDescArticulo_1());
        cv.put(ConstantsDB.LIST_DESCRIPCION2, producto.getDescArticulo_2());
        cv.put(ConstantsDB.LIST_CODPROD, producto.getCodProd());
        cv.put(ConstantsDB.LIST_CODBARRAS, producto.getCodBarras());
        cv.put(ConstantsDB.LIST_PRECIO, producto.getPrecio());
        cv.put(ConstantsDB.LIST_STOCK, producto.getStock());
        cv.put(ConstantsDB.LIST_OFFAVAILABLE, producto.getOff_available());
        cv.put(ConstantsDB.LIST_IP, producto.getIP());
        cv.put(ConstantsDB.LIST_SUC, producto.getSuc());
        cv.put(ConstantsDB.LIST_ESTADO, producto.getEstado());
        cv.put(ConstantsDB.LIST_TXTOFERTA, producto.getTxt_oferta());
        cv.put(ConstantsDB.LIST_PRECIOLISTA, producto.getPrecio_lista());


        return cv;
    }


    public long insertarProducto(List_Producto listproducto) {
        this.openWriteableDB();
        long rowID = db.insert(ConstantsDB.TABLA_LIST_PRODUCTO, null, clienteMapperContentValues(listproducto));
        this.closeDB();
        return rowID;
    }

    public void updatecodigoproduto(List_Producto listproducto) {

        this.openWriteableDB();
        String where = ConstantsDB.LIST_CODIGOPRODUCTO + "= ?";
        db.update(ConstantsDB.TABLA_LIST_PRODUCTO, clienteMapperContentValues(listproducto), where, new String[]{String.valueOf(listproducto.getCodigoProducto())});
        db.close();
    }

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, ConstantsDB.DB_NAME, null, ConstantsDB.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ConstantsDB.TABLA_PRODUCTO_SQL);
            db.execSQL(ConstantsDB.TABLA_LIST_PRODUCTO_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
    public ArrayList loadListProducto() {

        ArrayList<List_Producto> list = new ArrayList<>();
        String where = ConstantsDB.LIST_CODIGOPRODUCTO;
        this.openReadableDB();
        String[] campos = new String[]{ConstantsDB.LIST_CODIGOPRODUCTO,ConstantsDB.LIST_DESCRIPCION1,ConstantsDB.LIST_DESCRIPCION2, ConstantsDB.LIST_CODPROD, ConstantsDB.LIST_CODBARRAS,  ConstantsDB.LIST_PRECIO, ConstantsDB.LIST_STOCK,ConstantsDB.LIST_OFFAVAILABLE ,ConstantsDB.LIST_IP , ConstantsDB.LIST_SUC,ConstantsDB.LIST_ESTADO,ConstantsDB.LIST_TXTOFERTA,ConstantsDB.LIST_PRECIOLISTA };
        Cursor c = db.query(ConstantsDB.TABLA_LIST_PRODUCTO, campos, null, null, null, null, where +" DESC");

        try {
            while (c.moveToNext()) {
                List_Producto producto = new List_Producto();
                producto.setCodigoProducto(c.getInt(0));
                producto.setDescArticulo_1(c.getString(1));
                producto.setDescArticulo_2(c.getString(2));
                producto.setCodProd(c.getString(3));
                producto.setCodBarras(c.getString(4));
                producto.setPrecio(c.getString(5));
                producto.setStock(c.getString(6));
                producto.setOff_available(c.getString(7));
                producto.setIP(c.getString(8));
                producto.setSuc(c.getString(9));
                producto.setEstado(c.getString(10));
                producto.setTxt_oferta(c.getString(11));
                producto.setPrecio_lista(c.getString(12));
                list.add(producto);
            }
        } finally {
            c.close();
        }
        this.closeDB();
        return list;
    }

    public ArrayList loadListProductoOferta(String esoferta) {

       // Producto producto = new Producto();
      //  this.openReadableDB();
       // String where = ConstantsDB.PRO_CODIGOPRODUCTO+ "= ?" + " OR " + ConstantsDB.PRO_CODIGOEAN + "= ?" ;
      //  String[] campos = new String[]{ConstantsDB.PRO_IDPRODUCTO,ConstantsDB.PRO_CODIGOPRODUCTO,ConstantsDB.PRO_CODIGOEAN, ConstantsDB.PRO_DESCRIPCION, ConstantsDB.PRO_PESOFIJO,ConstantsDB.PRO_CANTIDAD,ConstantsDB.PRO_TOTALPESO,ConstantsDB.PRO_TIPOPESO};
       // String[] whereArgs = {codigoetiqueta,codigoProducto};
      //  Cursor c = db.query(ConstantsDB.TABLA_PRODUCTO, campos, where, whereArgs, null, null, null, null);

        ArrayList<List_Producto> list = new ArrayList<>();
        String where = ConstantsDB.LIST_CODIGOPRODUCTO;
        String donde = ConstantsDB.LIST_OFFAVAILABLE+ "= ?";
        String[] whereArgs = {esoferta};
        this.openReadableDB();
        String[] campos = new String[]{ConstantsDB.LIST_CODIGOPRODUCTO,ConstantsDB.LIST_DESCRIPCION1,ConstantsDB.LIST_DESCRIPCION2, ConstantsDB.LIST_CODPROD, ConstantsDB.LIST_CODBARRAS,  ConstantsDB.LIST_PRECIO, ConstantsDB.LIST_STOCK,ConstantsDB.LIST_OFFAVAILABLE ,ConstantsDB.LIST_IP , ConstantsDB.LIST_SUC,ConstantsDB.LIST_ESTADO,ConstantsDB.LIST_TXTOFERTA };
        Cursor c = db.query(ConstantsDB.TABLA_LIST_PRODUCTO, campos, donde, whereArgs, null, null, where +" DESC");

        try {
            while (c.moveToNext()) {
                List_Producto producto = new List_Producto();
                producto.setCodigoProducto(c.getInt(0));
                producto.setDescArticulo_1(c.getString(1));
                producto.setDescArticulo_2(c.getString(2));
                producto.setCodProd(c.getString(3));
                producto.setCodBarras(c.getString(4));
                producto.setPrecio(c.getString(5));
                producto.setStock(c.getString(6));
                producto.setOff_available(c.getString(7));
                producto.setIP(c.getString(8));
                producto.setSuc(c.getString(9));
                producto.setEstado(c.getString(10));
                producto.setTxt_oferta(c.getString(11));
                list.add(producto);
            }
        } finally {
            c.close();
        }
        this.closeDB();
        return list;
    }



    public void eliminarListProductos() {
        this.openWriteableDB();
        db.delete(ConstantsDB.TABLA_LIST_PRODUCTO, null, null);


        this.closeDB();
    }


    public Boolean Validar_List_Productos(List_Producto producto) {

        boolean error= false;
        this.openReadableDB();
        String where = ConstantsDB.LIST_CODBARRAS+ "= ?";
        //String[] whereArgs = {email};
        Cursor c = db.query(ConstantsDB.TABLA_LIST_PRODUCTO, null, where, new String[]{producto.getCodBarras()}, null, null, null, null);
        try{
            if( c.getCount()>0) {
                c.close();
                error = true;
            }

        }catch (Exception ex){
            Log.e("valiando" + "", "error al leer");
            error = true;
        }

        return error;
    }


}
