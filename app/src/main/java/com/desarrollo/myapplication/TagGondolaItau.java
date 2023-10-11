package com.desarrollo.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.desarrollo.myapplication.Adapter.ProductoAdapter;
import com.desarrollo.myapplication.Adapter.ProductoAdapterItau;
import com.desarrollo.myapplication.BaseDatos.ProductosDBItau;
import com.desarrollo.myapplication.Clases.Producto;
import com.desarrollo.myapplication.ParsearXML.ParserXmlItau;
import com.example.tscdll.TSCActivity;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import honeywell.connection.ConnectionBase;
import honeywell.connection.Connection_TCP;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TagGondolaItau extends AppCompatActivity implements BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener, Runnable {
    byte[] msgBuffer;
    private BarcodeReader barcodeReader;
    private Handler m_handler = new Handler(); // Main thread
    private TextView textView2;
    private EditText codigomanual;
    private Button buscarcodigomaual;
    TSCActivity TscDll = new TSCActivity();
    private String sucursal;
    private String m_printerMAC = null;
    private String m_ip = null;

    private String m_communicationMethod = null;
    private String m_printerComandMethod = null;
    private String m_printerIP = null;
    private int m_printerPort =0;

    ConnectionBase conn = null;
    private ProductoAdapterItau adapter;
    private ProductosDBItau db;

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
    private int descuento = 0;
    private TextView porcentaje;

    private boolean imprimirofertas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_gondola);


        porcentaje= findViewById(R.id.txt_porcentaje);

        cargarbarcoder();

        cargardatospreference();

        botonesyadapter();

        cargarLista();

    }

    private void botonesyadapter() {

        codigomanual = findViewById(R.id.txt_codigomanual);
        buscarcodigomaual = findViewById(R.id.btn_buscarcodigomanual);

        codigomanual.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean procesado = false;

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    presionarboton();

                    procesado = true;
                }
                return procesado;
            }
        });

        buscarcodigomaual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                presionarboton();
            }
        });

        buttonxml = findViewById(R.id.buttonxml);
        boton = findViewById(R.id.button);
        buttonxml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarPicking();
            }
        });

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    db = new ProductosDBItau(TagGondolaItau.this);
                    Producto pro = new Producto("PRODUCTO DE PRUEBA TAMAÑOS", "SEGUNDA DESCRIPCION DEL PRODUCTO", "116663", "1111690712025", "12.000", "6", "7", "8", "9", "10", "11", "N", "OFERTA","9999");
                    db.insertarProducto(pro);
                    cargarLista();
                } catch (Exception e) {
                    Log.e("errorO", "mensaje");
                }
            }
        });

        adapter = new ProductoAdapterItau();
        adapter.setOnNoteSelectedListener(new ProductoAdapterItau.OnNoteSelectedListener() {
            @Override
            public void onClick(final Producto producto) {

                lista_producto_seleccionada = producto;

                new Thread(TagGondolaItau.this, "PrintingTask").start();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reciclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                try {
                    db = new ProductosDBItau(TagGondolaItau.this);
                    db.eliminarProducto(adapter.getNoteAt(viewHolder.getAdapterPosition()).getCodigoProducto());
                    ultimocodigo = "ultimocodigo";
                    cargarLista();
                } catch (Exception e) {

                }
            }
        }).attachToRecyclerView(recyclerView);
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

            if (m_communicationMethod.equals("TCP/IP")){
                descuento = getIntent().getIntExtra("descuento", 0);
                porcentaje.setText(descuento + "%");
                porcentaje.setVisibility(View.VISIBLE);
            }


        } else {
            Toast.makeText(getApplicationContext(), "No hay datos a mostrar", Toast.LENGTH_LONG).show();
        }
    }

    private void cargarbarcoder() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        barcodeReader = MenuPrincipal.getBarcodeObject();

        if (barcodeReader != null) {
            barcodeReader.addBarcodeListener(this);

            try {
                barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);
            } catch (UnsupportedPropertyException e) {
                Toast.makeText(this, "Failed to apply properties", Toast.LENGTH_SHORT).show();
            }

            barcodeReader.addTriggerListener(this);
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_CHECK_DIGIT_TRANSMIT_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_E_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_E_CHECK_DIGIT_TRANSMIT_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_EAN_13_CHECK_DIGIT_TRANSMIT_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_GOOD_READ_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_VIBRATE_ENABLED, true);
            barcodeReader.setProperties(properties);

        }
    }


    private void presionarboton() {

        String codigoimprimir = codigomanual.getText().toString().trim();
        if (!codigoimprimir.equals("")) {


            imprimirCodigo2(codigoimprimir);
            codigomanual.setText("");
            ocultarteclado();
        }
        if (codigomanual.isFocused()) {
            ocultarteclado();
        }
    }

    public void ocultarteclado() {

        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void imprimirCodigo2(final String codigoverificar) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final String codigocaptrado = codigoverificar;

                if (!ultimocodigo.equals(codigocaptrado)) {

                    Pickinghttp = new OkHttpClient();
                    MediaType mediaType = MediaType.parse("text/xml");

                    RequestBody body = RequestBody.create(mediaType,
                            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
                                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns=\"http://tempuri.org/\">\r\n " +
                                    "  <soapenv:Header/>\r\n " +
                                    "  <soapenv:Body>\r\n   " +
                                    "   <ObtenerDatosArticuloEtiquetasPl>\r\n     " +
                                    "    <p_suc>"+ sucursal +"</p_suc>\r\n     " +
                                    "    <p_producto>"+ codigocaptrado +"</p_producto>\r\n   " +
                                    "   </ObtenerDatosArticuloEtiquetasPl>\r\n  " +
                                    " </soapenv:Body>\r\n" +
                                    "</soapenv:Envelope>");

                    RequestPicking = new Request.Builder()
                            .url("http://" + m_ip + "/WSSREtiquetas/EtiquetaService.asmx")
                            .post(body)
                            .addHeader("Content-Type", "text/xml")
                            .addHeader("Host", m_ip)
                            .addHeader("SOAPAction", "http://tempuri.org/ObtenerDatosArticuloEtiquetasPl")
                            .build();

                    EnableDialog(true, "Conectando", false);

                    Pickinghttp.newCall(RequestPicking).enqueue(new Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            EnableDialog(false, "",false);
                            DisplayPrintingStatusMessage("Conexion Fallo");
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {

                            if (response.isSuccessful()) {
                                try {
                                    final String myResponse = response.body().string();
                                    Log.e("Response: ",myResponse);

                                    ParserXmlItau parserXml = new ParserXmlItau(TagGondolaItau.this);
                                    Document doc = toXmlDocument(myResponse);
                                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                    Source xmlSource = new DOMSource(doc);
                                    Result outputTarget = new StreamResult(outputStream);
                                    TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
                                    InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
                                    if (parserXml.parsear(is,descuento)) {
                                        DisplayPrintingStatusMessage("Correcto");
                                        cargarLista2();
                                        ultimocodigo = codigocaptrado;
                                    } else {
                                        DisplayPrintingStatusMessage("No disponible");
                                    }
                                } catch (XmlPullParserException e) {
                                    e.printStackTrace();
                                } catch (TransformerConfigurationException e) {
                                    e.printStackTrace();
                                } catch (ParserConfigurationException e) {
                                    e.printStackTrace();
                                } catch (SAXException e) {
                                    e.printStackTrace();
                                } catch (TransformerException e) {
                                    e.printStackTrace();
                                }
                                EnableDialog(false, "Conectando", false);
                            } else {
                                DisplayPrintingStatusMessage("Error con la conexion Wifi.. Reintentar");
                                EnableDialog(false, "Conectando", false);
                            }
                        }
                    });
                } else {
                    DisplayPrintingStatusMessage("Ya Consulto este código");
                }
            }

        });

    }

    @Override
    public void onBarcodeEvent(BarcodeReadEvent event) {
        imprimirCodigo2(event.getBarcodeData());

    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        DisplayPrintingStatusMessage("Codigo no valido");
    }

    @Override
    public void onTriggerEvent(TriggerStateChangeEvent triggerStateChangeEvent) {
        DisplayPrintingStatusMessage("verificar excepcion");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            barcodeReader.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (barcodeReader != null) {
            barcodeReader.removeBarcodeListener(this);
            barcodeReader.removeTriggerListener(this);
        }
    }


    private void createCancelProgressDialog(String title, String message, String buttonText,Boolean cancelar) {
        dialog = new ProgressDialog(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (cancelar) {
            dialog.setButton(buttonText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {


                    cancelarrun = true;
                }
            });
        }
        dialog.show();
    }

    public void Asignar(final String numero, final String mensaje) {
        m_handler.post(new Runnable() {
            @Override
            public void run() {

                dialog.setTitle(numero);
                dialog.setMessage(mensaje);

            }
        });
    }


    public void EnableDialog(final boolean value, final String mensaje, final Boolean cancelar) {
        m_handler.post(new Runnable() {
            @Override
            public void run() {
                if (value) {
                    createCancelProgressDialog("Cargando: ", mensaje, "Cancelar",cancelar);
                } else {
                    if (dialog != null)
                        dialog.dismiss();
                }
            }
        });
    }


    private void restaurarimpresion() {

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    EnableDialog(true, "Restaurando Lista..",false);
                    if (list.size() > 0) {

                        for (Producto prod : list) {

                            if (prod.getIP().equals("SI")) {
                                prod.setIP("NO");
                                db.updatecodigoproduto(prod);
                            }

                        }
                        cargarLista2();
                    }
                    EnableDialog(false, "Enviando terminando...",false);
                    DisplayPrintingStatusMessage("Reimprimir restaurado");

                } catch (Exception e) {
                    Log.e("errorW", "mensaje");
                    EnableDialog(false, "Enviando terminando...",false);
                    DisplayPrintingStatusMessage("Fallo la base de datos.");
                }

            }
        };

        thread.start();


    }


    private static Document toXmlDocument(String str) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new InputSource(new StringReader(str)));
        return document;

    }

    public void DisplayPrintingStatusMessage(final String MsgStr) {

        m_handler.post(new Runnable() {
            public void run() {
                showToast(MsgStr);//2018 PH
            }// run()
        });

    }

    public void showToast(final String toast) {
        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
    }

    private void CargarPicking() {

        dialog = new ProgressDialog(this);
        dialog.setTitle("Titulo");
        dialog.setMessage("Mensjae");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ParserXmlItau par = new ParserXmlItau(TagGondolaItau.this);
        List<Producto> entries = null;

        try {

            InputStream asset = getResources().getAssets().open("responsexmlproducto1.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document docc = dBuilder.parse(asset);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Source xmlSource = new DOMSource(docc);
            Result outputTarget = new StreamResult(outputStream);
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
            InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
            par.parsear(is,50);

            cargarLista();

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        dialog.dismiss();
    }

    private void cargarLista2() {

        m_handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    db = new ProductosDBItau(TagGondolaItau.this);
                    list = db.loadProducto();
                    adapter.setNotes(list,m_communicationMethod,descuento);
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Log.e("errorW", "mensaje");
                }
            }
        });

    }

    private void cargarLista() {
        try {
            db = new ProductosDBItau(TagGondolaItau.this);
            list = db.loadProducto();
            adapter.setNotes(list,m_communicationMethod,descuento);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e("errorW", "mensaje");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menutag, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void Limpiar() {
        try {
            db = new ProductosDBItau(TagGondolaItau.this);
            db.eliminarProductos();
            ultimocodigo = "ultimocodigo";
            cargarLista();
            Toast.makeText(TagGondolaItau.this, "Se Limpio la Lista", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("error", "mensaje");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.limpiar:

                AlertDialog.Builder build4 = new AlertDialog.Builder(TagGondolaItau.this);
                build4.setMessage("¿Desea borrar lista? ").setPositiveButton("Borrar lista", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Limpiar();

                    }

                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog4 = build4.create();
                alertDialog4.show();


                break;

            case R.id.test_impresion:

                AlertDialog.Builder build3 = new AlertDialog.Builder(TagGondolaItau.this);
                build3.setMessage("¿Desea imprimir un test de impresión? ").setPositiveButton("Imprimir test", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {

                                    EnableDialog(true, "Enviando Documento...",true);

                                    if (m_communicationMethod.equals("TCP/IP")){

                                        prepararCodigo();
                                    }

                                    EnableDialog(false, "Enviando terminando...",false);
                                    DisplayPrintingStatusMessage("Impresión Exitosa.");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    EnableDialog(false, "Enviando terminando...",false);
                                    DisplayPrintingStatusMessage("Fallo conexion Bluetooth.");
                                }

                            }
                        };

                        thread.start();

                    }


                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                AlertDialog alertDialog3 = build3.create();
                alertDialog3.show();

                break;

            case R.id.Imprimir_todo:


                if (m_communicationMethod.equals("TCP/IP")){

                    AlertDialog.Builder build = new AlertDialog.Builder(TagGondolaItau.this);

                    build.setMessage("¿Desea imprimir toda la lista? ").setPositiveButton("Imprimir Todo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            tagimprimirall = true;
                            imprimirofertas = false;
                            new Thread(TagGondolaItau.this, "PrintingTask").start();
                        }

                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });

                    AlertDialog alertDialog = build.create();
                    alertDialog.show();

                }


                break;

            case R.id.Restaurar:

                AlertDialog.Builder build2 = new AlertDialog.Builder(TagGondolaItau.this);
                build2.setMessage("¿Desea restaurar tags impresos? ").setPositiveButton("Restaurar tags impresos", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        restaurarimpresion();

                    }


                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                AlertDialog alertDialog2 = build2.create();
                alertDialog2.show();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void prepararCodigo() {

        String m_data = "";
        if (m_printerComandMethod.equals("TSC")){

            m_data = "SIZE 99.10 mm, 71.1 mm\n"+
                    "BLINE 3 mm, 0 mm\n"+
                    "DIRECTION 0,0\n"+
                    "REFERENCE 0,0\n"+
                    "OFFSET 0 mm\n"+
                    "SET PEEL OFF\n"+
                    "SET CUTTER OFF\n"+
                    "SET PARTIAL_CUTTER OFF\n"+
                    "SET TEAR ON\n"+
                    "CLS\n"+
                    "CODEPAGE 1252\n"+
                    "TEXT 777,498,\"arial_bl.TTF\",180,13,12,\"Nombre Producto\"\n"+
                    "TEXT 777,443,\"arial_na.TTF\",180,13,12,\"Segundo Nombre producto\"\n"+
                    "TEXT 488,332,\"striketh.TTF\",180,10,8,\"$ 1200\"\n"+
                    "TEXT 488,141,\"arial_bl.TTF\",180,11,27,\"$ 980\"\n"+
                    "PRINT 1,1\n";

            msgBuffer = m_data.getBytes();

            ImprimirWifi();

        }else if (m_printerComandMethod.equals("HONEYWELL")){

            m_data ="^XA\n" +
                    "^PW799\n" +
                    "^LL650\n" +
                    "^MMT\n" +
                    "^XZ\n" +
                    "^XA\n" +
                    "^CI28\n" +
                    "^FWI\n" +
                    "^CF0,30\n"+
                    "^LH0,0\n" +
                    "^PON\n" +
                    "^MD0\n" +
                    "^PQ1,0,1,Y\n" +
                    "^XZ\n" +
                    "^XA\n" +
                    "^LRN\n" +
                    "^A0N,51,52^FO47,24^FDProducto 1^FS\n" +
                    "^A0N,34,34^FO53,82^FDProducto 2^FS\n" +
                    "^A0N,56,56^FO405,180^FD$ 20000^FS\n" +
                    "^A0N,101,102^FO366,373^FD$ 10000^FS\n" +
                    "^XZ";

            msgBuffer = m_data.getBytes();

            ImprimirWifi();

        }else{

            m_data ="<xpml><page quantity='0' pitch='70.1 mm'></xpml>'Seagull:2.1:DP\n" +
                    "INPUT OFF\n" +
                    "VERBOFF\n" +
                    "INPUT ON\n" +
                    "SYSVAR(48) = 0\n" +
                    "SYSVAR(35)=0\n" +
                    "OPEN \"tmp:setup.sys\" FOR OUTPUT AS #1\n" +
                    "PRINT#1,\"Printing,Media,Print Area,Media Margin (X),0\"\n" +
                    "PRINT#1,\"Printing,Media,Clip Default,On\"\n" +
                    "CLOSE #1\n" +
                    "SETUP \"tmp:setup.sys\"\n" +
                    "KILL \"tmp:setup.sys\"\n" +
                    "CLIP ON\n" +
                    "CLIP BARCODE ON\n" +
                    "LBLCOND 3,2\n" +
                    "<xpml></page></xpml><xpml><page quantity='1' pitch='70.1 mm'></xpml>CLL\n" +
                    "OPTIMIZE \"BATCH\" ON\n" +
                    "PP41,550:AN7\n" +
                    "NASC 8\n" +
                    "FT \"Univers Condensed Bold\"\n" +
                    "FONTSIZE 14\n" +
                    "FONTSLANT 0\n" +
                    "PT \"Descripcion 1\"\n" +
                    "PP43,498:FONTSIZE 12\n" +
                    "PT \"Descripcion 2\"\n" +
                    "PP374,400:FONTSIZE 24\n" +
                    "PT \"$ 5000\"\n" +
                    "PP370,215:FONTSIZE 36\n" +
                    "PT \"$ 4000\"\n" +
                    "LAYOUT RUN \"\"\n" +
                    "PF\n" +
                    "PRINT KEY OFF\n" +
                    "<xpml></page></xpml><xpml><end/></xpml>";

            msgBuffer = m_data.getBytes();

            ImprimirWifi();

        }
    }

    private void ImprimirWifi() {

        DisplayPrintingStatusMessage("Imprimiendo Wifi.");

        try
        {
            conn = null;
            conn = Connection_TCP.createClient(m_printerIP,m_printerPort , false);
            if(!conn.getIsOpen()) {
                conn.open();
            }

            int bytesWritten = 0;
            int bytesToWrite = 1024;
            int totalBytes = msgBuffer.length;
            int remainingBytes = totalBytes;
            while (bytesWritten < totalBytes)
            {
                if (remainingBytes < bytesToWrite)
                    bytesToWrite = remainingBytes;

                //Send data, 1024 bytes at a time until all data sent
                conn.write(msgBuffer, bytesWritten, bytesToWrite);
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


    @Override
    public void run() {

        try {

            EnableDialog(true, "Imprimiendo...",true);


            if (m_communicationMethod.equals("TCP/IP")){

                ImprimirWifi2();
            }

        } catch (Exception e) {

            e.printStackTrace();
            EnableDialog(false, "",false);
            DisplayPrintingStatusMessage("Fallo conexion Bluetooth.");
            tagimprimirall = false;
            cancelarrun = false;
        }
    }



    private void ImprimirWifi2() {

        try
        {

            conn = null;
            conn = Connection_TCP.createClient(m_printerIP,m_printerPort , false);
            if(!conn.getIsOpen()) {
                conn.open();
            }

            if (!tagimprimirall) {

                        //todo IMPRIMIR 1
                        Asignar("Imprimiendo: ", lista_producto_seleccionada.getDescArticulo_1());

                        if (m_printerComandMethod.equals("TSC")){
                            msgBuffer = prepararTSPL(lista_producto_seleccionada).getBytes();
                        }else if(m_printerComandMethod.equals("HONEYWELL")){
                            msgBuffer = prepararDP(lista_producto_seleccionada).getBytes();
                        }else{
                            msgBuffer = prepararDPL(lista_producto_seleccionada).getBytes();
                        }

                        senddatoswifiTCP(conn,msgBuffer);

                        Thread.sleep(100);
                        lista_producto_seleccionada.setIP("SI");
                        db.updatecodigoproduto(lista_producto_seleccionada);

                    }else {

                        //TODO IMPRIMIR VARIOS

                        db = new ProductosDBItau(TagGondolaItau.this);
                        for (Producto prod : list) {

                            if (cancelarrun) {
                                break;
                            }
                            if (!prod.getIP().equals("SI")){

                                    if (prod.getOff_available().equals("S")) {

                                        Asignar("Imprimiendo: ", prod.getDescArticulo_1());

                                        if (m_printerComandMethod.equals("TSC")){
                                            msgBuffer = prepararTSPL(prod).getBytes();
                                        }else if(m_printerComandMethod.equals("HONEYWELL")){
                                            msgBuffer = prepararDP(prod).getBytes();
                                        }else{
                                            msgBuffer = prepararDPL(prod).getBytes();
                                        }
                                        senddatoswifiTCP(conn,msgBuffer);
                                        Thread.sleep(100);

                                        prod.setIP("SI");
                                        db.updatecodigoproduto(prod);

                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

                                    }

                            }
                        }
                    }

            conn.close();
        } catch (Exception e) {
            if(conn != null)
                conn.close();
            e.printStackTrace();
        }

        cargarLista2();

        EnableDialog(false, "",false);

        cancelarrun = false;
        tagimprimirall = false;

    }

    private String prepararTSPL(Producto producto) {

        String m_data = "SIZE 99.10 mm, 71.1 mm\n"+
                "BLINE 3 mm, 0 mm\n"+
                "DIRECTION 0,0\n"+
                "REFERENCE 0,0\n"+
                "OFFSET 0 mm\n"+
                "SET PEEL OFF\n"+
                "SET CUTTER OFF\n"+
                "SET PARTIAL_CUTTER OFF\n"+
                "SET TEAR ON\n"+
                "CLS\n"+
                "CODEPAGE 1252\n"+
                "TEXT 777,498,\"arial_bl.TTF\",180,13,12,\"" + producto.getDescArticulo_1() + "\"\n"+
                "TEXT 777,443,\"arial_na.TTF\",180,13,12,\""+ producto.getDescArticulo_2() +"\"\n";

        m_data = m_data +
                    "TEXT 488,332,\"striketh.TTF\",180,10,8,\""+"$ " + producto.getPrecio_lista()+"\"\n"+
                    "TEXT 488,141,\"arial_bl.TTF\",180,11,27,\""+"$ " + producto.getPrecio() + "\"\n"+
                    "PRINT 1,1\n";

        return m_data;
    }

    private String prepararDP(Producto producto){


        String m_data =

                "<xpml><page quantity='0' pitch='70.1 mm'></xpml>'Seagull:2.1:DP\n" +
                        "INPUT OFF\n" +
                        "VERBOFF\n" +
                        "INPUT ON\n" +
                        "SYSVAR(48) = 0\n" +
                        "ERROR 15,\"FONT NOT FOUND\"\n" +
                        "ERROR 18,\"DISK FULL\"\n" +
                        "ERROR 26,\"PARAMETER TOO LARGE\"\n" +
                        "ERROR 27,\"PARAMETER TOO SMALL\"\n" +
                        "ERROR 37,\"CUTTER DEVICE NOT FOUND\"\n" +
                        "ERROR 1003,\"FIELD OUT OF LABEL\"\n" +
                        "SYSVAR(35)=0\n" +
                        "OPEN \"tmp:setup.sys\" FOR OUTPUT AS #1\n" +
                        "PRINT#1,\"Printing,Media,Print Area,Media Margin (X),0\"\n" +
                        "PRINT#1,\"Printing,Media,Clip Default,On\"\n" +
                        "CLOSE #1\n" +
                        "SETUP \"tmp:setup.sys\"\n" +
                        "KILL \"tmp:setup.sys\"\n" +
                        "CLIP ON\n" +
                        "CLIP BARCODE ON\n" +
                        "LBLCOND 3,2\n" +
                        "<xpml></page></xpml><xpml><page quantity='1' pitch='70.1 mm'></xpml>CLL\n" +
                        "OPTIMIZE \"BATCH\" ON\n" +
                        "PP41,550:AN7\n" +
                        "NASC 8\n" +
                        "FT \"Univers Condensed Bold\"\n" +
                        "FONTSIZE 14\n" +
                        "FONTSLANT 0\n" +
                        "PT \""+producto.getDescArticulo_1()+"\"\n" +
                        "PP43,498:FONTSIZE 12\n" +
                        "PT \""+producto.getDescArticulo_2()+"\"\n" +
                        "PP380,394:FT \"Univers Condensed Bold\",18,0,102\n" +
                        "PT \"" + "$" + producto.getPrecio_lista()+"\"\n" +
                        "PP333,225:FT \"Univers Condensed Bold\"\n" +
                        "FONTSIZE 36\n" +
                        "FONTSLANT 0\n" +
                        "PT \""+"$ " + producto.getPrecio() + " \"\n" +
                        "LAYOUT RUN \"\"\n" +
                        "PF\n" +
                        "PRINT KEY OFF\n" +
                        "<xpml></page></xpml><xpml><end/></xpml>";

        return m_data;
    }

    private String prepararDPL(Producto producto) {

        String m_data ="^XA\n" +
                "^PW799\n" +
                "^LL650\n" +
                "^MMT\n" +
                "^XZ\n" +
                "^XA\n" +
                "^CI28\n" +
                "^FWI\n" +
                "^CF0,30\n" +
                "^LH0,0\n" +
                "^PON\n" +
                "^MD0\n" +
                "^PQ1,0,1,Y\n" +
                "^XZ\n" +
                "^XA\n" +
                "^LRN\n"+
                "^A0N,51,52^FO47,24^FD"+producto.getDescArticulo_1()+"^FS\n" +
                "^A0N,34,34^FO53,82^FD"+producto.getDescArticulo_2() +"^FS\n" ;
            m_data = m_data +
                    "^A0N,56,56^FO405,180^FD$ "+ producto.getPrecio_lista()+"^FS\n" +
                    "^A0N,101,102^FO366,373^FD$ "+producto.getPrecio()+"^FS\n" +
                    "^XZ";


        return m_data;

    }

    private void senddatoswifiTCP(ConnectionBase conn, byte[] msgBuffer) {

        int bytesWritten = 0;
        int bytesToWrite = 1024;
        int totalBytes = msgBuffer.length;
        int remainingBytes = totalBytes;
        while (bytesWritten < totalBytes)
        {
            if (remainingBytes < bytesToWrite)
                bytesToWrite = remainingBytes;

            //Send data, 1024 bytes at a time until all data sent
            conn.write(msgBuffer, bytesWritten, bytesToWrite);
            bytesWritten += bytesToWrite;
            remainingBytes = remainingBytes - bytesToWrite;

        }

    }


}
