package com.desarrollo.myapplication.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desarrollo.myapplication.Clases.List_Producto;
import com.desarrollo.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class List_ProductoAdapter  extends RecyclerView.Adapter<List_ProductoAdapter.NoteViewHolder>{

    private List<List_Producto> notes;
    private List_ProductoAdapter.OnNoteSelectedListener onNoteSelectedListener;
    private List_ProductoAdapter.OnNoteDetailListener onDetailListener;
    private Context contex;
    public List_ProductoAdapter() {
        this.notes = new ArrayList<>();
    }

    public List_ProductoAdapter(List<List_Producto> notes) {
        this.notes = notes;
    }


    @Override
    public List_ProductoAdapter.NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View elementoTitular = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_producto, parent, false);


        contex = elementoTitular.getContext();

        return new List_ProductoAdapter.NoteViewHolder(elementoTitular);
    }

    @Override
    public void onBindViewHolder(List_ProductoAdapter.NoteViewHolder view, int pos) {
        view.bind(notes.get(pos));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public List<List_Producto> getNotes() {
        return notes;
    }

    public void setNotes(List<List_Producto> notes) {
        this.notes = notes;
    }

    public void setOnNoteSelectedListener(List_ProductoAdapter.OnNoteSelectedListener onNoteSelectedListener) {
        this.onNoteSelectedListener = onNoteSelectedListener;
    }

    public void setOnDetailListener(List_ProductoAdapter.OnNoteDetailListener onDetailListener) {
        this.onDetailListener = onDetailListener;
    }

    public interface OnNoteSelectedListener {
        void onClick(List_Producto note);
    }

    public interface OnNoteDetailListener {
        void onDetail(List_Producto note);
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
        private ImageView Checkimpreso;

        private LinearLayout layoutetiquet;


        public NoteViewHolder(View item) {
            super(item);

            codigoProducto = (TextView) item.findViewById(R.id.txt_codigoProducto);
            DescArticulo_1 = (TextView) item.findViewById(R.id.txt_DescArticulo_1);
            DescArticulo_2= (TextView) item.findViewById(R.id.txt_DescArticulo_2);
            CodProd= (TextView) item.findViewById(R.id.txt_CodProd);
            CodBarras= (TextView) item.findViewById(R.id.txt_CodBarras);
            Precio= (TextView) item.findViewById(R.id.txt_Precio);
            Stock= (TextView) item.findViewById(R.id.txt_Stock);
            IP= (TextView) item.findViewById(R.id.txt_IP);
            Producto= (TextView) item.findViewById(R.id.txt_Producto);
            Suc= (TextView) item.findViewById(R.id.txt_Suc);
            Mensaje= (TextView) item.findViewById(R.id.txt_Mensaje);
            Estado= (TextView) item.findViewById(R.id.txt_Estado);
            Oferta= (TextView) item.findViewById(R.id.txt_precio_lista);
            Checkimpreso = item.findViewById(R.id.img_view);

            layoutetiquet = item.findViewById(R.id.layoutetiqueta);
        }

        public void bind(final List_Producto producto) {


            codigoProducto.setText(producto.getCodigoProducto().toString());
            DescArticulo_1.setText(producto.getDescArticulo_1().toString());
            DescArticulo_2.setText(producto.getDescArticulo_2().toString());
            CodProd.setText(producto.getCodProd().toString());
            CodBarras.setText(producto.getCodBarras().toString());
            Precio.setText(producto.getPrecio().toString());
            Stock.setText(producto.getStock().toString());
            Oferta.setText(producto.getOff_available().toString());
            IP.setText(producto.getIP().toString());
            Suc.setText(producto.getSuc().toString());
            Estado.setText(producto.getEstado().toString());


            if (producto.getOff_available().toString().equals("N")){
                Oferta.setText("");


                if (producto.getIP().equals("NO")){

                    Checkimpreso.setImageResource(R.drawable.ic_printer);

                    layoutetiquet.setBackground(ContextCompat.getDrawable(contex, R.drawable.ic_tag_60x30));

                }else{
                    Checkimpreso.setImageResource(R.drawable.ic_replay_black_24dp);

                    layoutetiquet.setBackground(ContextCompat.getDrawable(contex, R.drawable.ic_tag_60x30_impreso));
                }


            }else{
                Oferta.setText(producto.getTxt_oferta());


                if (producto.getIP().equals("NO")){
                    Checkimpreso.setImageResource(R.drawable.ic_printer);
                    layoutetiquet.setBackground(ContextCompat.getDrawable(contex, R.drawable.ic_tag_60x30_amarillo));

                }else{
                    Checkimpreso.setImageResource(R.drawable.ic_replay_black_24dp);
                    layoutetiquet.setBackground(ContextCompat.getDrawable(contex, R.drawable.ic_tag_60x30_amarillo_impreso));
                }

            }



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onNoteSelectedListener != null) {
                        onNoteSelectedListener.onClick(producto);
                    }
                }
            });
        }
    }

}
