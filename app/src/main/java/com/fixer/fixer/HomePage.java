package com.fixer.fixer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.Manifest;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdView;

public class HomePage extends AppCompatActivity {
    private AdView mAdView;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 2;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 3;
    public static final int MY_PERMISSIONS_REQUEST_CALL = 4;
    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 5;
    Animation frombottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_home_page);
        ImageView imageView = (ImageView) findViewById(R.id.logo);
        ImageView imageView1 = (ImageView) findViewById(R.id.logotitle);
        ImageView imageView2 = (ImageView) findViewById(R.id.logotag);
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        imageView.setAnimation(frombottom);
        imageView1.setAnimation(frombottom);
        imageView2.setAnimation(frombottom);
*/
        setContentView(R.layout.login);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lloptions);
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        linearLayout.setAnimation(frombottom);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_CONTACTS,
                //android.Manifest.permission.WRITE_CONTACTS,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                //android.Manifest.permission.READ_SMS,
                android.Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.INTERNET
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

       /* Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(HomePage.this, ComplaintFeed.class));
                finish();
            }
        }, 3000);*/

    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void openlogin(View view) {
        startActivity(new Intent(HomePage.this, ComplaintFeed.class));
    }
}
