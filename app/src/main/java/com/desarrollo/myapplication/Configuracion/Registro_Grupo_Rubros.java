package com.desarrollo.myapplication.Configuracion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.desarrollo.myapplication.Adapter.GrupoRubroAdapter;
import com.desarrollo.myapplication.BaseDatos.ProductosDB;
import com.desarrollo.myapplication.BaseDatos.RubroDB;
import com.desarrollo.myapplication.Clases.GruposRubros;
import com.desarrollo.myapplication.ParsearXML.ParserRubros;
import com.desarrollo.myapplication.R;
import com.desarrollo.myapplication.TagGondola;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

public class Registro_Grupo_Rubros extends AppCompatActivity {

    private Button cargar, registrar, eliminar;
    private EditText numero, gruporubros;
    private ProgressDialog dialog;
    private RecyclerView recyclerviewrubros;
    private Handler m_handler = new Handler(); // Main thread

    private String codigoseelccionado = "";
    private RubroDB db;
    private GrupoRubroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro__grupo__rubros);

        cargar = findViewById(R.id.buttonnuevo);
        registrar = findViewById(R.id.buttonregistrar);
        eliminar = findViewById(R.id.buttoneliminar);
        recyclerviewrubros = findViewById(R.id.recyclerviewrubros);
        numero = findViewById(R.id.editTextcodigo);
        gruporubros = findViewById(R.id.editTextrubro);

        cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                numero.setText("");
                gruporubros.setText("");
                numero.requestFocus();

                codigoseelccionado = "";

            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RubroDB db;

                String n = numero.getText().toString().trim();
                String l = gruporubros.getText().toString().trim();

                if (!n.isEmpty() && !l.isEmpty()) {


                        db = new RubroDB(Registro_Grupo_Rubros.this);
                        GruposRubros rubro = new GruposRubros(n, l);

                        if (!db.Buscar_tabla_rubro(n, l)) {

                            db.insertarGrupoRubro(rubro);
                            DisplayPrintingStatusMessage("Registro Exitoso");
                            limpiar();
                            cargarLista();
                        } else {
                            DisplayPrintingStatusMessage("Ya esta Registrado");
                        }


                } else {

                    if (n.isEmpty()) {
                        numero.setError("Falta datos..");
                    }

                    if (l.isEmpty()) {
                        gruporubros.setError("Falta datos..");
                    }
                }

            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!codigoseelccionado.isEmpty()) {

                    db = new RubroDB(Registro_Grupo_Rubros.this);
                    db.eliminarRubro(codigoseelccionado);
                    DisplayPrintingStatusMessage("Se elimino Correctamente");

                    limpiar();
                    cargarLista();

                }
            }
        });


        adapter = new GrupoRubroAdapter();
        adapter.setOnNoteSelectedListener(new GrupoRubroAdapter.OnNoteSelectedListener() {
            @Override
            public void onClick(GruposRubros note) {

                codigoseelccionado = note.getCodigoGrupoRubros().toString();
                numero.setText(note.getNumeroGrupoRubro());
                gruporubros.setText(note.getDescripcionGrupoRubro());
                registrar.setText("REGISTRAR");

            }

        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerviewrubros);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        cargarLista();

    }

    private void limpiar() {


        numero.setText("");
        gruporubros.setText("");
        numero.requestFocus();

        codigoseelccionado = "";


        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void cargarLista() {


        try {
            RubroDB db = new RubroDB(Registro_Grupo_Rubros.this);
            ArrayList<GruposRubros> list = db.loadGrupoRubro();
            adapter.setNotes(list);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e("errorW", "mensaje");
        }
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

    private void cargarrubrosinterno() {

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    EnableDialog(true, "Enviando Documento...");

                    ParserRubros par = new ParserRubros(Registro_Grupo_Rubros.this);

                    try {
                        InputStream asset = getResources().getAssets().open("responsexmlRubros.xml");
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document docc = dBuilder.parse(asset);

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        Source xmlSource = new DOMSource(docc);
                        Result outputTarget = new StreamResult(outputStream);
                        TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
                        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
                        Borrar();
                        par.parsear(is);
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

                    EnableDialog(false, "Enviando Documento...");

                } catch (Exception e) {
                    e.printStackTrace();
                    EnableDialog(false, "Enviando terminando...");

                }

            }
        };

        thread.start();

    }


    private void createCancelProgressDialog(String title, String message, String buttonText) {

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

    public void EnableDialog(final boolean value, final String mensaje) {
        m_handler.post(new Runnable() {
            @Override
            public void run() {
                if (value) {
                    createCancelProgressDialog("Tarea: ", mensaje, "Cancelar");
                } else {
                    if (dialog != null)
                        dialog.dismiss();
                }
            }
        });
    }


    private void Borrar() {
        try {
            RubroDB dbd = new RubroDB(Registro_Grupo_Rubros.this);
            dbd.eliminarRubros();


        } catch (Exception e) {
            Log.e("errorO", "mensaje");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menugruporubro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.restaurar) {

            cargarrubrosinterno();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
