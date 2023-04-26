package egtec.com.mylocation;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static int PERMISSION_REQUEST_CODE = 302;
    private final int DATABASE_VERSION = 1;

    Button btnPermision;
    Button btnMostrar;
    Button btnGeoLocation;
    TextView txtLocations;
    FloatingActionButton fab;
    Spinner spinRotas;
    private SQLiteDatabase db = null;
    private Cursor cur = null;

    ArrayList<String> labels = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);

        initUiComponents();

        if(!checkPermission())
            requestPermission();

        //LocationTrack locationManager = new LocationTrack(getApplicationContext());
        //txtLocations.setText(txtLocations.getText() + ";" + locationManager.getLongitude() + "," + locationManager.getLatitude());

        db = new DataBaseManager(this, "mydb.db", DATABASE_VERSION, "", "").getWritableDatabase();
        //cur = db.query("agenda", new String[]{"id", " nome", "fone", "endereco"}, null, null, null, null, null);

        loadSpinnerData();
    }

    private void initUiComponents() {
        txtLocations = findViewById(R.id.txtLocations);
        btnMostrar = findViewById(R.id.btnMostrar);
        spinRotas = findViewById(R.id.spinRotas);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , ActivityNovaRota.class);
                //startActivity(intent);
                separacaoPlacaActivityForResult.launch(intent);
            }
        });

        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validar()) {

                    String rota = spinRotas.getSelectedItem().toString().split(" ")[0];

                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                    intent.putExtra("rota", rota);

                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Escolha uma rota!", Toast.LENGTH_SHORT).show();
                }

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
                    loadSpinnerData();


                }
            });

    private void loadSpinnerData() {
        ArrayList<String> rotas = getRotas();

        // Adding "Escolha a sua rota" option
        labels.add(0, "Escolha uma rota...");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, rotas);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.notifyDataSetChanged();
        // attaching data adapter to spinner
        spinRotas.setAdapter(dataAdapter);
    }

    public ArrayList<String> getRotas() {
        labels.clear();
        String selectQuery = "SELECT * FROM passeios";
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            // Add province name to arraylist
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("nome"));
            labels.add(id + " - " + name);
        }

        return labels;
    }

    public boolean validar() {
        boolean ok = true;

        if (spinRotas.getSelectedItemPosition() == 0)
            ok = false;


        return ok;
    }


    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }
}