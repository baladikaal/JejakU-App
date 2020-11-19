package com.baladika.jejaku;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.baladika.jejaku.Tambahan.DBHelper;
import com.baladika.jejaku.Tambahan.Utils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.juliomarcos.ImageViewPopUpHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class JejakDetail extends AppCompatActivity {

    private String Id;
    private DBHelper dbHelper;
    private TextView Judul,Isi,Tgl,Kota,Negara;
    private Button btnMaps;
    private String lon, lat;
    private ImageView foto;
    private byte[] blob;
    OutputStream outputStream;
    FloatingActionButton fab;
    File dir;
    Bitmap bitmap;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jejak_detail);


        //agar foto full sampai status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        loading = ProgressDialog.show(JejakDetail.this,
                "",
                "Please wait...",
                true,
                false);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

        Toolbar toolbar = (Toolbar) findViewById(R.id.detailTollbar);
        setSupportActionBar(toolbar);
//        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }else {
        }

        dbHelper = new DBHelper(getBaseContext());
        Id = getIntent().getExtras().getString("SendId");
        Judul = findViewById(R.id.detailJudul);
        Isi = findViewById(R.id.detailIsi);
        Tgl = findViewById(R.id.detailTgl);
        Kota = findViewById(R.id.detailKota);
        foto = findViewById(R.id.detailFoto);
        Negara = findViewById(R.id.detailNegara);
        btnMaps = findViewById(R.id.BtnMapDetail);
        fab = findViewById(R.id.floatSave);


        SQLiteDatabase ReadData = dbHelper.getReadableDatabase();
        Cursor c = ReadData.rawQuery("SELECT * FROM "+ DBHelper.MyColumns.TABLE + " WHERE id = '" +Id.trim()+"'", null);
        if (c.moveToFirst()){
            do {
                loading.dismiss();
                // Passing values
                Judul.setText(c.getString(c.getColumnIndex(DBHelper.MyColumns.nm_lokasi)));
                Isi.setText(c.getString(c.getColumnIndex(DBHelper.MyColumns.ket)));
                Tgl.setText(c.getString(c.getColumnIndex(DBHelper.MyColumns.tgl)));
                Kota.setText(c.getString(c.getColumnIndex(DBHelper.MyColumns.kota)));
                Negara.setText(c.getString(c.getColumnIndex(DBHelper.MyColumns.negara)));
                lon = c.getString(c.getColumnIndex(DBHelper.MyColumns.lon));
                lat = c.getString(c.getColumnIndex(DBHelper.MyColumns.lat));
                blob = c.getBlob(c.getColumnIndex(DBHelper.MyColumns.foto));
//                loading.dismiss();

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
                }catch (Exception e){
                    Log.d("INI ", "error "+e);
                }
            }
        });

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageViewPopUpHelper.enablePopUpOnClick(JejakDetail.this, foto);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = (BitmapDrawable) foto.getDrawable();
                bitmap = drawable.getBitmap();
                File file = Environment.getExternalStorageDirectory();
                dir = new File(file.getAbsolutePath()+"/JejakuDB/JejakuImage/");
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(JejakDetail.this);
                builder.setMessage("Download Foto?\nFoto akan disimpan di pada folder "+dir);
                builder.setCancelable(true);
                //download foto
                builder.setNegativeButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            dir.mkdirs();
                            File filE = new File(dir, System.currentTimeMillis()+".jpg");
                            try {
                                outputStream = new FileOutputStream(filE);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            Toast.makeText(getApplicationContext(), "Foto telah di download!", Toast.LENGTH_LONG).show();
                            outputStream.flush();
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                          }
                    }
                });
                builder.setPositiveButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });




        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JejakDetail.this, MapsDetail.class);
                intent.putExtra("setLat", lat);
                intent.putExtra("setLon", lon);
                intent.putExtra("setNamLok", Judul.getText());
                intent.putExtra("setKota", Kota.getText());
                intent.putExtra("setNegara", Negara.getText());
                startActivity(intent);
            }
        });
    }


    //fullscren foto
    public void fullScreen() {

    }

}
