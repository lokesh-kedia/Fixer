package com.fixer.fixer;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class NewPost extends AppCompatActivity {
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private double lat;
    private double lng;
    EditText ET3, ET4;
    String S1, S2, S3, S4, S5, city;
    FirebaseDatabase mFirebaseDatabase;
    ImageView imageView;
    DatabaseReference mMessagesDatabaseReference, mStates, mCity, mArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        String[] arraySpinner = new String[]{
                "Potholes", "Accident", "Landslides", "Hazard", "Street Sign", "Street light", "Broken Sidewalk"
        };
        Spinner s = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        Bundle b = getIntent().getExtras();
        String Location = b.getString("Location");
        lat = b.getDouble("lat");
        lng = b.getDouble("lng");
        S1 = b.getString("S1");
        S2 = b.getString("S2");

        String tr = b.getString("filepath");
        filePath = Uri.parse(tr);
        //Toast.makeText(this,tr,Toast.LENGTH_LONG).show();
        //Bitmap bitmap=(Bitmap) getIntent().getParcelableExtra("bitmap");
        byte[] byteArray = getIntent().getByteArrayExtra("bitmap");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView = (ImageView) findViewById(R.id.imgview);
        EditText editText = (EditText) findViewById(R.id.txtadd);
        editText.setText(Location);
        imageView.setImageBitmap(bmp);


    }

    public void cut(View view) {
        finish();
    }

    public void chooseImage(View view) {
        submit(view);
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
                            Toast.makeText(NewPost.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(NewPost.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void submit(View view) {
        ET3 = (EditText) findViewById(R.id.txtadd);
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
            mArea.child("lat").setValue(lat);
            mArea.child("lng").setValue(lng);
            mArea.child("UID").setValue(UID);
            mArea.child("Address").setValue(S4);
            mArea.child("Desc").setValue(S5);
            mArea.child("Oimg").setValue(Oimg);
            mArea.child("Key").setValue(key);
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

}
