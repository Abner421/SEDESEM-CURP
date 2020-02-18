package com.example.sedesem.ui.login;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Usuario {
    public String UID;
    public String Unombre;
    public String UapePat;
    public String UapeMat;
    public String Ucorreo;
    //public String Upass;

    public Usuario(String Uid, String unombre, String uapePat, String uapeMat, String ucorreo) {
        Unombre = unombre;
        UapePat = uapePat;
        UapeMat = uapeMat;
        Ucorreo = ucorreo;
        //Upass = upass;
        UID = Uid;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUnombre() {
        return Unombre;
    }

    public void setUnombre(String unombre) {
        Unombre = unombre;
    }

    public String getUapePat() {
        return UapePat;
    }

    public void setUapePat(String uapePat) {
        UapePat = uapePat;
    }

    public String getUapeMat() {
        return UapeMat;
    }

    public void setUapeMat(String uapeMat) {
        UapeMat = uapeMat;
    }

    public String getUcorreo() {
        return Ucorreo;
    }

    public void setUcorreo(String ucorreo) {
        Ucorreo = ucorreo;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", UID);
        result.put("nombre", Unombre);
        result.put("apePat", UapePat);
        result.put("apeMat", UapeMat);
        result.put("correo", Ucorreo);
        //result.put("passwd",Upass);

        return result;
    }
}
