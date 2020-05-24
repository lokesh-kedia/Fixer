package com.fixer.fixer;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComplaintFeed extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private String State, City;
    private ListView mMessageListView;
    private ChildEventListener valueEventListener;
    private ComplaintAdapter mMessageAdapter;
    private TextView mTextMessage;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Fragment currentFragment = null;
    private FragmentTransaction ft;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final int RC_SIGN_IN = 1;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    Intent ii = new Intent(getApplication(), ComplaintFeed.class);
                    startActivity(ii);
                    return true;
                case R.id.Search:
                    ii = new Intent(getApplication(), Main2Activity.class);
                    startActivity(ii);
                    return true;
                case R.id.Plus:
                    ii = new Intent(getApplication(), ComplainMap.class);
                    startActivity(ii);
                    return true;
                case R.id.Heart:
                    ii = new Intent(getApplication(), MapsActivity.class);
                    startActivity(ii);
                    return true;
                case R.id.Profile:
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
        setContentView(R.layout.activity_complaint_feed);
        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Menu menu = navigation.getMenu();
        MenuItem item = menu.getItem(0);
        item.setChecked(true);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.AnonymousBuilder().build());
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        progressDialog = new ProgressDialog(ComplaintFeed.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        State = "Rajasthan";
        City = "Jaipur";
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("States").child(State).child(City);
        attachDatabaseReadListener();
        mMessageListView = (ListView) findViewById(R.id.messageListView);

        List<ComplaintMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new ComplaintAdapter(this, R.layout.item_complaint, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
        firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        // String s = String.valueOf(plotNo.size());

    }

    private void attachDatabaseReadListener() {
        if (valueEventListener == null) {
            valueEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ComplaintMessage friendlyMessage = dataSnapshot.getValue(ComplaintMessage.class);
                    progressDialog.dismiss();
                    mMessageAdapter.add(friendlyMessage);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mMessagesDatabaseReference.addChildEventListener(valueEventListener);
        }
    }

    @Override
    protected void onStart() {
        //backButtonCount=0;
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                setuser();

                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void setuser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String Owner = user.getUid();
        String Name = user.getDisplayName();
        String Email = user.getEmail();
        String Photo = String.valueOf(user.getPhotoUrl());
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("Users").child(Owner).child("Profile");
        mMessagesDatabaseReference.child("Name").setValue(Name);
        mMessagesDatabaseReference.child("Email").setValue(Email);
        mMessagesDatabaseReference.child("Photo").setValue(Photo);


    }


}
