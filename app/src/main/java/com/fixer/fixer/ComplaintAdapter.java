package com.fixer.fixer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.List;

public class ComplaintAdapter extends ArrayAdapter<ComplaintMessage> {
    private Context mContext;
    private EditText activatedEditText;
    private DatabaseReference databaseReference;

    public ComplaintAdapter(Context context, int resource, List<ComplaintMessage> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_complaint, parent, false);
        }

        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);

        TextView problemTextView = (TextView) convertView.findViewById(R.id.problemTextView);
        TextView areaTextView = (TextView) convertView.findViewById(R.id.messagearea);
        final TextView dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
        TextView OwnerTextView = (TextView) convertView.findViewById(R.id.Owner);
        TextView descTextView = (TextView) convertView.findViewById(R.id.desc);
        final TextView likesTextView = (TextView) convertView.findViewById(R.id.likesTextView);
        final TextView dislikesTextView = (TextView) convertView.findViewById(R.id.dislikesTextView);
        TextView commentTextView = (TextView) convertView.findViewById(R.id.commentarea);
        ImageButton button = (ImageButton) convertView.findViewById(R.id.share);
        ImageButton button1 = (ImageButton) convertView.findViewById(R.id.like);
        ImageButton button3 = (ImageButton) convertView.findViewById(R.id.dislike);
        ImageButton button2 = (ImageButton) convertView.findViewById(R.id.comment);
        ImageView ownerImageView = (ImageView) convertView.findViewById(R.id.ownerimg);
        ImageButton itemmenu = (ImageButton) convertView.findViewById(R.id.itemmenu);
        EditText addcmt = (EditText) convertView.findViewById(R.id.addcmt);
        final ComplaintMessage message = getItem(position);

        boolean isPhoto = message.geturl() != null;
        if (isPhoto) {
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(message.geturl())
                    .into(photoImageView);
        } else {
            photoImageView.setVisibility(View.VISIBLE);
        }
        isPhoto = message.getOimg() != null;
        if (isPhoto) {
            ownerImageView.setVisibility(View.VISIBLE);
            Glide.with(ownerImageView.getContext())
                    .load(message.getOimg())
                    .into(ownerImageView);
        } else {
            // photoImageView.setVisibility(View.VISIBLE);
        }
       /* addcmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getClass() != EditText.class) {
                    activatedEditText.clearFocus();
                    activatedEditText.setFocusable(false);
                    activatedEditText.setFocusableInTouchMode(false);

                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(activatedEditText.getWindowToken(), 0);
                    }
                }
            }
        });*/
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = FirebaseDatabase.getInstance().getReference().child("States").child("Rajasthan").child("Jaipur").child(message.getKey()).child("Like").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                databaseReference.setValue(true);
                Toast.makeText(mContext, "Liked", Toast.LENGTH_LONG).show();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = FirebaseDatabase.getInstance().getReference().child("States").child("Rajasthan").child("Jaipur").child(message.getKey()).child("Dislike").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                databaseReference.setValue(true);
                Toast.makeText(mContext, "Disliked", Toast.LENGTH_LONG).show();
            }
        });
        try {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("States").child("Rajasthan").child("Jaipur").child(message.getKey()).child("Like");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long likes = dataSnapshot.getChildrenCount();
                    likesTextView.setText(String.valueOf(likes));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            databaseReference = FirebaseDatabase.getInstance().getReference().child("States").child("Rajasthan").child("Jaipur").child(message.getKey()).child("Dislike");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long dislikes = dataSnapshot.getChildrenCount();
                    dislikesTextView.setText(String.valueOf(dislikes));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {

        }
        descTextView.setText(message.getDesc());
        dateTextView.setText(message.getDate());
        OwnerTextView.setText(message.getOwner());
        OwnerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UID = message.getUID();

                Intent i = new Intent(getContext(), ViewUser.class);
                i.putExtra("UID", UID);
                getContext().startActivity(i);


            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_SUBJECT, "News");
                i.putExtra(android.content.Intent.EXTRA_TEXT, "Try");
                getContext().startActivity(Intent.createChooser(i, "Share via"));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, CommentsActivity.class);
                i.putExtra("Key", message.getKey());
                getContext().startActivity(i);
            }
        });
        return convertView;
    }


}