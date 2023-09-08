package com.desarrollo.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.desarrollo.myapplication.Adapter.List_ProductoAdapter;
import com.desarrollo.myapplication.BaseDatos.List_ProductosDB;
import com.desarrollo.myapplication.BaseDatos.RubroDB;
import com.desarrollo.myapplication.Clases.GruposRubros;
import com.desarrollo.myapplication.Clases.List_Producto;
import com.desarrollo.myapplication.ParsearXML.ParserList;
import com.example.tscdll.TSCActivity;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ListTagGondola extends AppCompatActivity implements Runnable {
    private Handler m_handler = new Handler(); // Main thread
    private OkHttpClient Pickinghttp;
    private Request RequestPicking;
    private List_ProductoAdapter adapter;
    private List_ProductosDB db;
    TSCActivity TscDll = new TSCActivity();
    private String sucursal;
    private String m_printerMAC;
    private String m_ip;

    private EditText txt_fechaclick;
    private Spinner Spinerrubro;
    private RadioGroup radiogroup;
    private CheckBox checkBoxOferta, checkBoxSinOferta;
    private DatePickerDialog datePickerDialog;
    private int year;
    private int month;
    private int dayOfMonth;
    private Calendar calendar;

    ArrayAdapter comboAdapter;
    private boolean tagimprimirall = false;
    private ArrayList<List_Producto> list;
    private ArrayList<List_Producto> list2;
    private List_Producto lista_producto_seleccionada;


    private String spinnerselecciontext;
    private String valorspinner;
    private Integer valorselec;


    private Boolean cancelarrun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tag_gondola);

        radiogroup = findViewById(R.id.idradiogroup);
        Spinerrubro = findViewById(R.id.spinnerrubro);
        checkBoxOferta = findViewById(R.id.checkBox_Oferta);
        checkBoxSinOferta = findViewById(R.id.checkBox_sinOferta);


        final SharedPreferences pref = getSharedPreferences("TEMP", Context.MODE_PRIVATE);
        Bundle parametros = getIntent().getExtras();

        if (parametros != null) {
            sucursal = (parametros.getString("suc"));
            m_printerMAC = (parametros.getString("mac"));
            m_ip = (parametros.getString("ip"));
        } else {
            Toast.makeText(getApplicationContext(), "No hay datos a mostrar", Toast.LENGTH_LONG).show();
        }


        adapter = new List_ProductoAdapter();
        adapter.setOnNoteSelectedListener(new List_ProductoAdapter.OnNoteSelectedListener() {
            @Override
            public void onClick(final List_Producto list_producto) {

                lista_producto_seleccionada = list_producto;

                new Thread(ListTagGondola.this, "PrintingTask").start();

            }
        });

        txt_fechaclick = findViewById(R.id.txtfechaclick);
        txt_fechaclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(ListTagGondola.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                txt_fechaclick.setText(day + "/" + (month + 1) + "/" + year);

                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("valorspinner", valorspinner);
                                editor.putString("fecha", txt_fechaclick.getText().toString());
                                editor.apply();

                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });


        Button btn_buscar = findViewById(R.id.btnbuscarlist);

        btn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder build2 = new AlertDialog.Builder(ListTagGondola.this);
                build2.setMessage("¿Desea realizar una nueva busqueda? ").setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        imprimirCodigo();

                    }

                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                AlertDialog alertDialog2 = build2.create();
                alertDialog2.show();

            }
        });

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        txt_fechaclick.setText(pref.getString("fecha", formattedDate));

        //spinnerselecciontext = pref.getString("valorspinner", "");

        Spinerrubro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                spinnerselecciontext = Spinerrubro.getSelectedItem().toString();

                String select = Spinerrubro.getSelectedItem().toString();

                try {
                    RubroDB dbb = new RubroDB(ListTagGondola.this);
                    ArrayList<GruposRubros> list = dbb.loadGrupoRubro();
                    for (GruposRubros log : list) {

                        if (log.getDescripcionGrupoRubro().equals(select)) {
                            valorspinner = log.getNumeroGrupoRubro();
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("valorspinner", valorspinner);
                            editor.putString("fecha", txt_fechaclick.getText().toString());
                            editor.putString("valorselec", spinnerselecciontext);
                            editor.apply();
                            Log.e("SELECCION", "seleciono" + select + " " + log.getNumeroGrupoRubro());
                        }
                    }

                } catch (Exception e) {
                    Log.e("errorW", "mensaje");

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        Button btnborrar = findViewById(R.id.btn_borrar);

        btnborrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Borrar();
            }
        });

        cargarSpiner();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(ListTagGondola.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        cargarLista();

    }

    public void checkbox_oferta(View v) {


        radiogroup.setBackground((ContextCompat.getDrawable(this, R.drawable.btn_fondo_oferta)));
        if (checkBoxOferta.isChecked()) {

            checkBoxSinOferta.setChecked(false);

        } else {

            checkBoxSinOferta.setChecked(true);

        }
        cargarListaTemp();

    }

    public void checkbox_sinoferta(View v) {

        radiogroup.setBackground((ContextCompat.getDrawable(this, R.drawable.btn_fondo_sin_oferta)));

        if (checkBoxSinOferta.isChecked()) {

            checkBoxOferta.setChecked(false);

        } else {

            checkBoxOferta.setChecked(true);

        }
        cargarListaTemp();
    }

    private void cargarSpiner() {

        try {
            RubroDB dbb = new RubroDB(ListTagGondola.this);
            ArrayList<GruposRubros> list = dbb.loadGrupoRubro();


            ArrayList<String> list2 = new ArrayList<>();
            for (GruposRubros log : list) {
                list2.add(log.Spinner());
                Log.i("---> Base de datos:L ", log.Spinner());
            }

            comboAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list2);

        } catch (Exception e) {
            Log.e("errorW", "mensaje");

        }

        //Cargo el spinner con los datos
        Spinerrubro.setAdapter(comboAdapter);

    }

    private void Borrar() {

        m_handler.post(new Runnable() {
            @Override
            public void run() {
                try {

                    db = new List_ProductosDB(ListTagGondola.this);
                    db.eliminarListProductos();
                    list = db.loadListProducto();
                    adapter.setNotes(list);
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Log.e("errorO", "mensaje");
                }
            }
        });

    }

    public void imprimirCodigo() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Pickinghttp = new OkHttpClient();

                MediaType mediaType = MediaType.parse("text/xml");
                RequestBody body = RequestBody.create(mediaType, "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
                        "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n " +
                        " <soap:Body>\r\n " +
                        "   <ObtenerDatosListaArticuloEtiquetasPl xmlns=\"http://tempuri.org/\">\r\n" +
                        "      <p_suc>" + sucursal + "</p_suc>\r\n" +
                        "      <p_fecha>" + txt_fechaclick.getText().toString() + "</p_fecha>\r\n " +
                        "     <p_rubro_grupo>" + valorspinner + "</p_rubro_grupo>\r\n" +
                        "    </ObtenerDatosListaArticuloEtiquetasPl>\r\n " +
                        " </soap:Body>\r\n</soap:Envelope>\r\n\r\n");

                RequestPicking = new Request.Builder()
                        .url("http://" + m_ip + "/WSSREtiquetas/EtiquetaService.asmx")
                        .post(body)
                        .addHeader("Content-Type", "text/xml")
                        .addHeader("User-Agent", "PostmanRuntime/7.18.0")
                        .addHeader("Accept", "*/*")
                        .addHeader("Cache-Control", "no-cache")
                        .addHeader("Postman-Token", "9422bd3d-cabb-4f6a-8a39-2de68c567620,1f43bdc5-c261-4495-8325-aa6778690d00")
                        .addHeader("Host", m_ip)
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Content-Length", "475")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("SOAPAction", "http://tempuri.org/ObtenerDatosListaArticuloEtiquetasPl")
                        .build();


                EnableDialog(true, "Conectando", false);
                Borrar();

                Pickinghttp.newCall(RequestPicking).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        DisplayPrintingStatusMessage("Conexion Fallo");
                        EnableDialog(false, "", false);
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

                        if (response.isSuccessful()) {
                            try {

                                Asignar("Cargando...", "Cargando");
                                final String myResponse = response.body().string();
                                ParserList parserXmlPicking = new ParserList(ListTagGondola.this);
                                Document doc = toXmlDocument(myResponse);
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                Source xmlSource = new DOMSource(doc);
                                Result outputTarget = new StreamResult(outputStream);
                                TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
                                InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

                                if (parserXmlPicking.parsear(is)) {

                                    DisplayPrintingStatusMessage("Completado");
                                    cargarLista2run();

                                } else {
                                    DisplayPrintingStatusMessage("No hay Datos");
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
            }
        });
    }

    private ProgressDialog dialog;

    private void createCancelProgressDialog(String title, String message, String buttonText, Boolean cancelar) {
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


    public void EnableDialog(final boolean value, final String mensaje, final boolean cancelar) {
        m_handler.post(new Runnable() {
            @Override
            public void run() {
                if (value) {
                    createCancelProgressDialog("Cargando...", mensaje, "Cancelar", cancelar);
                } else {
                    if (dialog != null)
                        dialog.dismiss();
                }
            }
        });
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


    private void cargarLista2() {

        m_handler.post(new Runnable() {
            @Override
            public void run() {
                try {

                    list2 = new ArrayList<>();
                    for (List_Producto pickingproducto : list) {

                        if (checkBoxOferta.isChecked()) {

                            if (pickingproducto.getOff_available().equals("S")) {
                                list2.add(pickingproducto);
                            }

                        } else {

                            if (pickingproducto.getOff_available().equals("N")) {
                                list2.add(pickingproducto);
                            }
                        }

                    }

                    adapter.setNotes(list2);
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Log.e("errorW", "mensaje");
                }
            }
        });

    }


    private void cargarLista2run() {


        m_handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    db = new List_ProductosDB(ListTagGondola.this);
                    list = db.loadListProducto();
                    cargarListaTemp();

                } catch (Exception e) {
                    Log.e("errorW", "mensaje");
                }
            }
        });

    }

    private void cargarLista() {
        try {
            db = new List_ProductosDB(ListTagGondola.this);
            list = db.loadListProducto();
            cargarListaTemp();

        } catch (Exception e) {
            Log.e("errorW", "mensaje");
        }
    }

    private void cargarListaTemp() {
        try {

            if (list.size() > 0) {
                list2 = new ArrayList<>();
                for (List_Producto pickingproducto : list) {

                    if (checkBoxOferta.isChecked()) {

                        if (pickingproducto.getOff_available().equals("S")) {
                            list2.add(pickingproducto);
                        }

                    } else {

                        if (pickingproducto.getOff_available().equals("N")) {
                            list2.add(pickingproducto);
                        }
                    }
                }

                adapter.setNotes(list2);
                adapter.notifyDataSetChanged();
            }


        } catch (Exception e) {
            Log.e("errorW", "mensaje");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menulist, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.test_impresion:

                AlertDialog.Builder build3 = new AlertDialog.Builder(ListTagGondola.this);
                build3.setMessage("¿Desea imprimir un test de impresión? ").setPositiveButton("Verificar impresión", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    EnableDialog(true, "Enviando Documento...", true);
                                    TscDll.openport(m_printerMAC);
                                    TscDll.setup(76, 35, 2, 1, 1, 4, 0);



                                    TscDll.clearbuffer();
                                    TscDll.sendcommand("CODEPAGE UTF-8\n");
                                    TscDll.sendcommand("SET TEAR ON\n");
                                    TscDll.sendcommand("DIRECTION 0\n");
                                    TscDll.sendcommand("TEXT 70,20,\"arial_na.TTF\",0,11,11,\"" + "descripcion1" + "\"\n");
                                    TscDll.sendcommand("TEXT 70,55,\"arial_na.TTF\",0,08,08,\"" + "descripcion2" + "\"\n");
                                    TscDll.sendcommand("TEXT 80,90,\"arial_na.TTF\",0,08,08,\"" + "Precio Anterior" + "\"\n");
                                    TscDll.sendcommand("TEXT 70,120,\"strike_t.TTF\",0,13,13,\"" + "$ 1.250" + "\"\n");
                                    TscDll.sendcommand("TEXT 280,80,\"arial_na.TTF\",0,08,08,\"" + "Precio" + "\"\n");
                                    TscDll.sendcommand("TEXT 280,115,\"arial_bl.TTF\",0,20,20,\"" + "$ 1.600" + "\"\n");
                                    TscDll.sendcommand("TEXT 400,190,\"arial_na.TTF\",0,11,11,\"" + "123456" + "\"\n");
                                    TscDll.barcode(70, 180, "128", 30, 1, 0, 1, 1, "123456");
                                    TscDll.printlabel(1, 1);

                                    TscDll.closeport(500);
                                    EnableDialog(false, "", false);
                                    DisplayPrintingStatusMessage("Impresión Exitosa.");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    EnableDialog(false, "", false);
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

                AlertDialog.Builder build = new AlertDialog.Builder(ListTagGondola.this);
                build.setMessage("¿Desea imprimir toda la lista? ").setPositiveButton("Imprimir todos", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tagimprimirall = true;
                        new Thread(ListTagGondola.this, "PrintingTask").start();

                    }


                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                AlertDialog alertDialog = build.create();
                alertDialog.show();

                break;

            case R.id.Restaurar:

                AlertDialog.Builder build2 = new AlertDialog.Builder(ListTagGondola.this);
                build2.setMessage("¿Desea restaurar Tags impresos? ").setPositiveButton("Restaurar Tags Impresos", new DialogInterface.OnClickListener() {
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


    private void restaurarimpresion() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    EnableDialog(true, "Restaurando Lista..", false);
                    if (list2.size() > 0) {
                        for (List_Producto prod : list2) {
                            if (prod.getIP().equals("SI")) {
                                prod.setIP("NO");
                                db.updatecodigoproduto(prod);
                            }
                        }
                        cargarLista2();
                    }
                    EnableDialog(false, "Enviando terminando...", false);
                    DisplayPrintingStatusMessage("Reimprimir restaurado");

                } catch (Exception e) {
                    Log.e("errorW", "mensaje");
                    EnableDialog(false, "Enviando terminando...", false);
                    DisplayPrintingStatusMessage("Fallo la base de datos.");
                }
            }
        };

        thread.start();


    }

    @Override
    public void run() {
        //PRUEBA DE IMPRESION

        try {

            EnableDialog(true, "Imprimiendo...", true);
            String estado = "00";
            TscDll.openport(m_printerMAC);
            TscDll.setup(76, 35, 2, 1, 1, 4, 0);

            if (!tagimprimirall) {

                Asignar("Imprimiendo: ", lista_producto_seleccionada.getDescArticulo_1());
                TscDll.clearbuffer();
                TscDll.sendcommand("CODEPAGE UTF-8\n");
                TscDll.sendcommand("SET TEAR ON\n");
                TscDll.sendcommand("DIRECTION 0\n");
                TscDll.sendcommand("TEXT 70,20,\"arial_na.TTF\",0,11,11,\"" + lista_producto_seleccionada.getDescArticulo_1() + "\"\n");
                TscDll.sendcommand("TEXT 70,55,\"arial_na.TTF\",0,08,08,\"" + lista_producto_seleccionada.getDescArticulo_2() + "\"\n");
                TscDll.sendcommand("TEXT 280,80,\"arial_na.TTF\",0,08,08,\"" + "Precio" + "\"\n");

                if(lista_producto_seleccionada.getOff_available().equals("N")){
                    TscDll.sendcommand("TEXT 280,115,\"arial_bl.TTF\",0,20,20,\"" + "$ " + lista_producto_seleccionada.getPrecio_lista() + "\"\n");
                }else{
                    TscDll.sendcommand("TEXT 80,90,\"arial_na.TTF\",0,08,08,\"" + "Precio Anterior" + "\"\n");
                    TscDll.sendcommand("TEXT 70,120,\"strike_t.TTF\",0,13,13,\"" + "$ " + lista_producto_seleccionada.getPrecio_lista() + "\"\n");
                    TscDll.sendcommand("TEXT 280,115,\"arial_bl.TTF\",0,20,20,\"" + "$ " + lista_producto_seleccionada.getPrecio() + "\"\n");
                }
                TscDll.sendcommand("TEXT 400,190,\"arial_na.TTF\",0,11,11,\"" + lista_producto_seleccionada.getCodProd() + "\"\n");
                TscDll.barcode(70, 180, "128", 30, 1, 0, 1, 1, lista_producto_seleccionada.getCodBarras());
                TscDll.printlabel(1, 1);
                lista_producto_seleccionada.setIP("SI");
                db.updatecodigoproduto(lista_producto_seleccionada);


            } else {

                db = new List_ProductosDB(ListTagGondola.this);
                for (List_Producto prod : list2) {

                    if (cancelarrun) {
                        break;
                    }


                    if (!prod.getIP().equals("SI")) {
                        Asignar("Imprimiendo: ", prod.getDescArticulo_1());
                        TscDll.clearbuffer();
                        TscDll.sendcommand("CODEPAGE UTF-8\n");
                        TscDll.sendcommand("SET TEAR ON\n");
                        TscDll.sendcommand("DIRECTION 0\n");
                        TscDll.sendcommand("TEXT 70,20,\"arial_na.TTF\",0,11,11,\"" + prod.getDescArticulo_1() + "\"\n");
                        TscDll.sendcommand("TEXT 70,55,\"arial_na.TTF\",0,08,08,\"" + prod.getDescArticulo_2() + "\"\n");


                        TscDll.sendcommand("TEXT 280,80,\"arial_na.TTF\",0,08,08,\"" + "Precio" + "\"\n");
                        if(prod.getOff_available().equals("N")){
                            TscDll.sendcommand("TEXT 280,115,\"arial_bl.TTF\",0,20,20,\"" + "$ " + prod.getPrecio_lista() + "\"\n");
                        }else{
                            TscDll.sendcommand("TEXT 80,90,\"arial_na.TTF\",0,08,08,\"" + "Precio Anterior" + "\"\n");
                            TscDll.sendcommand("TEXT 70,120,\"strike_t.TTF\",0,13,13,\"" + "$ " + prod.getPrecio_lista() + "\"\n");
                            TscDll.sendcommand("TEXT 280,115,\"arial_bl.TTF\",0,20,20,\"" + "$ " + prod.getPrecio() + "\"\n");
                        }
                        TscDll.sendcommand("TEXT 400,190,\"arial_na.TTF\",0,11,11,\"" + prod.getCodProd() + "\"\n");
                        TscDll.barcode(70, 180, "128", 30, 1, 0, 1, 1, prod.getCodBarras());
                        estado = TscDll.printerstatus();
                        TscDll.printlabel(1, 1);
                        prod.setIP("SI");
                        db.updatecodigoproduto(prod);
                        try {

                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }


                    int timepo = 50;
                    while (!estado.equals("00") && !estado.equals("20")) {

                        Asignar("Tapa Abierta", prod.getDescArticulo_1() + "\n\n" +
                                "Cambiar Rollo antes de: " + timepo);
                        estado = TscDll.printerstatus();
                        timepo--;

                        if (cancelarrun || timepo == 0) {
                            cancelarrun = true;
                            break;
                        }
                    }
                }
            }
             TscDll.closeport(500);
            cargarLista2();
            EnableDialog(false, "Finalizando...", false);
            DisplayPrintingStatusMessage("Finalizado.");
            cancelarrun = false;
            tagimprimirall = false;


        } catch (Exception e) {
            e.printStackTrace();
            EnableDialog(false, "Finalizando...", false);
            DisplayPrintingStatusMessage("Fallo conexion Bluetooth.");
            tagimprimirall = false;
            cancelarrun = false;

        }

    }
}
