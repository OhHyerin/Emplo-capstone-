package com.example.sm_capstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.Map;

import static com.example.sm_capstone.EmployID.date;

public class ScheduleRequest extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private EditText request_reference; //요청시 참고사항
    private String writer_id; //작성자 id
    private String writer_name; //작성자 name
    private String schedule_id; //스케줄넘버
    private String start_time, end_time;
    private Button btn_request; //요청하기 버튼
    private TextView date;
    private TextView starttime, endtime;
    private String select_date;

    private String store_num;
    private JSONArray idArray = new JSONArray();
    static RequestQueue requestQueue;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        Intent intent = getIntent();
        schedule_id = intent.getStringExtra("schedule_id");
        start_time = intent.getStringExtra("start_time");
        end_time = intent.getStringExtra("end_time");
        select_date = intent.getStringExtra("schedule_date");


        date = findViewById(R.id.request_date);     //해당 날짜
        starttime = findViewById(R.id.request_starttime);      //해당 출근 시간
        endtime = findViewById(R.id.request_endtime);          //해당 퇴근 시간
        request_reference = findViewById(R.id.request_reference);         //참고사항
        findViewById(R.id.request_save).setOnClickListener(this);      //완료 버튼
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        date.setText(select_date);
        starttime.setText(start_time);
        endtime.setText(end_time);


        if(mAuth.getCurrentUser()!=null){//User에 등록되어있는 작성자를 가져오기 위해서
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())//
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                writer_name=(String)task.getResult().getData().get(EmployID.name);//
                                writer_id=(String)task.getResult().getData().get(EmployID.documentId);
                                store_num=(String)task.getResult().getData().get(EmployID.storeNum);
                                findStoreCollegue(store_num);
                                Log.d("ScheduleRequest","현재 사용자 uid입니다:"+writer_id);
                                Log.d("ScheduleRequest","현재 사용자 이름입니다"+writer_name);
                                Log.d("ScheduleRequest", "현재 스케줄 id입니다:"+schedule_id);
                                Log.d("ScheduleRequest", "현재 스케줄 start_time입니다:"+start_time);
                                Log.d("ScheduleRequest", "현재 스케줄 end_time입니다:"+end_time);
                            }
                        }
                    });
        }


    }


    @Override
    public void onClick(View v) {
        if(mAuth.getCurrentUser()!=null){
//            String ScheduleID = mStore.collection("CalendarPost").document().getId();
            Intent intent = getIntent();
            schedule_id = intent.getStringExtra("schedule_id");
            String start_time2 = intent.getStringExtra("start_time2");
            String schedule_date = intent.getStringExtra("schedule_date");
            Log.d("확인", "해당 scheduleid:"+schedule_id);

            JSONObject dataObj = new JSONObject();
            try {
                dataObj.put("title","대타를 구합니다!");
                dataObj.put("body",schedule_date + " / " + start_time2);
                send(dataObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Map<String, Object> data = new HashMap<>();
            data.put(EmployID.documentId, mAuth.getCurrentUser().getUid());
            data.put(EmployID.request, "1");
            data.put(EmployID.request_reference, request_reference.getText().toString());
            mStore.collection("CalendarPost").document(schedule_id).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(getApplicationContext(),"Request complete",Toast.LENGTH_SHORT).show();
                }
            });

            finish();
        }

    }

    public void send(JSONObject input){
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("priority","high");

            requestData.put("data",input);
            System.out.println("개수는"+idArray.length());
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

    //내 매장번호와 같은 사람들을 찾아주는 함수
    public void findStoreCollegue(String store_num){
        mStore.collection(EmployID.user).whereEqualTo("storeNum",store_num)
                .addSnapshotListener(new EventListener<QuerySnapshot>()
                {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
                    {
                        try{
                            for(DocumentSnapshot snap : value.getDocuments()){
                                String token = String.valueOf(snap.getData().get(EmployID.tokenNum));
                                /* 자기 자신한테는 푸쉬가 안가게(최종배포때 주석 풀어서 배포)
                                SharedPreferences preferences = getSharedPreferences("token",MODE_PRIVATE);
                                String mytoken = preferences.getString("token","0");
                                if(!token.equals(mytoken)
                                */
                                idArray.put(token);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
