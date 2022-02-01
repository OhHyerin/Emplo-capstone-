package com.example.sm_capstone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sm_capstone.Board_Post.Home_Post;
import com.example.sm_capstone.R;

import java.util.List;

public class SHomeAdapter extends RecyclerView.Adapter<SHomeAdapter.SHomeViewHolder> {
    private List<Home_Post> datas;
    private Context mcontext;

    public  SHomeAdapter(Context mcontext, List<Home_Post> datas){
        this.datas = datas;
        this.mcontext=mcontext;
    }

    @NonNull
    @Override
    public SHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SHomeAdapter.SHomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_post,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SHomeViewHolder holder, int position) {
        Home_Post data=datas.get(position);
        holder.writer_name.setText(datas.get(position).getWriter_name());
        holder.title.setText(datas.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return (null != datas ? datas.size() : 0);
    }


    public class SHomeViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView writer_name;
        public SHomeViewHolder(@NonNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.post_contents);
            writer_name=itemView.findViewById(R.id.check_post);
        }
    }
}
