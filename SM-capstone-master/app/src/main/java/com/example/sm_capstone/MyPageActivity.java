package com.example.sm_capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.KakaoParameterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyPageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText nameEdit, phoneEdit, storeNameEdit, storeNumEdit;
    private TextView postv;
    String name, phoneNum, storeName, storeNum,store_id;
    String pos; //직원인지 매니저인지 감지
    private ImageButton logout_btn, modify_btn;
    Activity a;
    private ImageButton kakao_btn;
    private boolean permit;
    private SharedPreferences preferences;
    static RequestQueue requestQueue;
    private JSONArray idArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        nameEdit = findViewById(R.id.nameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        storeNameEdit = findViewById(R.id.mpStoreNameEdit);
        storeNumEdit = findViewById(R.id.mpStoreNumEdit);
        postv = findViewById(R.id.postv);
        logout_btn = findViewById(R.id.logout_btn);
        modify_btn = findViewById(R.id.modify_btn);
        kakao_btn = findViewById(R.id.kakao_btn);
        a = MyPageActivity.this;
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        preferences = getSharedPreferences("StoreInfo",MODE_PRIVATE);
        name = preferences.getString("Name","0");
        phoneNum = preferences.getString("PhoneNum","0");
        storeName = preferences.getString("StoreName","0");
        storeNum = preferences.getString("StoreNum","0");
        pos = preferences.getString("Type","0");
        nameEdit.setText(name);
        phoneEdit.setText(phoneNum);
        storeNameEdit.setText(storeName);
        storeNumEdit.setText(storeNum);
        postv.setText(pos);

        if(pos.equals("manager")){
            String storeNum2 = storeNumEdit.getText().toString();
            storeNumEdit.setText(storeNum2+"(수정불가)");
            storeNumEdit.setTextColor(Color.RED);
            storeNumEdit.setEnabled(false);
        }
        //추후에 허용기능이 추가되면 permit=true인 employee만 수정할 수 있게 함
        //직원은 매니저의 승인을 받고 매장번호 변경이 가능함
        else if(pos.equals("employee")){
            storeNumEdit.setEnabled(true);
        }


        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MyPageActivity.this);
                dlg.setMessage("로그아웃 하시겠습니까?");
                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dlg.show();
            }
        });

        modify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findManager(storeNumEdit.getText().toString());
                AlertDialog.Builder dlg = new AlertDialog.Builder(MyPageActivity.this);
                dlg.setMessage("수정 하시겠습니까?");
                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> userMap = new HashMap<>();
                        Map<String, Object> userMap2 = new HashMap<>();
                        String myName = nameEdit.getText().toString();
                        String myPhone = phoneEdit.getText().toString();
                        String myStoreName = storeNameEdit.getText().toString();
                        String myStoreNum = storeNumEdit.getText().toString();
                        userMap.put(EmployID.name, myName);
                        userMap.put(EmployID.phone_number, myPhone);
                        userMap.put(EmployID.storeName, myStoreName);
                        userMap2.putAll(userMap); //얕은복사
                        userMap.put(EmployID.storeNum, myStoreNum);

                        if(pos.equals("manager"))
                        {
                            mStore.collection("user").document(user.getUid()).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ((GlobalMethod)getApplicationContext()).modifyOK(a);
                                }
                            });
                            mStore.collection("Store").document(storeNumEdit.getText().toString()).update(nameEdit.getText().toString(),pos).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }

                        else //직원인데
                        {
                            if(!storeNum.equals(myStoreName)) //변경했다면
                            {
                                sendPush();
                                mStore.collection("user").document(user.getUid()).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(),"매장 번호는 매니저의 수락 후 변경됩니다.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else //똑같다면
                            {
                                mStore.collection("user").document(user.getUid()).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        ((GlobalMethod)getApplicationContext()).modifyOK(a);
                                    }
                                });
                                mStore.collection("Store").document(storeNumEdit.getText().toString()).update(nameEdit.getText().toString(),pos).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            }
                        }

                        //강호동 : 직원 or  송민호: 매니저 이런식으로 표시할 예정
                        //storeMap.put(user.getEmail(), pos);

                    }
                });
                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dlg.show();
            }
        });

    }

    public void sendPush(){
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("title","매장 가입 요청이 들어왔습니다!");
            dataObj.put("body",name+"님이 가입을 신청했습니다. 수락을 눌러주세요!");
            send(dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void send(JSONObject input){
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("priority","high");

            requestData.put("data",input);
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

    public void findManager(String num){
        idArray = new JSONArray();
        mStore.collection(EmployID.user).whereEqualTo("storeNum",num)
                .whereEqualTo(EmployID.type,"manager")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        try{
                            for(DocumentSnapshot snap : value.getDocuments()){
                                String token = String.valueOf(snap.getData().get(EmployID.tokenNum));
                                System.out.println("샌드토큰은:" + token);
                                idArray.put(token);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void kakaoLink(View view){
        FeedTemplate params = FeedTemplate.
                newBuilder(ContentObject.newBuilder("Emplo","https://image.genie.co.kr/Y/IMAGE/IMG_ALBUM/081/191/791/81191791_1555664874860_1_600x600.JPG",
                        LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com").build())
                        .setDescrption("매장번호를 입력해주세요:"+storeNum)
                        .build())

                .addButton(new ButtonObject("앱에서보기", LinkObject.newBuilder()
                        .setMobileWebUrl("https://developers.kakao.com")
                        .setAndroidExecutionParams("key1=value1")
                        .setIosExecutionParams("key1=value1").build()))
                .build();
        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {}

            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });
    }

    public void onBackPressed(){
        Intent intent = new Intent(MyPageActivity.this, HomeMainActivity.class);
        startActivity(intent);
        finish();
    }
}