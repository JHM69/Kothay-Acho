package com.selim_jnu.kothayacho;

import java.util.List;

public class Work {
    String id;
    String school, teacher, number, comment;
    List<String> books;

    public Work(String id, String school, String teacher, String number, String comment, List<String> books) {
        this.id = id;
        this.school = school;
        this.teacher = teacher;
        this.number = number;
        this.comment = comment;
        this.books = books;
    }

    public Work() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getBooks() {
        return books;
    }

    public void setBooks(List<String> books) {
        this.books = books;
    }
}
