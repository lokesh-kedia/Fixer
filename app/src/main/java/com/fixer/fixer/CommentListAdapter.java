package com.fixer.fixer;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CommentListAdapter extends ArrayAdapter<CommentMessage> {
    private Context mContext;

    public CommentListAdapter(@NonNull Context context, int resource, List<CommentMessage> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_comment, parent, false);
        }

        ImageView OwnerImg = (ImageView) convertView.findViewById(R.id.ownerimg);
        TextView Uname = (TextView) convertView.findViewById(R.id.Uname);
        TextView Comment = (TextView) convertView.findViewById(R.id.txtcmt);
        TextView Date = (TextView) convertView.findViewById(R.id.txtTime);
        final CommentMessage commentMessage = getItem(position);
        Uname.setText(commentMessage.getName());
        Comment.setText(commentMessage.getMessage());
        //Date.setText(commentMessage.getTime());
        return convertView;
    }


}