package com.fixer.fixer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(uid).child("Profile").getRef();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //UserEdit userEdit =dataSnapshot.getValue(UserEdit.class);
                String photo = dataSnapshot.child("Photo").getValue(String.class);
                String name = dataSnapshot.child("Name").getValue(String.class);
                String username = dataSnapshot.child("Username").getValue(String.class);
                String website = dataSnapshot.child("Website").getValue(String.class);
                String bio = dataSnapshot.child("Bio").getValue(String.class);
                String email = dataSnapshot.child("Email").getValue(String.class);
                String phone = dataSnapshot.child("Phone").getValue(String.class);
                String gender = dataSnapshot.child("Gender").getValue(String.class);
                ImageView imageView = (ImageView) findViewById(R.id.ip);
                EditText nametextView = (EditText) findViewById(R.id.name);
                EditText usernametextView = (EditText) findViewById(R.id.username);
                EditText websitetextView = (EditText) findViewById(R.id.website);
                EditText biotextView = (EditText) findViewById(R.id.bio);
                EditText emailtextView = (EditText) findViewById(R.id.email);
                EditText phonetextView = (EditText) findViewById(R.id.phone);
                EditText gendertextView = (EditText) findViewById(R.id.gender);

                Glide.with(EditProfile.this).load(photo).into(imageView);
                nametextView.setText(name);
                usernametextView.setText(username);
                websitetextView.setText(website);
                biotextView.setText(bio);
                emailtextView.setText(email);
                phonetextView.setText(phone);
                gendertextView.setText(gender);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void cut(View view) {
        finish();
    }

    public void savechage(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(uid).child("Profile").getRef();
        EditText nametextView = (EditText) findViewById(R.id.name);
        EditText usernametextView = (EditText) findViewById(R.id.username);
        EditText websitetextView = (EditText) findViewById(R.id.website);
        EditText biotextView = (EditText) findViewById(R.id.bio);
        EditText emailtextView = (EditText) findViewById(R.id.email);
        EditText phonetextView = (EditText) findViewById(R.id.phone);
        EditText gendertextView = (EditText) findViewById(R.id.gender);
        String name = nametextView.getText().toString();
        String username = usernametextView.getText().toString();
        String website = websitetextView.getText().toString();
        String bio = biotextView.getText().toString();
        String email = emailtextView.getText().toString();
        String phone = phonetextView.getText().toString();
        String gender = gendertextView.getText().toString();
        //UserEdit userEdit=new UserEdit(name,username,website,bio,email,phone,gender);
        databaseReference.child("Name").setValue(name);
        databaseReference.child("Username").setValue(username);
        databaseReference.child("Website").setValue(website);
        databaseReference.child("Bio").setValue(bio);
        databaseReference.child("Email").setValue(email);
        databaseReference.child("Phone").setValue(phone);
        databaseReference.child("Gender").setValue(gender);
        Toast.makeText(this, "Successfully saved", Toast.LENGTH_LONG).show();


    }

    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        ImageView imageView = (ImageView) findViewById(R.id.ip);


    }

    public void uploadImage(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(uid).child("Profile").getRef();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Users/" + uid + "/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                            databaseReference.child("Photo").setValue(url);
                            Toast.makeText(EditProfile.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfile.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = (ImageView) findViewById(R.id.ip);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                uploadImage(imageView);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
