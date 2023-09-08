package com.desarrollo.myapplication.ParsearXML;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.desarrollo.myapplication.BaseDatos.ProductosDB;
import com.desarrollo.myapplication.BaseDatos.RubroDB;
import com.desarrollo.myapplication.Clases.GruposRubros;
import com.desarrollo.myapplication.Clases.Producto;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parser XML de hoteles
 */
public class ParserRubros {

    // Namespace general. null si no existe
    private static final String ns = null;

    private RubroDB db;

    // Constantes del archivo Xml

    private static Context context;

    private static final String ETIQUETA_1= "Envelope";
    private static final String ETIQUETA_2= "Body";
    private static final String ETIQUETA_3= "ObtenerDatosArticuloEtiquetasResponse";
    private static final String ETIQUETA_4= "ObtenerDatosArticuloEtiquetasResult";
    private static final String ETIQUETA_5_Numero= "Numero";
    private static final String ETIQUETA_6_Descripcion = "Descripcion";



    public ParserRubros(Context context){
        this.context=context;
    }

    /**
     * Parsea un flujo XML a una lista de objetos {@link Producto}
     *
     * @param in flujo
     * @return Lista de hoteles
     * @throws XmlPullParserException
     * @throws IOException
     */


    public boolean parsear(InputStream in) throws XmlPullParserException, IOException {
        boolean estado = false;
        try {

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, null);
            parser.nextTag();
            if (leerEnvelope(parser)){
                estado = true;
            }
        } finally {
            in.close();
        }

        return estado;
    }

    private boolean leerEnvelope(XmlPullParser parser)throws XmlPullParserException, IOException {

        Boolean estado = false;

        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_1);
        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_2);
            while (parser.next() != XmlPullParser.END_TAG) {

                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_3);
                while (parser.next() != XmlPullParser.END_TAG) {

                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }

                    String nombreEtiqueta = parser.getName();
                    if (nombreEtiqueta.equals(ETIQUETA_4)) {

                        try {
                            db = new RubroDB(context);
                            GruposRubros rubro = RegistarRubro(parser);
                                db.insertarGrupoRubro(rubro);
                                estado = true;

                        } catch (Exception e) {
                            Log.e("errorXMLA", "mensaje");
                        }

                    } else {
                        saltarEtiqueta(parser);
                    }
                }
            }
        }

return estado;

    }

    private GruposRubros RegistarRubro(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_4);

         String Numero = null;
         String Descripcion = null;


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String namee = parser.getName();

            switch (namee) {
                case ETIQUETA_5_Numero:

                    Numero=(leerDescArticulo_1(parser));

                    break;
                case ETIQUETA_6_Descripcion:

                    Descripcion=(leerDescArticulo_2(parser));

                    break;

                default:
                    saltarEtiqueta(parser);
                    break;
            }
        }
        return new GruposRubros(Numero,Descripcion);
    }

    // Procesa las etiqueta <nombre> de los hoteles
    private String leerDescArticulo_1(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_5_Numero);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_5_Numero);
        return nombre;
    }

    private String leerDescArticulo_2(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_6_Descripcion);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_6_Descripcion);
        return nombre;
    }







    // Obtiene el texto de los atributos
    private String obtenerTexto(XmlPullParser parser) throws IOException, XmlPullParserException {
        String resultado = "";
        if (parser.next() == XmlPullParser.TEXT) {
            resultado = parser.getText();
            parser.nextTag();
        }
        return resultado;
    }

    // Salta aquellos objeteos que no interesen en la jerarquía XML.
    private void saltarEtiqueta(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
