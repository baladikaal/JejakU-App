package com.baladika.jejaku;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baladika.jejaku.Tambahan.DBHelper;
import com.baladika.jejaku.Tambahan.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class JejakUpdate extends AppCompatActivity {

    private String Id;
    private DBHelper dbHelper;
    private EditText nm_lok,  tgl, ket, kota, negara;
    private TextView lat, lon;
    private String getId, getNamaL, getLat, getLon, getTgl, getKet, getKota, getNegara;
    private Button Update;
    //untuk foto
    private ImageView foto;
    private static final int SELECT_PICTURE = 100;
    private Uri selectedImageUri;
    private byte[] blob;
    byte[] inputData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jejak_update);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.HomeColor));

//        getSupportActionBar().setTitle("Masukan Data Baru");
        dbHelper = new DBHelper(getBaseContext());
        nm_lok = findViewById(R.id.etNamaUp);
        lat = findViewById(R.id.etLatUp);
        lon = findViewById(R.id.etLotUp);
        tgl = findViewById(R.id.etTglUp);
        ket = findViewById(R.id.etKetUp);
        kota = findViewById(R.id.etKotaUp);
        negara = findViewById(R.id.etNegaraUp);
        foto = findViewById(R.id.etFotoUp);

        //Menerima Data Nama dan NIM yang telah dipilih Oleh User untuk diposes
        nm_lok.setText(getIntent().getExtras().getString("SendNama"));
        Id = getIntent().getExtras().getString("SendId");




        SQLiteDatabase ReadData = dbHelper.getReadableDatabase();
        Cursor c = ReadData.rawQuery("SELECT * FROM "+ DBHelper.MyColumns.TABLE + " WHERE id = '" +Id.trim()+"'", null);
        if (c.moveToFirst()){
            do {
                // Passing values
                lat.setText(c.getString(c.getColumnIndex("latitude")));
                lon.setText(c.getString(c.getColumnIndex("longitude")));
                tgl.setText(c.getString(c.getColumnIndex("tgl")));
                ket.setText(c.getString(c.getColumnIndex("ket")));
                kota.setText(c.getString(c.getColumnIndex(DBHelper.MyColumns.kota)));
                negara.setText(c.getString(c.getColumnIndex(DBHelper.MyColumns.negara)));
                blob = c.getBlob(c.getColumnIndex(DBHelper.MyColumns.foto));
                // Do something Here with values
            } while(c.moveToNext());
        }
        c.close();
        ReadData.close();



        foto.post(new Runnable() {
            @Override
            public void run() {
                try {
                    foto.setImageBitmap(Utils.getImage(blob));
                    Log.d("ini bos",""+blob);
                }catch (Exception e){
                    Log.d("INI ", "error "+e);
                }
            }
        });

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });


        Update = findViewById(R.id.btnUpdate);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String Foto = String.valueOf(foto.getResources());
//                Toast.makeText(JejakUpdate.this, ""+foto, Toast.LENGTH_SHORT).show();
                if (selectedImageUri == null){
                    Bitmap foto12 = Utils.getImage(blob);
                    inputData = Utils.getImageBytes(foto12);
                    setUpdateData();
                    startActivity(new Intent(JejakUpdate.this, JejakRiwayat.class));
                    finish();
                }else {
                    try {
                        //foto
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        inputData = Utils.getBytes(inputStream);
                        setUpdateData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(JejakUpdate.this, JejakRiwayat.class));
                    finish();
                }
            }
        });


    }

    private void setUpdateData() {
        getId = Id.toString();
        getNamaL = nm_lok.getText().toString();
        getLat = lat.getText().toString();
        getLon = lon.getText().toString();
        getTgl = tgl.getText().toString();
        getKet = ket.getText().toString();
        getKota = kota.getText().toString();
        getNegara = negara.getText().toString();


        SQLiteDatabase database = dbHelper.getReadableDatabase();

        //Memasukan Data baru pada 3 kolom (NIM, Nama dan Jurusan)
        ContentValues values = new ContentValues();


        values.put(DBHelper.MyColumns.foto, inputData);

        values.put(DBHelper.MyColumns.id, getId);
        values.put(DBHelper.MyColumns.nm_lokasi, getNamaL);
        values.put(DBHelper.MyColumns.lat, getLat);
        values.put(DBHelper.MyColumns.lon, getLon);
        values.put(DBHelper.MyColumns.tgl, getTgl);
        values.put(DBHelper.MyColumns.ket, getKet);
        values.put(DBHelper.MyColumns.kota, getKota);
        values.put(DBHelper.MyColumns.negara, getNegara);

        //Untuk Menentukan Data/Item yang ingin diubah, berdasarkan NIM
        String selection = DBHelper.MyColumns.id + " LIKE ?";
        String[] selectionArgs = {Id};
        database.update(DBHelper.MyColumns.TABLE, values, selection, selectionArgs);
        Toast.makeText(getApplicationContext(), "Berhasil Diubah", Toast.LENGTH_SHORT).show();
    }

    //foto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        inputData = Utils.getBytes(inputStream);
                        long lengthbmp = inputData.length;
                        if (lengthbmp > 2000000){
                            foto.setImageResource(R.drawable.ic_photo_camera_black_24dp);
                            Toast.makeText(JejakUpdate.this, "FOTO TIDAK BOLEH DARI 2MB !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                        }else {
                            foto.setImageURI(selectedImageUri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}