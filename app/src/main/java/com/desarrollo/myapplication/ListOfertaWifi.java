package com.desarrollo.myapplication;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.desarrollo.myapplication.Adapter.ProductoAdapter;
import com.desarrollo.myapplication.BaseDatos.ProductosDB;
import com.desarrollo.myapplication.Clases.Producto;
import com.example.tscdll.TSCActivity;

import java.util.ArrayList;

import honeywell.connection.ConnectionBase;
import honeywell.connection.Connection_TCP;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ListOfertaWifi extends AppCompatActivity implements Runnable {
    ConnectionBase conn = null;
    private String ip;
    private int port;
    byte[] printdatascpos = null;

    private TextView textView2;
    private EditText codigomanual;
    private Button buscarcodigomaual;
    TSCActivity TscDll = new TSCActivity();
    private String sucursal;
    private String m_printerMAC = null;
    private String m_ip = null;

    private String m_communicationMethod = null;
    private String m_printerIP = null;
    private int m_printerPort = 0;
    private String m_printerComandMethod;

    private ProductoAdapter adapter;
    private ProductosDB db;
    private Button boton, buttonxml;
    private String ultimocodigo = "ultimocodigo";
    private boolean tagimprimirall = false;
    private OkHttpClient Pickinghttp;
    private Request RequestPicking;
    private Boolean continuarimprimiendo;
    private Boolean cancelarrun = false;
    private ArrayList<Producto> list;
    private Producto lista_producto_seleccionada;
    private ProgressDialog dialog;
    private boolean imprimirofertas;

    private Button btnprinttest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_oferta_wifi);


        cargardatospreference();

        btnprinttest = findViewById(R.id.btn_print_wifi);

        btnprinttest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.e("IMPRIMIR"," " + m_communicationMethod + " " + m_printerIP + " "+ m_printerPort + " "+ m_printerComandMethod);
              //  new Thread(ListOfertaWifi.this, "PrintingTask").start();

            }
        });

    }


    private void cargardatospreference() {
        Bundle parametros = getIntent().getExtras();
        if (parametros != null) {

            sucursal = (parametros.getString("suc"));
            m_printerMAC = (parametros.getString("mac"));
            m_ip = (parametros.getString("ip"));

            m_communicationMethod = (parametros.getString("tipoconexion"));
            m_printerIP = (parametros.getString("ipimpre"));
            m_printerPort = (parametros.getInt("portimpre"));
            m_printerComandMethod  = (parametros.getString("PrinterComandMethod"));

            Log.e("Datos"," " + m_communicationMethod + " " + m_printerIP + " "+ m_printerPort + " "+ m_printerComandMethod);


        } else {
            Toast.makeText(getApplicationContext(), "No hay datos a mostrar", Toast.LENGTH_LONG).show();
        }
    }

    public void run() {

        try
        {
            conn = null;
            conn = Connection_TCP.createClient(ip,port , false);
            if(!conn.getIsOpen()) {
                conn.open();
            }

            int bytesWritten = 0;
            int bytesToWrite = 1024;
            int totalBytes = printdatascpos.length;
            int remainingBytes = totalBytes;
            while (bytesWritten < totalBytes)
            {
                if (remainingBytes < bytesToWrite)
                    bytesToWrite = remainingBytes;

                //Send data, 1024 bytes at a time until all data sent
                conn.write(printdatascpos, bytesWritten, bytesToWrite);
                bytesWritten += bytesToWrite;
                remainingBytes = remainingBytes - bytesToWrite;
                Thread.sleep(100);
            }

            //signals to close connection
            conn.close();
        } catch (Exception e) {
            if(conn != null)
                conn.close();
            e.printStackTrace();
        }
    }

}