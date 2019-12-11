package com.example.sedesem;

import android.content.Intent;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.*;
import java.util.Vector;

import com.example.sedesem.BaseDatos.Registros;

public class VistaRegistro extends AppCompatActivity implements View.OnClickListener {

    EditText CURP, ApPat, ApMat, Nombre, Sexo, FechaNac, Entidad, Region;

    ImageButton btnok, btnregresar;

    // Archivo para obtener datos del QR (CURP, nombres...)
    private static final int PERMISSION_REQUEST_CODE = 100;
    ScannedBarcodeActivity aux = new ScannedBarcodeActivity();

    //Arreglo info.
    Object arrInfo[] = new Object[8];

    Registros reg = new Registros();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_registro);

        CURP = findViewById(R.id.CURP);
        ApPat = findViewById(R.id.ApPat);
        ApMat = findViewById(R.id.ApMat);
        Nombre = findViewById(R.id.Nombre);
        Sexo = findViewById(R.id.Sexo);
        FechaNac = findViewById(R.id.FechaNac);
        Entidad = findViewById(R.id.Entidad);
        Region = findViewById(R.id.CodigoRegion);

        initViews();
        llenaCampos();
    }

    private void llenaCampos() { //Coloca los datos recibidos del archivo
        String estado = Environment.getExternalStorageState();
        Vector<String> datos = new Vector<>();
        if (Environment.MEDIA_MOUNTED.equals(estado)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    File sdcard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdcard.getAbsolutePath() + "/text");
                    if (dir.exists()) {
                        File file = new File(dir, aux.getNombreArchivo() + ".txt"); //El nombre del archivo debería ser igual que el CURP leído en el QR
                        //Toast.makeText(getApplicationContext(), "Archivo: " + aux.getNombreArchivo(), Toast.LENGTH_SHORT).show();
                        FileOutputStream os = null;
                        StringBuilder texto = new StringBuilder();
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String linea;
                            while ((linea = br.readLine()) != null) {
                                datos.add(linea); //Posiciones: 0:CURP,1:ApePat,2:ApeMat,3:Nombres,4:Sexo,5:FechaNac,6:Entidad,7:CodigoRegion,8:Longitud,9:Latitud,10:Altura
                                /*texto.append(linea);
                                texto.append("\n");*/
                            }
                            br.close();
                        } catch (IOException e) {
                        }
                        //txtSalida.setText(texto);
                        /* Aquí deberían llenarse los campos
                         */
                        CURP.setText(datos.get(0));
                        ApPat.setText(datos.get(1));
                        ApMat.setText(datos.get(2));
                        Nombre.setText(datos.get(3));
                        Sexo.setText(datos.get(4));
                        FechaNac.setText(datos.get(5));
                        Entidad.setText(datos.get(6));
                        Region.setText(datos.get(7));

                        arrInfo[0] = datos.get(0); //Arreglo de objetos para enviar
                        arrInfo[1] = datos.get(1);
                        arrInfo[2] = datos.get(2);
                        arrInfo[3] = datos.get(3);
                        arrInfo[4] = datos.get(4);
                        arrInfo[5] = datos.get(5);
                        arrInfo[6] = datos.get(6);
                        arrInfo[7] = datos.get(7);

                        CURP.setEnabled(false);
                        ApPat.setEnabled(false);
                        ApMat.setEnabled(false);
                        Nombre.setEnabled(false);
                        Sexo.setEnabled(false);
                        FechaNac.setEnabled(false);
                        Entidad.setEnabled(false);
                        Region.setEnabled(false);
                    }
                } else {
                    requestPermission();
                }
            } else {
                File sdcard = Environment.getExternalStorageDirectory();
                File dir = new File(sdcard.getAbsolutePath() + "/text");
                if (dir.exists()) {
                    File file = new File("ejemplo.txt");
                    FileOutputStream os = null;
                    StringBuilder texto = new StringBuilder();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String linea;
                        while ((linea = br.readLine()) != null) {
                            texto.append(linea);
                            texto.append("\n");
                        }
                        br.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    private void initViews() {
        btnregresar = findViewById(R.id.btnregresar);
        btnregresar.setOnClickListener(this);
        btnok = findViewById(R.id.btnok);
        btnok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnregresar:
                startActivity(new Intent(VistaRegistro.this, ScannedBarcodeActivity.class));
                break;
            case R.id.btnok:
                //Implementar función para mandar info.
                reg.setArreglo(arrInfo);
                //Función
                Toast.makeText(getApplicationContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VistaRegistro.this, MainActivity.class));
                break;
        }
    }

    //Métodos para la lectura del archivo
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(VistaRegistro.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(VistaRegistro.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(VistaRegistro.this, "Write External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(VistaRegistro.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public Object getArreglo(Object array){ return array; }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
}
