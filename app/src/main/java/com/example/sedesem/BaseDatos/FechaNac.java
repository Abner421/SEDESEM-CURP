package com.example.sedesem.BaseDatos;

public class FechaNac {
    private String FechaNac;
    private int status;

    public FechaNac(String FechaNac, int status){
        this.FechaNac = FechaNac;
        this.status = status;
    }

    public String getFechaNac() { return FechaNac; }

    public int getStatus() { return status; }
}
