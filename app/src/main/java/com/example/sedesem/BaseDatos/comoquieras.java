package com.example.sedesem.BaseDatos;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class comoquieras extends AsyncTask{
    //database helper object
    private DatabaseHelper db;

    public comoquieras(DatabaseHelper db, List<Name> names, List<Nombre> nombres, List<ApPat> apPats, List<ApMat> apMats, List<Sexo> sexos, List<FechaNac> fechaNacs, List<Entidad> entidads, List<Region> regions) {
        this.db = db;
        this.names = names;
        this.nombres = nombres;
        this.apPats = apPats;
        this.apMats = apMats;
        this.sexos = sexos;
        FechaNacs = fechaNacs;
        this.entidads = entidads;
        this.regions = regions;
    }

    //List to store all the names
    private List<Name> names; //CURP
    private List<Nombre> nombres;
    private List<ApPat> apPats;
    private List<ApMat> apMats;
    private List<Sexo> sexos;
    private List<FechaNac> FechaNacs;
    private List<Entidad> entidads;
    private List<Region> regions;

    int contador = db.getCounter();
    Cursor cursor = db.getNames();
    Cursor cNombre = db.getNombres();
    Cursor cApPat = db.getApPats();
    Cursor cApMat = db.getApMats();
    Cursor cSexo = db.getSexos();
    Cursor cFechaNac = db.getFechaNacs();
    Cursor cEntidad = db.getEntidades();
    Cursor cRegion = db.getRegiones();


    @Override
    protected Object doInBackground(Object[] params) {
        try {

            URL url = new URL("http://192.168.43.5/SEDESEM/recibe.php");

            JSONObject postDataParams = new JSONObject();
            for (int i = 0; i < contador; i++) {
                postDataParams.put("Curp", names.get(i).getName());
                postDataParams.put("Nombres", nombres.get(i).getNombre());
                postDataParams.put("ApePat", apPats.get(i).getApPat());
                postDataParams.put("ApeMat", apMats.get(i).getApMat());
                postDataParams.put("Sexo", sexos.get(i).getSexo());
                postDataParams.put("FechaNac", FechaNacs.get(i).getFechaNac());
                postDataParams.put("Entidad", entidads.get(i).getEntidad());
                postDataParams.put("CodigoReg", regions.get(i).getRegion());

            }
            Log.e("params", postDataParams.toString());


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();

            } else {
                //return new String("false : "+responseCode);
            }
        } catch (Exception e) {
            String error = ("Exception: " + e.getMessage());
            System.out.println(error);
        }
        return null;
    }
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
