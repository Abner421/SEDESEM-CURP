package com.example.sedesem.BaseDatos;

public class Entidad {
    private String Entidad;
    private int status;

    public Entidad(String Entidad, int status){
        this.Entidad = Entidad;
        this.status = status;
    }

    public String getEntidad() { return Entidad; }

    public int getStatus() { return status; }
}
