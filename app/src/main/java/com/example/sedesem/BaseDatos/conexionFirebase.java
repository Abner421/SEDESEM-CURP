package com.example.sedesem.BaseDatos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sedesem.R;

import com.example.sedesem.ScannedBarcodeActivity;
import com.example.sedesem.VistaRegistro;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

public class conexionFirebase extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;// Referencia base de datos global

    private Button buttonSaveFirebase;
    private Button btnSyncFirebase;

    //List to store all the names
    private List<Name> names; //CURP
    private List<Nombre> nombres;
    private List<ApPat> apPats;
    private List<ApMat> apMats;
    private List<Sexo> sexos;
    private List<FechaNac> FechaNacs;
    private List<Entidad> entidads;
    private List<Region> regions;

    Vector<String> vecArchs = new Vector<>();
    File[] files;

    private ArrayAdapter<String> adapt;

    List<String> lineas = new ArrayList<>();

    private Object[] info;
    private ListView archs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexion_firebase);

        //Sin conexión
        /*FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        DatabaseReference curpRef = FirebaseDatabase.getInstance().getReference("registros");
        curpRef.keepSynced(true);*/

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]


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

        btnSyncFirebase = findViewById(R.id.btnSyncFirebase);
        buttonSaveFirebase = findViewById(R.id.buttonSaveFirebase);

        btnSyncFirebase.setOnClickListener(this);
        buttonSaveFirebase.setOnClickListener(this);

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

    public void obtenerRegistros(){
        String line;

        File sdcard = Environment.getExternalStorageDirectory();
        for (int i = 0; i < vecArchs.size(); i++) {
            File file = new File(sdcard.getAbsolutePath() + "/text/" + vecArchs.elementAt(i) + ".txt");
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

                bufferedReader.close();
            } catch (FileNotFoundException ex) {
                //Log.d(TAG, ex.getMessage());
            } catch (IOException ex) {
                //Log.d(TAG, ex.getMessage());
            }

            String name = lineas.get(0).trim();
            String nombre = lineas.get(3);
            String apPat = lineas.get(1);
            String apMat = lineas.get(2);
            String sexo = lineas.get(4);
            String fechaNac = lineas.get(5);
            String entidad = lineas.get(6);
            int region = Integer.parseInt(lineas.get(7));

            agregarRegistro(name, nombre, apPat, apMat, sexo, fechaNac, entidad, region);

            lineas.clear();
        }

    }

    private void agregarRegistro(final String name_id, final String nombre, final String apePat, final String apeMat, final String sexo, final String fechaNac, final String entidad, final int reg){
        mDatabase.child("registros").child("registros").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String curp = name_id;
                        String apPat = apePat;
                        String apMat = apeMat;
                        String nom = nombre;
                        String sex = sexo;
                        String fNac = fechaNac;
                        String ent = entidad;
                        int region = reg;
                        Persona persona = new Persona(curp,apPat,apMat,nom,sex,fNac,ent,region);
                        Map<String, Object> vals = persona.toMap();

                        Map<String, Object> actualizar = new HashMap<>();
                        actualizar.put("/registros/"+curp,vals);

                        mDatabase.updateChildren(actualizar);
                        try {
                            Thread.sleep(200);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.e(TAG,"Registro correcto");
                        Toast.makeText(getApplicationContext(), "Correcto", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(conexionFirebase.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonSaveFirebase:
                startActivity(new Intent(conexionFirebase.this, ScannedBarcodeActivity.class));
                break;
            case R.id.btnSyncFirebase:
                obtenerRegistros();
                break;
        }
    }
}
