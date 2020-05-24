package com.fixer.fixer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageShow extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private String location;
    private double lat, lng;
    private Bitmap bitmap;
    private String S1, S2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);
        ImageView imageView = (ImageView) findViewById(R.id.ip);
        Bundle b = getIntent().getExtras();
        location = b.getString("Location");
        S1 = b.getString("S1");
        S2 = b.getString("S2");
        lat = b.getDouble("lat");
        lng = b.getDouble("lng");

        chooseImage(imageView);

    }

    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        ImageView imageView = (ImageView) findViewById(R.id.ip);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = (ImageView) findViewById(R.id.ip);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                //uploadImage(imageView);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void cut(View view) {
        finish();
    }

    public void next(View view) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Intent i = new Intent(this, NewPost.class);
        i.putExtra("Location", location);
        i.putExtra("lat", lat);
        i.putExtra("lng", lng);
        i.putExtra("bitmap", byteArray);
        i.putExtra("S1", S1);
        i.putExtra("S2", S2);
        i.putExtra("filepath", String.valueOf(filePath));
        startActivity(i);
    }
}
