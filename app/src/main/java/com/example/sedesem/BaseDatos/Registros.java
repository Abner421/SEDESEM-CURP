package com.example.sedesem.BaseDatos;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Output;
import android.os.AsyncTask;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.example.sedesem.MainActivity;
import com.example.sedesem.PictureBarcodeActivity;
import com.example.sedesem.R;

import com.example.sedesem.ScannedBarcodeActivity;
import com.example.sedesem.VistaRegistro;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.net.ssl.HttpsURLConnection;
public class Registros extends AppCompatActivity implements View.OnClickListener {

    /*
     * this is the url to our webservice
     * make sure you are using the ip instead of localhost
     * it will not work if you are using localhost
     * */
    public static final String URL_SAVE_NAME = "http://192.168.43.5/SEDESEM/alta.php";

    //database helper object
    private DatabaseHelper db;

    private static final String TAG = "MainActivity";

    //View objects
    private Button buttonSave;
    private Button btnSync;
    private EditText editTextName; //CURP
    private EditText editNombre;
    private EditText editApPat;
    private EditText editApMat;
    private EditText editSexo;
    private EditText editFechaNac;
    private EditText editEntidad;
    private EditText editRegion;
    private ListView listViewNames;
    private ListView archs;
    private TextView txt;

    //List to store all the names
    private List<Name> names; //CURP
    private List<Nombre> nombres;
    private List<ApPat> apPats;
    private List<ApMat> apMats;
    private List<Sexo> sexos;
    private List<FechaNac> FechaNacs;
    private List<Entidad> entidads;
    private List<Region> regions;

    private Object[] info;

    //1 means data is synced and 0 means data is not synced
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "com.example.sedesem";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    //adapterobject for list view
    private NameAdapter nameAdapter;

    private ArrayAdapter<String> adapt;

    int contador;

    Vector<String> vecArchs = new Vector<>();
    File[] files;

    List<String> lineas = new ArrayList<>();

