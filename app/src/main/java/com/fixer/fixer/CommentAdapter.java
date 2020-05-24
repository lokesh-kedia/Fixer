package com.fixer.fixer;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    List<CommentMessage> commentMessages;
    Context context;

    public CommentAdapter(List<CommentMessage> commentMessages, Context context) {
        this.commentMessages = commentMessages;
        this.context = context;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        CommentHolder commentHolder = new CommentHolder(view);
        return commentHolder;
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        CommentMessage commentMessage = commentMessages.get(position);
        holder.Uname.setText(commentMessage.getName());
        holder.Comment.setText(commentMessage.getMessage());
        //holder.Date.setText(commentMessage.getTime());
    }

    @Override
    public int getItemCount() {
        return commentMessages.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder {
        TextView Uname, Comment, Date;
        ImageView OwnerImg;


        public CommentHolder(View itemView) {
            super(itemView);
            OwnerImg = (ImageView) itemView.findViewById(R.id.ownerimg);
            Uname = (TextView) itemView.findViewById(R.id.Uname);
            Comment = (TextView) itemView.findViewById(R.id.txtcmt);
            Date = (TextView) itemView.findViewById(R.id.txtTime);

        }
    }


}
