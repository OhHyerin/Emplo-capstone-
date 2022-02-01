package com.example.sm_capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactActivity extends AppCompatActivity {

    private ArrayList<Contact> mArrayList;
    private ContactAdapter mAdapter;
    private ContactAdapterVerEmployee mAdapterE;
    Activity a;
    private int size = 0;
    private TextView tv;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private String myType;
    private String store_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        a = ContactActivity.this;
        tv = findViewById(R.id.accept_tv);

        SharedPreferences preferences = getSharedPreferences("ContactInfo", MODE_PRIVATE);
        myType = preferences.getString("myType","0");
        System.out.println("내타입은:"+ myType);
        SharedPreferences preferences2 = getSharedPreferences("StoreInfo", MODE_PRIVATE);
        store_num = preferences2.getString("StoreNum","0");

        if(myType.equals("manager"))
        {
            tv.setVisibility(View.VISIBLE);
        }
        else
        {
            tv.setVisibility(View.GONE);
        }

        final RecyclerView mRecyclerView = findViewById(R.id.contact_recyclerview);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();
        mStore.collection("user")
                .whereEqualTo("storeNum", store_num)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null){
                            mArrayList.clear();
                            for (DocumentSnapshot doc : value.getDocuments()){
                                Map<String, Object> map = doc.getData();
                                String name = String.valueOf(map.get(EmployID.name));
                                String phoneNum = String.valueOf(map.get(EmployID.phone_number));
                                String accept = String.valueOf(map.get(EmployID.accept));
                                String id = String.valueOf(map.get(EmployID.documentId));
                                String black = String.valueOf(map.get(EmployID.accept));
                                String type = String.valueOf(map.get(EmployID.type));
                                Contact data = new Contact(name, phoneNum, accept, id);
                                if(!black.equals("black"))
                                    mArrayList.add(data);
                            }

                            if(myType.equals("manager"))
                            {
                                mAdapter = new ContactAdapter(mArrayList);
                                mRecyclerView.setAdapter(mAdapter);
                                mAdapter.setOnDeleteClickListener(new ContactAdapter.OnDeleteClickListener() {
                                    @Override
                                    public void onDeleteClick(View v, int position) {
                                        Contact item = mAdapter.getItem(position);
                                        String id = item.getContactId();
                                        deleteDialog(id, mAdapter);

                                    }
                                });

                                mAdapter.setOnOkClickListener(new ContactAdapter.OnOkClickListener() {
                                    @Override
                                    public void onOkClick(View v, int position) {
                                        Contact item = mAdapter.getItem(position);
                                        String id = item.getContactId();
                                        okDialog(id, mAdapter);
                                    }
                                });
                                System.out.println("여기맞나?");
                            }
                            else {
                                mAdapterE = new ContactAdapterVerEmployee(mArrayList);
                                mRecyclerView.setAdapter(mAdapterE);
                                System.out.println("여기맞나!");
                            }

                        }
                    }
                });


    }

    public void deleteDialog(final String id, final ContactAdapter adapter){
        final Dialog builder = new Dialog(this);
        builder.setContentView(R.layout.custom_dialog);
        builder.show();
        final Button yesbtn=(Button)builder.findViewById(R.id.yesButton);
        final Button nobtn=(Button)builder.findViewById(R.id.noButton);

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> data = new HashMap<>();
                data.put("accept", "black");
                mStore.collection("user").document(id).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                });
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

    public void okDialog(final String id, final ContactAdapter adapter){
        final Dialog builder = new Dialog(this);
        builder.setContentView(R.layout.custom_dialog_ok);
        builder.show();
        final Button yesbtn=(Button)builder.findViewById(R.id.yesButton);
        final Button nobtn=(Button)builder.findViewById(R.id.noButton);

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> data = new HashMap<>();
                data.put("accept", "yes");
                mStore.collection("user").document(id).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "수락 되었습니다.", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                });
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