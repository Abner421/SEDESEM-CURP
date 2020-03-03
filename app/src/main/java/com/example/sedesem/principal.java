package com.example.sedesem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.sedesem.BaseDatos.Persona;
import com.example.sedesem.BaseDatos.Usuario;
import com.example.sedesem.BaseDatos.conexionFirebase;
import com.example.sedesem.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class principal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference mDatabase;

    private TextView nombreCuenta;
    private TextView correoCuenta;

    private EditText mNombreAuth;
    private EditText mApePatAuth;
    private EditText mApeMatAuth;

    // [START initialize_auth]

    // [START declare_auth]
    private FirebaseAuth mAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_registros, R.id.nav_stats,
                R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        //NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.getMenu().getItem(0).setChecked(true);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        if (user.getDisplayName().equals("")) {
            dialogNombre();
        } else {
            Log.d("Existe", "Si existe");
        }

        /*setContentView(R.layout.dialog_nombre);
        mNombreAuth = findViewById(R.id.txtNombreAuth);
        mApePatAuth = findViewById(R.id.txtApePatAuth);
        mApeMatAuth = findViewById(R.id.txtApeMatAuth);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu); //Este es el menú de arriba
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Este método obtiene los identificadores del menú superior
        switch (item.getItemId()) {
            case R.id.nav_registros:
                Toast.makeText(this, "Registros fue seleccionado", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_stats:
                Toast.makeText(this, "Estadísticas fue seleccionado", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) { //Este método obtiene los identificadores del menú lateral (navigation drawer)
        switch (menuItem.getItemId()) {
            case R.id.nav_registros:
                Intent i = new Intent(principal.this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.nav_stats:
                Toast.makeText(this, "Estadísticas fue seleccionado", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                signOut();
                startActivity(new Intent(principal.this, LoginActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void signOut() {
        mAuth.signOut();
        Toast.makeText(this, "Has cerrado sesión correctamente", Toast.LENGTH_SHORT).show();
    }

    public void dialogNombre() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_nombre, null);

        final String fecha = new SimpleDateFormat("yyyyMMdd").format(new Date());

        final EditText nombre = dialogView.findViewById(R.id.txtNombreAuth);
        final EditText apePat = dialogView.findViewById(R.id.txtApePatAuth);
        final EditText apeMat = dialogView.findViewById(R.id.txtApeMatAuth);

        Button btnConfirmar = dialogView.findViewById(R.id.btnConfirmarAuth);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Función para actualizar registro Firebase Auth
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(nombre.getText().toString() + " " + apePat.getText().toString())
                        .build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialogBuilder.dismiss();
                                    Log.e("Actualización", "User profile updated.");
                                }
                            }
                        });
                //-----------------------------------
                agregarUsuario(nombre.getText().toString(), apePat.getText().toString(), apeMat.getText().toString()
                        , fecha, user.getUid(), user.getEmail());

            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void agregarUsuario(final String nombre, final String apePat, final String apeMat,
                                final String fecha, final String uid, final String correo) {
        mDatabase.child("registros").child("usuarios").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuario usuario = new Usuario(uid, apePat, apeMat, nombre, correo, fecha);
                        Map<String, Object> vals = usuario.toMap();

                        Map<String, Object> actualizar = new HashMap<>();
                        actualizar.put("/usuarios/" + uid, vals);

                        Toast.makeText(getApplicationContext(), "Correcto", Toast.LENGTH_SHORT).show();

                        mDatabase.updateChildren(actualizar); //Orden para actualizar firebase con el registro
                        try {
                            Thread.sleep(200);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.e("Registro", "Registro correcto");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(principal.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void updateUI(FirebaseUser user) {
        nombreCuenta = findViewById(R.id.nombreCuenta);
        correoCuenta = findViewById(R.id.correoCuenta);

        nombreCuenta.setText(user.getDisplayName());
        correoCuenta.setText(user.getEmail());
    }

    private boolean checaUsuario(FirebaseUser usr) {
        return usr.getDisplayName() != "";
    }

    private boolean validateForm() {
        boolean valid = true;

        String nombre = mNombreAuth.getText().toString();
        String apePat = mApePatAuth.getText().toString();
        String apeMat = mApeMatAuth.getText().toString();
        if (TextUtils.isEmpty(nombre)) {
            mNombreAuth.setError("Ingresa tu nombre");
            valid = false;
        } else {
            mNombreAuth.setError(null);
        }

        if (TextUtils.isEmpty(apePat)) {
            mApePatAuth.setError("Ingresa tu apellido");
            valid = false;
        } else {
            mApePatAuth.setError(null);
        }

        if (TextUtils.isEmpty(apeMat)) {
            mApeMatAuth.setError("Ingresa tu apellido");
            valid = false;
        } else {
            mApeMatAuth.setError(null);
        }

        return valid;
    }
}
