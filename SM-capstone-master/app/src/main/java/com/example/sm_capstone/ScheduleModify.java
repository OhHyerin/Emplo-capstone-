package com.example.sm_capstone;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.sm_capstone.adapter.subCalendarAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ScheduleModify extends Dialog implements View.OnClickListener, TimePicker.OnTimeChangedListener{

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private Context context;
    private ImageView btn_add_submit; //완료버튼
    private String writer_id; //작성자 넘버
    private String writer_name; //작성자 이름
    private String date; //날짜
    private String schedule_id; //스케즐넘버
    private String request = "0";  //대타신청 유무
    private EditText mreference; //참고사항
    private int hourOfDay1, hourOfDay2;
    private int minute1, minute2;

    private Calendar c1, c2;
    private TimePicker start_time, end_time;

    DateFormat fmtDateAndTime = DateFormat.getDateInstance();
    TextView dateAndTimeLabel;
    TextView startTimeLabel, endTimeLabel;
    Calendar dateAndTime = Calendar.getInstance();
    TextView name;
    Intent intent;

    //Listener설정과 설정된 날자를 Calendar 객체에 설정 : set눌렀을 때 처리
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }
    };
    //Listener 설정과 설정된 시간을 Calendar 객체에 설정:set눌렀을 때 처리
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            updateDateLabel();
        }
    };

    private void updateDateLabel() {
        dateAndTimeLabel.setText(fmtDateAndTime.format(dateAndTime.getTime()));
    }


    public ScheduleModify(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public ScheduleModify(@NonNull Context context, String schedule_id) {
        super(context);
        this.context = context;
        this.schedule_id = schedule_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_schedule_modify);

        mreference=findViewById(R.id.schedule_reference); //참고사항

        btn_add_submit = (ImageView)findViewById(R.id.btn_add_submit);
        btn_add_submit.setOnClickListener(this);

        if(mAuth.getCurrentUser()!=null){//User에 등록되어있는 작성자를 가져오기 위해서
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())//
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                writer_name=(String)task.getResult().getData().get(EmployID.name);//
                                writer_id=(String)task.getResult().getData().get(EmployID.documentId);
                                Log.d("확인","현재 사용자 uid입니다:"+writer_id);
                                Log.d("확인","현재 사용자 이름입니다"+writer_name);

                                name = (TextView) findViewById(R.id.txt_add_name);
                                name.setText(writer_name);

                            }
                        }
                    });
        }


        ImageView btn_date = (ImageView) findViewById(R.id.btn_date);
        btn_date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                new DatePickerDialog(context, d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        //출근시간 TimePicker
        c1 = Calendar.getInstance();
        hourOfDay1 = c1.get(c1.HOUR_OF_DAY);
        minute1 = c1.get(c1.MINUTE);
        startTimeLabel = (TextView)findViewById(R.id.startTimeLabel);
        start_time = (TimePicker)findViewById(R.id.tp_starttime);
        startTimeLabel.setText(hourOfDay1+":"+minute1);
        start_time.setOnTimeChangedListener(this);

        //퇴근시간 TimePicker
        c2 = Calendar.getInstance();
        hourOfDay2 = c2.get(c2.HOUR_OF_DAY);
        minute2 = c2.get(c2.MINUTE);
        endTimeLabel = (TextView) findViewById(R.id.endTimeLabel);
        end_time = (TimePicker)findViewById(R.id.tp_endtime);
        endTimeLabel.setText(hourOfDay2+":"+minute2);
        end_time.setOnTimeChangedListener(this);




        dateAndTimeLabel = (TextView) findViewById(R.id.dateAndTime);


        updateDateLabel();
    }

    @Override
    public void onClick(View v) {
        if(mAuth.getCurrentUser()!=null){
//            intent = new Intent(getContext(), subCalendarAdapter.class);
//            getContext().startActivity(intent);
//            Intent intent=getIntent();
//            schedule_id = intent.getStringExtra(schedule_id);

            Log.d("aaa", "스케줄수정하는 사람:"+writer_name);
            Log.d("aaa", "스케줄수정하는 id값:"+schedule_id);

            Map<String, Object> data = new HashMap<>();
            data.put(EmployID.documentId,mAuth.getCurrentUser().getUid()); //유저 고유번호
            data.put(EmployID.writer_name, writer_name); //작성자 이름
//            data.put(EmployID.schedule_id, schedule_id); //스케줄 고유번호
            data.put(EmployID.writer_id,writer_id); //작성자 id
            data.put(EmployID.date, dateAndTimeLabel.getText().toString()); //날짜
            data.put(EmployID.start_time, startTimeLabel.getText().toString()); //출근시간
            data.put(EmployID.end_time, endTimeLabel.getText().toString()); //퇴근시간
            data.put(EmployID.reference, mreference.getText().toString()); //참고사항
            data.put(EmployID.request, request); //대타신청 유무
            mStore.collection("CalendarPost").document(schedule_id).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getContext(), "update complete", Toast.LENGTH_SHORT).show();
                }
            });

            if(v==btn_add_submit) {
                dismiss();
            }
        }

    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        if(view==start_time){
            startTimeLabel.setText(hourOfDay+":"+minute);
        }
        else{
            endTimeLabel.setText(hourOfDay+":"+minute);
        }

    }
}
