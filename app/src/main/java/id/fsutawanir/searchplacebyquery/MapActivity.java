package id.fsutawanir.searchplacebyquery;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by fsutawanir on 12/23/15.
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    LatLng mLatLng;
    String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        double latitude = getIntent().getDoubleExtra(MainActivity.LATITUDE_KEY, 0.0);
        double longitude = getIntent().getDoubleExtra(MainActivity.LONGITUDE_KEY, 0.0);
        mName = getIntent().getStringExtra(MainActivity.NAME_KEY);
        mLatLng = new LatLng(latitude, longitude);

        final SupportMapFragment supportMapFragment = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map));
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatLng, 10);
        googleMap.clear();
        googleMap.animateCamera(cameraUpdate);
        googleMap.addMarker(
            new MarkerOptions()
                .position(mLatLng)
                .title(mName)
        );
    }
}
