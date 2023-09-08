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

import com.desarrollo.myapplication.Clases.GruposRubros;
import com.desarrollo.myapplication.Clases.Producto;
import com.desarrollo.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class GrupoRubroAdapter extends RecyclerView.Adapter<GrupoRubroAdapter.NoteViewHolder> {

    private List<GruposRubros> notes;
    private OnNoteSelectedListener onNoteSelectedListener;

    private Context contex;

    public GrupoRubroAdapter() {
        this.notes = new ArrayList<>();
    }

    public GrupoRubroAdapter(List<GruposRubros> notes) {
        this.notes = notes;
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View elementoTitular = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grupo_rubro, parent, false);

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


    public List<GruposRubros> getNotes() {
        return notes;
    }

    public void setNotes(List<GruposRubros> notes) {
        this.notes = notes;
    }

    public void setOnNoteSelectedListener(OnNoteSelectedListener onNoteSelectedListener) {
        this.onNoteSelectedListener = onNoteSelectedListener;
    }




    public interface OnNoteSelectedListener {
        void onClick(GruposRubros note);
    }




    public class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView idcodigo;
        private TextView numero;
        private TextView descripcion;


        public NoteViewHolder(View item) {
            super(item);

            idcodigo = (TextView) item.findViewById(R.id.txt_idrubro);
            numero = (TextView) item.findViewById(R.id.txt_numerorubro);
            descripcion = (TextView) item.findViewById(R.id.txt_descripcion);

        }

        public void bind(final GruposRubros producto) {


            idcodigo.setText(producto.getCodigoGrupoRubros().toString());
            numero.setText(producto.getNumeroGrupoRubro().toString());
         descripcion.setText(producto.getDescripcionGrupoRubro().toString());
          //  descripcion.setText("mal");

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
