package com.example.sedesem.BaseDatos;

public class Regs {
    private String curp;
    private String nombre;
    private String apPat;
    private String apMat;
    private String sexo;
    private String fechaNac;
    private String entidad;
    private int region;

    public Regs(String curp, String nombre, String apPat, String apMat,
                String sexo, String fechaNac, String entidad, int region) {
        this.curp = curp;
        this.nombre = nombre;
        this.apPat = apPat;
        this.apMat = apMat;
        this.sexo = sexo;
        this.fechaNac = fechaNac;
        this.entidad = entidad;
        this.region = region;
    }

    public String getApMat() {
        return apMat;
    }

    public void setApMat(String apMat) {
        this.apMat = apMat;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApPat() {
        return apPat;
    }

    public void setApPat(String apPat) {
        this.apPat = apPat;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }
}
