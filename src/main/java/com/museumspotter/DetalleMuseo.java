package com.museumspotter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotClient;
import com.google.android.gms.awareness.snapshot.LocationResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.museumspotter.modelos.Museum;

import java.io.InputStream;
import java.net.URL;

public class DetalleMuseo extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    FirebaseDatabase database;
    DatabaseReference myRef;
    Museum museum;
    LatLngBounds.Builder builder;

    RecyclerView recycle;

    TextView nombre, direccion, calificacion, gratis, gratisEstudiantes, gratisDomingo, categoria;
    private static final String TAG = Menu.class.getSimpleName();
    private static final int Request_User_Location_Code = 99;


    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    //LocationRequest locationRequest;
    Location lastLocation;
    private Marker currentLocationMarker;
    LatLng latLng;
    private Location locActual;
    private double latitude, longitude;
    ImageView imagenMuseum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_museo);
        final String idMuseum = getIntent().getStringExtra("id");
        builder = new LatLngBounds.Builder();

        nombre = findViewById(R.id.nombreDetalleMuseum);
        direccion = findViewById(R.id.direccionDetalleMuseum);
        calificacion = findViewById(R.id.calificacionDetalleMuseum);
        gratis = findViewById(R.id.gratisDetalleMuseum);
        gratisEstudiantes = findViewById(R.id.gratisEstudiantesDetalleMuseum);
        gratisDomingo = findViewById(R.id.gratisDomingoDetalleMuseum);
        categoria = findViewById(R.id.categoriaDetalleMuseum);
        imagenMuseum = findViewById(R.id.imagenDetalleMuseum);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        checkUserLocationPermission();
        SnapshotClient snapshotClient = Awareness.getSnapshotClient(getApplicationContext());

        Task<LocationResponse> locationResponseTask = snapshotClient.getLocation();

        locationResponseTask.addOnCompleteListener(new OnCompleteListener<LocationResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationResponse> task) {
                locActual = task.getResult().getLocation();
                onLocationChanged(locActual);


            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDetalle);
        mapFragment.getMapAsync(this);
        museum = new Museum();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("museums");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot museo : dataSnapshot.getChildren()) {
                    Museum museumFB = museo.getValue(Museum.class);
                    if (museumFB.getId() == Integer.parseInt(idMuseum)) {
                        museum = museumFB;
                        new DownLoadImageTask(imagenMuseum).execute(museum.getImagen());
                    }
                    System.out.println(museum.getNombre());


                }

                findMuseum();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        locActual = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        lastLocation = location;
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }


        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Tu ubicación");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        //Añadir el marcador creado a tu nueva ubicación
        currentLocationMarker = mMap.addMarker(markerOptions);

        //Mover la cámara
        //CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(latLng,14);


        builder.include(currentLocationMarker.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Awareness.getSnapshotClient(this).getApi())
                .build();

        googleApiClient.connect();


        System.out.println(googleApiClient.isConnected());
        System.out.println(googleApiClient.hasConnectedApi(Awareness.getSnapshotClient(this).getApi()));
    }

    private void findMuseum() {
        MarkerOptions options = new MarkerOptions();
        options.title(museum.getNombre());
        options.position(new LatLng(museum.getLatitud(), museum.getLongitud()));
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.addMarker(options);

        builder.include(options.getPosition());



        nombre.setText(museum.getNombre());
        direccion.setText(museum.getDireccion());
        calificacion.setText("" + museum.getCalificacion());

        if (museum.isGratis()) {
            gratis.setText("Sí");
        } else {
            gratis.setText("No");
        }
        if (museum.isGratisEstudiantes()) {
            gratisEstudiantes.setText("Sí");
        } else {
            gratisEstudiantes.setText("No");
        }
        if (museum.isGratisDomingo()) {
            gratisDomingo.setText("Sí");
        } else {
            gratisDomingo.setText("No");
        }
        for (String categoriaString : museum.getCategoria()) {
            if (categoriaString != null) {
                categoria.setText(categoria.getText() + categoriaString + "\n");

            }
        }


    }

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        } else {
            return true;
        }
    }

    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return logo;
        }


        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
