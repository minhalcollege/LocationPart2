package tomerbu.edu.locationpart2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {


    private GoogleMap mMap;
    private Button btnSearch;
    private EditText etQuery;

    //Location:
    //Fused -> Both Cellular AND GPS
    private FusedLocationProviderClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationClient = new FusedLocationProviderClient(this);

        requestLocation();

        btnSearch = findViewById(R.id.btnSearch);
        etQuery = findViewById(R.id.etQuery);
        btnSearch.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        String text = etQuery.getText().toString();
        //1)Internet permission
        // ???
        //geocoding , reveresegeocoding
        Geocoder geocoder = new Geocoder(this);
        try {
            Address address = geocoder.getFromLocationName(text, 1).get(0);
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();

            LatLng latLng = new LatLng(latitude, longitude);
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            mMap.addMarker(new MarkerOptions().position(latLng).title("גן ילדים").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } catch (IOException e) {
            //TODO: show an Alert to the user...
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private static final int RC_REQUEST_LOCATION = 1;

    private void requestLocation() {
        int checkPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION); //BitMask
        //if no permission -> Reuqest the permission (And Return from the method)
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            //Request the permission:
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_REQUEST_LOCATION);
            return;//get out of the method
        }
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10 * 1000);
        request.setFastestInterval(1 * 1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationClient.requestLocationUpdates(request, cb, null);
    }

    LocationCallback cb = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();

            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                findTheAddress(latLng);
            }
        }
    };

    private void findTheAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_REQUEST_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}
