package com.example.sedesem;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sedesem.*;
import com.example.sedesem.BaseDatos.Registros;
import com.example.sedesem.BaseDatos.conexionFirebase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_CODE = 100;
    protected TextView tvLatitud, tvLongitud, tvAltura, tvPrecision;
    Button btnTakePicture, btnScanBarcode, btnRegistros, btnFirebase;
    private LocationManager locManager;
    private Location loc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        initViews();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Posicion(String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()), String.valueOf(loc.getAltitude()), String.valueOf(loc.getAccuracy()));
            //actualizarPosicion();
        }

    }

    private void initViews() {
        btnTakePicture = findViewById(R.id.btnTakePicture);
        btnScanBarcode = findViewById(R.id.btnScanBarcode);
        btnRegistros = findViewById(R.id.btnRegistros);
        btnFirebase = findViewById(R.id.btnFirebase);

        btnTakePicture.setOnClickListener(this);
        btnScanBarcode.setOnClickListener(this);
        btnRegistros.setOnClickListener(this);
        btnFirebase.setOnClickListener(this);

        requestPermission();
    }

    public String Posicion(String lat, String longit, String Altura, String precision) {
        String latitud = lat;
        String longitud = longit;
        String altura = Altura;
        String prec = precision;

        StringBuilder res = new StringBuilder();
        res.append(latitud + "\n");
        res.append(longitud + "\n");
        res.append(altura + "\n");
        res.append(prec + "\n");

        return res.toString();
    }

    private void actualizarPosicion() {
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(MainActivity.this, "Coordenadas GPS actualizadas: " + "lat --->" + location.getLatitude() + "\nlong -->"
                        + location.getLongitude(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Toast.makeText(MainActivity.this, "Cambios en proveedor" + s + "Estado--->" + i, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderEnabled(String s) {
                Toast.makeText(MainActivity.this, "Proveedor habilitado" + s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(MainActivity.this, "Proveedor deshabilitado" + s, Toast.LENGTH_LONG).show();
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locListener);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnTakePicture:
                startActivity(new Intent(MainActivity.this, PictureBarcodeActivity.class));
                break;
            case R.id.btnScanBarcode:
                startActivity(new Intent(MainActivity.this, ScannedBarcodeActivity.class));
                break;
            case R.id.btnRegistros:
                startActivity(new Intent(MainActivity.this, conexionFirebase.class));
                break;
            case R.id.btnFirebase:
                startActivity(new Intent(MainActivity.this, regsFire.class));
                break;
        }

    }

    //Métodos para la lectura del archivo
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

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
