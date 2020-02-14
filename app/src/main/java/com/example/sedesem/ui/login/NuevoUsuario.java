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
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sedesem.BaseDatos.ApPat;
import com.example.sedesem.R;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NuevoUsuario extends AppCompatActivity implements View.OnClickListener {

    //variables globales de inicializacion
    public static String nombreArchivo;
    private Vibrator vibrator;
    SurfaceView lector2;
    TextView correo, confCorreo;

    EditText pass1, pass2, Nombre, ApePat, ApeMat;

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

        Nombre = findViewById(R.id.Nombre);
        ApePat = findViewById(R.id.ApePat);
        ApeMat = findViewById(R.id.ApeMat);
        pass1 = findViewById(R.id.pass1);
        pass2 = findViewById(R.id.pass2);

        correo = findViewById(R.id.correo);
        confCorreo = findViewById(R.id.confCorreo);

        btnConfirmar.setOnClickListener(this);
        btnLimpiar.setOnClickListener(this);
    }

    public boolean validaCorreo() {
        String a = correo.getText().toString();
        String b = confCorreo.getText().toString();

        // Patrón para validar el email
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        String email = correo.getText().toString();

        Matcher mather = pattern.matcher(email);
        if (mather.find()) {
            if (correo.getText().toString().equals(confCorreo.getText().toString())) {
                return true;
            } else {
                confCorreo.setError("No coinciden los correos");
                return false;
            }
        } else {
            correo.setError("correo no valido");
            return false;
        }
    }

    public boolean armausuario() {
        String a = Nombre.getText().toString();
        String b = ApePat.getText().toString();
        String c = ApeMat.getText().toString();

        if (a.isEmpty()) {
            Nombre.setError("Campo Vacio");
            return false;
        } else if (b.isEmpty()) {
            ApePat.setError("Campo Vacio");
            return false;
        } else if (c.isEmpty()) {
            ApeMat.setError("Campo Vacio");
            return false;
        }
        return true;
    }

    public boolean validarContrase() {

        String a = pass1.getText().toString();
        String b = pass2.getText().toString();

        if (a.isEmpty() || a.length() < 8) {
            pass1.setError("Minimo 8 caracteres alfanumericos");
            return false;
        } else if (!a.equals(b)) {
            pass2.setError("No coinciden las contraseñas");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirmar:
                armausuario();
                if (validarContrase() && armausuario() && validaCorreo())
                    Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnLimpiar:

                break;
        }
    }
}