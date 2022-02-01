package com.example.sm_capstone;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.sm_capstone.Board_Post.Home_Post;
import com.example.sm_capstone.adapter.HomeAdapter;
import com.example.sm_capstone.adapter.SHomeAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateList {
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private final Context context;

    private SHomeAdapter sAdapter;
    private HomeAdapter mAdapter;

    public CreateList(Context context){
        this.context=context;
    }
   public List createRecyclerviewList(){
      final List<Home_Post>sDatas;
        //mDatas = new ArrayList<>();//동적게시판 용
        sDatas=new ArrayList<>();//정적게시판용
        mStore.collection("Post")//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                // .whereEqualTo("board_part","동적게시판")//1은 동적, 2는 정적 게시판
                .orderBy(EmployID.timestamp, Query.Direction.DESCENDING)//시간정렬순으로
                .addSnapshotListener(
                        new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (queryDocumentSnapshots != null) {
                                    sDatas.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                                    for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                        Map<String, Object> shot = snap.getData();
                                        String title = String.valueOf(shot.get(EmployID.title));
                                        String writer_name=String.valueOf(shot.get(EmployID.writer_name));
                                        Home_Post data = new Home_Post(writer_name,title);
                                        sDatas.add(data);//여기까지가 게시글에 해당하는 데이터 적용
                                    }

                                }
                            }
                        });
        return sDatas;
    }
}
