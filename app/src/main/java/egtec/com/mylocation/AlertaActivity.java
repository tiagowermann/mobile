package egtec.com.mylocation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import egtec.com.mylocation.Model.Alerta;

public class AlertaActivity extends AppCompatActivity {

    final int CAMERA_PIC_REQUEST = 301;
    final static int PERMISSION_REQUEST_CODE = 302;

    Button btnFoto;
    Spinner spinCategorias;

    ArrayList<String> labels = new ArrayList<String>();
    private SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta);

        btnFoto = findViewById(R.id.btnFoto);
        spinCategorias = findViewById(R.id.spinCategorias);
                db = new DataBaseManager(this, "mydb.db", 1, "", "").getWritableDatabase();



        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPermissionCamera()) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, CAMERA_PIC_REQUEST);

                } else {
                    requestPermissionCamera();
                }

            }
        });
        loadSpinnerData();

    }

    private void loadSpinnerData() {
        ArrayList<String> rotas = getCategorias();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, rotas);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.notifyDataSetChanged();
        // attaching data adapter to spinner
        spinCategorias.setAdapter(dataAdapter);
    }

    public ArrayList<String> getCategorias() {
        labels.clear();
        String selectQuery = "SELECT * FROM categorias";
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            // Add province name to arraylist
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("descricao"));
            labels.add(id + " - " + name);
        }

        return labels;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_PIC_REQUEST) {
            Bitmap image = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            Alerta alerta = new Alerta();
            alerta.setId_categoria((int) spinCategorias.getSelectedItemId() + 1);
            alerta.setId_passeio(ActivityNovaRota.rota.getId());
            alerta.setFoto(encoded);

            db = new DataBaseManager(AlertaActivity.this, "mydb.db", 1, "", "").getWritableDatabase();
            db.execSQL("insert into alertas (id_rota, id_categoria, foto) values ('" + alerta.getId_passeio() + "', '" + alerta.getId_categoria() + "', '" + alerta.getFoto() + "');");

            finish();

        }

    }

    private boolean checkPermissionCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermissionCamera() {

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

}