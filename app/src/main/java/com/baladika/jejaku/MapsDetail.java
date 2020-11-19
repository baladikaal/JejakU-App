package com.baladika.jejaku;

import androidx.fragment.app.FragmentActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsDetail extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String Lon,Lat, NamLok, Kota, Negara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_detail);

        //agar full sampai status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        //getPosition dan judul
        Lat = getIntent().getExtras().getString("setLat");
        Lon = getIntent().getExtras().getString("setLon");
        NamLok = getIntent().getExtras().getString("setNamLok");
        Kota = getIntent().getExtras().getString("setKota");
        Negara = getIntent().getExtras().getString("setNegara");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng lokasi = new LatLng(Double.parseDouble(Lat), Double.parseDouble(Lon));
        mMap.addMarker(new MarkerOptions()
                .position(lokasi)
                .title(NamLok).snippet(Kota+", "+Negara));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lokasi));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 20));
    }
}
