package com.example.sm_capstone.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sm_capstone.Board_Post.Home_Post;
import com.example.sm_capstone.CalendarActivity;
import com.example.sm_capstone.CreateList;
import com.example.sm_capstone.DynamicBoard;
import com.example.sm_capstone.EmployID;
import com.example.sm_capstone.R;
import com.example.sm_capstone.adapter.HomeAdapter;
import com.example.sm_capstone.adapter.SHomeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FirebaseAuth Auth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private Context context;
    private RecyclerView h_dynamicBoard,static_board;//동적게시판
    private HomeAdapter mAdapter;
    private SHomeAdapter sAdapter;
    private List<Home_Post> mDatas,sDatas;//동적게시판데이터와 정적게시판 데이터
    private TextView dynamic,staticboard;
    CreateList _list=new CreateList(getContext());

    /*public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.activity_home, container, false);

        context =container.getContext();
        dynamic=root.findViewById(R.id.dynamic);
        staticboard=root.findViewById(R.id.staticboard);

        Button.OnClickListener onClickListener=new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.btn_board:
                       Intent intent=new Intent(getActivity(),DynamicBoard.class);
                       intent.putExtra("board_part","동적게시판");//1은 동적게시판,2는 정적게시판
                        startActivity(intent);
                        break;
                    case R.id.btn_calendar:
                        Intent intent2=new Intent(getActivity(), CalendarActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.btn_staticboard:
                        Intent intent3=new Intent(getActivity(),DynamicBoard.class);
                        intent3.putExtra("board_part","정적게시판");//1은 동적게시판,2는 정적게시판
                        startActivity(intent3);
                        break;
                }
            }
        };
        Button D_board = (Button) root.findViewById(R.id.btn_board) ;
        Button Calendar=(Button)root.findViewById(R.id.btn_calendar);
        Button S_board=root.findViewById(R.id.btn_staticboard);
        Calendar.setOnClickListener(onClickListener);
        D_board.setOnClickListener(onClickListener) ;
        S_board.setOnClickListener(onClickListener);
        h_dynamicBoard=(RecyclerView)root.findViewById(R.id.home_recyclerview2);
        static_board=(RecyclerView) root.findViewById(R.id.home_recyclerview3);
        sDatas=new ArrayList<>();//정적게시판용
        sDatas.addAll(_list.createRecyclerviewList());
//        Log.d("확인",sDatas.get(0).toString());

        sAdapter= new SHomeAdapter(getContext(),sDatas);
        static_board.setAdapter(sAdapter);
        return root;
    }

    public void onStart() {
        super.onStart();

        mDatas = new ArrayList<>();//동적게시판 용
        sDatas=new ArrayList<>();//정적게시판용
        mStore.collection("Post")//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                // .whereEqualTo("board_part","동적게시판")//1은 동적, 2는 정적 게시판

        //sDatas.addAll(_list.createRecyclerviewList());
         

                .orderBy(EmployID.timestamp, Query.Direction.DESCENDING)//시간정렬순으로
                .addSnapshotListener(
                        new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (queryDocumentSnapshots != null) {
                                    mDatas.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                                    for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                        Map<String, Object> shot = snap.getData();
                                        String title = String.valueOf(shot.get(EmployID.title));
                                        //String board_part=String.valueOf(shot.get(EmployID.board_part));
                                        String writer_name=String.valueOf(shot.get(EmployID.name));
                                        Home_Post data = new Home_Post(writer_name,title);
                                        mDatas.add(data);//여기까지가 게시글에 해당하는 데이터 적용

                                    }
                                    mAdapter = new HomeAdapter(getContext(),mDatas);//mDatas라는 생성자를 넣어줌
                                    h_dynamicBoard.setAdapter(mAdapter);


                                }
                            }
                        });

    }*/
}