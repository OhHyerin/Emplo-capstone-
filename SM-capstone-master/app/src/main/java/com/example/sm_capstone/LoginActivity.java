package com.example.sm_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.example.sm_capstone.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity<CheckB> extends AppCompatActivity implements View.OnClickListener {

    private Button btn_login;
    private Button btn_register;
    private EditText emailEdit,passEdit;
    private CheckBox autoLogin;

    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private String store_num;
    private FirebaseUser currentUser;
    private CheckBox autoCheck;
    Activity a;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*if(mAuth.getCurrentUser()!=null){//User에 등록되어있는 작성자를 가져오기 위해서
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())//
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                store_num=(String)task.getResult().getData().get(EmployID.storeNum);//
                                System.out.println("스플래쉬 매장:"+store_num);

                            }
                        }
                    });
        }*/

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);

//        getSupportActionBar().hide();

        a = LoginActivity.this;
        loading = findViewById(R.id.loadingBar);
        loading.setVisibility(View.INVISIBLE);
        emailEdit = findViewById(R.id.emailEdit);
        passEdit = findViewById(R.id.passEdit);
        autoCheck=(CheckBox)findViewById(R.id.btn_chklogin);
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        autoCheck.setChecked(pref.getBoolean("autocheck",false));
    }
    @Override// 자동로그인 메소드 추가예정->xml linearlayout형태로 바꿔야함
    public void onStart(){
        super.onStart();
        currentUser = mAuth.getCurrentUser();

        if(autoCheck.isChecked()==true)
        {
            if (currentUser != null) {
                startActivity(new Intent(this, HomeMainActivity.class));
                finish();
            }
        }
    }
    public void onStop()
    {
        super.onStop();
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        CheckBox autoCheck = (CheckBox)findViewById(R.id.btn_chklogin);
        editor.putBoolean("autocheck", autoCheck.isChecked());
        editor.commit();
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_login:
                String u_id=emailEdit.getText().toString();
                String u_pw=passEdit.getText().toString();
                if(u_id.equals("") || u_pw.equals(""))
                    ((GlobalMethod)getApplicationContext()).LoginBlank(a);

                else if(u_id!=null && u_pw!=null){
                    loginStart(u_id,u_pw);
                }
                break;
            case R.id.btn_register:
                startActivity(new Intent(this,SignupActivity.class));
                break;
        }
    }
    public void loginStart(String id,String pw){
        loading.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(id,pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()) {
                    loading.setVisibility(View.INVISIBLE);
                    ((GlobalMethod)getApplicationContext()).LoginFail(a);
                }
                else{
                    currentUser =mAuth.getCurrentUser();//현재 유저확인
                    Toast.makeText(LoginActivity.this,"로그인 성공",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeMainActivity.class));
                    finish();
                }
            }
        });
    }


}
