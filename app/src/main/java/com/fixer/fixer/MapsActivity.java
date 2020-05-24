package com.fixer.fixer;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mMessagesDatabaseReference;
    private ValueEventListener valueEventListener;
    String State, City;
    ProgressDialog progressDialog;
    ArrayList<ComplaintMessage> complaintMessages = new ArrayList<>();
    ArrayList<LatLng> latLngs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        State = "Rajasthan";
        City = "Jaipur";
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("States").child(State).child(City);
        attachDatabaseReadListener();

    }

    private void attachDatabaseReadListener() {
        if (valueEventListener == null) {
            valueEventListener = new ValueEventListener() {
                int i = 0;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot plots : dataSnapshot.getChildren()) {
                        ComplaintMessage friendlyMessage = plots.getValue(ComplaintMessage.class);
                        complaintMessages.add(friendlyMessage);
                        double lat = friendlyMessage.getLat();
                        double lng = friendlyMessage.getLng();
                        LatLng sydney = new LatLng(lat, lng);
                        latLngs.add(sydney);
                        //mMap.addMarker(new MarkerOptions().position(sydney).title("Pit"));
                        int height = 100;
                        int width = 60;
                        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.placeholder);
                        Bitmap b = bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                .position(sydney).draggable(false).snippet(String.valueOf(i)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        i++;
                        progressDialog.dismiss();
                        click();

                    }
                }

                public void onCancelled(DatabaseError databaseError) {
                }

            };
            mMessagesDatabaseReference.addListenerForSingleValueEvent(valueEventListener);

        }

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

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();

    }

    @Override
    protected void onStart() {
        super.onStart();
        attachDatabaseReadListener();

    }

    public void click() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 25));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(25), 2000, null);


                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.complaintmapitem);
                linearLayout.setVisibility(View.VISIBLE);
                String pos = marker.getSnippet();
                int p = Integer.parseInt(pos);
                mMessagesDatabaseReference = mMessagesDatabaseReference.child(complaintMessages.get(p).getKey()).child("Like");
                mMessagesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        TextView likestextView = (TextView) findViewById(R.id.likesTextView);
                        likestextView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mMessagesDatabaseReference = mMessagesDatabaseReference.child(complaintMessages.get(p).getKey()).child("Dislike");
                mMessagesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        TextView dislikestextView = (TextView) findViewById(R.id.dislikesTextView);
                        dislikestextView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                String add = complaintMessages.get(p).getAddress();
                String uname = complaintMessages.get(p).getOwner();
                String img = complaintMessages.get(p).geturl();
                ImageView imageView = (ImageView) findViewById(R.id.img);
                Glide.with(MapsActivity.this).load(img).into(imageView);
                TextView textView = (TextView) findViewById(R.id.mapadd);
                TextView textView1 = (TextView) findViewById(R.id.unamemap);
                textView.setText(add);
                textView1.setText(uname);
                return false;
            }
        });
    }
}
