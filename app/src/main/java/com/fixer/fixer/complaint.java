package com.fixer.fixer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class complaint extends AppCompatActivity {
    private Uri filepath;
    ImageView imageView;

    private FirebaseStorage storage;
    private RadioGroup RG;
    private StorageReference storageReference;
    private Uri filePath;
    private DrawerLayout mDrawerLayout;
    private final int PICK_IMAGE_REQUEST = 71;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 12;
    private final int CAPTURE_IMAGE_REQUEST = 86;
    private TextView textView, stateView;
    private String[] add;
    private GoogleMap mMap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private double plotlat;
    private double plotlng;
    private ProgressDialog progressDialog;
    private int backButtonCount = 0;
    //private  EditText editText;
    Camera camera;
    Button btnChoose;
    EditText ET3, ET4;
    String S1, S2, S3, S4, S5, city;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mMessagesDatabaseReference, mStates, mCity, mArea;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    //mTextMessage.setText("Home");
                    Intent ii = new Intent(getApplication(), ComplaintFeed.class);
                    startActivity(ii);
                    return true;
                case R.id.Search:
                    //mTextMessage.setText("Search");
                    return true;
                case R.id.Plus:
                    ii = new Intent(getApplication(), complaint.class);
                    startActivity(ii);
                    return true;
                case R.id.Heart:
                    //mTextMessage.setText("You");
                    return true;
                case R.id.Profile:
                    //mTextMessage.setText("Profile");
                    ii = new Intent(getApplication(), Profile.class);
                    startActivity(ii);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Menu menu = navigation.getMenu();
        MenuItem item = menu.getItem(2);
        item.setChecked(true);
        //navigation.setSelectedItemId(R.id.Plus);
        ET3 = (EditText) findViewById(R.id.addview);
        ET4 = (EditText) findViewById(R.id.descview);
        String[] arraySpinner = new String[]{
                "Potholes", "Accident", "Landslides", "Hazard", "Street Sign", "Street light", "Broken Sidewalk"
        };
        Spinner s = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        progressDialog = new ProgressDialog(complaint.this);
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
                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.pin);
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
                            Toast.makeText(complaint.this, "yahan", Toast.LENGTH_LONG).show();
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

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrview);
        ((mapfrag) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new mapfrag.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });


        btnChoose = (Button) findViewById(R.id.btnChoose);
        imageView = (ImageView) findViewById(R.id.imgView);



/*
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("IN")
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }*/
    }

    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.main_menu, menu);
         return true;
     }*/
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.sign_out_menu:
                FirebaseAuth.getInstance().signOut();
                return true;
            case R.id.action_item_two:
                Intent i = new Intent(complaint.this, ChatList.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
    @Override
    public void onBackPressed() {
        if (backButtonCount >= 1) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    @Override
    protected void onStart() {
        backButtonCount = 0;
        super.onStart();
    }

    public void add(double plotlat, double plotlng) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());


            addresses = geocoder.getFromLocation(plotlat, plotlng, 1);
            if (addresses != null) {
                String addressLine = addresses.get(0).getAddressLine(0);
                S2 = addresses.get(0).getLocality();
                S1 = addresses.get(0).getAdminArea();
                ET3.setText(addressLine);
                progressDialog.dismiss();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String s = (String) place.getAddress();
                ET3.setText(s);

            }
        } else if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            filePath = data.getData();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            // TODO: Handle the error.
            // Log.i(TAG, status.getStatusMessage());

        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }

    }

    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    public void uploadImage(View view) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("States/" + S1 + "/" + S2 + "/" + S3 + "/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                            mArea.child("url").setValue(url);
                            Toast.makeText(complaint.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(complaint.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    public void captureImage(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
        }
    }

    public void submit(View view) {
        ET3 = (EditText) findViewById(R.id.addview);
        ET4 = (EditText) findViewById(R.id.descview);
        S4 = ET3.getText().toString();
        S5 = ET4.getText().toString();

        if (S4.equals("") || S5.equals(""))
            Toast.makeText(this, "All the Fields are Required", Toast.LENGTH_LONG).show();
        else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String Owner = user.getDisplayName();
            String UID = user.getUid();
            String Oimg = user.getPhotoUrl().toString();
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("States");
            mStates = mMessagesDatabaseReference.child(S1);
            mCity = mStates.child(S2);
            String key = mCity.push().getKey();
            S3 = key;
            mArea = mCity.child(key);
            mArea.child("Owner").setValue(Owner);
            mArea.child("Date").setValue(formattedDate);
            mArea.child("lat").setValue(plotlat);
            mArea.child("lng").setValue(plotlng);
            mArea.child("UID").setValue(UID);
            mArea.child("Address").setValue(S4);
            mArea.child("Desc").setValue(S5);
            mArea.child("Oimg").setValue(Oimg);
            ET3.setText("");
            ET4.setText("");
            uploadImage(imageView);
            user = FirebaseAuth.getInstance().getCurrentUser();
            Owner = user.getUid();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("Users").child(Owner).child("Uploads").child(S1 + "_" + S2 + "_" + S3);
            mMessagesDatabaseReference.setValue("0");
            Toast.makeText(this, "Complaint Uploaded Successfully", Toast.LENGTH_LONG).show();


        }
    }

    public void cut(View view) {
        finish();
    }
}
