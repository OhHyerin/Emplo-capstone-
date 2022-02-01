package com.example.sm_capstone;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sm_capstone.Board_Post.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>{

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private List<CalendarPost> datas;
    private Context mcontext;
    private String user_name;
    private String schedule_id;

    public ScheduleAdapter(List<CalendarPost> datas, Context mcontext) {
        this.datas = datas;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //view생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        //view생성 후 view를 관리하기위한 viewholder생성
        ScheduleViewHolder holder = new ScheduleViewHolder(view);


        if(mAuth.getCurrentUser()!=null){
            mStore.collection("CalendarPost").document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){

                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                user_name=(String)task.getResult().getData().get(EmployID.name);
                                Log.d("확인", "현재 사용자 이름입니다"+user_name);
                            }
                        }
                    });
        }

        //생성된 viewholder를 OnBindViewHolder로 넘겨줌
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, final int position) {
        //해당row의 위치에 해당하는 data를 가져옴
        CalendarPost data = datas.get(position);
        holder.start_time.setText(datas.get(position).getStart_time());
        holder.end_time.setText(datas.get(position).getEnd_time());
        holder.writer_name.setText(datas.get(position).getWriter_name());
        holder.reference.setText(datas.get(position).getReference());

        ImageView btn_modify = holder.btn_modify;
        btn_modify.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("aaaa","버튼을 누른 아이템의 위치는 "+position);
            }
        });

        ImageView btn_delete = holder.btn_delete;
        btn_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("aaaa","버튼을 누른 아이템의 위치는 "+position);
            }
        });

        ImageView btn_request = holder.btn_request;
        btn_request.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("aaaa","버튼을 누른 아이템의 위치는 "+position);
            }
        });

    }

    @Override
    public int getItemCount() {
        //삼항 연산자
        //arrayList값이 null이 아니면 size반환
        return (datas != null ? datas.size() : 0);
    }

    public interface EventListener<QuerySnapshot>{
        boolean onOptionItemSelected(MenuItem item);
        void onItemClicked(int position);
    }



    class ScheduleViewHolder extends RecyclerView.ViewHolder{

        private TextView writer_name;
        private TextView start_time;
        private TextView end_time;
        private TextView reference;
        public ImageView btn_modify;
        public ImageView btn_delete;
        public ImageView btn_request;

        public ScheduleViewHolder(@NonNull View itemView){
            super(itemView);
            writer_name = itemView.findViewById(R.id.writer_name);
            start_time = itemView.findViewById(R.id.startTimeLabel);
            end_time = itemView.findViewById(R.id.endTimeLabel);
            reference = itemView.findViewById(R.id.schedule_reference);
            btn_modify = itemView.findViewById(R.id.btn_modify);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            btn_request = itemView.findViewById(R.id.btn_request);

            btn_modify.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mcontext, ScheduleModify.class);
                    intent.putExtra("schedule_id", schedule_id);
                }
            });


            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(getAdapterPosition());
                }
            });

        }

        public void removeItem(int position){
            datas.remove(position);
            notifyItemRemoved(position);
        }
    }


}
