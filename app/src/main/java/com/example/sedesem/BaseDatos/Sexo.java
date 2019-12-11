package com.example.sedesem.BaseDatos;

public class Sexo {
    private String Sexo;
    private int status;

    public Sexo(String Sexo, int status){
        this.Sexo = Sexo;
        this.status = status;
    }

    public String getSexo() { return Sexo; }

    public int getStatus() { return status; }
}
