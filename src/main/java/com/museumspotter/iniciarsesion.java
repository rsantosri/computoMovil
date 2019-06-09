package com.museumspotter;

import android.content.Intent;

import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class iniciarsesion extends AppCompatActivity implements View.OnClickListener {

    Button b1;
    FirebaseAuth mAuth;
    EditText username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciarsesion);
        mAuth = FirebaseAuth.getInstance();
        b1 = findViewById(R.id.login);
        b1.setOnClickListener(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

    }


    @Override
    public void onClick(View v) {
        iniciarSesionORegistrar();
    }

    private void iniciarSesionORegistrar() {
        final String usuario = username.getText().toString().trim();
        String contrasena = password.getText().toString().trim();

        if (usuario.isEmpty()) {
            username.setError("Email Required");
            username.requestFocus();
            return;
        }


        if (contrasena.isEmpty()) {
            password.setError("Password Required");
            password.requestFocus();
            return;
        }

        if (contrasena.length() < 6) {
            password.setError("Password is shorter than 6 characters");
            password.requestFocus();
            return;
        }

        registrarUsuario();

    }

    private void registrarUsuario(){
        String usuario = username.getText().toString().trim();
        String contrasena = password.getText().toString().trim();

        try{
            mAuth.createUserWithEmailAndPassword(usuario, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Usuario registrado", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(iniciarsesion.this, RegistraDatosUsuario.class);
                        startActivity(i);
                        finish();
                    }else{
                        iniciaSesion();
                    }
                }
            });


        } catch (Exception ex){
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void iniciaSesion(){
        String usuario = username.getText().toString().trim();
        String contrasena = password.getText().toString().trim();
        try {
                mAuth.signInWithEmailAndPassword(usuario, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent i = new Intent(iniciarsesion.this, com.museumspotter.Menu.class);
                            startActivity(i);
                            finish();


                        } else {

                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        } catch(Exception ex){
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
