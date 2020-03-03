package com.example.sedesem.BaseDatos;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Usuario {
    public String uid;
    public String apePat;
    public String apeMat;
    public String nombre;
    public String email;
    public String fecha;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Usuario(String uid, String apePat, String apeMat, String nombre, String email, String fecha) {
        this.uid = uid;
        this.apePat = apePat;
        this.apeMat = apeMat;
        this.nombre = nombre;
        this.email = email;
        this.fecha = fecha;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("apePat", apePat);
        result.put("apeMat", apeMat);
        result.put("nombre", nombre);
        result.put("email", email);
        result.put("fecha", fecha);

        return result;
    }
}
