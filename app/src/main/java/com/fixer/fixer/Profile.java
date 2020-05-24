package com.fixer.fixer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private GridView imageGrid;
    private ArrayList<ComplaintMessage> complaintMessages = new ArrayList<>();
    private ArrayList<String> bitmapList;
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
                    ii = new Intent(getApplication(), ComplainMap.class);
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
        setContentView(R.layout.activity_profile);
        imageGrid = (GridView) findViewById(R.id.gridview);
        bitmapList = new ArrayList<String>();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Menu menu = navigation.getMenu();
        MenuItem item = menu.getItem(4);
        item.setChecked(true);
        // navigation.setSelectedItemId(R.id.Profile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(uid).child("Profile").getRef();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photo = dataSnapshot.child("Photo").getValue(String.class);
                ImageView imageView = (ImageView) findViewById(R.id.ip);
                String name = dataSnapshot.child("Name").getValue(String.class);
                Glide.with(Profile.this).load(photo).into(imageView);
                TextView textView = (TextView) findViewById(R.id.lname);
                textView.setText(name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Followers").getRef();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView txtfollr = (TextView) findViewById(R.id.txtfollr);
                long follr = dataSnapshot.getChildrenCount();
                txtfollr.setText(String.valueOf(follr));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Following").getRef();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView txtfollw = (TextView) findViewById(R.id.txtfollw);
                long follw = dataSnapshot.getChildrenCount();
                txtfollw.setText(String.valueOf(follw));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(uid).child("Uploads").getRef();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView posttxt = (TextView) findViewById(R.id.txtpost);
                long i = dataSnapshot.getChildrenCount();
                posttxt.setText(String.valueOf(i));

                for (DataSnapshot posts : dataSnapshot.getChildren()) {
                    String post = posts.getKey();
                    post = post.replace("_", "/");
                    //Toast.makeText(Profile.this,post,Toast.LENGTH_LONG).show();
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("States").child(post);
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ComplaintMessage post = dataSnapshot.getValue(ComplaintMessage.class);
                            complaintMessages.add(post);
                            String photo = dataSnapshot.child("url").getValue(String.class);
                            try {
                                bitmapList.add(photo);
                                imageGrid.setAdapter(new ImageAdapter(Profile.this, bitmapList));
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                Toast.makeText(Profile.this, e.toString(), Toast.LENGTH_LONG).show();
                                Log.e("ye hai", e.toString());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        imageGrid.setAdapter(new ImageAdapter(this, bitmapList));
        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String Owner = complaintMessages.get(i).getOwner();
                //Intent ii = new Intent(Profile.this,MyPost.class);
                //ii.putExtra();
            }
        });

    }

    @Override
    public void onBackPressed() {

            super.onBackPressed();

    }


    public void edit(View view) {
        Intent i = new Intent(Profile.this, EditProfile.class);
        startActivity(i);
    }


    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(Profile.this, ComplaintFeed.class);
        startActivity(i);
    }
}
