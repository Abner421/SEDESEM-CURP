package com.example.sedesem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sedesem.BaseDatos.Persona;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class regsFire extends AppCompatActivity implements View.OnClickListener {


    DatabaseReference mRootReference;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regs_fire);

        mRootReference = FirebaseDatabase.getInstance().getReference("registros");

        solicitarDatosBase();
    }

    private void solicitarDatosBase() {

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final ArrayList<String> curps = new ArrayList<>();
        final ListView registrosFirebase = findViewById(R.id.registrosFirebase);

        /*final ListView registrosFirebase = findViewById(R.id.registrosFirebase);
        final ArrayList<String> curps = new ArrayList<>();
        mRootReference.child("registros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Agrega las curps al arreglo para mostrarlas posteriormente
                    //curps.add(snapshot.getKey());
                    Query stmt = mRootReference.orderByKey().startAt(user.getUid());
                            curps.add(stmt);

                }
                ArrayAdapter adaptar = new ArrayAdapter(regsFire.this, android.R.layout.simple_list_item_1, curps);
                registrosFirebase.setAdapter(adaptar);
            }*/
        mRootReference.orderByChild("uidUsuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot stegoHeightSnapshot) {
                Query query = mRootReference.orderByKey().startAt(user.getUid());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Data is ordered by increasing height, so we want the first entry
                        //DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            curps.add(snapshot.getKey());
                            Log.wtf("wtf", snapshot.getKey());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // ...
                        Log.wtf("wtf", "no srive");
                    }
                });
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        //Adaptador para mostrar los registros presentes en la base de datos
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, curps);
        registrosFirebase.setAdapter(adaptador);
    }

    @Override
    public void onClick(View v) {

    }

}
