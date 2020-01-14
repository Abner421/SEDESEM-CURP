package com.example.sedesem;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.*;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import com.example.sedesem.BaseDatos.conexionFirebase;

public class VistaRegistro extends AppCompatActivity implements View.OnClickListener {

    EditText CURP, ApPat, ApMat, Nombre, Sexo, FechaNac, Entidad, Region;

    private String mDirAbsoluto = null;

    ImageButton btnok, btnregresar;

    Button btnFotos;
    TextView contadorFotos;

    String currentPhotoPath;
    private String pictureFilePath;
    String ruta1, ruta2, ruta3;
    File pic1, pic2, pic3;

    File foto = null;

    boolean foto1, foto2, foto3, cont;

    ImageView ivFoto1, ivFoto2, ivFoto3;

    private static final int REQUEST_CODE_CAMARA = 1;
    private static final int SCALE_FACTOR_IMAGE_VIEW = 4;
    private static final String ALBUM = "foto";
    private static final String EXTENSION_JPEG = ".jpg";

    private Uri imageUri;

    // Archivo para obtener datos del QR (CURP, nombres...)
    private static final int PERMISSION_REQUEST_CODE = 100;

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;

    ScannedBarcodeActivity aux = new ScannedBarcodeActivity();

    public Vector<String> datos = new Vector<>();

    //Arreglo info.
    Object[] arrInfo = new Object[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_registro);
        foto1 = false;
        foto2 = false;
        foto3 = false;
        ruta1 = null;
        ruta2 = null;
        ruta3 = null;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btnFotos.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    private void llenaCampos() { //Coloca los datos recibidos del archivo
        String estado = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(estado)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    File sdcard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdcard.getAbsolutePath() + "/text");
                    if (dir.exists()) {
                        File file = new File(dir, ScannedBarcodeActivity.getNombreArchivo() + ".txt"); //El nombre del archivo debería ser igual que el CURP leído en el QR
                        FileOutputStream os = null;
                        StringBuilder texto = new StringBuilder();
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String linea;
                            while ((linea = br.readLine()) != null) {
                                datos.add(linea); //Posiciones: 0:CURP,1:ApePat,2:ApeMat,3:Nombres,4:Sexo,5:FechaNac,6:Entidad,7:CodigoRegion,8:Longitud,9:Latitud,10:Altura
                            }
                            br.close();
                        } catch (IOException e) {
                        }
                        /* Aquí deberían llenarse los campos */
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
        btnok = findViewById(R.id.btnok);
        btnFotos = findViewById(R.id.btnFotos);

        btnregresar.setOnClickListener(this);
        btnok.setOnClickListener(this);
        btnFotos.setOnClickListener(this);

        ivFoto1 = findViewById(R.id.ivFoto1);
        ivFoto2 = findViewById(R.id.ivFoto2);
        ivFoto3 = findViewById(R.id.ivFoto3);

        contadorFotos = findViewById(R.id.contadorFotos);
        cont = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnregresar:
                startActivity(new Intent(VistaRegistro.this, ScannedBarcodeActivity.class));
                break;
            case R.id.btnok:
                //Mensaje de confirmación
                if (!cont) {
                    Toast.makeText(this, "Por favor, toma las fotos requeridas", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Archivo guardado exitosamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VistaRegistro.this, conexionFirebase.class));
                }
                break;
            case R.id.btnFotos:
                tomaFoto();
                break;
        }
    }

    //Métodos para la lectura del archivo
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(VistaRegistro.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(VistaRegistro.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(VistaRegistro.this, "Verifique los permisos de de la aplicación en configuración.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(VistaRegistro.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                btnFotos.setEnabled(true);
            }
        }
    }

    private void tomaFoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            //startActivityForResult(cameraIntent, 1);
            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(VistaRegistro.this,
                        "com.example.sedesem.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, 1);
            }
        }
    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "PIC_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile, ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            File imgFile = new File(pictureFilePath);
            if (imgFile.exists() && !foto1 && !foto2 && !foto3) {
                addToGallery(Uri.fromFile(imgFile));
                ruta1 = Uri.fromFile(imgFile).toString();
                ivFoto1.setImageURI(Uri.fromFile(imgFile));
                try {
                    agregaRutas(ruta1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                foto1 = true;
                contadorFotos.setText("1/3 Fotos");
            } else if (imgFile.exists() && foto1 && !foto2 && !foto3) {
                addToGallery(Uri.fromFile(imgFile));
                ruta2 = Uri.fromFile(imgFile).toString();
                ivFoto2.setImageURI(Uri.fromFile(imgFile));
                try {
                    agregaRutas(ruta2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                foto2 = true;
                contadorFotos.setText("2/3 Fotos");
            } else if (imgFile.exists() && foto1 && foto2 && !foto3) {
                addToGallery(Uri.fromFile(imgFile));
                ruta3 = Uri.fromFile(imgFile).toString();
                ivFoto3.setImageURI(Uri.fromFile(imgFile));
                try {
                    agregaRutas(ruta3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                foto3 = true;
                contadorFotos.setText("3/3 Fotos");
                cont = true;
                btnFotos.setEnabled(false);
            }
        }
    }

    public void agregaRutas(String rutaFoto) throws IOException {
        String estado = Environment.getExternalStorageState();
        BufferedWriter bw = null;
        FileWriter fw = null;

        if (Environment.MEDIA_MOUNTED.equals(estado)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    File sdcard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdcard.getAbsolutePath() + "/text");
                    if (dir.exists()) {
                        File file = new File(dir, ScannedBarcodeActivity.getNombreArchivo() + ".txt"); //El nombre del archivo debería ser igual que el CURP leído en el QR
                        fw = new FileWriter(file.getAbsoluteFile(), true);
                        bw = new BufferedWriter(fw);
                        bw.write(rutaFoto + "+\n");
                        bw.close();
                        fw.close();
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
    }

    private void addToGallery(Uri ruta) {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        try {
            // externalStorage
            String ExternalStorageDirectory = Environment.getExternalStorageDirectory() + File.separator;
            // uri de la imagen seleccionada
            Uri uri = ruta;
            //carpeta "imagenesguardadas"
            String rutacarpeta = "SEDESEM/";
            // nombre del nuevo png
            String nombre = "SEDESEM_" + timeStamp + ".png";

            // Compruebas si existe la carpeta "imagenesguardadas", sino, la crea
            File directorioImagenes = new File(ExternalStorageDirectory + rutacarpeta);
            if (!directorioImagenes.exists())
                directorioImagenes.mkdirs();

            // le pasas al bitmap la uri de la imagen seleccionada
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            // pones las medidas que quieras del nuevo .png
            int bitmapWidth = 720; // para utilizar width de la imagen original: bitmap.getWidth();
            int bitmapHeight = 1080; // para utilizar height de la imagen original: bitmap.getHeight();
            Bitmap bitmapout = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, false);
            //creas el nuevo png en la nueva ruta
            bitmapout.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(ExternalStorageDirectory + rutacarpeta + nombre));

            // le pones parametros necesarios a la imagen para que se muestre en cualquier galería

            File filefinal = new File(ExternalStorageDirectory + rutacarpeta + nombre);

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Titulo");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Descripción");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, filefinal.toString().toLowerCase(Locale.getDefault()).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, filefinal.getName().toLowerCase(Locale.getDefault()));
            values.put("_data", filefinal.getAbsolutePath());
            ContentResolver cr = getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            //
            Log.w("Guardado!", "Guardado como: " + filefinal);

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }
}
