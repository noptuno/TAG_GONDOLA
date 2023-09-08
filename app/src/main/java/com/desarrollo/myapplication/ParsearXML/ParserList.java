package com.desarrollo.myapplication.ParsearXML;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.desarrollo.myapplication.BaseDatos.List_ProductosDB;
import com.desarrollo.myapplication.Clases.List_Producto;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class ParserList {
    // Namespace general. null si no existe
    private static final String ns = null;

    private List_ProductosDB db;

    // Constantes del archivo Xml

    private static Context context;



    private static final String ETIQUETA_1= "Envelope";
    private static final String ETIQUETA_2= "Body";

    private static final String ETIQUETA_3= "ObtenerDatosListaArticuloEtiquetasPlResponse";

    private static final String ETIQUETA_4= "ObtenerDatosListaArticuloEtiquetasPlResult";

    private static final String ETIQUETA_4_1= "lstArt";

    private static final String ETIQUETA_4_2= "ClEtiquetas";


    private static final String ETIQUETA_1_DescArticulo_1= "DescArticulo_1";
    private static final String ETIQUETA_2_DescArticulo_2 = "DescArticulo_2";
    private static final String ETIQUETA_3_CodProd= "CodProd";
    private static final String ETIQUETA_4_CodBarras = "CodBarras";
    private static final String ETIQUETA_5_Precio = "Precio";
    private static final String ETIQUETA_6_Stock = "Stock";
    private static final String ETIQUETA_7_off_available = "off_available";
    private static final String ETIQUETA_8_IP = "IP";
    private static final String ETIQUETA_9_Suc = "Suc";
    private static final String ETIQUETA_10_Estado = "Estado";
    private static final String ETIQUETA_11_TXTOFERTA = "txt_oferta";
    private static final String ETIQUETA_12_PRECIOLISTA = "Precio_Lista";


    public ParserList(Context context){
        this.context=context;
    }

    /**
     * Parsea un flujo XML a una lista de objetos {@link List_Producto}
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

                    parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_4);
                    while (parser.next() != XmlPullParser.END_TAG) {

                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_4_1);
                        while (parser.next() != XmlPullParser.END_TAG) {

                            if (parser.getEventType() != XmlPullParser.START_TAG) {
                                continue;
                            }

                        String nombreEtiqueta = parser.getName();
                        if (nombreEtiqueta.equals(ETIQUETA_4_2)) {

                            try {
                                db = new List_ProductosDB(context);
                                List_Producto prod = RegistrarProducto(parser);

                               // if (db.Validar_List_Productos(prod)){
                                    if (!prod.getEstado().equals("false")) {
                                        db.insertarProducto(prod);
                                        estado = true;
                                    } else {
                                        estado = false;
                                    }
                               // }


                            } catch (Exception e) {
                                Log.e("errorXMLA", "mensaje");
                            }

                        } else {
                            saltarEtiqueta(parser);
                        }
                    }
                    }

                }
            }
        }

        return estado;

    }

    private List_Producto RegistrarProducto(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_4_2);

        String DescArticulo_1 = null;
        String DescArticulo_2 = null;
        String CodProd = null;
        String CodBarras = null;
        String Precio = null;
        String Stock = null;
        String off_available = null;
        String IP = null;
        String Suc = null;
        String Estado = null;
        String txt_oferta = null;
        String Precio_Lista = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String namee = parser.getName();

            switch (namee) {
                case ETIQUETA_1_DescArticulo_1:

                    DescArticulo_1=(leerDescArticulo_1(parser));

                    break;
                case ETIQUETA_2_DescArticulo_2:

                    DescArticulo_2=(leerDescArticulo_2(parser));

                    break;
                case ETIQUETA_3_CodProd:

                    CodProd=(leerCodProd(parser));
                    break;

                case ETIQUETA_4_CodBarras:
                    CodBarras=(leerCodBarras(parser));
                    break;

                case ETIQUETA_5_Precio:
                    Precio=(leerPrecio(parser));
                    break;

                case ETIQUETA_6_Stock:
                    Stock=leerStock(parser);
                    break;

                case ETIQUETA_7_off_available:
                    off_available=leerIP(parser);
                    break;

                case ETIQUETA_8_IP:

                    IP=leerProducto(parser);
                    IP= "NO";
                    break;

                case ETIQUETA_9_Suc:
                    Suc=leerSuc(parser);
                    break;

                case ETIQUETA_10_Estado:
                    Estado=leerMensaje(parser);
                    break;

                case ETIQUETA_11_TXTOFERTA:
                    txt_oferta=leerTxtoferta(parser);
                    break;

                case ETIQUETA_12_PRECIOLISTA:
                    Precio_Lista=leerPrecioLista(parser);
                    break;

                 default:
                    saltarEtiqueta(parser);
                    break;
            }
        }
        return new List_Producto(DescArticulo_1,DescArticulo_2,CodProd,CodBarras,Precio,Stock,off_available,IP,Suc,Estado,txt_oferta,Precio_Lista);
    }

    // Procesa las etiqueta <nombre> de los hoteles
    private String leerDescArticulo_1(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_1_DescArticulo_1);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_1_DescArticulo_1);
        return nombre;
    }

    private String leerDescArticulo_2(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_2_DescArticulo_2);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_2_DescArticulo_2);
        return nombre;
    }
    private String leerCodProd(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_3_CodProd);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_3_CodProd);
        return nombre;
    }

    private String leerCodBarras(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_4_CodBarras);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_4_CodBarras);
        return nombre;
    }
    private String leerPrecio(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_5_Precio);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_5_Precio);
        return nombre;
    }
    private String leerStock(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_6_Stock);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_6_Stock);
        return nombre;
    }
    private String leerIP(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_7_off_available);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_7_off_available);
        return nombre;
    }
    private String leerProducto(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_8_IP);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_8_IP);
        return nombre;
    }

    private String leerSuc(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_9_Suc);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_9_Suc);
        return nombre;
    }
    private String leerMensaje(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_10_Estado);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_10_Estado);
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

    private String leerTxtoferta(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_11_TXTOFERTA);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_11_TXTOFERTA);
        return nombre;
    }
    private String leerPrecioLista(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_12_PRECIOLISTA);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_12_PRECIOLISTA);
        return nombre;
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
