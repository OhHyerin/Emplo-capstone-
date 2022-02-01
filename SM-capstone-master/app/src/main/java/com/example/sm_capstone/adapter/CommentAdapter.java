package com.example.sm_capstone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sm_capstone.Board_Post.Comment;
import com.example.sm_capstone.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> mcomment_data;

    public CommentAdapter(List<Comment> mcontent_data){
        this.mcomment_data=mcontent_data;
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment data=mcomment_data.get(position);
        holder.writer_name.setText(mcomment_data.get(position).getC_writer());
        holder.comment.setText(mcomment_data.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return mcomment_data.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{

        private TextView writer_name;
        private TextView comment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            writer_name=itemView.findViewById(R.id.comment_item_username);
            comment=itemView.findViewById(R.id.comment_contents);

        }
    }

}
