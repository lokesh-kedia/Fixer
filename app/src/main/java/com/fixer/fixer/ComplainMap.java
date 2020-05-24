package com.fixer.fixer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ComplainMap extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private Double plotlat, plotlng;
    ProgressDialog progressDialog;
    private String addressLine;
    private String S1, S2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setLocation();
    }

    private void setLocation() {
        progressDialog = new ProgressDialog(ComplainMap.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();
                            LatLng loc = new LatLng(lat, lng);
                            plotlat = lat;
                            plotlng = lng;


                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(loc)
                                    .zoom(18)
                                    .build();
                            int height = 50;
                            int width = 60;
                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.placeholder);
                            Bitmap b = bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                    .position(loc).draggable(true));
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                                    2000, null);
                            add(lat, lng);
                            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                @Override
                                public void onMarkerDragStart(Marker marker) {

                                }

                                @Override
                                public void onMarkerDrag(Marker marker) {

                                }

                                @Override
                                public void onMarkerDragEnd(Marker marker) {
                                    // mMap.clear();
                                    //mMap.addMarker(new MarkerOptions()
                                    //              .position(marker.getPosition()));
                                    plotlat = marker.getPosition().latitude;
                                    plotlng = marker.getPosition().longitude;

                                    mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                                    add(plotlat, plotlng);

                                }
                            });
                            // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 18));
                            // mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);


                        } else {
                            Toast.makeText(ComplainMap.this, "yahan", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };

                LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 100, locationListener);

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    public void cut(View view) {
        finish();
    }

    public void chooseImage(View view) {
        Intent i = new Intent(ComplainMap.this, ImageShow.class);
        i.putExtra("Location", addressLine);
        i.putExtra("lat", plotlat);
        i.putExtra("lng", plotlng);
        i.putExtra("S1", S1);
        i.putExtra("S2", S2);
        startActivity(i);
        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);*/


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = (ImageView) findViewById(R.id.ip);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(double plotlat, double plotlng) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());


            addresses = geocoder.getFromLocation(plotlat, plotlng, 1);
            if (addresses != null) {
                addressLine = addresses.get(0).getAddressLine(0);
                S2 = addresses.get(0).getLocality();
                S1 = addresses.get(0).getAdminArea();
                //ET3.setText(addressLine);
                EditText editText = (EditText) findViewById(R.id.loctxt);
                editText.setText(addressLine);
                progressDialog.dismiss();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
