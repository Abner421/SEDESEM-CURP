package com.example.sedesem.BaseDatos;

public class Nombre {
    private String Nombre;
    private int status;

    public Nombre(String Nombre, int status){
        this.Nombre = Nombre;
        this.status = status;
    }

    public String getNombre() { return Nombre; }

    public int getStatus() { return status; }
}
