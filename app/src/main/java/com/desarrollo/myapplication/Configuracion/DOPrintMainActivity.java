package com.desarrollo.myapplication.Configuracion;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.desarrollo.myapplication.BaseDatos.RubroDB;
import com.desarrollo.myapplication.Clases.GruposRubros;
import com.desarrollo.myapplication.Constantes.Constante;
import com.desarrollo.myapplication.R;
import com.desarrollo.myapplication.TagGondolaItau;
import com.example.tscdll.TSCActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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


public class DOPrintMainActivity extends AppCompatActivity implements Runnable {

    //Keys to pass data to/from FileBrowseActivity
    static final String FOLDER_NAME_KEY = "com.honeywell.doprint.Folder_Name_Key";
    static final String FOLDER_PATH_KEY = "com.honeywell.doprint.Folder_Path_Key";
    public static Integer ESPERA = 0;
    //Keys to pass data to Connection Activity
    static final String CONNECTION_MODE_KEY = "com.honeywell.doprint.Connection_Mode_Key";
    static final String PRINTER_IPADDRESS_KEY = "com.honeywell.doprint.PRINTER_IPAddress_Key";

    static final String PRINTER_TCPIPPORT_KEY = "com.honeywell.doprint.PRINTER_TCPIPPort_Key";
    static final String BLUETOOTH_DEVICE_NAME_KEY = "com.honeywell.doprint.PRINTER_Bluetooth_Device_Name_Key";
    static final String BLUETOOTH_DEVICE_ADDR_KEY = "com.honeywell.doprint.PRINTER_Bluetooth_Device_Addr_Key";
    static final String USB_DEVICENAME = "com.honeywell.dmrprint.USB_DEVICENAME";
    static final String USB_PRODUCTOID = "com.honeywell.doprint.USB_PRODUCTOID";

    static final String PRINTER_USB_NOMBRE = "com.honeywell.doprint.PRINTER_USB_NOMBRE";
    static final String PRINTER_USB_DIRECCION = "com.honeywell.doprint.PRINTER_USB_DIRECCION";
    static final String PRINTER_USB_ID = "com.honeywell.doprint.PRINTER_USB_ID";
    static final String PRINTER_USB_KEY = "com.honeywell.doprint.PRINTER_USB_Key";

    TSCActivity TscDll = new TSCActivity();

    private Context mContext;

    private String ipwebservice;
    private String webservice;
    private String numerodesucursal;

    private LinearLayout layoutprinter;
    private String m_selectedPath;
    private String m_printerIP = null;
    private String m_printerMAC = null;
    private int m_printerPort = 515;

    private int sucursaltext = 0;
    private String connectionType;
    //ConnectionBase conn = null;
    private int m_printHeadWidth = 384; //572

    private Button btnrubros;

    private DispositivoBT btconfig;
    private String g_PrintStatusStr;

    ArrayAdapter<CharSequence> adapter = null;

    //array to contain the filenames inside a directory
    List<String> filesList = new ArrayList<>();

    static final int CONFIG_CONNECTION_REQUEST = 0; // for Connection Settings
    private static final int REQUEST_PICK_FILE = 1; //for File browsing

    private Handler m_handler = new Handler(); // Main thread

    private String m_printerMode = "";
    Button m_printButton;
    Button m_saveButton;

    private EditText tip;
    private EditText twe;

    private String m_printerComandMethod= null;


    String m_devicename;
    String m_productoid;

    Button m_configConnectionButton;
    RadioGroup m_performTaskRadioGroup;

    //EditText
    TextView m_connectionInfoStatus;
    TextView m_actionTextView;

    //Spinners
    Spinner m_connectionSpinner;
    Spinner m_PrinterModerSpinner;
    EditText txtsucursal;
    Spinner m_printItemsSpinner;
    Spinner m_printHeadSpinner;
    Spinner Spinnerrubros;
    ArrayAdapter comboAdapter;
    Button btncambiarpassword;
    private int mini = 0;

