package com.example.sm_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private Button val_Button;//중복확인버튼
    private Button val_Button2; //매장번호 중복확인 버튼
    private Button regButton;
    private EditText nameEdit, passEdit,passChkEdit,phoneNumEdit,emailEdit;
    private EditText storeNumEdit, storeNameEdit;
    private ImageView storeNumImage, storeNameImage;
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    Activity a;
    private RadioGroup posGroup;
    private RadioButton manager;//관리자인지 직원인지
    private RadioButton employee;
    int dep;
    Boolean check = false;
    String type;//관리자, 직원 저장
    int storeCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_acitivity);
        setTitle("회원가입");
        a = SignupActivity.this;
        storeCheck = 0; //기본0 1은 중복체크완료
        //getSupportActionBar().hide();

        val_Button = findViewById(R.id.valButton);
        regButton = findViewById(R.id.regButton);

        nameEdit = findViewById(R.id.username);
        passEdit = findViewById(R.id.password);
        passChkEdit = findViewById(R.id.passCheck);
        phoneNumEdit = findViewById(R.id.phoneNum);
        emailEdit = findViewById(R.id.email);
        storeNameEdit = findViewById(R.id.storeNameLayout);
        storeNumEdit = findViewById(R.id.storeNumLayout);
        val_Button2 = findViewById(R.id.valButton2);
        storeNumImage = findViewById(R.id.storeNumImage);
        storeNameImage = findViewById(R.id.storeNameImage);
        //checkBox
        posGroup = findViewById(R.id.posGroup);
        manager = findViewById(R.id.manager);
        employee = findViewById(R.id.employee);

        //직위 체크안했을때는 숨기기
        storeNameEdit.setVisibility(View.GONE); storeNumEdit.setVisibility(View.GONE);
        storeNumImage.setVisibility(View.GONE); storeNameImage.setVisibility(View.GONE); val_Button2.setVisibility(View.GONE);

        RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener =new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.employee) {
                    type="employee";
                    storeNameEdit.setVisibility(View.GONE); storeNumEdit.setVisibility(View.GONE);
                    storeNumImage.setVisibility(View.GONE); storeNameImage.setVisibility(View.GONE); val_Button2.setVisibility(View.GONE);
                }
                else if(checkedId == R.id.manager){
                    type = "manager";
                    storeNameEdit.setVisibility(View.VISIBLE); storeNumEdit.setVisibility(View.VISIBLE);
                    storeNumImage.setVisibility(View.VISIBLE); storeNameImage.setVisibility(View.VISIBLE); val_Button2.setVisibility(View.VISIBLE);
                }
                else{
                    type="employee";
                    storeNameEdit.setVisibility(View.VISIBLE); storeNumEdit.setVisibility(View.VISIBLE);
                    storeNumImage.setVisibility(View.VISIBLE); storeNameImage.setVisibility(View.VISIBLE); val_Button2.setVisibility(View.VISIBLE);
                }
            }
        };
        posGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);


        val_Button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDuplicate();
            }
        });
        val_Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDuplicate2();
            }
        });
        //regButton ->회원가입 완료버튼
        regButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerInfo();

            }
        });

    }

    public void checkDuplicate(){//아이디 중복확인 메소드
        mAuth.fetchSignInMethodsForEmail(emailEdit.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if(task.getResult().getSignInMethods().size()==0){
                            ((GlobalMethod)getApplicationContext()).idPossible(a);
                            val_Button.setEnabled(false);
                            val_Button.setTextColor(Color.GREEN);
                            val_Button.setText("사용가능");
                        }
                        else if(emailEdit.getText().toString()=="")
                            Toast.makeText(getApplicationContext(), "아이디를 입력하세요",Toast.LENGTH_SHORT).show();
                        else{
                            ((GlobalMethod)getApplicationContext()).idDuplicate(a);
                        }

                    }
                });
    }

    public void checkDuplicate2(){//매장번호 중복확인 메소드
        mstore.collection("Store").document(storeNumEdit.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) //매장번호가 이미 존재하면
                    {
                        ((GlobalMethod)getApplicationContext()).storeDuplicate(a);
                        storeCheck = 1;
                    }
                    else //없으면
                    {
                        ((GlobalMethod)getApplicationContext()).storePossible(a);
                        storeCheck = 2;
                        val_Button2.setEnabled(false);
                        val_Button2.setTextColor(Color.GREEN);
                        val_Button2.setText("사용가능");
                    }
                }
            }
        });
    }

    public void registerInfo() {//회원가입 등록 메소드
        if (val_Button.isEnabled()) {
            ((GlobalMethod)getApplicationContext()).idChkPlz(a);
        }
        else if(manager.isChecked() && val_Button2.isEnabled())
            ((GlobalMethod)getApplicationContext()).storeChkPlz(a);
        else {
            Toast.makeText(getApplicationContext(), "잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
            mAuth.createUserWithEmailAndPassword(emailEdit.getText().toString(), passEdit.getText().toString()).
                    addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user!=null) {
                                    Map<String, Object> userMap = new HashMap<>();

                                    //EmploID란 클래스를 통해서 사용자별 정보를 객체별로 저장하는 클래스
                                    userMap.put(EmployID.documentId, user.getUid());//고유 식별번호
                                    userMap.put(EmployID.name, nameEdit.getText().toString());
                                    userMap.put(EmployID.phone_number, phoneNumEdit.getText().toString());
                                    userMap.put(EmployID.email, emailEdit.getText().toString());
                                    userMap.put(EmployID.password, passEdit.getText().toString());
                                    userMap.put(EmployID.type, type);
                                    userMap.put(EmployID.storeName,storeNameEdit.getText().toString());
                                    userMap.put(EmployID.storeNum,storeNumEdit.getText().toString());
                                    if(type.equals("manager"))
                                        userMap.put(EmployID.accept, "yes");
                                    else
                                        userMap.put(EmployID.accept, "no");

                                    Log.d("확인","가입자 형식:"+type);
                                    Log.d("확인","documentId:"+mAuth.getCurrentUser().getUid());
                                    Log.d("확인","name:"+nameEdit.getText().toString());
                                    Log.d("확인","phone_number:"+phoneNumEdit.getText().toString());
                                    Log.d("확인","email:"+ emailEdit.getText().toString());
                                    Log.d("확인","password:"+ passEdit.getText().toString());
                                    Log.d("확인","employ:"+ EmployID.user);
                                    mstore.collection(EmployID.user).document(user.getUid()).set(userMap, SetOptions.merge());
                                    //매니저로 가입할 때는 매장고유번호를 부여하며 store컬렉션에 매장을 추가해줌.
                                    if(type.equals("manager")){
                                        Map<String, Object> storeMap = new HashMap<>();
                                        storeMap.put(EmployID.storeNum, storeNumEdit.getText().toString());
                                        storeMap.put(EmployID.storeName, storeNameEdit.getText().toString());
                                        storeMap.put("manager", nameEdit.getText().toString());
                                        storeMap.put("managerID",user.getUid());
                                        storeMap.put(EmployID.timestamp, FieldValue.serverTimestamp());
                                        mstore.collection("Store").document(storeNumEdit.getText().toString()).set(storeMap, SetOptions.merge());
                                    }
                                    finish();
                                }
                            } else {
                                Toast.makeText(SignupActivity.this, "비밀번호는 6자리 이상 입력해주세요",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}