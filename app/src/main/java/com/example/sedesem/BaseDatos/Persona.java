package com.example.sedesem.BaseDatos;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Persona {
    public String curp_id;
    public String apePat;
    public String apeMat;
    public String nombre;
    public String sexo;
    public String fechaNac;
    public String entidad;
    public int region;
    public String latitud;
    public String longitud;
    public String altitud;
    public String precision;

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getAltitud() {
        return altitud;
    }

    public void setAltitud(String altitud) {
        this.altitud = altitud;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getCurp_id() {
        return curp_id;
    }

    public void setCurp_id(String curp_id) {
        this.curp_id = curp_id;
    }

    public String getApePat() {
        return apePat;
    }

    public void setApePat(String apePat) {
        this.apePat = apePat;
    }

    public String getApeMat() {
        return apeMat;
    }

    public void setApeMat(String apeMat) {
        this.apeMat = apeMat;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public Persona(String curp_id, String apePat, String apeMat, String nombre, String sexo,
                   String fechaNac, String entidad, int region, String longitud,
                   String latitud, String altitud, String precision) {
        this.curp_id = curp_id;
        this.apePat = apePat;
        this.apeMat = apeMat;
        this.nombre = nombre;
        this.sexo = sexo;
        this.fechaNac = fechaNac;
        this.entidad = entidad;
        this.region = region;
        this.longitud = longitud;
        this.latitud = latitud;
        this.altitud = altitud;
        this.precision = precision;
    }

    public Persona(){

    }


    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("curp_id",curp_id);
        result.put("apePat",apePat);
        result.put("apeMat",apeMat);
        result.put("nombre",nombre);
        result.put("sexo",sexo);
        result.put("fechaNac",fechaNac);
        result.put("entidad",entidad);
        result.put("region",region);
        result.put("longitud", longitud);
        result.put("latitud", latitud);
        result.put("altitud", altitud);
        result.put("precision", precision);

        return result;
    }
}
