package com.example.sedesem.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sedesem.MainActivity;
import com.example.sedesem.R;
import com.example.sedesem.ScannedBarcodeActivity;
import com.example.sedesem.VistaRegistro;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class NuevoUsuario extends AppCompatActivity {

    //variables globales de inicializacion
    public static String nombreArchivo;
    private Vibrator vibrator;
    SurfaceView lector2;
    TextView nombre, apeMat, apePat, correo, confCorreo;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnConfirmar, btnLimpiar;
    String intentData = "";
    //----------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario);

        btnConfirmar = findViewById(R.id.btnConfirmar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        lector2 = findViewById(R.id.lector2);

        nombre = findViewById(R.id.Nombre);
        apePat = findViewById(R.id.ApePat);
        apeMat = findViewById(R.id.ApeMat);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ScannedBarcodeActivity.this, MainActivity.class));
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ScannedBarcodeActivity.this, MainActivity.class));
            }
        });

        initialiseDetectorsAndSources();
    }

    protected void initialiseDetectorsAndSources() {

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        lector2.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(NuevoUsuario.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(lector2.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(NuevoUsuario.this, new
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
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {

                    nombre.post(new Runnable() {

                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                nombre.removeCallbacks(null);
                                intentData = barcodes.valueAt(0).email.address;
                                nombre.setText(intentData);
                            } else {
                                intentData = barcodes.valueAt(0).displayValue;
                                if (intentData.contains("|")) {
                                    long[] patron = {100, 200, 200};
                                    //vibrator.vibrate(patron, -1);
                                    StringTokenizer tk = new StringTokenizer(intentData, "|"); //Esto solo funciona si es una CURP
                                    String primero = tk.nextToken();    //Control
                                    String LapePat = tk.nextToken();     //Nombre
                                    String LapeMat = tk.nextToken();
                                    String Lnombre = tk.nextToken();

                                    nombre.setText(Lnombre);
                                    apePat.setText(LapePat);
                                    apeMat.setText(LapeMat);
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}