package com.example.sm_capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sm_capstone.Board_Post.Home_Post;
import com.example.sm_capstone.Board_Post.Post;
import com.example.sm_capstone.adapter.BoardAdapter;
import com.example.sm_capstone.adapter.HomeAdapter;

import com.example.sm_capstone.adapter.SHomeAdapter;
import com.example.sm_capstone.ui.home.HomeFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity  implements  BoardAdapter.EventListener{

    private Button btn_home,btn_mypage,btn_static;
    private FirebaseAuth Auth = FirebaseAuth.getInstance();
    private Context context;
    private RecyclerView dynamicBoard;//???????????????
    private ImageView logo_btn;
    private HomeAdapter mAdapter;
    private SHomeAdapter sAdapter;
    private List<Home_Post> mDatas, sDatas;
    private String store_num;
    private Button btn_logou;//????????????????????? ???????????? ??????
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private long backKeyPressedTime = 0;
    private Toast toast;
    private TextView dynamic,staticboard;
    private RecyclerView h_dynamicBoard,static_board;//???????????????
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();//????????? ?????? ????????????
    private TextView todaySchedule, todayTime;
    private String user_name;
    private String start_time, end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        System.out.println("?????? ????????????");
        dynamic=findViewById(R.id.dynamic);
        staticboard=findViewById(R.id.staticboard);
        dynamicBoard=findViewById(R.id.recyclerview);
        h_dynamicBoard=(RecyclerView)findViewById(R.id.home_recyclerview2);
        static_board=(RecyclerView)findViewById(R.id.home_recyclerview3);
        todaySchedule=findViewById(R.id.today_schedule);
        todayTime=findViewById(R.id.today_time);


        SharedPreferences preferences = getSharedPreferences("StoreInfo",MODE_PRIVATE);
        store_num = preferences.getString("StoreNum","0");
        logo_btn=findViewById(R.id.logo);
        logo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,HomeMainActivity.class));
                finish();
            }
        });


        user_name = preferences.getString("Name", "0");
        Log.d("onCreate", "user_name : "+user_name);

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MM. d.");
        String getTime = dateFormat.format(mDate);
        Log.d("HomeActivity", "???????????? : "+getTime);

        todaySchedule.setText(user_name+"??? ????????? ????????? ");


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
                                        Log.d("aaa", start_time + " ?????? " +end_time);


                                        CalendarPost data = new CalendarPost(user_name, start_time, end_time);

                                        todayTime.setText(start_time+"?????? "+end_time+"?????????.");
                                    }
                                }
                            }
                        }
                );


    }

    @Override
    public void onStart() {
        super.onStart();
        mDatas = new ArrayList<>();//??????????????? ???
        sDatas=new ArrayList<>();//??????????????????
        mStore.collection("Post")//????????????????????? ?????? ?????????????????? ????????? ??????
                // .whereEqualTo("board_part","???????????????")//1??? ??????, 2??? ?????? ?????????
                .orderBy(EmployID.timestamp, Query.Direction.DESCENDING)//?????????????????????
                .addSnapshotListener(
                        new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (queryDocumentSnapshots != null) {
                                    mDatas.clear();//?????? ????????? ??????????????? ?????? ?????????????????? ???????????? ?????? ??????
                                    sDatas.clear();
                                    for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                        Map<String, Object> shot = snap.getData();
                                        String title = String.valueOf(shot.get(EmployID.title));
                                        //String board_part=String.valueOf(shot.get(EmployID.board_part));
                                        String writer_name=String.valueOf(shot.get(EmployID.name));
                                        String post_storenum = String.valueOf(shot.get(EmployID.storeNum));
                                        Home_Post data = new Home_Post(writer_name,title);
                                        String board_type = (String) snap.getData().get("board_part");
                                        if(board_type == null || post_storenum == null)
                                        {System.out.println("??????");}
                                        else if(board_type.equals("???????????????") && post_storenum.equals(store_num))
                                        {mDatas.add(data);}//??????????????? ???????????? ???????????? ????????? ??????
                                        else if(board_type.equals("???????????????") && post_storenum.equals(store_num))
                                            sDatas.add(data);
                                    }
                                    mAdapter = new HomeAdapter(getApplicationContext(),mDatas);//mDatas?????? ???????????? ?????????
                                    sAdapter = new SHomeAdapter(getApplicationContext(),sDatas);
                                    h_dynamicBoard.setAdapter(mAdapter);
                                    static_board.setAdapter(sAdapter);
                                }
                            }
                        });



    }

    @Override
    public boolean onOptionItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onItemClicked(int position) {

    }

}
