package com.example.sedesem.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sedesem.BaseDatos.Usuario;
import com.example.sedesem.MainActivity;
import com.example.sedesem.R;
import com.example.sedesem.principal;
import com.example.sedesem.ui.login.LoginViewModel;
import com.example.sedesem.ui.login.LoginViewModelFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";
    private DatabaseReference mDatabase;

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText emailForgotten;
    private EditText mNombre;
    private EditText mApePat;
    private EditText mApeMat;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);

        // Views
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        emailForgotten = findViewById(R.id.txtEmailForgot);
        mNombre = findViewById(R.id.txtNombreAuth);
        mApePat = findViewById(R.id.txtApePatAuth);
        mApeMat = findViewById(R.id.txtApeMatAuth);

        //setProgressBar(R.id.progressBar);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.verifyEmailButton).setOnClickListener(this);
        findViewById(R.id.btnForgot).setOnClickListener(this);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        //showProgressBar();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "No se pudo registrar una nueva cuenta con ese correo, intente nuevamente",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        //hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        //showProgressBar();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "No se encuentra un usuario registrado con ese correo, intente nuevamente",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        //hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verifyEmailButton).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verifyEmailButton).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Email de verificación enviado a: " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "Fallo el envío de correo de verificación",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    public void sendPasswordReset(String email) {
        // [START send_password_reset]
        FirebaseAuth auth = FirebaseAuth.getInstance();

        final String emailAddress = email;

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(LoginActivity.this,
                                    "El correo de recuperación de contraseña ha sido enviado a" + emailAddress,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "No se pudo realizar el envío, verifique de nuevo",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END send_password_reset]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Ingresa un correo");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Requerido");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private boolean validateName() {
        boolean valid = true;
        String nombre = mNombre.getText().toString();
        if (TextUtils.isEmpty(nombre)) {
            mNombre.setError("Ingresa un correo");
            valid = false;
        } else {
            mNombre.setError(null);
        }

        String apePat = mApePat.getText().toString();
        if (TextUtils.isEmpty(apePat)) {
            mApePat.setError("Ingresa un correo");
            valid = false;
        } else {
            mApePat.setError(null);
        }

        String apeMat = mApeMat.getText().toString();
        if (TextUtils.isEmpty(apeMat)) {
            mApeMat.setError("Ingresa un correo");
            valid = false;
        } else {
            mApeMat.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        //hideProgressBar();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            //mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            if (user.isEmailVerified()) {
                startActivity(new Intent(LoginActivity.this, principal.class));
            }

            findViewById(R.id.btnForgot).setVisibility(View.GONE);
            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
            findViewById(R.id.idVistaNombres).setVisibility(View.VISIBLE);
            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
            findViewById(R.id.signedInButtons).setVisibility(View.GONE);
            findViewById(R.id.idVistaNombres).setVisibility(View.GONE);
        }


    }

    public void dialogoEmail() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pwd, null);

        final EditText email = dialogView.findViewById(R.id.txtEmailForgot);
        Button btnConfirmar = dialogView.findViewById(R.id.btnSend);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancel);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                Log.w("EMIAL", email.getText().toString());
                sendPasswordReset(email.getText().toString());
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    @Override
    public void onClick(View v) {
        final String fecha = new SimpleDateFormat("yyyyMMdd").format(new Date());
        final FirebaseUser user = mAuth.getCurrentUser();
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.emailSignInButton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.signOutButton) {
            signOut();
        } else if (i == R.id.verifyEmailButton) {
            if (!validateName()) {
                return;
            }
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(mNombre.getText().toString() + " " + mApePat.getText().toString())
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                            }
                        }
                    });
            agregarUsuario(mNombre.getText().toString(), mApePat.getText().toString(), mApeMat.getText().toString()
                    , fecha, user.getUid(), user.getEmail());
            sendEmailVerification();
            signOut();
        } else if (i == R.id.btnForgot) {
            dialogoEmail();
        }
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
                        Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
