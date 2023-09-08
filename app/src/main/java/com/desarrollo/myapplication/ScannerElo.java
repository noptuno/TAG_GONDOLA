package com.desarrollo.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.desarrollo.myapplication.Clases.Producto;
import com.desarrollo.myapplication.ParsearXML.ParserXml;
import com.desarrollo.myapplication.ParsearXML.ParserXmlElo;
import com.desarrollo.myapplication.apiadapter.ActivityMonitor;
import com.desarrollo.myapplication.apiadapter.ApiAdapter;
import com.desarrollo.myapplication.apiadapter.ApiAdapterFactory;
import com.elo.device.DeviceManager;
import com.elo.device.ProductInfo;
import com.elo.device.enums.BcrEnableControl;
import com.elo.device.enums.EloPlatform;
import com.elo.device.inventory.Inventory;
import com.elo.device.inventory.PrinterSupported;
import com.elo.device.peripherals.BarCodeReader;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

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


public class ScannerElo extends AppCompatActivity {
    private OkHttpClient Pickinghttp;

    private String sucursal;

    private String m_printerMAC = null;
    private String m_ip = null;
    private ApiAdapter apiAdapter;



    private Inventory inventory;
    private Handler m_handler = new Handler(); // Main thread
    private Request RequestPicking;
    private EditText txtbarcoderreader;
    private ProgressDialog dialog;
    public static ProductInfo productInfo;
    private TextView eloDescArticulo_1;
    private TextView eloDescArticulo_2;
    private TextView eloCodProd;
    private TextView eloCodBarras;
    private TextView eloPrecio;
    private TextView eloOferta;
    private LinearLayout elolayoutetiquet;
    private TextView estado;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnBcrOff();
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiAdapter.getActivityMonitor().onActivityEvent(ActivityMonitor.EVENT_ON_RESUME);