    public void readWrite(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Pero que ha pasao'!?");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros);

        //initializing views and objects
        db = new DatabaseHelper(this);
        names = new ArrayList<>();
        nombres = new ArrayList<>();
        apPats = new ArrayList<>();
        apMats = new ArrayList<>();
        sexos = new ArrayList<>();
        FechaNacs = new ArrayList<>();
        entidads = new ArrayList<>();
        regions = new ArrayList<>();

        info = new Object[7];

        archs = findViewById(R.id.archs);

        btnSync = findViewById(R.id.btnSync);
        buttonSave = findViewById(R.id.buttonSave);/*
        editTextName = findViewById(R.id.editTextName); //CURP
        editNombre = findViewById(R.id.editNombre);
        editApPat = findViewById(R.id.editApPat);
        editApMat = findViewById(R.id.editApMat);
        editSexo = findViewById(R.id.editSexo);
        editFechaNac = findViewById(R.id.editFechaNac);
        editEntidad = findViewById(R.id.editEntidad);
        editRegion = findViewById(R.id.editRegion);*/
        //listViewNames = findViewById(R.id.listViewNames);


        //adding click listener to button
        buttonSave.setOnClickListener(this);
        btnSync.setOnClickListener(this);

        //calling the method to load all the stored names
        loadNames();

        //the broadcast receiver to update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //loading the names again
                loadNames();
            }
        };

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));

        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /*
     * this method will
     * load the names from the database
     * with updated sync status
     * */
    private void loadNames() {

        /*names.clear();
        Cursor cursor = db.getNames();
        if (cursor.moveToFirst()) {
            do {
                Name name = new Name( //CURP
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                names.add(name); //CURP
            } while (cursor.moveToNext());

            //Añadido
            nombres.clear();
            cursor = db.getNombres();
            do {
                Nombre nombre = new Nombre(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOMBRE)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                nombres.add(nombre);
            } while (cursor.moveToNext());

            apPats.clear();
            cursor = db.getApPats();
            do {
                ApPat apPat = new ApPat(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_APPAT)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                apPats.add(apPat);
            } while (cursor.moveToNext());

            apMats.clear();
            cursor = db.getApMats();
            do {
                ApMat apMat = new ApMat(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_APMAT)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                apMats.add(apMat);
            } while (cursor.moveToNext());

            sexos.clear();
            cursor = db.getSexos();
            do {
                Sexo sexo = new Sexo(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SEXO)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                sexos.add(sexo);
            } while (cursor.moveToNext());

            FechaNacs.clear();
            cursor = db.getFechaNacs();
            do {
                FechaNac FechaNac = new FechaNac(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FECHANAC)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                FechaNacs.add(FechaNac);
            } while (cursor.moveToNext());

            entidads.clear();
            cursor = db.getEntidades();
            do {
                Entidad entidad = new Entidad(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ENTIDAD)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                entidads.add(entidad);
            } while (cursor.moveToNext());

            regions.clear();
            cursor = db.getRegiones();
            do {
                Region region = new Region(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_REGION)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                regions.add(region);
            } while (cursor.moveToNext());
        }

        nameAdapter = new NameAdapter(this, R.layout.names, names, nombres, apPats, apMats);
        //nameAdapter = new NameAdapter(this, R.layout.names, names);
        listViewNames.setAdapter(nameAdapter);*/
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard.getAbsolutePath() + "/text");

            files = file.listFiles();


            for (int i = 0; i < files.length; i++) {
                String sub = files[i].getName();
                StringTokenizer st = new StringTokenizer(sub, ".");
                vecArchs.add(st.nextToken());
            }

            adapt = new ArrayAdapter<>(
                    this, android.R.layout.simple_list_item_1, vecArchs);


            archs.setAdapter(adapt);
        } catch (Exception e) {
        }

    }

    /*
     * this method will simply refresh the list
     * */
    private void refreshList() {
        adapt.notifyDataSetChanged();
    }

    /*
     * this method is saving the name to their server MySQL
     * */
    private void saveNameToServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Guardando...");
        progressDialog.show();

        String line;

        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard.getAbsolutePath() + "/text/" + vecArchs.lastElement() + ".txt");

        try {

            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + System.getProperty("line.separator"));
                lineas.add(line);
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            //Log.d(TAG, ex.getMessage());
        } catch (IOException ex) {
            //Log.d(TAG, ex.getMessage());
        }


