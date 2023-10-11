package com.desarrollo.myapplication.ParsearXML;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.desarrollo.myapplication.BaseDatos.ProductosDBItau;
import com.desarrollo.myapplication.Clases.Producto;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parser XML de hoteles
 */
public class ParserXmlItau {

    // Namespace general. null si no existe
    private static final String ns = null;

    private ProductosDBItau dbitau;

    // Constantes del archivo Xml

    private static Context context;


    private static final String ETIQUETA_1= "Envelope";
    private static final String ETIQUETA_2= "Body";
    private static final String ETIQUETA_3= "ObtenerDatosArticuloEtiquetasPlResponse";
    private static final String ETIQUETA_4= "ObtenerDatosArticuloEtiquetasPlResult";

    private static final String ETIQUETA_3_DescArticulo_1= "DescArticulo_1";
    private static final String ETIQUETA_4_DescArticulo_2 = "DescArticulo_2";
    private static final String ETIQUETA_5_CodProd= "CodProd";
    private static final String ETIQUETA_6_CodBarras = "CodBarras";
    private static final String ETIQUETA_7_Precio = "Precio";
    private static final String ETIQUETA_8_Stock = "Stock";
    private static final String ETIQUETA_9_IP = "IP";
    private static final String ETIQUETA_10_Producto = "Producto";
    private static final String ETIQUETA_11_Suc = "Suc";
    private static final String ETIQUETA_12_Mensaje = "Mensaje";
    private static final String ETIQUETA_13_Estado = "Estado";
    private static final String ETIQUETA_14_Off_available = "off_available";
    private static final String ETIQUETA_15_Txt_Oferta = "txt_oferta";

    private static final String ETIQUETA_16_Precio_Lista = "Precio_lista";

    public ParserXmlItau(Context context){
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


    public boolean parsear(InputStream in, int descuento) throws XmlPullParserException, IOException {
        boolean estado = false;
        try {

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, null);
            parser.nextTag();
            if (leerEnvelope(parser,descuento)){
                estado = true;
            }
        } finally {
            in.close();
        }

        return estado;
    }

    private boolean leerEnvelope(XmlPullParser parser, int descuento)throws XmlPullParserException, IOException {

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
                            dbitau = new ProductosDBItau(context);

                            Producto prod = RegistrarProducto(parser);
                            if (!prod.getEstado().equals("false"))
                            {
                                if(prod.getOff_available().equals("N")){

                                }

                                if (prod.getTxt_oferta().equals("")){


                                }

                                float precio = Float.parseFloat(prod.getPrecio());
                                float descuentox = (precio * descuento) / 100.0f;
                                float precioConDescuento = precio - descuentox;

                                prod.setPrecio(""+precioConDescuento);


                                dbitau.insertarProducto(prod);
                                estado = true;
                            }else{
                                estado = false;
                            }

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

    private Producto RegistrarProducto(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_4);

         String DescArticulo_1 = null;
         String DescArticulo_2 = null;
         String CodProd = null;
         String CodBarras = null;
         String Precio = null;
         String Stock = null;
         String IP = null;
         String Producto = null;
         String Suc = null;
         String Mensaje = null;
         String Estado = null;
         String Off_available = null;
        String Txt_oferta = null;
        String Precio_lista = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String namee = parser.getName();

            switch (namee) {
                case ETIQUETA_3_DescArticulo_1:

                    DescArticulo_1=(leerDescArticulo_1(parser));

                    break;
                case ETIQUETA_4_DescArticulo_2:

                    DescArticulo_2=(leerDescArticulo_2(parser));

                    break;
                case ETIQUETA_5_CodProd:

                    CodProd=(leerCodProd(parser));
                break;

                case ETIQUETA_6_CodBarras:
                    CodBarras=(leerCodBarras(parser));
                    break;

                case ETIQUETA_7_Precio:
                    Precio=(leerPrecio(parser));
                    break;

                case ETIQUETA_8_Stock:
                    Stock=leerStock(parser);
                    break;

                case ETIQUETA_9_IP:
                    //NOTA AQUI COMENTO LA FUNCIONA PARA DEJAR SIEMPRE LA PALABRA "NO"
                    // ESTOES PARA SABER CUANDO UN ARTICULO FUE IMPRESO
                    //ASI NO TENGO QUE USAR UNA NUEVA VARIABLE EN TODA LA BASE DE DATOS

                    IP=leerIP(parser);
                    IP = "NO";
                    break;

                case ETIQUETA_10_Producto:
                    Producto=leerProducto(parser);
                    break;

                case ETIQUETA_11_Suc:
                    Suc=leerSuc(parser);
                    break;

                case ETIQUETA_12_Mensaje:
                    Mensaje=leerMensaje(parser);
                    break;

                case ETIQUETA_13_Estado:
                    Estado=leerEstado(parser);
                    break;

                case ETIQUETA_14_Off_available:
                    Off_available=leerOffavailable(parser);
                    break;

                case ETIQUETA_15_Txt_Oferta:
                    Txt_oferta=leerTxtoferta(parser);
                    break;

                case ETIQUETA_16_Precio_Lista:
                    Precio_lista=leerTxtPreciolista(parser);
                    break;


                default:
                    saltarEtiqueta(parser);
                    break;
            }
        }
        return new Producto(DescArticulo_1,DescArticulo_2,CodProd,CodBarras,Precio,Stock,IP,Producto,Suc,Mensaje,Estado,Off_available,Txt_oferta,Precio_lista);
    }

    // Procesa las etiqueta <nombre> de los hoteles
    private String leerDescArticulo_1(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_3_DescArticulo_1);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_3_DescArticulo_1);
        return nombre;
    }

    private String leerDescArticulo_2(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_4_DescArticulo_2);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_4_DescArticulo_2);
        return nombre;
    }
    private String leerCodProd(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_5_CodProd);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_5_CodProd);
        return nombre;
    }

    private String leerCodBarras(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_6_CodBarras);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_6_CodBarras);
        return nombre;
    }
    private String leerPrecio(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_7_Precio);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_7_Precio);
        return nombre;
    }
    private String leerStock(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_8_Stock);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_8_Stock);
        return nombre;
    }
    private String leerIP(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_9_IP);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_9_IP);
        return nombre;
    }
    private String leerProducto(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_10_Producto);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_10_Producto);
        return nombre;
    }

    private String leerSuc(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_11_Suc);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_11_Suc);
        return nombre;
    }
    private String leerMensaje(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_12_Mensaje);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_12_Mensaje);
        return nombre;
    }
    private String leerEstado(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_13_Estado);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_13_Estado);
        return nombre;
    }
    private String leerOffavailable(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_14_Off_available);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_14_Off_available);
        return nombre;
    }
    private String leerTxtoferta(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_15_Txt_Oferta);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_15_Txt_Oferta);
        return nombre;
    }

    private String leerTxtPreciolista(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_16_Precio_Lista);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_16_Precio_Lista);
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
