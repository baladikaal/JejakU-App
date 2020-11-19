package com.baladika.jejaku;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baladika.jejaku.Tambahan.DBHelper;
import com.baladika.jejaku.Tambahan.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class JejakAddInfo extends AppCompatActivity {

    private EditText nm_lok, ket;
    private TextView Kota,Negara,tgl;
    String lat,lon;
    private Button tambah;
    AlertDialog alertDialog;
    //Variable Untuk Menyimpan Input Dari Ueer
    private String setNamaL, setLat,setLon, setTgl, setKet,setKota,setNegara;
    String updatedCity,updatedCountry;
    //untuk foto
    private ImageView Foto;
    private static final int SELECT_PICTURE = 100;
    private Uri selectedImageUri;
    byte[] inputData;

    //Variable Untuk Inisialisasi Database DBMahasiswa
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jejak_add_info);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.HomeColor));

        nm_lok = findViewById(R.id.etNama);
        tgl = findViewById(R.id.txtTgl);
        ket = findViewById(R.id.etKet);
        Kota = findViewById(R.id.txtKota);
        Negara = findViewById(R.id.txtNegara);
        tambah = findViewById(R.id.btnTambah);
        Foto = findViewById(R.id.etFoto);

        String getLon = getIntent().getStringExtra("setLon");
        final String getLat = getIntent().getStringExtra("setLat");
        //ambil tnggal skrng
        final String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        tgl.setText(currentDate);
        lat = getLat;
        lon = getLon;
        getNegara();
        Kota.setText(updatedCity);
        Negara.setText(updatedCountry);




        //untuk foto
        Foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

        //Inisialisasi dan Mendapatkan Konteks dari DBMahasiswa
        dbHelper = new DBHelper(getBaseContext());

        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cek inputdata apakah null atau tidak?
                if (inputData == null){
                    Toast.makeText(JejakAddInfo.this, "Mohon Masukkan foto terlebih dahulu", Toast.LENGTH_SHORT).show();
                }else {
                    long lengthbmp = inputData.length;
                    //cek foto lokasi, null atau tidak
                    if (selectedImageUri == null) {
                        if (lengthbmp > 2000000){
                            Foto.setImageResource(R.drawable.ic_photo_camera_black_24dp);
                            Toast.makeText(JejakAddInfo.this, "FOTO TIDAK BOLEH DARI 2MB !", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        //cek ukuran foto
                        if (lengthbmp > 2000000){
                            Toast.makeText(JejakAddInfo.this, "FOTO TIDAK BOLEH DARI 2MB, ULANGI", Toast.LENGTH_SHORT).show();
                        }else {
                            setData();
                            try {
                                saveData();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            finish();
                            Toast.makeText(getApplicationContext(),"Lokasi Berhasil Disimpan", Toast.LENGTH_SHORT).show();
                            clearData();
                            Intent intent = new Intent(JejakAddInfo.this, JejakRiwayat.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    //cek besar/size foto tidak boleh lebih dari 2MB
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        inputData = Utils.getBytes(inputStream);
                        long lengthbmp = inputData.length;
                        if (lengthbmp > 2000000){
                            Foto.setImageResource(R.drawable.ic_photo_camera_black_24dp);
                            Toast.makeText(JejakAddInfo.this, "FOTO TIDAK BOLEH DARI 2MB !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                        }else {
                            Foto.setImageURI(selectedImageUri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //Berisi Statement-Statement Untuk Mendapatkan Input Dari User
    private void setData(){

        setNamaL = nm_lok.getText().toString();
        setTgl = tgl.getText().toString();
        setKet = ket.getText().toString();
        setKota = Kota.getText().toString();
        setNegara = Negara.getText().toString();
        setLat = lat;
        setLon = lon;

    }

    //Berisi Statement-Statement Untuk Menyimpan Data Pada Database
    private void saveData() throws IOException {
        //Mendapatkan Repository dengan Mode Menulis
        SQLiteDatabase create = dbHelper.getWritableDatabase();

        //Membuat Map Baru, Yang Berisi Nama Kolom dan Data Yang Ingin Dimasukan
        ContentValues values = new ContentValues();

        //foto
        values.put(DBHelper.MyColumns.foto, inputData);
        //lainnya
        values.put(DBHelper.MyColumns.nm_lokasi, setNamaL);
        values.put(DBHelper.MyColumns.lat, setLat);
        values.put(DBHelper.MyColumns.lon, setLon);
        values.put(DBHelper.MyColumns.tgl, setTgl);
        values.put(DBHelper.MyColumns.ket, setKet);
        values.put(DBHelper.MyColumns.kota, setKota);
        values.put(DBHelper.MyColumns.negara, setNegara);

        //Menambahkan Baris Baru, Berupa Data Yang Sudah Diinputkan pada Kolom didalam Database
        create.insert(DBHelper.MyColumns.TABLE, null, values);
    }

    private void clearData(){
        nm_lok.setText("");
        tgl.setText("");
        ket.setText("");
    }


    //get negara dan provinsi
    void getNegara(){
        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);

        Locale locale = Locale.getDefault();

        try {
            Geocoder geocoder = new Geocoder(JejakAddInfo.this, locale);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            updatedCity = addresses.get(0).getAdminArea();

            if (updatedCity == null) {
                updatedCity = addresses.get(0).getAdminArea();
            }

            updatedCountry = addresses.get(0).getCountryName();


        }catch (Exception e){
            Log.d("INI : ", "error bos");
        }
    }
}
