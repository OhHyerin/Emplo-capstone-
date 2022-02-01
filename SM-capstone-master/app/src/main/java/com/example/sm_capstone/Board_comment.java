package com.example.sm_capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sm_capstone.Board_Post.Comment;
import com.example.sm_capstone.adapter.CommentAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board_comment extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private RecyclerView mCommentRecyclerView;
    private TextView c_title,c_text,c_writer,c_date;//게시글의 제목,내용,작성자
    private ImageView img1,img2;
    private CommentAdapter commentAdapter;
    private List<Comment> mcomment;
    private EditText comment_edit;//댓글작성;
    private String post_t,board_name,comment_post;//게시글의 제목, 게시판 위치(동적,정적),게시글의 uid
    int com_pos=0;
    private String photoUrl,post_id,current_user,user_name;
    private ActionBar actionBar;
    private int comment_num=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_comment);
        c_date=(TextView)findViewById(R.id.c_post_date);
        c_writer = (TextView) findViewById(R.id.Comment_writer);//본문 작성자
        c_title = (TextView) findViewById(R.id.Comment_title);//제목
        c_text = (TextView) findViewById(R.id.Comment_text);//본문
        comment_edit = (EditText) findViewById(R.id.Edit_comment);//댓글 작성 내용
        //img1 = (ImageView) findViewById(R.id.Comment_photo); //작성자 프로필 이미지
        img2 =  (ImageView) findViewById(R.id.Comment_photo2); //작성자가 올린 이미지
        mCommentRecyclerView = findViewById(R.id.comment_recycler);//코멘트 리사이클러뷰
        Intent intent = getIntent();//데이터 전달받기
        com_pos = intent.getExtras().getInt("position");
        c_writer.setText(intent.getStringExtra("writer_name"));
        c_text.setText(intent.getStringExtra("content"));
        c_title.setText(intent.getStringExtra("title"));
        c_date.setText(intent.getStringExtra("post_date"));
        post_id=intent.getStringExtra("post_id");//어떤 게시글인지
        current_user=mAuth.getCurrentUser().getUid();//현재 사용자의 uid
        board_name=intent.getStringExtra("board_part");//동적게시판인지 정적게시판인지
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        //게시글에 첨부된 사진
        getPostPhoto();

        if(mAuth.getCurrentUser()!=null){//유저의 이름을 가져오기 위해서
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())//
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                               user_name=(String)task.getResult().getData().get(EmployID.name);//
                                Log.d("확인","현재 사용자 이름입니다"+user_name);
                            }
                        }
                    });
        }
        findViewById(R.id.comment_button).setOnClickListener(this);

        comment_num=Integer.parseInt(intent.getStringExtra("comment_num"));
    }
    @Override
    protected void onStart() {
        super.onStart();
        //if(Integer.toString(com_pos)==comment_p) {//게시글의 위치와 댓글의 위치가 같은거를 보여줌
        mcomment = new ArrayList<>();//리사이클러뷰에 표시할 댓글 목록
        mStore.collection("Comment")
                .whereEqualTo("comment_post", post_id)//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                .orderBy(EmployID.timestamp, Query.Direction.ASCENDING)//시간정렬순으로 이건 처음에 작성한게 제일 위로 올라감 게시글과 반대
                .addSnapshotListener(new EventListener<QuerySnapshot>(){
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            mcomment.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                            for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                Map<String, Object> shot = snap.getData();
                                String w_name=String.valueOf(shot.get(EmployID.w_comment));
                                String documentId = String.valueOf(shot.get(EmployID.documentId));
                                String comment = String.valueOf(shot.get(EmployID.comment));
                                String board_part= String.valueOf(shot.get(EmployID.board_part));
                                String num_comment=String.valueOf(shot.get(EmployID.comment_post));
                                Comment data = new Comment(w_name,comment, documentId,board_part,num_comment);
                                mcomment.add(data);//여기까지가 게시글에 해당하는 데이터 적용
                            }
                        }
                        commentAdapter = new CommentAdapter(mcomment);//mDatas라는 생성자를 넣어줌
                        mCommentRecyclerView.setAdapter(commentAdapter);
                    }

                });
    }

    @Override
    public void onClick(View v) {
        comment_num++;//댓글작성시 개수 증가
        if (mAuth.getCurrentUser() != null) {//새로 Comment란 컬렉션에 넣어줌// 공백일경우 작동안됨
            Map<String, Object> data = new HashMap<>();
            data.put(EmployID.documentId,mAuth.getCurrentUser().getUid());//유저 고유번호
            data.put(EmployID.comment,comment_edit.getText().toString());//게시글 내용
            data.put(EmployID.timestamp, FieldValue.serverTimestamp());//파이어베이스 시간을 저장 그래야 게시글 정렬이 시간순가능
            data.put(EmployID.w_comment,user_name);
            Intent intent = getIntent();//데이터 전달받기
            //data.put(EmployID.title,post_t);//게시글의 제목을 넣어준다 비교하기위해서

            com_pos = intent.getExtras().getInt("position");//
            //Log.d("확인","위치"+com_pos);
            data.put(EmployID.post_num, Integer.toString(com_pos));//작성된 게시판의 위치를 댓글에 저장
            data.put(EmployID.comment_post,post_id);
            mStore.collection("Comment").add(data);//댓글 콜렉션에 저장
            mStore.collection("Post").document(post_id).update("post_comment_num",Integer.toString(comment_num));
            View view = this.getCurrentFocus();//작성버튼을 누르면 에딧텍스트 키보드 내리게 하기
            if (view != null) {//댓글작성시 키보드 내리고 댓글에 작성한 내용 초기화
                InputMethodManager hide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                hide.hideSoftInputFromWindow(view.getWindowToken(), 0);
                comment_edit.setText("");
            }

            finish();
            startActivity(intent);
        }
    }

    public void getPostPhoto(){
        Intent intent = getIntent();//데이터 전달받기
         String spost_photo=intent.getExtras().getString("photo_url");
        Log.d("String spost값", spost_photo);
        if ( !spost_photo.equals("null")) {
            Log.d("피포토사진있음", intent.getExtras().getString("photo_url"));
            Picasso.get()
                    .load(intent.getStringExtra("photo_url"))
                    .into(img2);
        }
        else
        {
            Log.d("사진빔", "사진이 비어있어요");
            img2.getLayoutParams().height= 0;
            img2.setVisibility(View.GONE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comment_toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){//게시글 작성자와 현재 사용자와의 uid가 같으면 기능 수행가능하게
        Intent intent = getIntent();//데이터 전달받기
        String p_writer=intent.getExtras().getString("writer_name");
        switch (item.getItemId()){
            case R.id.first:
                deleteDialog(p_writer);
                break;
            case R.id.second:
                if(p_writer.equals(user_name)) {
                    Intent intent1=new Intent(this,Board_Update.class);
                    intent1.putExtra("post_id",post_id);
                    Log.d("확인","board_part확인:"+board_name);
                    intent1.putExtra("board_part","동적게시판");
                    startActivity(intent1);//게시글 수정
                }
                else
                {
                    Toast.makeText(this,"작성자가 아닙니다.",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    public void deleteDialog(final String writer){
        final Dialog builder =new Dialog(this);
        //builder.setTitle("삭제하기").setMessage("삭제하시겠습니까?");
        builder.setContentView(R.layout.custom_dialog);
        builder.show();

        final Button yesbtn=(Button)builder.findViewById(R.id.yesButton);
        final Button nobtn=(Button)builder.findViewById(R.id.noButton);
        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(writer.equals(user_name)) {
                    mStore.collection("Post").document(post_id)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("확인", "삭제되었습니다.");
                                    finish();
                                }
                            });
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"작성자가 아닙니다.",Toast.LENGTH_SHORT).show();
                }
                builder.dismiss();
            }
        });
        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

    }
}
