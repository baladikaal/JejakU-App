package com.baladika.jejaku.Tambahan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.provider.BaseColumns;

public class DBHelper extends SQLiteOpenHelper {

    //InnerClass, untuk mengatur artibut seperti Nama Tabel, nama-nama kolom dan Query
    public static abstract class MyColumns implements BaseColumns {
        //Menentukan Nama Table dan Kolom
        public static final String TABLE = "riwayat";
        public static final String id = "id";
        public static final String nm_lokasi = "nama_lokasi";
        public static final String lon = "longitude";
        public static final String lat = "latitude";
        public static final String tgl = "tgl";
        public static final String ket = "ket";
        public static final String kota = "kota";
        public static final String negara = "negara";
        public static final String foto = "foto";

    }

    private static final String DB_NAME = "DBjejak.db";
    private static final int VERSION = 1;

    //Query yang digunakan untuk membuat Tabel
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "+MyColumns.TABLE+
            "("+MyColumns.id+" INTEGER PRIMARY KEY AUTOINCREMENT, "+MyColumns.nm_lokasi+" TEXT, "+MyColumns.lon+
            " TEXT NOT NULL, "+MyColumns.lat+" TEXT NOT NULL, "+MyColumns.tgl+
            " TEXT, "+MyColumns.ket+" TEXT, "+MyColumns.kota+" TEXT" +
            ", "+MyColumns.negara+" TEXT, "+MyColumns.foto+" BLOB)";

    //Query yang digunakan untuk mengupgrade Tabel
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "+MyColumns.TABLE;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}
