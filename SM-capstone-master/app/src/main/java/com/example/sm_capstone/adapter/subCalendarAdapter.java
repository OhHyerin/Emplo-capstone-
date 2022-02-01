package com.example.sm_capstone.adapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sm_capstone.CalendarActivity;
import com.example.sm_capstone.CalendarPost;
import com.example.sm_capstone.DynamicBoard;
import com.example.sm_capstone.EmployID;
import com.example.sm_capstone.PostWrite;
import com.example.sm_capstone.R;
import com.example.sm_capstone.ScheduleAdd;
import com.example.sm_capstone.ScheduleModify;
import com.example.sm_capstone.ScheduleRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.sm_capstone.EmployID.board_part;
import static com.example.sm_capstone.EmployID.end_time;
import static com.example.sm_capstone.EmployID.request_reference;
import static com.example.sm_capstone.EmployID.start_time;


public class subCalendarAdapter extends RecyclerView.Adapter<subCalendarAdapter.ItemViewHolder> {
    private List<CalendarPost> datas;
    private Context mcontext;
    private ScheduleModify scheduleModify;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    String writer_name;
    String schedule_id;
    String start_time;
    String end_time;
    String date;
    String start_time2;
    String request;
    String request_reference;
    String user_name;
    String store_num;
    static RequestQueue requestQueue;
    private JSONArray idArray = new JSONArray();


