package com.desarrollo.myapplication.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desarrollo.myapplication.Clases.Producto;
import com.desarrollo.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.NoteViewHolder> {

    private List<Producto> notes;
    private OnNoteSelectedListener onNoteSelectedListener;
    private OnNoteDetailListener onDetailListener;
    private OnPersonCheckedListener onPersonCheckedListener;

    private Context contex;
    private String tipoconexion;
    private int descuento = 0;

    public ProductoAdapter() {
        this.notes = new ArrayList<>();
    }

    public ProductoAdapter(List<Producto> notes) {
        this.notes = notes;
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View elementoTitular = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_producto, parent, false);

        contex = elementoTitular.getContext();

        return new NoteViewHolder(elementoTitular);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder view, int pos) {
        view.bind(notes.get(pos));
    }


    @Override
    public int getItemCount() {
        return notes.size();
    }

    public List<Producto> getNotes() {
        return notes;
    }

    public void setNotes(List<Producto> notes,String tipoconexion,int descuento) {
        this.notes = notes;
        this.tipoconexion = tipoconexion;
        this.descuento = descuento;
    }

    public void setOnNoteSelectedListener(OnNoteSelectedListener onNoteSelectedListener) {
        this.onNoteSelectedListener = onNoteSelectedListener;
    }

    public void setOnDetailListener(OnNoteDetailListener onDetailListener) {
        this.onDetailListener = onDetailListener;
    }

    public void setOnPersonCheckedListener(OnPersonCheckedListener onPersonCheckedListener) {
        this.onPersonCheckedListener = onPersonCheckedListener;
    }


    public interface OnNoteSelectedListener {
        void onClick(Producto note);
    }

    public interface OnNoteDetailListener {
        void onDetail(Producto note);
    }

    public interface OnPersonCheckedListener {
        void onPersonChecked(Producto note);
    }

    public Producto getNoteAt(int position) {
        return notes.get(position);
    }




    public class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView codigoProducto;
        private TextView DescArticulo_1;
        private TextView DescArticulo_2;
        private TextView CodProd;
        private TextView CodBarras;
        private TextView Precio;
        private TextView Stock;
        private TextView IP;
        private TextView Producto;
        private TextView Suc;
        private TextView Mensaje;
        private TextView Estado;
        private TextView Oferta;

        private TextView PrecioLista;
        private TextView txtPrecioanterior;
        private ImageView Checkimpreso;

        private LinearLayout layoutetiquet;

        public NoteViewHolder(View item) {
            super(item);

            DescArticulo_1 = (TextView) item.findViewById(R.id.txt_DescArticulo_1);
            DescArticulo_2 = (TextView) item.findViewById(R.id.txt_DescArticulo_2);
            CodProd = (TextView) item.findViewById(R.id.txt_CodProd);
            CodBarras = (TextView) item.findViewById(R.id.txt_CodBarras);
            Precio = (TextView) item.findViewById(R.id.txt_Precio);
            Stock = (TextView) item.findViewById(R.id.txt_Stock);
            IP = (TextView) item.findViewById(R.id.txt_IP);
            Producto = (TextView) item.findViewById(R.id.txt_Producto);
            Suc = (TextView) item.findViewById(R.id.txt_Suc);
            Mensaje = (TextView) item.findViewById(R.id.txt_Mensaje);
            Estado = (TextView) item.findViewById(R.id.txt_Estado);
           //Oferta = (TextView) item.findViewById(R.id.txt_precio_lista);
            PrecioLista = (TextView) item.findViewById(R.id.txt_precio_lista);
            layoutetiquet = item.findViewById(R.id.layoutetiqueta);
            Checkimpreso = item.findViewById(R.id.img_view);
            txtPrecioanterior= (TextView) item.findViewById(R.id.textViewprecioanterior);


        }

        public void bind(final Producto producto) {

try{


    DescArticulo_1.setText(producto.getDescArticulo_1().toString());
    DescArticulo_2.setText(producto.getDescArticulo_2().toString());
    CodProd.setText(producto.getCodProd().toString());
    CodBarras.setText(producto.getCodBarras().toString());
    Stock.setText(producto.getStock().toString());
    IP.setText(producto.getIP().toString());
    Producto.setText(producto.getProducto().toString());
    Suc.setText(producto.getSuc().toString());
    Mensaje.setText(producto.getMensaje().toString());
    Estado.setText(producto.getEstado().toString());
    Precio.setText("$ "+producto.getPrecio().toString());
    PrecioLista.setText("$ "+producto.getPrecio_lista().toString());


    if (producto.getOff_available().toString().equals("N")) {

        if (producto.getIP().toString().equals("NO")){
            Checkimpreso.setImageResource(R.drawable.ic_printer);
            layoutetiquet.setBackground(ContextCompat.getDrawable(contex, R.drawable.ic_tag_60x30));
            PrecioLista.setVisibility(View.INVISIBLE);
            txtPrecioanterior.setVisibility(View.INVISIBLE);

        }else{
            Checkimpreso.setImageResource(R.drawable.ic_replay_black_24dp);
            layoutetiquet.setBackground(ContextCompat.getDrawable(contex, R.drawable.ic_tag_60x30_impreso));
            PrecioLista.setVisibility(View.INVISIBLE);
            txtPrecioanterior.setVisibility(View.INVISIBLE);
        }
    } else {

        if (producto.getIP().toString().equals("NO")){
            Checkimpreso.setImageResource(R.drawable.ic_printer);
            layoutetiquet.setBackground(ContextCompat.getDrawable(contex, R.drawable.ic_tag_60x30_amarillo));
            PrecioLista.setVisibility(View.VISIBLE);
            txtPrecioanterior.setVisibility(View.VISIBLE);

        }else{
            Checkimpreso.setImageResource(R.drawable.ic_replay_black_24dp);
            layoutetiquet.setBackground(ContextCompat.getDrawable(contex, R.drawable.ic_tag_60x30_amarillo_impreso));
            PrecioLista.setVisibility(View.VISIBLE);
            txtPrecioanterior.setVisibility(View.VISIBLE);
        }
    }


}catch (Exception e){
    Log.e("Ex",e.toString());
}

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onNoteSelectedListener != null) {
                        onNoteSelectedListener.onClick(producto);
                    }
                }
            });



/*
            Checkimpreso.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPersonCheckedListener != null) {
                        // Invoco mi listener
                        onPersonCheckedListener.onPersonChecked(producto);
                    }
                }
            });
*/

        }





    }
}
