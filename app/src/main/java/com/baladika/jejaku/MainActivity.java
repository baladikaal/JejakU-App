package com.baladika.jejaku;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.baladika.jejaku.Tambahan.DBHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private CardView maps,riwayat,backup,restore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

        changeColor(R.color.HomeColor);

        verifyStoragePermissions(MainActivity.this);


        maps =  findViewById(R.id.cardMaps);
        riwayat = findViewById(R.id.cardRiwayat);
        backup = findViewById(R.id.cardBackup);
        restore = findViewById(R.id.cardRestore);

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), JejakMaps.class);
                startActivity(intent);
            }
        });

        riwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), JejakRiwayat.class);
                startActivity(intent);
            }
        });

        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Backup data anda?\nFile akan disimpan di "+Environment.getExternalStorageDirectory()+"/JejakuDB");
                    builder.setCancelable(true);
                    //sekalian backup data
                    builder.setNegativeButton("IYA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                //jika folder jejakuDB tidak ada
                                File myDirectory = new File(Environment.getExternalStorageDirectory(), "JejakuDB");
                                if(!myDirectory.exists()) {
                                    myDirectory.mkdirs();
                                    backupDB();
                                }else {
                                    //jika folder jejakuDB ada
                                    backupDB();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.setPositiveButton("TIDAK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Restore data anda?\nData sekarang akan hilang dan mengembalikan data terakhir anda Backup");
                    builder.setCancelable(true);
                    //sekalian backup data
                    builder.setNegativeButton("IYA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
                                //final
                                String inFileName = path+"/JejakuDB/DBjejak.db";
                                File dbFile = new File(inFileName);
                                FileInputStream fis = new FileInputStream(dbFile);

                                String outFileName = "/data/data/com.baladika.jejaku/databases/DBjejak.db";

                                // Open the empty db as the output stream
                                OutputStream output = new FileOutputStream(outFileName);

                                // Transfer bytes from the inputfile to the outputfile
                                byte[] buffer = new byte[1024];
                                int length;

                                while ((length = fis.read(buffer))>0)
                                {
                                    output.write(buffer, 0, length);
                                }

                                Toast.makeText(MainActivity.this, "Restore Successfully", Toast.LENGTH_SHORT).show();
                                // Close the streams
                                output.flush();
                                output.close();
                                fis.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "File tidak ditemukan! \nPastikan anda sudah Backup data anda terlebih dahulu!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setPositiveButton("TIDAK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION

    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    void backupDB(){
        try {
            final String inFileName = "/data/data/com.baladika.jejaku/databases/DBjejak.db";
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Environment.getExternalStorageDirectory()+"/JejakuDB/DBjejak.db";

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);


            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();
            Toast.makeText(getApplication(), "Data telah di backup", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //merubah warna status bar
    public void changeColor(int resourseColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,resourseColor));
        }
    }
}
