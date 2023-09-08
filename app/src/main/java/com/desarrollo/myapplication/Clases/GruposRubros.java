package com.desarrollo.myapplication.Clases;

import android.content.Intent;

import com.desarrollo.myapplication.Adapter.GrupoRubroAdapter;

public class GruposRubros {

    Integer CodigoGrupoRubros;
    String numeroGrupoRubro;
    String DescripcionGrupoRubro;

    public GruposRubros() {
    }




    public Integer getCodigoGrupoRubros() {
        return CodigoGrupoRubros;
    }

    public void setCodigoGrupoRubros(Integer codigoGrupoRubros) {
        CodigoGrupoRubros = codigoGrupoRubros;
    }

    public String getNumeroGrupoRubro() {
        return numeroGrupoRubro;
    }

    public void setNumeroGrupoRubro(String numeroGrupoRubro) {
        this.numeroGrupoRubro = numeroGrupoRubro;
    }

    public String getDescripcionGrupoRubro() {
        return DescripcionGrupoRubro;
    }

    public void setDescripcionGrupoRubro(String descripcionGrupoRubro) {
        DescripcionGrupoRubro = descripcionGrupoRubro;
    }

    public GruposRubros(String numeroGrupoRubro, String descripcionGrupoRubro) {

        this.numeroGrupoRubro = numeroGrupoRubro;
        this.DescripcionGrupoRubro = descripcionGrupoRubro;
    }

    public String toString() {
        return "Rubros{" +
                "codigorUBRO=" + CodigoGrupoRubros +
                ", numero='" + numeroGrupoRubro + '\'' +
                ", descripcion='" + DescripcionGrupoRubro + '\'' +
                '}';
    }
    public String Spinner() {
        return DescripcionGrupoRubro;
    }


}