    private int automatico = 0;
    byte[] printData = {0};

    // Configuration files to load
    String ApplicationConfigFilename = "applicationconfigg.dat";



    DMRPrintSettings g_appSettings = new DMRPrintSettings("", 0, "0", 0, 0, "","",0,0,"","","","");
    //2018 PH
    private IntentFilter mIntentFilter;//Se usa para el broadcast. Del lado del servicio asigno un setAction al intent que voy a enviar con sendBroadcast.
    // De este lado, al estar registrado el BroadcastReceiver con el IntentFilter, se "filtra" por esa acción del setAction y se produce el onReceive del BroadcastReceiver.
    Intent serviceIntent;
    public static final String mBroadcastStringAction = "broadcastStringAction";
    private String m_selectedPathFolder;
    Button m_iniciarTaskButton;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION = 0;

    HashMap<String, UsbDevice> mDeviceList;
    Iterator<UsbDevice> mDeviceIterator;


    @Override
    protected void onResume() {
        super.onResume();
        cargarSpiner();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doprint_main);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int readExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (writeExternalPermission != PackageManager.PERMISSION_GRANTED || readExternalPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }
        }

        mContext = this.getApplicationContext();

        DMRPrintSettings appSettings = ReadApplicationSettingFromFile();
        if (appSettings != null)

            g_appSettings = appSettings;
        //Get parameters from application settings
        m_printerMAC = g_appSettings.getPrinterMAC();
        m_printerMode = g_appSettings.getSelectedPrintMode();//2018 PH
        numerodesucursal = g_appSettings.getSuc();//2018 PH
        ipwebservice = g_appSettings.getIpwebservice();//2018 PH
        webservice = g_appSettings.getWebservice();//2018 PH

        m_printerIP = g_appSettings.getPrinterIP();
        m_printerPort = g_appSettings.getSelectedPrinterPort();

        layoutprinter = findViewById(R.id.linear_prn);


        //======Mapping UI controls from our activity xml===========//

        tip = findViewById(R.id.txt_ipwebservice);
        twe = findViewById(R.id.txtwebservice);
        m_connectionInfoStatus = (TextView) findViewById(R.id.communication_status_information);

        m_connectionSpinner = (Spinner) findViewById(R.id.connection_spinner);
        m_PrinterModerSpinner  = (Spinner) findViewById(R.id.spi_selectprintmode);


        m_configConnectionButton = (Button) findViewById(R.id.configConn_button);
        m_printButton = (Button) findViewById(R.id.print_button);
         m_saveButton = (Button) findViewById(R.id.saveSettings_button);

        m_connectionSpinner.setSelection(g_appSettings.getCommunicationMethod());
        m_PrinterModerSpinner.setSelection(g_appSettings.getPrinterComandMethodPosition());


        //m_SelectedPrintModerSpiner.setSelection(g_appSettings.getSelectedPrintMode());



        btncambiarpassword = findViewById(R.id.btncambiarcontra);
        txtsucursal = findViewById(R.id.sucursal);
        txtsucursal.setText(numerodesucursal);
        tip.setText(ipwebservice);
        twe.setText(webservice);
        Spinnerrubros = (Spinner) findViewById(R.id.spinnerrubros);

        btnrubros = findViewById(R.id.btnrubros);

        btnrubros.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DOPrintMainActivity.this, Registro_Grupo_Rubros.class);
                  startActivity(intent);

            }
        });


        //setSelectedPrintMode


        m_PrinterModerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                m_printerComandMethod = m_PrinterModerSpinner.getSelectedItem().toString();


                g_appSettings.setPrinterComandMethod(m_printerComandMethod);


                g_appSettings.setPrinterComandMethodPosition(m_PrinterModerSpinner.getSelectedItemPosition());


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        m_connectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                connectionType = m_connectionSpinner.getSelectedItem().toString();
                g_appSettings.setCommunicationType(connectionType);

                if (connectionType.equals("TCP/IP"))
                {
                    if (m_printerIP.length() == 0)
                    {
                        m_connectionInfoStatus.setText(R.string.connection_not_configured);
                    }
                    else {
                        String printerInfo = "Printer's IP Address/Port: "+ m_printerIP + ":"+ Integer.toString(m_printerPort);
                        m_connectionInfoStatus.setText(printerInfo);
                    }

                    layoutprinter.setVisibility(View.VISIBLE);

                }else if (connectionType.equals("Bluetooth")) {

                    layoutprinter.setVisibility(View.GONE);

                    if (m_printerMAC.length() == 0) {
                        m_connectionInfoStatus.setText(R.string.connection_not_configured);
                    } else {
                        String printerInfo = "Printer's MAC Address: " + m_printerMAC;
                        m_connectionInfoStatus.setText(printerInfo);
                    }
                }

                g_appSettings.setCommunicationMethod(m_connectionSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        // ------------------------------------------------
        // Handles when user presses connection config button
        // -------------------------------------------------
        m_configConnectionButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent connSettingsIntent = new Intent("com.desarrollo.myapplication.ConnectionSettingsActivity");
                Spinner connectionSpinner = (Spinner) findViewById(R.id.connection_spinner);
                String connectionType = connectionSpinner.getSelectedItem().toString();
                connSettingsIntent.putExtra(CONNECTION_MODE_KEY, connectionType);
                connSettingsIntent.putExtra(PRINTER_IPADDRESS_KEY, m_printerIP);
                connSettingsIntent.putExtra(PRINTER_TCPIPPORT_KEY, m_printerPort);
                connSettingsIntent.putExtra(BLUETOOTH_DEVICE_ADDR_KEY, m_printerMAC);
                connSettingsIntent.putExtra(USB_DEVICENAME, m_devicename);
                connSettingsIntent.putExtra(USB_PRODUCTOID, m_productoid);
                startActivityForResult(connSettingsIntent, CONFIG_CONNECTION_REQUEST);
            }
        });


        btncambiarpassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DOPrintMainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.alerdiaglogcrearpassword, null);

                final EditText mEmail = (EditText) mView.findViewById(R.id.etEmail);
                final EditText mPassword = (EditText) mView.findViewById(R.id.etPassword);
                final EditText mPassword2 = (EditText) mView.findViewById(R.id.etpassword2);
                final TextView text = (TextView) mView.findViewById(R.id.txt_sucursal);
                Button mLogin = (Button) mView.findViewById(R.id.btnLogin);


                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                mLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mPassword.getText().toString().isEmpty() && !mPassword2.getText().toString().isEmpty()) {

                            if (mPassword.getText().toString().equals(mPassword2.getText().toString())){

                                SharedPreferences pref = getSharedPreferences(Constante.LOGINNAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(Constante.CREADO,"SI");
                                editor.putString(Constante.PASSWORD,mPassword2.getText().toString());
                                editor.apply();

                                Toast.makeText(DOPrintMainActivity.this,"Se actualizo su Contraseña",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }else{

                                mPassword.setError("No coinciden");
                                mPassword2.setError("No coinciden");

                            }

                        } else {

                            mPassword.setError("Falta datos");
                            mPassword2.setError("Falta Datos");

                        }
                    }
                });

            }
        });


        m_printButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(DOPrintMainActivity.this, "PrintingTask").start();
            }
        });


        m_saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (!tip.getText().toString().isEmpty() && !txtsucursal.getText().toString().isEmpty() && tip.getText().toString().length()>1 && !txtsucursal.getText().toString().equals("0")) {

                    ipwebservice = tip.getText().toString().trim();
                    webservice = twe.getText().toString().trim();
                    numerodesucursal = txtsucursal.getText().toString().trim();
                    g_appSettings.setIpwebservice(ipwebservice);
                    g_appSettings.setWebservice(webservice);
                    g_appSettings.setSuc(numerodesucursal);
                    g_appSettings.setConfigurado(1);



                    SaveApplicationSettingToFile();
                }else{
                    tip.setError("Verifique su Ip");
                    txtsucursal.setError("Verifique su Numero");
                }
            }
        });

        Spinnerrubros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                String select = Spinnerrubros.getSelectedItem().toString();

                try {
                    RubroDB dbb = new RubroDB(DOPrintMainActivity.this);
                    ArrayList<GruposRubros> list = dbb.loadGrupoRubro();
                    for (GruposRubros log : list) {

                        if (log.getDescripcionGrupoRubro().equals(select)){

                            Log.e("SELECCION", "seleciono" + select +" "+ log.getNumeroGrupoRubro());
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



        cargarSpiner();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);
    }

    private void Borrar() {
        try {
            RubroDB dbd = new RubroDB(DOPrintMainActivity.this);
            dbd.eliminarRubros();
            cargarSpiner();

        } catch (Exception e) {
            Log.e("errorO", "mensaje");
        }

    }

    private void cargarSpiner() {


        try {
            RubroDB dbb = new RubroDB(DOPrintMainActivity.this);
            ArrayList<GruposRubros> list = dbb.loadGrupoRubro();


            ArrayList<String> list2= new ArrayList<>();
            for (GruposRubros log : list) {
                list2.add(log.Spinner());
                Log.i("---> Base de datos:L ", log.Spinner() );
            }

         comboAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list2);
            
        } catch (Exception e) {
            Log.e("errorW", "mensaje");

        }
        
      
        //Cargo el spinner con los datos
        Spinnerrubros.setAdapter(comboAdapter);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //get results from connection settings activity
            case CONFIG_CONNECTION_REQUEST: {
                if (resultCode == RESULT_OK) {
                    // get the bundle data from the TCP/IP Config Intent.
                    Bundle extras = data.getExtras();

                    if (extras != null) {
                        //===============Get data from Bluetooth configuration=================//
                        if (connectionType.equals("Bluetooth")) {
                            String btAddressString = extras.getString(BLUETOOTH_DEVICE_ADDR_KEY);
                            if (btAddressString != null)
                                m_printerMAC = btAddressString.toUpperCase(Locale.US);
                            if (!m_printerMAC.matches("[0-9A-fa-f:]{17}")) {
                                m_printerMAC = formatBluetoothAddress(m_printerMAC);
                            }

                            String printerInfo = "Printer's MAC Address: " + m_printerMAC;
                            m_connectionInfoStatus.setText(printerInfo);
                        }
                        //==============Get data from TCP/IP configuration===================//
                        else if (connectionType.equals("TCP/IP")) {

                            m_printerIP = extras.getString(PRINTER_IPADDRESS_KEY);
                            m_printerPort = extras.getInt(PRINTER_TCPIPPORT_KEY);

                            String printerInfo = "Printer's IP Address/Port: " + m_printerIP + ":" + Integer.toString(m_printerPort);
                            m_connectionInfoStatus.setText(printerInfo); //valid values are 3inch

                        }

                        g_appSettings.setPrinterIP(m_printerIP);
                        g_appSettings.setSelectedPrinterPort(m_printerPort);
                        g_appSettings.setPrinterMAC(m_printerMAC);


                    }
                }
                break;
            }

            case REQUEST_PICK_FILE: {
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        //========Get the file path===============//
                        m_selectedPath = extras.getString(FOLDER_PATH_KEY);

                        if (!filesList.contains(m_selectedPath))
                            filesList.add(m_selectedPath);
                        if (adapter != null) {
                            m_printItemsSpinner.setAdapter(adapter);

                            //2018 PH

                            //if item is not on the list, then add
                            if (adapter.getPosition(m_selectedPath) < 0) {
                                adapter.add(m_selectedPath);
                                adapter.notifyDataSetChanged();
                            }
                            //selects the selected one.
                            m_printItemsSpinner.setSelection(adapter.getPosition(m_selectedPath));
                        }
                    }
                }
                break;
            }
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //reload your ScrollBars by checking the newConfig

    }

    // -------------------------------------------------
    // Read the application configuration information from a file.
    // -------------------------------------------------

    DMRPrintSettings ReadApplicationSettingFromFile() {
        DMRPrintSettings ret = null;
        InputStream instream;
        try {
            //showToast("Loading configuration");
            showToast("Cargando configuración...");
            instream = openFileInput(ApplicationConfigFilename);
        } catch (FileNotFoundException e) {

            Log.e("DOPrint", e.getMessage(), e);
            //showToast("No configuration loaded");
            showToast("No hay configuración para cargar...");
            return null;
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(instream);

            try {
                ret = (DMRPrintSettings) ois.readObject();
            } catch (ClassNotFoundException e) {
                Log.e("DOPrint", e.getMessage(), e);
                ret = null;
            }
        } catch (Exception e) {
            Log.e("DOPrint", e.getMessage(), e);
            ret = null;
        } finally {
            try {
                if (instream != null)
                    instream.close();
            } catch (IOException ignored) {
            }
        }
        return ret;
    }


    public boolean SaveApplicationSettingToFile() {

        boolean bRet = true;
        FileOutputStream fos = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            // write the object to the output stream object.
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(g_appSettings);

            // convert the output stream object to array of bytes
            byte[] buf = bos.toByteArray();

            // write the array of bytes to file output stream
            fos = openFileOutput(ApplicationConfigFilename,
                    Context.MODE_PRIVATE);
            fos.write(buf);

            File f = getDir(ApplicationConfigFilename, 0);
            Log.e("DOPrint", "Save Application settings to file: " + f.getName());
            //showToast("Application Settings saved");
            showToast("Configuración guardada correctamente.");

            finish();
        } catch (IOException ioe) {
            Log.e("DOPrint", "error", ioe);
            showToast(ioe.getMessage());
            bRet = false;
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException ioe) {

                showToast(ioe.getMessage());
            }
        }
        return bRet;
    }// SaveApplicationSettingToFile()

    public void showToast(final String toast) {
        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * Converts Bluetooth Address string from 00ABCDEF0102 format => 00:AB:CD:EF:01:02 format
     *
     * @param bluetoothAddr - Bluetooth Address string to convert
     */
    public String formatBluetoothAddress(String bluetoothAddr) {
        //Format MAC address string
        StringBuilder formattedBTAddress = new StringBuilder(bluetoothAddr);
        for (int bluetoothAddrPosition = 2; bluetoothAddrPosition <= formattedBTAddress.length() - 2; bluetoothAddrPosition += 3)
            formattedBTAddress.insert(bluetoothAddrPosition, ":");
        return formattedBTAddress.toString();
    }


    //2018 PH: SOBREESCRIBIENDO ESTE MÉTODO, AL PRESIONAR EL BOTÓN BACK EN LA ACTIVIDAD PRINCIPAL NO SE VUELVE AL LA PANTALLA
    //ANTERIOR (EN CASO DE EXISTIR) Y SE MANDA LA APLICACIÓN AL BACKGROUND (Y EL SERVICIO SIGUE FUNCIONANDO)
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!m_printerMAC.equals("") && !numerodesucursal.equals("")) {
            finish();
        } else {
            showToast("Verifique configuración.");
        }
    }

    /**
     * Display printing status message to the Textview in the
     * DOPrinterDemoActivity
     *
     * @param MsgStr - message to display
     */
    public void DisplayPrintingStatusMessage(final String MsgStr, final String nombrePDF) {
        g_PrintStatusStr = MsgStr;
        m_handler.post(new Runnable() {
            public void run() {
                ((TextView) findViewById(R.id.printing_status_textview)).setText(g_PrintStatusStr);
                showToast(MsgStr);//2018 PH

            }// run()
        });
    }


    //2018 PH - implemento esto método para evaluar el estado del servicio mediante un booleano estático y para recargas los valores de la configuración
    @Override
    protected void onPostResume() {
        super.onPostResume();

        // registerReceiver(impresion, mIntentFilter);

        //m_connectionSpinner.setSelection(g_appSettings.getCommunicationMethod());//cargo el spinner de la conexión según la configuración
        //sucursal.setSelection(g_appSettings.getSelectedItemIndex());
        //m_printerModeSpinner.setSelection(g_appSettings.getSelectedModeIndex());//cargo el spinner del modo de impresión según la configuración
        //m_printHeadSpinner.setSelection(g_appSettings.getPrintheadWidthIndex());//cargo el spinner del ancho del papel según la configuración


    }

    @Override
    public void run() {
        try {


            EnableDialog(true, "Enviando Documento...");


            File f = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/" + "/arial_bl.TTF");
            InputStream is =  mContext.getResources().openRawResource(R.raw.arial_black);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();


            File f2 = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/" + "/arial_na.TTF");
            InputStream is2 =  mContext.getResources().openRawResource(R.raw.arial_narrow);
            int size2 = is2.available();
            byte[] buffer2 = new byte[size2];
            is2.read(buffer2);
            is2.close();
            FileOutputStream fos2 = new FileOutputStream(f2);
            fos2.write(buffer2);
            fos2.close();


            File f3 = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/" + "/strike_t.TTF");
            InputStream is3 =  mContext.getResources().openRawResource(R.raw.strikethrough);
            int size3 = is3.available();
            byte[] buffer3 = new byte[size3];
            is3.read(buffer3);
            is3.close();
            FileOutputStream fos3 = new FileOutputStream(f3);
            fos3.write(buffer3);
            fos3.close();


            TscDll.openport(m_printerMAC);
            TscDll.setup(76, 35, 2, 1, 1, 4, 0);
            TscDll.clearbuffer();
            TscDll.sendcommand("CODEPAGE UTF-8\n");
            TscDll.sendcommand("SET TEAR ON\n");
            TscDll.sendcommand("DIRECTION 0\n");
            TscDll.downloadttf("arial_bl.TTF");
            TscDll.downloadttf("arial_na.TTF");
            TscDll.downloadttf("strike_t.TTF");
            TscDll.sendcommand("TEXT 70,20,\"arial_na.TTF\",0,11,11,\"" + "Descripcion 1" + "\"\n");
            TscDll.sendcommand("TEXT 70,55,\"arial_na.TTF\",0,08,08,\"" + "Descripcion 2" + "\"\n");
            TscDll.sendcommand("TEXT 70,120,\"strike_t.TTF\",0,10,10,\"" + "$ " + "1.256" + "\"\n");
            TscDll.sendcommand("TEXT 280,100,\"arial_bl.TTF\",0,20,20,\"" + "$ " + "1.256" + "\"\n");
            TscDll.sendcommand("TEXT 400,190,\"arial_na.TTF\",0,11,11,\"" + "123456" + "\"\n");
            TscDll.barcode(80, 180, "128", 30, 1, 0, 1, 1, "123456");
            TscDll.printlabel(1, 1);
            TscDll.closeport(5000);
            EnableDialog(false, "Enviando terminando...");

        } catch (Exception e) {
            e.printStackTrace();
            EnableDialog(false, "Enviando terminando...");
            //Toast.makeText(Imprimiretiquetas.this, "Problema con la conexion Bluetooth.. Reintentar" ,Toast.LENGTH_SHORT).show();
        }
    }

    public void EnableDialog(final boolean value, final String mensaje) {
        m_handler.post(new Runnable() {
            @Override
            public void run() {

                if (value) {
                    createCancelProgressDialog("Imprimiendo...", mensaje, "Cancelar");
                } else {
                    if (dialog != null)
                        dialog.dismiss();
                }

            }
        });
    }

    private ProgressDialog dialog;

    private void createCancelProgressDialog(String title, String message, String buttonText) {
        dialog = new ProgressDialog(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton(buttonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Use either finish() or return() to either close the activity or just the dialog

            }
        });
        dialog.show();
    }

}

