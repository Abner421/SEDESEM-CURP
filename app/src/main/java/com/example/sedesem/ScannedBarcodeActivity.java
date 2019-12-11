package com.example.sedesem;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.StringTokenizer;

import java.io.File;
import java.io.FileWriter;

public class ScannedBarcodeActivity extends AppCompatActivity {


    public static String nombreArchivo;
    private Vibrator vibrator;
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnAction;
    String intentData = "";
    boolean isEmail = false;

    /* Para GPS */
    private LocationManager locManager;
    private Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);

        initViews();

        //Vibrador
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        btnAction = findViewById(R.id.btnAction);

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (intentData.length() > 0) {
                    if (isEmail)
                        //startActivity(new Intent(ScannedBarcodeActivity.this, EmailActivity.class).putExtra("email_address", intentData));
                        Toast.makeText(ScannedBarcodeActivity.this, "", Toast.LENGTH_SHORT); //No hace nada
                    else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentData)));
                    }
                }


            }
        });
    }

    protected void initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Escáner iniciado", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //Toast.makeText(getApplicationContext(), "Escáner detenido para evitar fallos", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {

                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                txtBarcodeValue.removeCallbacks(null);
                                intentData = barcodes.valueAt(0).email.address;
                                txtBarcodeValue.setText(intentData);
                                isEmail = true;
                                btnAction.setText("Agregar Contenido a Formulario");
                            } else {
                                isEmail = false;
                                btnAction.setText("Abrir Vínculo");
                                intentData = barcodes.valueAt(0).displayValue;
                                if (intentData.contains("|")) {
                                    long[] patron = {100, 200, 200};
                                    vibrator.vibrate(patron, -1);
                                    StringTokenizer tk = new StringTokenizer(intentData, "|"); //Esto solo funciona si es una CURP
                                    String primero = tk.nextToken();
                                    String segundo = tk.nextToken();
                                    String tercero = tk.nextToken();
                                    String cuarto = tk.nextToken();
                                    String quinto = tk.nextToken();
                                    String sexto = tk.nextToken();
                                    String septimo = tk.nextToken();
                                    String octavo = tk.nextToken();
                                    /*Toast.makeText(getApplicationContext(), primero + "\n" + segundo + "\n" + tercero
                                            + "\n" + cuarto + "\n" + quinto + "\n" + sexto + "\n" + septimo + "\n" + octavo, Toast.LENGTH_SHORT).show();*/

                                    //txtBarcodeValue.setText(intentData);
                                    //btnAction.setVisibility(View.VISIBLE);
                                    btnAction.setText("Agregar a Formulario");
                                    //Coordenadas de geolocalización
                                    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        return;
                                    }
                                    loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                    String longitud = String.valueOf(loc.getLongitude());
                                    String latitud = String.valueOf(loc.getLatitude());
                                    String altitud = String.valueOf(loc.getAltitude());
                                    String precision = String.valueOf(loc.getAccuracy());

                                    //Creación de archivo
                                    File sdcard = Environment.getExternalStorageDirectory();
                                    File file = new File(sdcard.getAbsolutePath() + "/text");
                                    if (!file.exists()) {
                                        file.mkdir();
                                    }
                                    try {
                                        File gpxFile = new File(file, primero + ".txt");
                                        FileWriter writer = new FileWriter(gpxFile);
                                        writer.append(primero + "\n" + segundo + "\n" + tercero
                                                + "\n" + cuarto + "\n" + quinto + "\n" + sexto + "\n" + septimo + "\n" + octavo + "\nLongitud: " + longitud
                                                + "\nLatitud: " + latitud + "\nAltitud: " + altitud + "\nPrecisión: " + precision);
                                        writer.flush();
                                        writer.close();
                                        //Toast.makeText(getApplicationContext(), "Archivo creado exitosamente", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                    }

                                    nombreArchivo(primero);
                                    startActivity(new Intent(ScannedBarcodeActivity.this, VistaRegistro.class));
                                } else {
                                    txtBarcodeValue.setText(intentData);
                                }
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    public String nombreArchivo(String CURP) {
        nombreArchivo = CURP;
        return nombreArchivo;
    }

    public static String getNombreArchivo() {
        return nombreArchivo;
    }
}
