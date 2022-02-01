package com.example.sm_capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeMainActivity extends AppCompatActivity {

    private ImageView homeBtn, contactBtn, calendarBtn, myPageBtn;
    private ImageView staticImage, dynamicImage;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private String name, phoneNum, storeName, store_num, type; //매장번호
    private long backKeyPressedTime = 0;
    private Toast toast;
    static RequestQueue requestQueue;
    static String regId;
    private TextView todaySchedule, todayTime, txt_today_schedule;
    private JSONArray idArray = new JSONArray();
    private String user_name;
    private String accept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);

        staticImage = findViewById(R.id.staticImage);
        dynamicImage = findViewById(R.id.dynamicImage);
        homeBtn = findViewById(R.id.home_btn);
        contactBtn = findViewById(R.id.contact_btn);
        calendarBtn = findViewById(R.id.calendar_btn);
        myPageBtn = findViewById(R.id.mypage_btn);
        todaySchedule = findViewById(R.id.today_schedule);
        todayTime = findViewById(R.id.today_time);
        txt_today_schedule = findViewById(R.id.txt_today_schedule);

        if(mAuth.getCurrentUser()!=null){//User에 등록되어있는 작성자를 가져오기 위해서
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())//
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                store_num=(String)task.getResult().getData().get(EmployID.storeNum);
                                name=(String)task.getResult().getData().get(EmployID.name);
                                phoneNum=(String)task.getResult().getData().get(EmployID.phone_number);
                                storeName=(String)task.getResult().getData().get(EmployID.storeName);
                                type = (String)task.getResult().getData().get(EmployID.type);
                                accept=(String)task.getResult().getData().get(EmployID.accept);
                                findStoreCollegue(store_num);
                                todaySchedule(name);
                                if(accept.equals("no") || accept.equals("black"))
                                {
                                    staticImage.setEnabled(false);
                                    dynamicImage.setEnabled(false);
                                    homeBtn.setEnabled(false);
                                    contactBtn.setEnabled(false);
                                    calendarBtn.setEnabled(false);
                                    todaySchedule.setText("이용하시려면 매니저의 수락을 기다리세요");
                                    txt_today_schedule.setText("이용 불가");
                                    todayTime.setText("");
                                }
                                SharedPreferences preferences = getSharedPreferences("StoreInfo",MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("StoreNum",store_num);
                                editor.putString("StoreName",storeName);
                                editor.putString("Name",name);
                                editor.putString("PhoneNum",phoneNum);
                                editor.putString("Type",type);
                                editor.commit();

                                SharedPreferences preferences2 = getSharedPreferences("ContactInfo", MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = preferences2.edit();
                                editor2.putString("myType", type);
                                editor2.commit();
                            }
                        }
                    });
        }


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("확인", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        System.out.println("토큰이 발급되었습니다"+token);
                        SharedPreferences preferences = getSharedPreferences("token",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token",token);
                        editor.commit();
                        Map<String, Object> map = new HashMap<>();
                        map.put(EmployID.tokenNum, token);
                        mStore.collection(EmployID.user).document(mAuth.getCurrentUser().getUid()).update(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        System.out.println("발급성공");
                                    }
                                });
                        Log.d("확인", token);
                    }
                });




        staticImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeMainActivity.this, DynamicBoard.class);
                intent.putExtra("board_part","정적게시판");
                startActivity(intent);
            }
        });


        dynamicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeMainActivity.this, DynamicBoard.class);
                intent.putExtra("board_part","동적게시판");
                startActivity(intent);
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeMainActivity.this, HomeMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeMainActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeMainActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        myPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeMainActivity.this, MyPageActivity.class);
                startActivity(intent);
                finish();
            }
        });




//        Button pushTest = findViewById(R.id.pushTest);
//        pushTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                JSONObject dataObj = new JSONObject();
//                try {
//                    dataObj.put("title","대타를 구합니다!");
//                    dataObj.put("body","2020년 12월20일(예시)");
//                    send(dataObj);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//
//        requestQueue = Volley.newRequestQueue(getApplicationContext());
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
                                idArray.put(token);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void todaySchedule(String myname){

        user_name = myname;

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MM. d.");
        String getTime = dateFormat.format(mDate);
        Log.d("HomeActivity", "오늘날짜 : "+getTime);

        todaySchedule.setText(user_name+"님 오늘의 일정은 ");


        mStore.collection("CalendarPost")
                .whereEqualTo("date", getTime)
                .whereEqualTo("writer_name", user_name)
                .addSnapshotListener(
                        new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(value != null){
                                    for(DocumentSnapshot snap : value.getDocuments()){
                                        Map<String, Object> shot = snap.getData();
                                        String start_time = String.valueOf(shot.get(EmployID.start_time));
                                        String end_time = String.valueOf(shot.get(EmployID.end_time));
                                        Log.d("aaa", start_time + " 부터 " +end_time);


                                        CalendarPost data = new CalendarPost(user_name, start_time, end_time);

                                        todayTime.setText(start_time+"부터 "+end_time+"입니다.");
                                    }
                                }
                            }
                        }
                );
    }

    @Override
    public void onBackPressed(){
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
            toast.cancel();
        }
    }
}