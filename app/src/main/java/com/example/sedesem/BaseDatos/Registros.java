package com.example.sedesem.BaseDatos;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;

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
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.sedesem.R;

import com.example.sedesem.VistaRegistro;


public class Registros extends AppCompatActivity implements View.OnClickListener {

    Object arrInfo[] = new Object[8];
    /*
     * this is the url to our webservice
     * make sure you are using the ip instead of localhost
     * it will not work if you are using localhost
     * */
    public static final String URL_SAVE_NAME = "http://192.168.43.5/SEDESEM/alta.php";

    //database helper object
    private DatabaseHelper db;

    //View objects
    private Button buttonSave;
    private EditText editTextName; //CURP
    private EditText editNombre;
    private EditText editApPat;
    private EditText editApMat;
    private EditText editSexo;
    private EditText editFechaNac;
    private EditText editEntidad;
    private EditText editRegion;
    private ListView listViewNames;

    //List to store all the names
    private List<Name> names; //CURP
    private List<Nombre> nombres;
    private List<ApPat> apPats;
    private List<ApMat> apMats;
    private List<Sexo> sexos;
    private List<FechaNac> FechaNacs;
    private List<Entidad> entidads;
    private List<Region> regions;


    //1 means data is synced and 0 means data is not synced
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "com.example.syncmysql";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    //adapterobject for list view
    private NameAdapter nameAdapter;

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

        /*buttonSave = findViewById(R.id.buttonSave);
        editTextName = findViewById(R.id.editTextName); //CURP
        editNombre = findViewById(R.id.editNombre);
        editApPat = findViewById(R.id.editApPat);
        editApMat = findViewById(R.id.editApMat);
        editSexo = findViewById(R.id.editSexo);
        editFechaNac = findViewById(R.id.editFechaNac);
        editEntidad = findViewById(R.id.editEntidad);
        editRegion = findViewById(R.id.editRegion);*/
        listViewNames = findViewById(R.id.listViewNames);

        //adding click listener to button
        //buttonSave.setOnClickListener(this);

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
        names.clear();
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
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REGION)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                regions.add(region);
            } while (cursor.moveToNext());
        }

        nameAdapter = new NameAdapter(this, R.layout.names, names, nombres, apPats, apMats);
        //nameAdapter = new NameAdapter(this, R.layout.names, names);
        listViewNames.setAdapter(nameAdapter);
    }

    /*
     * this method will simply refresh the list
     * */
    private void refreshList() {
        nameAdapter.notifyDataSetChanged();
    }

    /*
     * this method is saving the name to ther server MySQL
     * */
    private void saveNameToServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Guardando...");
        progressDialog.show();


        final String name = editTextName.getText().toString().trim();
        final String nombre = editNombre.getText().toString().trim();
        final String apPat = editApPat.getText().toString().trim();
        final String apMat = editApMat.getText().toString().trim();
        final String sexo = editSexo.getText().toString().trim();
        final String fechaNac = editFechaNac.getText().toString().trim();
        final String entidad = editEntidad.getText().toString().trim();
        final int region = Integer.parseInt(editRegion.getText().toString());

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
                            } else {
                                //if there is some error
                                //saving the name to sqlite with status unsynced
                                saveNameToLocalStorage(name, nombre, apPat, apMat, sexo, fechaNac, entidad, region, NAME_NOT_SYNCED_WITH_SERVER);
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
                params.put("name", name);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    //saving the name to local storage sqlite
    private void saveNameToLocalStorage(String name_id, String nombre, String apPat, String apMat, String sexo,
                                        String fechaNac, String entidad, int region, int status) {
        editTextName.setText("");
        db.addName(name_id, nombre, apPat, apMat, sexo, fechaNac, entidad, region, status);
        Name n = new Name(name_id, status);
        names.add(n);

        Nombre nom = new Nombre(nombre, status);
        nombres.add(nom);
        refreshList();
    }

    @Override
    public void onClick(View view) {
        saveNameToServer();
    }
}
