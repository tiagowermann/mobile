package egtec.com.mylocation;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import egtec.com.mylocation.Model.RotaModel;

public class ActivityNovaRota extends AppCompatActivity {

    final static int PERMISSION_REQUEST_CODE = 302;


    EditText editNomeRota;
    EditText editDescricaoRota;
    Button btnIniciar;
    Button btnParar;
    Button btnAlerta;

    static RotaModel rota;

    static TextView textCoordenadas;

    private SQLiteDatabase db = null;
    LocationTrack locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_rota);

        editNomeRota = findViewById(R.id.editNomeRota);
        editDescricaoRota = findViewById(R.id.editDescricaoRota);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnParar = findViewById(R.id.btnParar);
        btnAlerta = findViewById(R.id.btnAlerta);
        textCoordenadas = findViewById(R.id.localizacaoTextView);



        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validar()) {
                    if(!checkPermission())
                        requestPermission();

                    // pega local
                    locationManager = new LocationTrack(getApplicationContext());

                    rota = new RotaModel();
                    rota.setNome(editNomeRota.getText().toString());
                    rota.setDescricao(editDescricaoRota.getText().toString());
                    rota.setLocais(locationManager.getLatitude() + "," + locationManager.getLongitude());


                    db = new DataBaseManager(ActivityNovaRota.this, "mydb.db", 1, "", "").getWritableDatabase();
                    db.execSQL("insert into passeios (nome, descricao, locais) values ('" + rota.getNome() + "', '" + rota.getDescricao() + "', '" + rota.getLocais() + "');");

                    String selectQuery = "SELECT MAX(id) as max_id FROM passeios";
                    Cursor cursor = db.rawQuery(selectQuery, null);

                    btnAlerta.setVisibility(View.VISIBLE);
                    btnParar.setVisibility(View.VISIBLE);

                    if(cursor.moveToNext()) {
                        int id = cursor.getInt(cursor.getColumnIndex("max_id"));
                        rota.setId(id);
                    }



                } else {
//                    Toast.makeText(ActivityNovaRota.this, "Favor preencha os dados!", Toast.LENGTH_SHORT).show();
                    Toast toast = Toast.makeText(ActivityNovaRota.this, "Preencha os dados!!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        btnParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validar()) {
                    locationManager.stopListener();
                    db.execSQL("UPDATE passeios SET locais = '" + rota.getLocais() + "' WHERE id = " + rota.getId());
                    finish();
                } else {
                    Toast.makeText(ActivityNovaRota.this, "Inicie a rota!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ActivityNovaRota.this , AlertaActivity.class);
                //startActivity(intent);
                separacaoPlacaActivityForResult.launch(intent);

            }
        });

    }

    ActivityResultLauncher<Intent> separacaoPlacaActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                    }

                }
            });



    public boolean validar() {
        boolean ok = true;

        if(editNomeRota.getText().toString().equals(""))
            ok = false;

        if(editDescricaoRota.getText().toString().equals(""))
            ok = false;


        return ok;
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }



}
