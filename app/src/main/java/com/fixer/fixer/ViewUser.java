package com.fixer.fixer;

import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewUser extends AppCompatActivity {
    private GridView imageGrid;
    private ArrayList<ComplaintMessage> complaintMessages = new ArrayList<>();
    private ArrayList<String> bitmapList;
    private String UID;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        imageGrid = (GridView) findViewById(R.id.gridview);
        bitmapList = new ArrayList<String>();

        Bundle b = getIntent().getExtras();
        UID = b.getString("UID");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("Profile").getRef();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photo = dataSnapshot.child("Photo").getValue(String.class);
                ImageView imageView = (ImageView) findViewById(R.id.ip);
                String name = dataSnapshot.child("Name").getValue(String.class);
                Glide.with(ViewUser.this).load(photo).into(imageView);
                TextView textView = (TextView) findViewById(R.id.lname);
                textView.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("Followers").getRef();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView txtfollr = (TextView) findViewById(R.id.txtfolr);
                long follr = dataSnapshot.getChildrenCount();
                txtfollr.setText(String.valueOf(follr));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("Following").getRef();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView txtfollw = (TextView) findViewById(R.id.txtfolw);
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
                .getReference().child("Users").child(UID).child("Uploads").getRef();
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
                                imageGrid.setAdapter(new ImageAdapter(ViewUser.this, bitmapList));
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                Toast.makeText(ViewUser.this, e.toString(), Toast.LENGTH_LONG).show();
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

    public void follow(View view) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("Followers").child(UID);
        databaseReference.setValue(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Following").child(UID);
        databaseReference.setValue(true);
        Toast.makeText(this, "Followed", Toast.LENGTH_LONG).show();
    }

    public void cut(View view) {
        finish();
    }
}
