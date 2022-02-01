package com.example.sm_capstone;

import com.google.firebase.firestore.ServerTimestamp;

import java.sql.Time;
import java.util.Date;

public class CalendarPost {

    private String documentId; //작성자의 uid
    private String writer_name;  //작성자 이름
    private String schedule_id;  //스케줄의 uid
    private String date;       //스케줄 날짜
    private String start_time;   //출근 시간
    private String end_time;     //퇴근 시간
    private String reference;  //스케줄 참고사항
    private String request;
    private String storeNum;
    private String request_reference;

    public String getRequest_reference() {return request_reference;}

    public void setRequest_reference(String request_reference) {this.request_reference = request_reference;}

    public String getRequest() {return request;}
    public void setRequest(String request) {this.request = request;}
    public String getStoreNum() {return storeNum;}
    public void setStoreNum(String storeNum) {this.storeNum = storeNum;}
    @ServerTimestamp
    private Date caldender_date;
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public String getWriter_name() { return writer_name; }
    public void setWriter_name(String writer_name) { this.writer_name = writer_name;  }
    public String getSchedule_id() { return schedule_id;    }
    public void setSchedule_id(String schedule_id) { this.schedule_id = schedule_id;    }
    public String getStart_time() { return start_time;    }
    public void setStart_time(String start_time) { this.start_time = start_time;    }
    public String getEnd_time() { return end_time;    }
    public void setEnd_time(String end_time) { this.end_time = end_time;    }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Date getCaldender_date() {
        return caldender_date;
    }

    public void setCaldender_date(Date caldender_date) {
        this.caldender_date = caldender_date;
    }

    //빈 생성자
    public CalendarPost(){    }

    public CalendarPost(String documentId, String writer_name, String schedule_id, String date, String start_time, String end_time, String reference, String request, String storeNum, String request_reference){
        this.documentId = documentId;
        this.writer_name = writer_name;
        this.schedule_id = schedule_id;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.reference = reference;
        this.storeNum = storeNum;
        this.request = request;
        this.request_reference = request_reference;
    }

    public CalendarPost(String writer_name, String start_time, String end_time){
        this.writer_name = writer_name;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    @Override
    public String toString() {
        return "CalendarPost{" +
                "documentId='" + documentId + '\'' +
                ", writer_name='" + writer_name + '\'' +
                ", schedule_id='" + schedule_id + '\'' +
                ", date='" + date + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", reference='" + reference + '\'' +
                ", caldender_date=" + caldender_date + '\'' +
                ", request=" + request + '\'' +
                ", storeNum=" + storeNum + '\'' +
                ", request_reference=" + request_reference + '\'' +
                '}';
    }
}