//      info = new VistaRegistro().getArreglo();
        final String name = lineas.get(0).trim();
        final String nombre = lineas.get(3);
        final String apPat = lineas.get(1);
        final String apMat = lineas.get(2);
        final String sexo = lineas.get(4);
        final String fechaNac = lineas.get(5);
        final String entidad = lineas.get(6);
        final int region = Integer.parseInt(lineas.get(7));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //if there is a success
                                //storing the name to sqlite with status synced
                                saveNameToLocalStorage(name, nombre, apPat, apMat, sexo, fechaNac, entidad, region, NAME_SYNCED_WITH_SERVER);
                                Toast.makeText(getApplicationContext(), "Guardado exitosamente con status: 1", Toast.LENGTH_SHORT).show();
                            } else {
                                //if there is some error
                                //saving the name to sqlite with status unsynced
                                saveNameToLocalStorage(name, nombre, apPat, apMat, sexo, fechaNac, entidad, region, NAME_NOT_SYNCED_WITH_SERVER);
                                Toast.makeText(getApplicationContext(), "Guardado exitosamente con status: 0", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        //on error storing the name to sqlite with status unsynced
                        saveNameToLocalStorage(name, nombre, apPat, apMat, sexo, fechaNac, entidad, region, NAME_NOT_SYNCED_WITH_SERVER);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //params.put("name", name);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

        startActivity(new Intent(Registros.this, ScannedBarcodeActivity.class));
    }

    //saving the name to local storage sqlite
    public void saveNameToLocalStorage(String name_id, String nombre, String apPat, String apMat, String sexo, //Modificado
                                       String fechaNac, String entidad, int region, int status) {
        //editTextName.setText("");
        db.addName(name_id, nombre, apPat, apMat, sexo, fechaNac, entidad, region, status);
        Name n = new Name(name_id, status);
        names.add(n);

        Nombre nom = new Nombre(nombre, status);
        nombres.add(nom);

        ApPat apPat1 = new ApPat(apPat, status);
        apPats.add(apPat1);

        ApMat apMat1 = new ApMat(apMat, status);
        apMats.add(apMat1);

        Sexo sex = new Sexo(sexo, status);
        sexos.add(sex);

        FechaNac nacs = new FechaNac(fechaNac, status);
        FechaNacs.add(nacs);

        Entidad ent = new Entidad(entidad, status);
        entidads.add(ent);

        Region reg = new Region(region, status);
        regions.add(reg);
        refreshList();
    }

    public void sincronizarMySQL() throws JSONException, IOException{
        //DEBEN DEFINIRSE NUEVOS CURSORES PARA CADA TIPO DE ARREGLO QUE SE REQUIERA
        //ArrayList<String> personas = new ArrayList<>();
        ArrayList<Regs> personas = new ArrayList<>();
        contador = db.getCounter();
        Cursor cursor = db.getNames();
        Cursor cNombre = db.getNombres();
        Cursor cApPat = db.getApPats();
        Cursor cApMat = db.getApMats();
        Cursor cSexo = db.getSexos();
        Cursor cFechaNac = db.getFechaNacs();
        Cursor cEntidad = db.getEntidades();
        Cursor cRegion = db.getRegiones();

        names.clear();
        if (cursor.moveToFirst() && cNombre.moveToFirst() && cApPat.moveToFirst() && cApMat.moveToFirst() && cSexo.moveToFirst()
                && cFechaNac.moveToFirst() && cEntidad.moveToFirst() && cRegion.moveToFirst()) {
            do {
                Name name = new Name( //CURP
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                names.add(name); //CURP
            } while (cursor.moveToNext());

            nombres.clear();
            //cursor = db.getNombres();

            do {
                Nombre nombre = new Nombre(
                        cNombre.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOMBRE)),
                        cNombre.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                nombres.add(nombre);
            } while (cNombre.moveToNext());

            apPats.clear();
            do {
                ApPat apPat = new ApPat(
                        cApPat.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_APPAT)),
                        cApPat.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                apPats.add(apPat);
            } while (cApPat.moveToNext());

            apMats.clear();
            do {
                ApMat apMat = new ApMat(
                        cApMat.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_APMAT)),
                        cApMat.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                apMats.add(apMat);
            } while (cApMat.moveToNext());

            sexos.clear();
            do {
                Sexo sexo = new Sexo(
                        cSexo.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SEXO)),
                        cSexo.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                sexos.add(sexo);
            } while (cSexo.moveToNext());

            FechaNacs.clear();
            do {
                FechaNac FechaNac = new FechaNac(
                        cFechaNac.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FECHANAC)),
                        cFechaNac.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                FechaNacs.add(FechaNac);
            } while (cFechaNac.moveToNext());

            entidads.clear();
            do {
                Entidad entidad = new Entidad(
                        cEntidad.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ENTIDAD)),
                        cEntidad.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                entidads.add(entidad);
            } while (cEntidad.moveToNext());

            regions.clear();
            do {
                Region region = new Region(
                        cRegion.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_REGION)),
                        cRegion.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                regions.add(region);
            } while (cRegion.moveToNext());

        }
       // new comoquieras(db,names,nombres,apPats,apMats,sexos,FechaNacs,entidads,regions).execute();

    }
    /*@Override
    protected void onPostExecute(String result) {
        Toast.makeText(getApplicationContext(), result,
                Toast.LENGTH_LONG).show();
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSave:
                saveNameToServer();
                break;
            case R.id.btnSync:
                readWrite();
                try {
                    sincronizarMySQL();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


}

