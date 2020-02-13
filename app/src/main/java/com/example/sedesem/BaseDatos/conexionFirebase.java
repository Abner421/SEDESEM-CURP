package com.example.sedesem.BaseDatos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sedesem.MainActivity;
import com.example.sedesem.R;

import com.example.sedesem.ScannedBarcodeActivity;
import com.example.sedesem.VistaRegistro;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class conexionFirebase extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;// Referencia base de datos global

    private ImageButton buttonSaveFirebase;
    private ImageButton btnSyncFirebase;
    private ImageButton btnHome;
    //FirebaseStorage storage = FirebaseStorage.getInstance();

    FirebaseAuth mAuth; //Requeridos para Firebase-storage
    FirebaseUser mUser;

    Vector<String> vecArchs = new Vector<>();
    File[] files;

    private ArrayAdapter<String> adapt;

    List<String> lineas = new ArrayList<>();

    private ListView archs;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://sedesembd.appspot.com");

    private String ident = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexion_firebase);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        archs = findViewById(R.id.archs);

        btnSyncFirebase = findViewById(R.id.btnSyncFirebase);
        buttonSaveFirebase = findViewById(R.id.buttonSaveFirebase);
        btnHome = findViewById(R.id.btnHome);

        btnSyncFirebase.setOnClickListener(this);
        buttonSaveFirebase.setOnClickListener(this);
        btnHome.setOnClickListener(this);

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

    public void obtenerRegistros() {
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
            String longitud = lineas.get(8);
            String latitud = lineas.get(9);
            String altitud = lineas.get(10);
            String precision = lineas.get(11);

            agregarRegistro(name, nombre, apPat, apMat, sexo, fechaNac, entidad, region, longitud, latitud, altitud, precision); //Manda el registro para añadir a la base de datos Firebase
        }

    }

    private void agregarRegistro(final String name_id, final String nombre, final String apePat, final String apeMat,
                                 final String sexo, final String fechaNac, final String entidad, final int reg,
                                 final String longitud, final String latitud, final String altitud,
                                 final String precision) {

        mDatabase.child("registros").child("registros").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Persona persona = new Persona(name_id, apePat, apeMat, nombre, sexo, fechaNac, entidad, reg, longitud, latitud, altitud, precision);
                        Map<String, Object> vals = persona.toMap();

                        Map<String, Object> actualizar = new HashMap<>();
                        actualizar.put("/registros/" + name_id, vals);
                        for (int i = 12; i < 15; i++) {
                            if (i == 12) {
                                ident = "A";
                            } else if (i == 13) {
                                ident = "B";
                            } else if (i == 14) {
                                ident = "C";
                            }
                            String aux = lineas.get(i).substring(8);
                            subirFoto(aux, ident);
                            try {
                                Thread.sleep(800); //Delay para que los registros se suban correctamente
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Toast.makeText(getApplicationContext(), "Correcto", Toast.LENGTH_SHORT).show();
                        lineas.clear(); //Limpia el vector para el siguiente registro

                        mDatabase.updateChildren(actualizar); //Orden para actualizar firebase con el registro
                        try {
                            Thread.sleep(200);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.e(TAG, "Registro correcto");
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
        switch (v.getId()) {
            case R.id.buttonSaveFirebase:
                startActivity(new Intent(conexionFirebase.this, ScannedBarcodeActivity.class));
                break;
            case R.id.btnSyncFirebase:
                obtenerRegistros();
                break;
            case R.id.btnHome:
                startActivity(new Intent(conexionFirebase.this, MainActivity.class));
                break;
        }
    }

    private void subirFoto(String cadUri, String ident) {
        String original = cadUri.substring(71); //Obtiene la fecha de la creación original del archivo, solo para referencia
        Uri file = Uri.fromFile(new File(cadUri)); //Obtiene la ruta del archivo original
        StorageReference refChild = storageRef.child(lineas.get(0) + ident); //Crea el archivo dentro de la base de datos
        UploadTask uploadTask = refChild.putFile(file); //Sube el archivo
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(conexionFirebase.this, "Falló subida de foto", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setCustomMetadata("Hora/Fecha", original)
                .build();

        // Update metadata properties
        refChild.updateMetadata(metadata)
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        // Updated metadata is in storageMetadata
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        Toast.makeText(conexionFirebase.this, "Falló al incrustar metadatos", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
