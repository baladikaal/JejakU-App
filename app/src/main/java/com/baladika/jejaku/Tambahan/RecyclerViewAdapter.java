package com.baladika.jejaku.Tambahan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.baladika.jejaku.JejakDetail;
import com.baladika.jejaku.JejakUpdate;
import com.baladika.jejaku.R;

import java.sql.Blob;
import java.util.ArrayList;

//Class Adapter ini Digunakan Untuk Mengatur Bagaimana Data akan Ditampilkan
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList namaList;
    private ArrayList idList;
    private ArrayList tglList;
    private ArrayList ketList;
    private ArrayList FotoList;
    private Context context; //Membuat Variable Context

    //Membuat Konstruktor pada Class RecyclerViewAdapter
    public RecyclerViewAdapter(ArrayList idList,ArrayList namaList, ArrayList tglList, ArrayList ketList, ArrayList FotoList){
        this.idList = idList;
        this.namaList = namaList;
        this.tglList = tglList;
        this.ketList = ketList;
        this.FotoList = FotoList;
    }

    //ViewHolder Digunakan Untuk Menyimpan Referensi Dari View-View
    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView Id, Nama, Tgl, Ket;
        private ImageView Foto;
        private ImageButton Overflow;
        private RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            //Mendapatkan Context dari itemView yang terhubung dengan Activity ViewData
            context = itemView.getContext();

            //Menginisialisasi View-View untuk kita gunakan pada RecyclerView
            Id = itemView.findViewById(R.id.idRYT);
            Nama = itemView.findViewById(R.id.namaRYT);
            Tgl = itemView.findViewById(R.id.tglRYT);
            Ket = itemView.findViewById(R.id.ketRYT);
            Foto = itemView.findViewById(R.id.fotoRYT);
            Overflow = itemView.findViewById(R.id.overflow);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Membuat View untuk Menyiapkan dan Memasang Layout yang Akan digunakan pada RecyclerView
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_design, parent, false);
        return new ViewHolder(V);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        //Memanggil Nilai/Value Pada View-View Yang Telah Dibuat pada Posisi Tertentu
        final String Id = (String) idList.get(position);
        final String Nama = (String) namaList.get(position);//Mengambil data (Nama) sesuai dengan posisi yang telah ditentukan
        final String Tgl = (String) tglList.get(position);//Mengambil data (Jurusan) sesuai dengan posisi yang telah ditentukan
        final String Ket = (String) ketList.get(position);//Mengambil data (NIM) sesuai dengan posisi yang telah ditentukan
        final byte[] Foto = (byte[]) FotoList.get(position);
        holder.Nama.setText(Nama);
        holder.Tgl.setText(Tgl);
        holder.Ket.setText(Ket);
        holder.Id.setText(Id);
//        holder.Foto.setImageBitmap(Utils.getImage(Foto));
        holder.Foto.post(new Runnable() {
            @Override
            public void run() {
//                if (holder.Foto.getResources().equals(null))
                holder.Foto.setImageBitmap(Utils.getImage(Foto));

            }
        });


        //RV ketika di klik dan menuju ke jejak detail
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, JejakDetail.class);
                intent.putExtra("SendId", holder.Id.getText().toString());
                context.startActivity(intent);
            }
        });


        //Mengimplementasikan Menu Popup pada Overflow (ImageButton)
        holder.Overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Membuat Instance/Objek dari PopupMenu
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.pop_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:
//                                Toast.makeText(context,"ini delete", Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setMessage("Yakin ingin menghapus data? \nJika Anda menghapus data, maka data dihapus " +
                                        "secara permanen!");
                                builder.setCancelable(true);
                                builder.setNegativeButton("YAKIN !", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Menghapus Data Dari Database
                                        DBHelper getDatabase = new DBHelper(view.getContext());
                                        SQLiteDatabase DeleteData = getDatabase.getWritableDatabase();
                                        //Menentukan di mana bagian kueri yang akan dipilih
                                        String selection = DBHelper.MyColumns.id + " LIKE ?";
                                        //Menentukan Nama Dari Data Yang Ingin Dihapus
                                        String[] selectionArgs = {holder.Id.getText().toString()};
                                        DeleteData.delete(DBHelper.MyColumns.TABLE, selection, selectionArgs);

                                        //Menghapus Data pada List dari Posisi Tertentu
                                        int position = idList.indexOf(Id);
                                        idList.remove(position);
                                        notifyItemRemoved(position);
                                        Toast.makeText(view.getContext(),"Data Dihapus",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.setPositiveButton("Nggak deh..", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                break;

                            case R.id.update:
//                                Toast.makeText(context,"ini update", Toast.LENGTH_SHORT).show();
                                Intent dataForm = new Intent(view.getContext(), JejakUpdate.class);
                                dataForm.putExtra("SendId", holder.Id.getText().toString());
                                dataForm.putExtra("SendNama", holder.Nama.getText().toString());
                                context.startActivity(dataForm);
                                ((Activity)context).finish();
                                //Statement Update
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }




    @Override
    public int getItemCount() {
        //Menghitung Ukuran/Jumlah Data Yang Akan Ditampilkan Pada RecyclerView
        return idList.size();
    }


}