    public  subCalendarAdapter(Context mcontext, List<CalendarPost> datas) {
        this.mcontext=mcontext;
        this.datas=datas;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if(mAuth.getCurrentUser()!=null){
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())
               .get()
               .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if(task.getResult()!=null){
                           user_name = (String)task.getResult().getData().get(EmployID.name);
                           store_num = (String)task.getResult().getData().get(EmployID.storeNum);
                           Log.d("확인","현재 사용자 이름입니다"+user_name);
                       }
                   }
               });
        }

        return new subCalendarAdapter.ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder,int position) {

        CalendarPost data=datas.get(position);
        holder.writer_name.setText(datas.get(position).getWriter_name());
        holder.start_time.setText(datas.get(position).getStart_time());
        holder.end_time.setText(datas.get(position).getEnd_time());
        holder.reference.setText(datas.get(position).getReference());
        date = data.getDate();
        start_time2 = data.getStart_time();
        final int pos = holder.getAdapterPosition();

//        writer_name = datas.get(pos).getWriter_name();
//        start_time = datas.get(pos).getStart_time();
//        end_time = datas.get(pos).getEnd_time();
//        request_reference = datas.get(pos).getRequest_reference();


        Log.d("onBindViewHolder테스트", "writer_name : "+datas.get(pos).getWriter_name());
        Log.d("onBindViewHolder테스트","schedule_id : "+datas.get(pos).getSchedule_id());
        Log.d("onBindViewHolder테스트", "start_time : "+datas.get(pos).getStart_time());
        Log.d("onBindViewHolder테스트", "end_time : "+datas.get(pos).getEnd_time());
        Log.d("onBindViewHolder테스트", "request : "+datas.get(pos).getRequest());
        Log.d("onBindViewHolder테스트", "pos.request : "+datas.get(pos).getRequest());
        Log.d("onBindViewHolder테스트", "request_reference : "+datas.get(pos).getRequest_reference());



        ////////////////////수정버튼//////////////////////
        ImageView btn_modify = holder.btn_modify;
        btn_modify.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Log.d("subCalendarAdapter", "modify버튼위치"+position);
                Log.d("subCalendarAdapter","선택한스케줄ID"+datas.get(pos).getSchedule_id());
                Log.d("subCalendarAdapter", "start_time변수값:"+datas.get(pos).getStart_time());
                Log.d("subCalendarAdapter", "end_time변수값:"+datas.get(pos).getEnd_time());

//                Intent intent = new Intent(mcontext, ScheduleModify.class);
//                intent.putExtra(schedule_id, schedule_id);

                if(datas.get(pos).getWriter_name().equals(user_name)){
                    scheduleModify = new ScheduleModify(mcontext, datas.get(pos).getSchedule_id());
                    scheduleModify.setCanceledOnTouchOutside(true);
                    scheduleModify.setCancelable(true);
                    scheduleModify.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    scheduleModify.show();
                }
                else
                {
                    Toast.makeText(mcontext, "작성자가 아닙니다", Toast.LENGTH_SHORT).show();
                }

            }
        });


        ///////////////////삭제버튼/////////////////////////////
        ImageView btn_delete = holder.btn_delete;
        btn_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Log.d("aaa", "delete버튼위치"+position);
                deleteDialog(datas.get(pos).getWriter_name(), datas.get(pos).getSchedule_id());
            }
        });

        ///////////////////////요청버튼////////////////////////////////////////
        ImageView btn_request = holder.btn_request;
        Log.d("확인", "request값:" + request);
        if(datas.get(pos).getRequest().equals("0")) {
            Log.d("subCalendarAdapter", "request=0일때" + datas.get(pos).getRequest());
            btn_request.setColorFilter(null);
        }
        if(datas.get(pos).getRequest().equals("1")){
            Log.d("subCalendarAdapter", "request=1일때" + datas.get(pos).getRequest());
            btn_request.setColorFilter(Color.parseColor("#008000"), PorterDuff.Mode.SRC_IN);
        }

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("subCalendarAdapter", "request버튼위치" + position);
                Log.d("subCalendarAdapter", "넘겨지는 schedule_id는" + datas.get(pos).getSchedule_id());
                if(datas.get(pos).getRequest().equals("0")) {
                    requestDialog(datas.get(pos).getWriter_name(), datas.get(pos).getSchedule_id());
                }
                if(datas.get(pos).getRequest().equals("1")){
                    acceptDialog(datas.get(pos).getWriter_name(), datas.get(pos).getSchedule_id(), datas.get(pos).getRequest(), datas.get(pos).getRequest_reference());
                }
            }
        });


        //아래에 선언한 버튼클릭리스너를 여기에 구현하시면 됩니다!
        //아마 포지션도 고려를 해야할거에요
    }

    @Override
    public int getItemCount() {
        return (null != datas ? datas.size() : 0);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView writer_name;
        private TextView start_time;
        private TextView end_time;
        private TextView reference;
        public ImageView btn_modify;
        public ImageView btn_delete;
        public ImageView btn_request;


        //여기에 calendar_list에 있는 버튼을 선언!!
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            writer_name=itemView.findViewById(R.id.calendar_name);
            start_time=itemView.findViewById(R.id.calendar_starttime);
            end_time=itemView.findViewById(R.id.calendar_endtime);
            reference=itemView.findViewById(R.id.calendar_reference);

            btn_modify = itemView.findViewById(R.id.btn_modify);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            btn_request = itemView.findViewById(R.id.btn_request);


        }
    }

    public void deleteDialog(final String writer,final String schedule_id){
        final Dialog builder = new Dialog(mcontext);
        builder.setContentView(R.layout.dialog_schedule_delete);
        builder.show();

        final Button delete_yes = (Button)builder.findViewById(R.id.delete_yes);
        final Button delete_no = (Button)builder.findViewById(R.id.delete_no);

        delete_yes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(writer.equals(user_name)){
                    mStore.collection("CalendarPost").document(schedule_id)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("확인", "삭제되었습니다");
                                }
                            });
                }
                else
                {
                    Toast.makeText(mcontext, "작성자가 아닙니다", Toast.LENGTH_SHORT).show();
                }

                builder.dismiss();
            }
        });
        delete_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
    }


    public void requestDialog(final String writer,final String schedule_id){
        final Dialog builder = new Dialog(mcontext);
        builder.setContentView(R.layout.dialog_schedule_request);
        builder.show();

        final Button request_yes = (Button)builder.findViewById(R.id.request_yes);
        final Button request_no = (Button)builder.findViewById(R.id.request_no);

        request_yes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(writer.equals(user_name)){
                    ////request화면으로 연결
                    Intent intent = new Intent(mcontext, ScheduleRequest.class);
                    intent.putExtra("schedule_id", schedule_id);
                    intent.putExtra("start_time", start_time);
                    intent.putExtra("end_time", end_time);
                    intent.putExtra("schedule_date",date);
                    intent.putExtra("start_time2",start_time2);
                    Log.d("확인","requestDialog넘겨지는 schedule_id는"+schedule_id);
                    Log.d("확인","requestDialog넘겨지는 start_time는"+start_time);
                    mcontext.startActivity(intent);
                }
                else
                {
                    Toast.makeText(mcontext, "작성자가 아닙니다", Toast.LENGTH_SHORT).show();
                }
                builder.dismiss();
            }
        });
        request_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
    }

    public void acceptDialog(final String writer, final String schedule_id, final String request, final String request_reference){
        final Dialog builder = new Dialog(mcontext);
        builder.setContentView(R.layout.dialog_schedule_accept);
        builder.show();
        mStore.collection("CalendarPost").whereEqualTo("schedule_id",schedule_id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(DocumentSnapshot snap : value.getDocuments()){
                            String token = String.valueOf(snap.getData().get("tokenNum"));
                            idArray.put(token);
                            System.out.println("토큰 잘나오나"+token);
                        }
                    }
                });
        TextView mreference = (TextView)builder.findViewById(R.id.request_reference);
        mreference.setText(request_reference);

        final Button accept_yes = (Button)builder.findViewById(R.id.accept_yes);
        final Button accept_no = (Button)builder.findViewById(R.id.accept_no);

        accept_yes.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()!=null){
                    Log.d("subCalendarAdapter","요청수락할 schedule_id:"+schedule_id);

                    JSONObject dataObj = new JSONObject();
                    try {
                        dataObj.put("title","대타요청이 수락되었습니다!");
                        dataObj.put("body",user_name + "님이 " + date + "근무 대타를 수락하였습니다.");

                        send(dataObj, schedule_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Map<String, Object> data = new HashMap<>();
                    data.put(EmployID.documentId, mAuth.getCurrentUser().getUid());
//                    data.put(EmployID.writer_id, mAuth.getCurrentUser().getUid());
                    data.put(EmployID.writer_name, user_name);
                    data.put(EmployID.request, "0");
                    mStore.collection("CalendarPost").document(schedule_id).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(mcontext.getApplicationContext(), "Accept complete", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                builder.dismiss();
            }
        });

        requestQueue = Volley.newRequestQueue(mcontext);

        accept_no.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
    }

    public void send(JSONObject input, String schedule_id){
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("priority","high");
            requestData.put("data",input);
            System.out.println("스케줄아이디:"+schedule_id);


            requestData.put("registration_ids",idArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ){
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key=AAAAAjmqEkM:APA91bE7afelkMdIe7zS11X5i1oiXsjDf1OGhYLBda6_fZYNzmvoHMWYx_baBLulkzQQz2JqmH6jZm8TSVhPzxMrAbsvJSRJJeTk0gVAWalOFiFh-VBmZMBduJ5xR9JwVoD5l6iGYbHb");

                return headers;
            }

            @Override
            protected String getParamsEncoding() {
                Map<String, String> params = new HashMap<String, String>();
                return super.getParamsEncoding();
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    public void checkNum(){
        if(mAuth.getCurrentUser()!=null){
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                store_num = (String)task.getResult().getData().get(EmployID.storeNum);
                                Log.d("subCalendarAdapter", "store_num : "+store_num);
                            }
                        }
                    });
        }
    }
}
