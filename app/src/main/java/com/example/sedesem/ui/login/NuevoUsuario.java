package com.example.sedesem.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sedesem.BaseDatos.conexionFirebase;
import com.example.sedesem.MainActivity;
import com.example.sedesem.R;

import com.example.sedesem.ScannedBarcodeActivity;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NuevoUsuario extends AppCompatActivity implements View.OnClickListener {

    //variables globales de inicializacion
    public static String nombreArchivo;
    private Vibrator vibrator;
    SurfaceView lector2;
    TextView correo, confCorreo;
    private static final int RC_SIGN_IN = 123;

    EditText pass1, pass2, Nombre, ApePat, ApeMat;

    public FirebaseUser user;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnConfirmar, btnLimpiar, btnRegistraCorreo;
    String intentData = "";
    //----------------------------------------------------------------
    //********************* conexion firebase **********************************
    private DatabaseReference mDatabase;// Referencia base de datos global
    //**************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]


        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnRegistraCorreo = findViewById(R.id.btnRegistraCorreo);

        Nombre = findViewById(R.id.Nombre);
        ApePat = findViewById(R.id.ApePat);
        ApeMat = findViewById(R.id.ApeMat);

        btnLimpiar.setOnClickListener(this);
        btnRegistraCorreo.setOnClickListener(this);
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

    public void limpieza() {
        Nombre.setText("");
        ApePat.setText("");
        ApeMat.setText("");
    }

    private void agregarRegistro(final String usuario_id, final String nombre, final String apePat, final String apeMat,
                                 final String correo) {

        mDatabase.child("usuarios").child("usuarioss").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuario usuario = new Usuario(usuario_id, nombre, apePat, apeMat, correo);
                        Map<String, Object> vals = usuario.toMap();

                        Map<String, Object> actualizar = new HashMap<>();
                        actualizar.put("/usuarios/" + usuario_id, vals);

                        //Toast.makeText(getApplicationContext(), "Correcto", Toast.LENGTH_SHORT).show();

                        mDatabase.updateChildren(actualizar); //Orden para actualizar firebase con el registro
                        try {
                            Thread.sleep(200);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(NuevoUsuario.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegistraCorreo:
                if (armausuario()) {
                    createSignInIntent();
                }
                break;
            case R.id.btnLimpiar:
                limpieza();
                break;
        }
    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                agregarRegistro(user.getUid(), Nombre.getText().toString(), ApePat.getText().toString(), ApeMat.getText().toString(),
                        user.getEmail());
                // ...
            } else {
                Toast.makeText(this, "Registro de cuenta fallido", Toast.LENGTH_SHORT).show();
            }
        }
        startActivity(new Intent(NuevoUsuario.this, VistaSesion.class));
    }
    // [END auth_fui_result]
}