        //updatePaperStatus();

    }

    @Override
    protected void onPause() {
        super.onPause();
        apiAdapter.getActivityMonitor().onActivityEvent(ActivityMonitor.EVENT_ON_PAUSE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        apiAdapter.getActivityMonitor().onActivityEvent(ActivityMonitor.EVENT_ON_START);
    }

    @Override
    protected void onStop() {
        super.onStop();
        apiAdapter.getActivityMonitor().onActivityEvent(ActivityMonitor.EVENT_ON_STOP);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_elo);

        txtbarcoderreader = findViewById(R.id.txt_codigo);
        eloDescArticulo_1 = findViewById(R.id.txt_DescArticulo_11);
        eloDescArticulo_2 = findViewById(R.id.txt_DescArticulo_22);
        eloCodProd = findViewById(R.id.txt_CodProdd);
        eloCodBarras = findViewById(R.id.txt_CodBarrass);
        eloPrecio = findViewById(R.id.txt_Precioo);
        eloOferta = findViewById(R.id.txt_ofertaa);
        elolayoutetiquet = findViewById(R.id.layoutetiqueta);
        estado = findViewById(R.id.TXTESTADO);
        eloDescArticulo_1.setText("");
        eloDescArticulo_2.setText("");
        eloCodProd.setText("");
        eloCodBarras.setText("");
        eloPrecio.setText("");
        eloOferta.setText("");

        Bundle parametros = getIntent().getExtras();

        if (parametros != null) {
            sucursal = (parametros.getString("suc"));
            m_printerMAC = (parametros.getString("mac"));
            m_ip = (parametros.getString("ip"));

        } else {
            Toast.makeText(getApplicationContext(), "No hay datos a mostrar", Toast.LENGTH_LONG).show();
        }

        inventory();

        txtbarcoderreader.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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


        ocultarteclado();

    }

    private void inventory() {

        inventory = DeviceManager.getInventory(this);

        if (!inventory.isEloSdkSupported()) {
        Toast.makeText(this, "Platform not recognized or supported, sorry", Toast.LENGTH_LONG).show();
        }

       // productInfo = DeviceManager.getPlatformInfo();
       // EloPlatform platform = productInfo.eloPlatform;

        apiAdapter = ApiAdapterFactory.getInstance(this).getApiAdapter(inventory);

        if (apiAdapter == null) {
            Log.d("TAF", "Cannot find support for this platform");
        }

        if (inventory.barCodeReaderEnableControl() == BcrEnableControl.FULL) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    turnBcrOn();
                }
            });
        }

    }

    private void updatePaperStatus() {

        try {
            if (apiAdapter.hasPaper()) {
                estado.setText("OK");

            } else {
                estado.setText("Out");

            }

        } catch (IOException e) {
            Toast.makeText(ScannerElo.this, "IOError occured trying to check for paper", Toast.LENGTH_SHORT).show();
        }

    }

    private void presionarboton() {

        String codigoimprimir = txtbarcoderreader.getText().toString().trim();
        if (!codigoimprimir.equals("")) {
            imprimirCodigo(codigoimprimir);
            txtbarcoderreader.setText("");
            ocultarteclado();
        }
        if (txtbarcoderreader.isFocused()) {
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

    private BarCodeReader.BarcodeReadCallback callback = new BarCodeReader.BarcodeReadCallback() {
        @Override
        public void onBarcodeRead(byte[] bytes) {
            String output;
            try {
                output = new String(bytes, "US-ASCII");
            } catch (UnsupportedEncodingException e) {
                output = "--UnReadable--";
            }
            final String outputCopy = output;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imprimirCodigo(outputCopy);
                    // txtbarcoderreader.setText(outputCopy);
                }
            });

        }
    };


    public void onClickPrintReceiptButton(View v) {

        if (!inventory.hasPrinter()) {
            Toast.makeText(this, "Printer not connected", Toast.LENGTH_SHORT).show();
        }
/*
        if (inventory.getPrinterDeviceType() == PrinterSupported.STAR) {
            Toast.makeText(this, "This Printer can not print recovery barcode, refer SDK documentation to get recovery barcode", Toast.LENGTH_LONG).show();
        }
*/
        try {
            apiAdapter.printDemo();
        } catch (RuntimeException | IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }




    }


    public void turnBcrOn() {
        if (inventory.barCodeReaderEnableControl() == BcrEnableControl.FULL) {
            if (inventory.barCodeReaderSupportsVComMode()) {

                // Can't do much otherwise
                apiAdapter.setBarCodeReaderCallback(callback);
                apiAdapter.setBarCodeReaderEnabled(true);

            } else {
                if (!apiAdapter.isBarCodeReaderEnabled()) {
                    apiAdapter.setBarCodeReaderEnabled(true);
                }
            }
        }
    }

    public void turnBcrOff() {
        if (inventory.barCodeReaderEnableControl() == BcrEnableControl.FULL) {
            if (inventory.barCodeReaderSupportsVComMode()) {

                // Can't do much otherwise
                apiAdapter.setBarCodeReaderEnabled(false);
                apiAdapter.setBarCodeReaderCallback(null);

            } else {
                if (apiAdapter.isBarCodeReaderEnabled()) {
                    apiAdapter.setBarCodeReaderEnabled(false);
                }
            }
        }
    }

    public void onClickTurnOn(View view) {
        Log.d("PRENDER", "Turning on Barcode Reader");
        apiAdapter.setBarCodeReaderEnabled(true);
    }

    public void onClickTurnOff(View view) {
        Log.d("APAGAR", "Turning off Barcode Reader");
        apiAdapter.setBarCodeReaderEnabled(false);
    }

    public void imprimirCodigo(final String codigoverificar) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final String codigocaptrado = codigoverificar;

                final com.desarrollo.myapplication.Clases.Producto[] prod = {new Producto()};
                Pickinghttp = new OkHttpClient();
                MediaType mediaType = MediaType.parse("text/xml");

                RequestBody body = RequestBody.create(mediaType,
                        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
                                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                                "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n " +
                                " <soap:Body>\r\n  " +
                                "  <ObtenerDatosArticuloEtiquetas xmlns=\"http://tempuri.org/\">\r\n " +
                                "     <p_suc>" + sucursal + "</p_suc>\r\n    " +
                                "  <p_producto>" + codigocaptrado + "</p_producto>\r\n " +
                                "   </ObtenerDatosArticuloEtiquetas>\r\n " +
                                " </soap:Body>\r\n" +
                                "</soap:Envelope>\r\n\r\n");

                RequestPicking = new Request.Builder()
                        .url("http://" + m_ip + "/WSSREtiquetas/EtiquetaService.asmx")
                        .post(body)
                        .addHeader("Content-Type", "text/xml")
                        .addHeader("User-Agent", "PostmanRuntime/7.18.0")
                        .addHeader("Accept", "*/*")
                        .addHeader("Cache-Control", "no-cache")
                        .addHeader("Postman-Token", "a76f7625-a2c8-4806-853a-877dff25011f,faebb6f7-d421-44eb-8a54-eb1786e95136")
                        .addHeader("Host", m_ip)
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Content-Length", "428")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("cache-control", "no-cache")
                        .build();

                EnableDialog(true, "Conectando", false);

                Pickinghttp.newCall(RequestPicking).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        EnableDialog(false, "", false);
                        DisplayPrintingStatusMessage("Conexion Fallo");
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

                        if (response.isSuccessful()) {
                            try {

                                final String myResponse = response.body().string();

                                ParserXmlElo parserXmlPicking = new ParserXmlElo(ScannerElo.this);

                                Document doc = toXmlDocument(myResponse);
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                Source xmlSource = new DOMSource(doc);
                                Result outputTarget = new StreamResult(outputStream);
                                TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
                                InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

                                Producto a = parserXmlPicking.parsear(is);

                                cardatosmostrar(a);

                                DisplayPrintingStatusMessage(a.getDescArticulo_1().toString());


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

    private void cardatosmostrar(final Producto a) {


        m_handler.post(new Runnable() {
            public void run() {

                if (a != null) {

                    try {
                        eloDescArticulo_1.setText(a.getDescArticulo_1());
                        eloDescArticulo_2.setText(a.getDescArticulo_2());
                        eloCodProd.setText(a.getCodProd());
                        eloCodBarras.setText(a.getCodProd());
                        eloPrecio.setText(a.getPrecio());
                        eloOferta.setText(a.getTxt_oferta());


                        if (a.getOff_available().toString().equals("N")) {

                            eloOferta.setText("");
                            elolayoutetiquet.setBackground(ContextCompat.getDrawable(ScannerElo.this, R.drawable.ic_tag_60x30));

                        } else {
                            eloOferta.setText(a.getTxt_oferta());
                            elolayoutetiquet.setBackground(ContextCompat.getDrawable(ScannerElo.this, R.drawable.ic_tag_60x30_amarillo));
                        }

                        DisplayPrintingStatusMessage("correxcto");
                    } catch (Exception e) {
                        DisplayPrintingStatusMessage("No disponible");
                        eloDescArticulo_1.setText("");
                        eloDescArticulo_2.setText("");
                        eloCodProd.setText("");
                        eloCodBarras.setText("");
                        eloPrecio.setText("");
                        eloOferta.setText("");

                    }


                }
            }// run()
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
                    createCancelProgressDialog("Cargando: ", mensaje, "Cancelar", cancelar);
                } else {
                    if (dialog != null)
                        dialog.dismiss();
                }
            }
        });
    }

    private void createCancelProgressDialog(String title, String message, String buttonText, Boolean cancelar) {
        dialog = new ProgressDialog(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton(buttonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        dialog.show();
    }


}
