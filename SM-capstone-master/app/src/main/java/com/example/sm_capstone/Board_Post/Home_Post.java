package com.example.sm_capstone.Board_Post;

public class Home_Post {

    private String board_part;
    private String title;
    private String writer_name;
    public Home_Post(){

    }

    public String getWriter_name() {
        return writer_name;
    }

    public void setWriter_name(String writer_name) {
        this.writer_name = writer_name;
    }

    public Home_Post(String writer_name, String title) {
        this.writer_name=writer_name;
        this.title = title;
    }

    public String getBoard_part() {
        return board_part;
    }

    public void setBoard_part(String board_part) {
        this.board_part = board_part;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Home_Post{" +
                "title='" + title + '\'' +
                ", writer_name='" + writer_name + '\'' +
                '}';
    }
}
