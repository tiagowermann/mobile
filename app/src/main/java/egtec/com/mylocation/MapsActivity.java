package egtec.com.mylocation;

import androidx.fragment.app.FragmentActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import egtec.com.mylocation.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    String[] local;
    String id_rota;
    private SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        id_rota = bundle.getString("rota");

        db = new DataBaseManager(MapsActivity.this, "mydb.db", 1, "", "").getWritableDatabase();

        String selectQuery = "SELECT * FROM passeios WHERE id = " + id_rota;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToNext())
            local = cursor.getString(cursor.getColumnIndex("locais")).split(";");

        //local = bundle.getStringArray("local");

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = null;
        for (int i = 1 ; i < local.length; i++) {
            sydney = new LatLng(Double.parseDouble(local[i].split(",")[0]), Double.parseDouble(local[i].split(",")[1]));
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney" + i));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
        //LatLng sydney = new LatLng(Double.parseDouble(local[0]), Double.parseDouble(local[1]));
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
    }
}