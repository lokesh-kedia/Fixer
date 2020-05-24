package com.fixer.fixer;

import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<CommentMessage> commentMessages = new ArrayList<>();
    RecyclerView recyclerView;
    String key;
    CommentAdapter commentAdapter;
    CommentListAdapter commentListAdapter;
    ListView listView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private String State, City;
    private ListView mMessageListView;
    private ChildEventListener valueEventListener;
    private CommentListAdapter mMessageAdapter;
    private TextView mTextMessage;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Bundle b = getIntent().getExtras();
        key = b.getString("Key");
       /* recyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("States").child("Rajasthan").child("Jaipur").child(key).child("Comments");
        attachDatabaseReadListener();*/
        /*recyclerView = (RecyclerView) findViewById(R.id.listcmt);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<CommentMessage> commentMessages=new ArrayList<>();
        commentAdapter = new CommentAdapter(commentMessages, this);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(commentAdapter);*/
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        State = "Rajasthan";
        City = "Jaipur";
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("States").child(State).child(City).child(key).child("Comments");
        attachDatabaseReadListener();
        mMessageListView = (ListView) findViewById(R.id.listview);
        List<CommentMessage> commentMessages = new ArrayList<>();
        mMessageAdapter = new CommentListAdapter(this, R.layout.item_comment, commentMessages);
        mMessageListView.setAdapter(mMessageAdapter);
    }


    public void cut(View view) {
        finish();
    }

    public void send(View view) {
        EditText editText = (EditText) findViewById(R.id.cmttxt);
        String message = String.valueOf(editText.getText());
        String sender = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String time;
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("States").child("Rajasthan").child("Jaipur").child(key).child("Comments");
        String key1 = databaseReference.push().getKey();
        databaseReference = databaseReference.child(key1);
       /* databaseReference.child("message").setValue(message);
        databaseReference.child("sender").setValue(sender);
        databaseReference.child("name").setValue(name);*/
        CommentMessage commentMessage = new CommentMessage(sender, message, name, "");
        databaseReference.setValue(commentMessage);
        editText.setText("");
    }

    private void attachDatabaseReadListener() {
        if (valueEventListener == null) {
            valueEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    CommentMessage commentMessage = dataSnapshot.getValue(CommentMessage.class);
                    mMessageAdapter.add(commentMessage);
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
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();

    }
}
