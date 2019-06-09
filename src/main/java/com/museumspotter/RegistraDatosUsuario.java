package com.museumspotter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.museumspotter.modelos.Museum;
import com.museumspotter.modelos.User;

import java.util.Random;

public class RegistraDatosUsuario extends AppCompatActivity implements View.OnClickListener {


    Button b1;
    FirebaseUser firebaseUser;
    TextView correo;
    Switch estudiante, costo, distancia;
    Spinner museo;
    User user;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registra_datos_usuario);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        correo = findViewById(R.id.emailUser);
        correo.setText(firebaseUser.getEmail());
        estudiante = findViewById(R.id.switchEstudiante);
        costo = findViewById(R.id.switchCosto);
        distancia = findViewById(R.id.switchDistancia);
        museo = findViewById(R.id.spinnerMuseum);
        String[] data= {"General","Arte Moderno", "Interactivo", "Escultural"};

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,data);

        museo.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        b1 = findViewById(R.id.btnCrearPerfil);
        b1.setOnClickListener(this);

        user = new User();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot usuario: dataSnapshot.getChildren()){
                    User usuarioFB = usuario.getValue(User.class);
                    if (usuarioFB.getCorreo().equalsIgnoreCase(firebaseUser.getEmail())){
                        user = usuarioFB;
                    }
                    System.out.println(user.getCorreo());


                }

                setUserData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        Random random = new Random();
        if (user.getId() <= 0){
            int id = random.nextInt(100000000);
            user.setId(id);
        }
        user.setCorreo(firebaseUser.getEmail());
        user.setEstudiante(estudiante.isChecked());
        user.setSoloMuseosGratis(costo.isChecked());
        user.setDistanciaCaminable(distancia.isChecked());
        user.setTagBusqueda(String.valueOf(museo.getSelectedItem()));

        myRef = database.getReference("/users/" + user.getId());
        myRef.setValue(user);

        Intent i = new Intent(RegistraDatosUsuario.this, com.museumspotter.Menu.class);
        startActivity(i);
        finish();

    }

    private void setUserData(){
        estudiante.setChecked(user.isEstudiante());
        costo.setChecked(user.isSoloMuseosGratis());
        distancia.setChecked(user.isDistanciaCaminable());
        museo.setSelection(adapter.getPosition(user.getTagBusqueda()));

    }
}
