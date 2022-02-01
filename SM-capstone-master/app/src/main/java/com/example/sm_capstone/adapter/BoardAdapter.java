package com.example.sm_capstone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sm_capstone.Board_Post.Post;
import com.example.sm_capstone.Board_comment;
import com.example.sm_capstone.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {

    private List<Post> datas;
    private Context mcontext;


    public BoardAdapter(Context mcontext, List<Post> datas) {
        this.datas = datas;
        this.mcontext=mcontext;

    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BoardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        Post data=datas.get(position);
        holder.title.setText(datas.get(position).getTitle());
        holder.contents.setText(datas.get(position).getContents());
        holder.writer.setText(datas.get(position).getWriter_name());
        holder.comment_num.setText(datas.get(position).getComment_num());
        holder.post_date.setText(datas.get(position).getPost_date());
        if ( !datas.get(position).getPost_photo().isEmpty()) {
            Picasso.get()
                    .load(datas.get(position).getPost_photo())
                    .into(holder.post_photo);
        }
        else
        {
            Picasso.get()
                    .load(R.drawable.emplo)
                    .into(holder.post_photo);
        }
        final int pos=holder.getAdapterPosition();//몇번째 게시글인지
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Context context=v.getContext();
                if(pos!=RecyclerView.NO_POSITION){
                    Intent intent=new Intent(v.getContext(), Board_comment.class);
                    intent.putExtra("title",datas.get(pos).getTitle());
                    intent.putExtra("content",datas.get(pos).getContents());//
                    intent.putExtra("writer_name",datas.get(pos).getWriter_name());//게시글 작성자 이름
                    intent.putExtra("photo_url",datas.get(pos).getPost_photo());//게시글에 첨부된 사진
                    intent.putExtra("post_id",datas.get(pos).getPost_id());//어떠 게시글인지 구분
                    intent.putExtra("uid",datas.get(pos).getDocumentId());//게시글작성자의 uid
                    intent.putExtra("position",pos);//몇 번째 게시글인지
                    intent.putExtra("board_part",datas.get(pos).getBoard_part());//동적게시판인지, 정적게시판인지
                    intent.putExtra("time",datas.get(pos).getDate());
                    intent.putExtra("post_date",datas.get(pos).getPost_date());
                    intent.putExtra("comment_num",datas.get(pos).getComment_num());
                    mcontext.startActivity(intent);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public interface  EventListener<QuerySnapshot>{
        boolean onOptionItemSelected(MenuItem item);

        void onItemClicked(int position);
    }

    class BoardViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView contents;
        private TextView writer;
        private ImageView post_photo;
        private TextView comment_num;
        private TextView post_date;
        public BoardViewHolder(@NonNull View itemView){
            super(itemView);
            post_photo=itemView.findViewById(R.id.post_imageView);
            title=itemView.findViewById(R.id.post_title);
            contents=itemView.findViewById(R.id.post_contents);
            writer=itemView.findViewById(R.id.post_writer);
            comment_num=itemView.findViewById(R.id.comment_num);
            post_date=itemView.findViewById(R.id.post_date);
        }
    }
}
