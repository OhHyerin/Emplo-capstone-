package com.example.sm_capstone.Board_Post;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comment {
    private String c_writer;//댓글 단사람 이름
   // private String c_photo; //댓글 단사람 사진 추가예정
    private String comment;//댓글
    private String documentId;//댓글 단사람 고유식별번호
    private String board_post;
    private String post_title;
    @ServerTimestamp
    private Date comment_date;
    private String comment_post;//게시글의 uid

    public Comment(String c_writer,  String comment, String documentId, String board_post,  String comment_post) {
        this.c_writer = c_writer;
        this.comment = comment;
        this.documentId = documentId;
        this.board_post =board_post;
        this.comment_post = comment_post;
    }

    public String getC_writer() {
        return c_writer;
    }

    public void setC_writer(String c_writer) {
        this.c_writer = c_writer;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getBoard_post() {
        return board_post;
    }

    public void setBoard_post(String board_post) {
        this.board_post = board_post;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public Date getComment_date() {
        return comment_date;
    }

    public void setComment_date(Date comment_date) {
        this.comment_date = comment_date;
    }

    public String getComment_post() {
        return comment_post;
    }

    public void setComment_post(String comment_post) {
        this.comment_post = comment_post;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "c_writer='" + c_writer + '\'' +
                ", comment='" + comment + '\'' +
                ", documentId='" + documentId + '\'' +
                ", board_post='" + board_post + '\'' +
                ", comment_date=" + comment_date +
                ", comment_post='" + comment_post + '\'' +
                '}';
    }
}
