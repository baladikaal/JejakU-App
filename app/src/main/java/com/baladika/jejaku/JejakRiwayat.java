package com.baladika.jejaku;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baladika.jejaku.Tambahan.DBHelper;
import com.baladika.jejaku.Tambahan.RecyclerViewAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class JejakRiwayat extends AppCompatActivity {

    private DBHelper dbHelper;
    private TextView TxtKlik;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList idList;
    private ArrayList NamaList;
    private ArrayList TglList;
    private ArrayList KetList;
    private ArrayList FotoList;
    private ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jejak_riwayat);

        loading = ProgressDialog.show(JejakRiwayat.this,
                "",
                "Please wait...",
                true,
                false);

        idList = new ArrayList<>();
        NamaList = new ArrayList<>();
        TglList = new ArrayList<>();
        KetList = new ArrayList<>();
        FotoList = new ArrayList<>();
        dbHelper = new DBHelper(getBaseContext());
        recyclerView = findViewById(R.id.recycler);
        TxtKlik = findViewById(R.id.TxtKlik);
        try {
            getData();
        }catch (Exception e){
            Toast.makeText(JejakRiwayat.this, "Terjadi Kesalahan data, Hubungi admin : baladikaal@gmail.com", Toast.LENGTH_LONG).show();
        }
        //Menggunakan Layout Manager, Dan Membuat List Secara Vertical
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new RecyclerViewAdapter(idList, NamaList, TglList, KetList, FotoList);
        //Memasang Adapter pada RecyclerView
        recyclerView.setAdapter(adapter);
        //Membuat Underline pada Setiap Item Didalam List
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.line));
        recyclerView.addItemDecoration(itemDecoration);

        TxtKlik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(JejakRiwayat.this, "Berhasil", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(JejakRiwayat.this, MapsALL.class);
                startActivity(intent);
            }
        });

//        ActionBar bar = getSupportActionBar();
//        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.HomeColor)));
//        bar.setTitle(Html.fromHtml("<font color='#000'>Daftar Lokasi</font>"));
        changeColor(R.color.HomeColor);
    }

    //Berisi Statement-Statement Untuk Mengambi Data dari Database
    @SuppressLint("Recycle")
    protected void getData(){
        //Mengambil Repository dengan Mode Membaca
        SQLiteDatabase ReadData = dbHelper.getReadableDatabase();
        Cursor cursor = ReadData.rawQuery("SELECT * FROM "+ DBHelper.MyColumns.TABLE,null);

        cursor.moveToFirst();//Memulai Cursor pada Posisi Awal

        //Melooping Sesuai Dengan Jumlan Data (Count) pada cursor
        for(int count=0; count < cursor.getCount(); count++){
            loading.dismiss();
            cursor.moveToPosition(count);//Berpindah Posisi dari no index 0 hingga no index terakhir
            idList.add(cursor.getString(0));
            NamaList.add(cursor.getString(1));//Menambil Data Dari Kolom 0 (NIM)
            TglList.add(cursor.getString(4));//Menambil Data Dari Kolom 1 (Nama)
            KetList.add(cursor.getString(5));//Menambil Data Dari Kolom 2 (Jurusan)
            FotoList.add(cursor.getBlob(8));
        }
    }

    //merubah warna status bar
    public void changeColor(int resourseColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,resourseColor));
        }
    }
}
