package com.museumspotter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.annotations.Nullable;
import com.museumspotter.modelos.Museum;
import com.museumspotter.modelos.RecyclerAdapterMuseum;
import com.museumspotter.modelos.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Menu extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        LocationListener {

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
    private ArrayList<Museum> listaMuseo;
    private RecyclerView recyclerView;
    Toolbar toolbar;
    FirebaseUser firebaseUser;
    User user;

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        recyclerView = findViewById(R.id.recyclerView);
        toolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);
        checkUserLocationPermission();
        SnapshotClient snapshotClient = Awareness.getSnapshotClient(getApplicationContext());

        Task<LocationResponse> locationResponseTask = snapshotClient.getLocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationResponseTask.addOnCompleteListener(new OnCompleteListener<LocationResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationResponse> task) {
                locActual = task.getResult().getLocation();
                onLocationChanged(locActual);



                listaMuseo = new ArrayList<>();
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                FirebaseDatabase database = FirebaseDatabase.getInstance();


                DatabaseReference myRef = database.getReference("users");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userFB : dataSnapshot.getChildren()) {
                            User usuarioFB = userFB.getValue(User.class);

                            if (usuarioFB.getCorreo().equalsIgnoreCase(firebaseUser.getEmail())) {
                                user = usuarioFB;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                myRef = database.getReference("museums");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot museo : dataSnapshot.getChildren()) {
                            Museum museum = museo.getValue(Museum.class);
                            listaMuseo.add(museum);
                            System.out.println(museum.getNombre());
                        }
                        findMuseums();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        });


    }

    @Override
    public void onClick(View v) {

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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));


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

    private void findMuseums() {

        List<Museum> listaNueva = new ArrayList();
        Collections.sort(listaMuseo, new Comparator<Museum>() {
            @Override
            public int compare(Museum o1, Museum o2) {
                if (o1.getCalificacion() > o2.getCalificacion()) return -1;
                if (o1.getCalificacion() < o2.getCalificacion()) return 1;
                return 0;
            }
        });

        //Date today = new Date();


        Calendar calendar = Calendar.getInstance();
        //calendar.setTime(today);


        int day = calendar.get(Calendar.DAY_OF_WEEK);
        boolean isDomingo = false;
        if (day == Calendar.SUNDAY) {
            isDomingo = true;
        }

        for (Museum museum : listaMuseo) {
            boolean agregar = false;
            double distancia = 0.0;
            distancia = distance(locActual.getLatitude(), museum.getLatitud(),
                    locActual.getLongitude(), museum.getLongitud(), 0, 0);
            if (user.isDistanciaCaminable()) {

                if (distancia > 1600) {
                    agregar = false;
                } else {
                    if (user.isSoloMuseosGratis()) {
                        if (museum.isGratis()) {

                            for (String categoria : museum.getCategoria()) {
                                if (categoria != null) {
                                    if (categoria.equalsIgnoreCase(user.getTagBusqueda())) {
                                        agregar = true;
                                    }
                                }
                            }
                        } else {
                            if (museum.isGratisDomingo() && isDomingo) {
                                for (String categoria : museum.getCategoria()) {
                                    if (categoria != null) {
                                        if (categoria.equalsIgnoreCase(user.getTagBusqueda())) {
                                            agregar = true;
                                        }
                                    }
                                }
                            } else if (museum.isGratisEstudiantes() && user.isEstudiante()) {
                                for (String categoria : museum.getCategoria()) {
                                    if (categoria != null) {
                                        if (categoria.equalsIgnoreCase(user.getTagBusqueda())) {
                                            agregar = true;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for (String categoria : museum.getCategoria()) {
                            if (categoria != null) {
                                if (categoria.equalsIgnoreCase(user.getTagBusqueda())) {
                                    agregar = true;
                                }
                            }
                        }
                    }
                }
            } else if (distancia < 16000) {
                if (user.isSoloMuseosGratis()) {
                    if (museum.isGratis()) {

                        for (String categoria : museum.getCategoria()) {
                            if (categoria != null) {
                                if (categoria.equalsIgnoreCase(user.getTagBusqueda())) {
                                    agregar = true;
                                }
                            }
                        }
                    } else {
                        if (museum.isGratisDomingo() && isDomingo) {
                            for (String categoria : museum.getCategoria()) {
                                if (categoria != null) {
                                    if (categoria.equalsIgnoreCase(user.getTagBusqueda())) {
                                        agregar = true;
                                    }
                                }
                            }
                        } else if (museum.isGratisEstudiantes() && user.isEstudiante()) {
                            for (String categoria : museum.getCategoria()) {
                                if (categoria != null) {
                                    if (categoria.equalsIgnoreCase(user.getTagBusqueda())) {
                                        agregar = true;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (String categoria : museum.getCategoria()) {
                        if (categoria != null) {
                            if (categoria.equalsIgnoreCase(user.getTagBusqueda())) {
                                agregar = true;
                            }
                        }
                    }
                }
            }


            if (agregar) {
                listaNueva.add(museum);
            }
        }
        List<MarkerOptions> markers = new ArrayList<>();
        for (Museum museum : listaNueva) {


            MarkerOptions options = new MarkerOptions();
            options.title(museum.getNombre());
            options.position(new LatLng(museum.getLatitud(), museum.getLongitud()));
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));


            markers.add(options);
            mMap.addMarker(options);
        }
        if (markers.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (MarkerOptions m : markers) {
                builder.include(m.getPosition());
            }
            builder.include(currentLocationMarker.getPosition());

            LatLngBounds bounds = builder.build();

            int padding = 50; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mMap.animateCamera(cu);
        }

        if (listaNueva.size() > 0){
            RecyclerAdapterMuseum recyclerAdapterMuseum = new RecyclerAdapterMuseum(listaNueva, this);
            RecyclerView.LayoutManager recycler = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(recycler);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(recyclerAdapterMuseum);
        } else{
            TextView error = findViewById(R.id.error);
            error.setText("No pudimos encontrar museos para tu perfil :(" +
                    "\nPuedes cambiar tu perfil para encontrar diferentes museos");
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(Menu.this, RegistraDatosUsuario.class);
                startActivity(i);

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private double distance(double lat1, double lat2, double lon1,
                            double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
