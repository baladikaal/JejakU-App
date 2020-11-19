package com.baladika.jejaku;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.baladika.jejaku.Tambahan.DBHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Random;

public class MapsALL extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DBHelper dbHelper;
    private ArrayList NamaList;
    private ArrayList LatList;
    private ArrayList LonList;
    private ArrayList KotaList;
    private ArrayList NegaraList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_all);

        //agar full sampai status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        //variabel
        dbHelper = new DBHelper(getBaseContext());
        NamaList = new ArrayList<>();
        LatList = new ArrayList<>();
        LonList = new ArrayList<>();
        KotaList = new ArrayList<>();
        NegaraList = new ArrayList<>();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        SQLiteDatabase ReadData = dbHelper.getReadableDatabase();
        Cursor cursor = ReadData.rawQuery("SELECT * FROM "+ DBHelper.MyColumns.TABLE,null);

        cursor.moveToFirst();//Memulai Cursor pada Posisi Awal

        //Melooping Sesuai Dengan Jumlan Data (Count) pada cursor
        for(int count=0; count < cursor.getCount(); count++){

            cursor.moveToPosition(count);//Berpindah Posisi dari no index 0 hingga no index terakhir
            NamaList.add(cursor.getString(1));//Menambil Data Dari Kolom 0 (NIM)
            LonList.add(cursor.getString(2));
            LatList.add(cursor.getString(3));
            KotaList.add(cursor.getString(6));
            NegaraList.add(cursor.getString(7));
            LatLng markerPosition = new LatLng(Double.parseDouble(String.valueOf(LatList.get(count))),
                    Double.parseDouble(String.valueOf(LonList.get(count))));
            mMap.addMarker(new MarkerOptions()
            .position(markerPosition)
            .title(String.valueOf(NamaList.get(count)))
                    .snippet(String.valueOf(KotaList.get(count))+", "+String.valueOf(NegaraList.get(count)))
            .icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerPosition));
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 10));
        }
    }
}
