package com.fixer.fixer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> bitmapList;

    public ImageAdapter(Context context, ArrayList<String> bitmapList) {
        super(context, R.layout.item_grid, bitmapList);
        this.context = context;
        this.bitmapList = bitmapList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_grid, parent, false);
        }
        // ImageView imageView= (ImageView) convertView.findViewById(R.id.grid);
        Glide
                .with(context)
                .load(bitmapList.get(position))
                .into((ImageView) convertView);

        return convertView;
    }
}
