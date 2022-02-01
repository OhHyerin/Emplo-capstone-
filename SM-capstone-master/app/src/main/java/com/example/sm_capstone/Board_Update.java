package com.example.sm_capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Board_Update extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth=FirebaseAuth.getInstance();//사용자 정보 가져오기
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();
    private EditText mTitle,mContents;//제목, 내용
    private ImageView post_photo;
    private ProgressBar post_progressBar;
    private String photoUrl; //사진 저장 변수
    private String board_part,post_id,writer_id;
    private Uri uriProfileImage;
    private ImageView post_imageView;
    private String postImageUrl;
    private static final int CHOOSE_IMAGE = 101;
    private String writer_name;//작성자
    private String store_num;
    private ImageView cross_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);
        mTitle=findViewById(R.id.Post_write_title);//제목 , item_post.xml의 변수와 혼동주의
        mContents=findViewById(R.id.Post_write_contents);
        findViewById(R.id.Post_save).setOnClickListener(this);
        post_photo =findViewById(R.id.Post_photo);
        post_imageView = findViewById(R.id.post_imageview);
        post_imageView.setVisibility(View.INVISIBLE);
        post_progressBar = findViewById(R.id.post_progressbar);

        cross_btn = findViewById(R.id.cross_btn);
        cross_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(mAuth.getCurrentUser()!=null){//User에 등록되어있는 작성자를 가져오기 위해서
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())//
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                writer_name=(String)task.getResult().getData().get(EmployID.name);//
                                writer_id=(String)task.getResult().getData().get(EmployID.documentId);
                                store_num= (String) task.getResult().getData().get(EmployID.storeNum);
                                Log.d("확인","현재 사용자 uid입니다:"+writer_id);
                                Log.d("확인","현재 사용자 이름입니다"+writer_name);
                            }
                        }
                    });
        }

        post_photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showImageChoose();
            }
        });
        //회원 개인사진 불러오기
        loadUserPhoto();
    }
    @Override
    public void onClick(View v) {
        if(mAuth.getCurrentUser()!=null){
            //String PostID=mStore.collection("Post").document().getId();//제목이 같아도 게시글이 겹치지않게
            Intent intent=getIntent();
            board_part=intent.getStringExtra("board_part");//동적게시판은 1 정적게시판은 2 예정
            post_id=intent.getStringExtra("post_id");
            Log.d("확인","여기는 게시글 작성:"+board_part);
            Map<String,Object> data=new HashMap<>();
            data.put(EmployID.documentId,mAuth.getCurrentUser().getUid());//유저 고유번호
            data.put(EmployID.title,mTitle.getText().toString());//게시글제목
            data.put(EmployID.contents,mContents.getText().toString());//게시글 내용
            data.put(EmployID.timestamp, FieldValue.serverTimestamp());//파이어베이스 시간을 저장 그래야 게시글 정렬이 시간순가능
            data.put(EmployID.name,writer_name);
            data.put(EmployID.post_id,post_id);//게시글 ID번호
            data.put(EmployID.writer_id,writer_id);
            data.put(EmployID.board_part,board_part);
            data.put(EmployID.writer_id,writer_id);
            data.put(EmployID.post_url,photoUrl);
            data.put(EmployID.storeNum,store_num);
            if(!TextUtils.isEmpty(postImageUrl))
            {
                data.put(EmployID.post_photo,postImageUrl);//게시글에 포함된 사진
            }
            mStore.collection("Post").document(post_id).update(data).addOnCompleteListener(new OnCompleteListener<Void>(){

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(),"Update complite",Toast.LENGTH_SHORT).show();
                }
            });//Post라는 테이블에 데이터를 입력하는것/
            finish();
        }
    }
    private void showImageChoose(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Post Image"), CHOOSE_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData()!= null)
        {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriProfileImage);
                post_imageView.setImageBitmap(bitmap);
                post_imageView.setVisibility(View.VISIBLE);

                uploadImageToFirebaseStorage();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis() + ".jpg");

        if(uriProfileImage !=null)
        {
            post_progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            post_progressBar.setVisibility(View.GONE);
                            profileImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    postImageUrl=task.getResult().toString();
                                    Log.i("postURL",postImageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            post_progressBar.setVisibility(View.GONE);

                        }
                    });
        }
    }

    public void loadUserPhoto(){
        FirebaseUser user= mAuth.getCurrentUser();
        if(user!=null) {
            if (user.getPhotoUrl() == null) {
                Log.d("사진", "포토유알엘이 비어있어요.");
            }
            if (user.getPhotoUrl() != null) {
                photoUrl = user.getPhotoUrl().toString();
            }
        }
    }
}
